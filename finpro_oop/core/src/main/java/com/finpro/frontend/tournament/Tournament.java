package com.finpro.frontend.tournament;

import com.finpro.frontend.content.Opponent;
import com.finpro.frontend.content.Roster;
import com.finpro.frontend.event.EventBus;
import com.finpro.frontend.event.GameEventType;
import com.finpro.frontend.match.Match;
import com.finpro.frontend.match.MatchStrategy;
import com.finpro.frontend.match.Team;
import com.finpro.frontend.model.Career;
import com.finpro.frontend.model.Stat;
import java.util.List;
import java.util.Random;

/**
 * Format karier: 5 pertandingan Liga Champions.
 * Perempat final 2 leg, semi final 2 leg, final 1 pertandingan.
 * Posisi saat ini diambil dari Career.getWeek() (pekan 1 = match pertama).
 */
public class Tournament {
    public static final List<Round> ROUNDS = List.of(
        new Round(0, "Perempat Final", 1, 2, true),
        new Round(0, "Perempat Final", 2, 2, false),
        new Round(1, "Semi Final", 1, 2, true),
        new Round(1, "Semi Final", 2, 2, false),
        new Round(2, "Final", 1, 1, true));
    public static final int TOTAL_MATCHES = ROUNDS.size();

    private final Career career;
    private final EventBus bus;
    private final Random random;
    private boolean eliminated;
    private boolean champion;

    public Tournament(Career career, EventBus bus, Random random) {
        this.career = career;
        this.bus = bus;
        this.random = random;
    }

    public int currentIndex() { return career.getWeek() - 1; }

    public boolean isOver() { return eliminated || champion || currentIndex() >= TOTAL_MATCHES; }
    public boolean isChampion() { return champion; }
    public boolean isEliminated() { return eliminated; }

    public Round currentRound() { return roundAt(currentIndex()); }

    public Round roundAt(int index) {
        if (index < 0 || index >= TOTAL_MATCHES) {
            throw new IllegalStateException("Turnamen sudah selesai.");
        }
        return ROUNDS.get(index);
    }

    public Opponent currentOpponent() { return Roster.opponentFor(currentRound().stage()); }

    /** Umumkan awal ronde (memicu scene pembuka lewat Observer). */
    public void announceRound() {
        bus.publish(GameEventType.ROUND_INTRO, "Ronde: " + currentRound().getName(), currentIndex());
    }

    public Match createMatch(MatchStrategy playerTactic) {
        Opponent opponent = currentOpponent();
        Team home = new Team(Roster.CLUB, playerTactic);
        Team away = new Team(opponent.name(), opponent.strategy());
        return new Match(career, home, away, random, bus);
    }

    /**
     * Dipanggil setelah match selesai. roundIndex harus indeks ronde yang barusan dimainkan
     * (Match sudah menaikkan pekan, jadi jangan ambil dari currentIndex()).
     */
    public TieOutcome finishMatch(int roundIndex, int goalsFor, int goalsAgainst) {
        Round round = roundAt(roundIndex);
        career.addTie(goalsFor, goalsAgainst);

        if (!round.isDeciding()) {
            int sign = Integer.compare(goalsFor, goalsAgainst);
            if (sign > 0) {
                bus.publish(GameEventType.LEG_WON, "Menang di leg pertama.", roundIndex);
            } else if (sign < 0) {
                bus.publish(GameEventType.LEG_LOST, "Kalah di leg pertama.", roundIndex);
            }
            return new TieOutcome(TieOutcome.Type.NEXT_LEG,
                    "Leg pertama selesai. Agregat " + career.getTieFor() + " - " + career.getTieAgainst() + ".",
                    career.getTieFor(), career.getTieAgainst(), List.of());
        }

        int aggFor = career.getTieFor();
        int aggAgainst = career.getTieAgainst();
        boolean won;
        List<String> penalties = List.of();
        String how;
        if (aggFor != aggAgainst) {
            won = aggFor > aggAgainst;
            how = round.legs() > 1 ? "Agregat " + aggFor + " - " + aggAgainst + "." : "Skor akhir " + aggFor + " - " + aggAgainst + ".";
        } else {
            PenaltyShootout.Result p = PenaltyShootout.run(random,
                    career.getPlayer().getStat(Stat.SHOOTING), Roster.CLUB, Roster.opponentFor(round.stage()).name());
            won = p.playerWon();
            penalties = p.log();
            how = (round.legs() > 1 ? "Agregat " : "Skor ") + aggFor + " - " + aggAgainst + ", adu penalti "
                    + p.playerGoals() + " - " + p.opponentGoals() + ".";
        }
        career.resetTie();

        boolean isFinal = roundIndex == TOTAL_MATCHES - 1;
        if (won) {
            if (isFinal) {
                champion = true;
                bus.publish(GameEventType.TIE_WON, "JUARA LIGA CHAMPIONS!", roundIndex);
                return new TieOutcome(TieOutcome.Type.CHAMPION, "JUARA! " + how, aggFor, aggAgainst, penalties);
            }
            bus.publish(GameEventType.TIE_WON, "Lolos ke babak berikutnya.", roundIndex);
            return new TieOutcome(TieOutcome.Type.ADVANCED, "Lolos ke " + ROUNDS.get(roundIndex + 1).stageName() + "! " + how,
                    aggFor, aggAgainst, penalties);
        }
        eliminated = true;
        bus.publish(GameEventType.TIE_LOST, "Tersingkir.", roundIndex);
        return new TieOutcome(TieOutcome.Type.ELIMINATED, "Tersingkir di " + round.stageName() + ". " + how,
                aggFor, aggAgainst, penalties);
    }
}
