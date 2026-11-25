package com.finpro.frontend.demo;

import com.finpro.frontend.command.CommandResult;
import com.finpro.frontend.command.PassCommand;
import com.finpro.frontend.command.ShootCommand;
import com.finpro.frontend.engine.CareerClock;
import com.finpro.frontend.engine.GameLoop;
import com.finpro.frontend.engine.GameManager;
import com.finpro.frontend.match.Match;
import com.finpro.frontend.match.Tactic;
import com.finpro.frontend.match.Team;
import com.finpro.frontend.match.TurnResult;
import com.finpro.frontend.match.TurnType;
import com.finpro.frontend.model.Career;
import com.finpro.frontend.model.CareerRecord;
import com.finpro.frontend.story.StoryBook;
import com.finpro.frontend.story.StoryManager;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

/** Tes cepat tanpa UI. Boleh dihapus kalau UI sudah jadi. */
public class ConsoleDemo {
    public static void main(String[] args) throws Exception {
        Path tmp = Files.createTempDirectory("finpro");
        GameManager gm = GameManager.getInstance();
        gm.setSavePath(tmp.resolve("career.sav"));

        Career career = gm.newCareer("Kelvin");
        Random rnd = new Random(7);
        gm.getEventBus().subscribe(new StoryManager(career, gm.getEventBus(), StoryBook.defaultBook()));
        StoryManager story = null;

        GameLoop loop = new GameLoop(new CareerClock());
        for (int i = 0; i < 60; i++) loop.update(0.016f);

        for (int w = 0; w < 3; w++) {
            Match match = new Match(career, new Team("Garuda FC", Tactic.BALANCED),
                    new Team("Rival FC", Tactic.random(rnd)), rnd, gm.getEventBus());
            while (!match.isFinished()) {
                TurnResult tr = match.nextTurn();
                if (tr.type() == TurnType.PLAYER_CHANCE) {
                    match.playerAct(new PassCommand(career, rnd));
                    CommandResult r = match.playerAct(new ShootCommand(career, rnd));
                    if (!r.success()) match.skipChance();
                }
                if (tr.type() == TurnType.BREAK) { /* UI bisa ganti strategi di sini */ }
            }
        }

        gm.getEventLog().getLines().forEach(System.out::println);
        CareerRecord r = career.getRecord();
        System.out.printf("%nRECORD: %d main, %dM %dS %dK, %d gol, %d assist | pekan %d | energi %d%n",
                r.getMatches(), r.getWins(), r.getDraws(), r.getLosses(), r.getGoals(), r.getAssists(),
                career.getWeek(), career.getPlayer().getEnergy());

        System.out.println(gm.saveCareer().message());
        Career before = career;
        System.out.println(gm.loadCareer().message());
        Career after = gm.getCareer();
        System.out.println("Load sama? week " + (before.getWeek() == after.getWeek())
                + ", gol " + (before.getRecord().getGoals() == after.getRecord().getGoals())
                + ", nama " + after.getPlayer().getName());

        // tes file rusak
        Path f = gm.getSavePath();
        Files.writeString(f, Files.readString(f).replace("week=4", "week=9"));
        System.out.println("Tampered: " + gm.loadCareer().message());
        System.out.println("Karier masih ada setelah gagal load? " + gm.hasCareer());
        gm.saveCareer();
        Files.writeString(f, "sampah acak\n");
        System.out.println("Sampah: " + gm.loadCareer().message());
        System.out.println("Tanpa file: " + gm.loadCareer().message());
    }
}
