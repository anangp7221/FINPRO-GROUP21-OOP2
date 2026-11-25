package com.finpro.frontend.match;

import com.finpro.frontend.command.Command;
import com.finpro.frontend.command.CommandHistory;
import com.finpro.frontend.command.CommandResult;
import com.finpro.frontend.command.ShootCommand;
import com.finpro.frontend.event.EventBus;
import com.finpro.frontend.event.GameEventType;
import com.finpro.frontend.model.Career;
import com.finpro.frontend.model.MatchStats;
import com.finpro.frontend.model.Stat;
import java.util.List;
import java.util.Random;

/**
 * Match turn-based. Alur dari UI:
 *   1. nextTurn()      -> TurnResult (PLAYER_CHANCE / OPPONENT_ATTACK / BREAK / FULL_TIME)
 *   2. kalau PLAYER_CHANCE: playerAct(PassCommand/ShootCommand), undoLast(), atau skipChance()
 *   3. ulangi sampai isFinished()
 */
public class Match {
    public static final int MINUTES_PER_TURN = 5;
    public static final int HALF_LENGTH = 45;

    private final Career career;
    private final Team home;
    private final Team away;
    private final Random random;
    private final EventBus bus;
    private final CommandHistory history = new CommandHistory();

    private MatchState state = new FirstHalfState();
    private int minute;

    private boolean awaitingPlayer;
    private int actionsInChance;
    private int goalsBefore;
    private int assistsBefore;

    public Match(Career career, Team home, Team away, Random random, EventBus bus) {
        this.career = career;
        this.home = home;
        this.away = away;
        this.random = random;
        this.bus = bus;
        publish(GameEventType.MATCH_START, "Kick-off: " + home.getName() + " vs " + away.getName(), 0);
    }

    // ---------- API untuk UI ----------

    public TurnResult nextTurn() {
        if (awaitingPlayer) {
            throw new IllegalStateException("Selesaikan peluang dulu (playerAct / skipChance).");
        }
        return state.onTurn(this);
    }

    public CommandResult playerAct(Command command) {
        if (!awaitingPlayer) {
            return new CommandResult(false, "Tidak ada peluang yang sedang berjalan.");
        }
        CommandResult result = history.execute(command);
        if (!result.success()) {
            return result;
        }
        actionsInChance++;
        if (command instanceof ShootCommand) {
            closeChance(true);
        }
        return result;
    }

    public CommandResult undoLast() {
        if (!awaitingPlayer || actionsInChance == 0) {
            return new CommandResult(false, "Tidak ada aksi di peluang ini yang bisa dibatalkan.");
        }
        actionsInChance--;
        return history.undo();
    }

    public CommandResult skipChance() {
        if (!awaitingPlayer) {
            return new CommandResult(false, "Tidak ada peluang yang sedang berjalan.");
        }
        closeChance(false);
        return new CommandResult(true, "Peluang dilepas ke rekan setim.");
    }

    public boolean isFinished() { return state.isFinished(); }
    public boolean isAwaitingPlayer() { return awaitingPlayer; }
    public int getMinute() { return minute; }
    public MatchState getState() { return state; }
    public Team getHome() { return home; }
    public Team getAway() { return away; }
    public Career getCareer() { return career; }
    public CommandHistory getHistory() { return history; }
    public List<String> getActionLog() { return history.getLog(); }

    public String scoreLine() {
        return home.getName() + " " + home.getGoals() + " - " + away.getGoals() + " " + away.getName();
    }

    // ---------- dipakai State (package-private) ----------

    Random getRandom() { return random; }

    void advanceMinute() { minute = Math.min(minute + MINUTES_PER_TURN, HALF_LENGTH * 2); }

    void setState(MatchState newState) {
        state = newState;
        newState.onEnter(this);
    }

    void publish(GameEventType type, String message, int value) {
        bus.publish(type, minute + "' " + message, value);
    }

    TurnResult openPlayerChance() {
        MatchStats ms = career.getMatchStats();
        awaitingPlayer = true;
        actionsInChance = 0;
        goalsBefore = ms.getGoals();
        assistsBefore = ms.getAssists();
        String text = "Peluang untuk " + home.getName() + "! Umpan, tembak, atau lepas.";
        publish(GameEventType.PLAYER_CHANCE, text, 0);
        return new TurnResult(minute, TurnType.PLAYER_CHANCE, text);
    }

    TurnResult resolveOpponentAttack() {
        int stamina = career.getPlayer().getStat(Stat.STAMINA);
        int chance = clamp(away.getStrategy().scoreChance()
                - home.getStrategy().defenseRating() - stamina / 10, 5, 90);
        boolean goal = random.nextInt(100) < chance;
        String text;
        if (goal) {
            away.addGoals(1);
            text = "GOL " + away.getName() + "! " + scoreLine();
            publish(GameEventType.GOAL_CONCEDED, text, 1);
        } else {
            text = away.getName() + " menyerang, tapi berhasil dihalau.";
        }
        return new TurnResult(minute, TurnType.OPPONENT_ATTACK, text);
    }

    // ---------- internal ----------

    private void closeChance(boolean shot) {
        MatchStats ms = career.getMatchStats();
        int goals = ms.getGoals() - goalsBefore;
        int assists = ms.getAssists() - assistsBefore;

        if (goals > 0) {
            home.addGoals(goals);
            publish(GameEventType.PLAYER_GOAL,
                    "GOL! " + career.getPlayer().getName() + " mencetak gol. " + scoreLine(), goals);
        }
        if (assists > 0) {
            publish(GameEventType.PLAYER_ASSIST, "Assist tercatat untuk " + career.getPlayer().getName() + ".", assists);
        }
        if (!shot) {
            // pemain melepas bola: rekan setim coba menyelesaikan, peluangnya setengah dari serangan normal
            int chance = clamp(home.getStrategy().scoreChance() - away.getStrategy().defenseRating(), 5, 90) / 2;
            if (random.nextInt(100) < chance) {
                home.addGoals(1);
                publish(GameEventType.TEAM_GOAL, "GOL rekan setim! " + scoreLine(), 1);
            }
        }

        ms.setPendingPass(false);
        awaitingPlayer = false;
        actionsInChance = 0;
        state.onTurnClosed(this);
    }

    private static int clamp(int v, int min, int max) { return Math.max(min, Math.min(max, v)); }
}
