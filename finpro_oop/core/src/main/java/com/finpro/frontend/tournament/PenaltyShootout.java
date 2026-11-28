package com.finpro.frontend.tournament;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Adu penalti otomatis bila agregat seri. Peluang pemain naik seiring stat SHOOTING. */
public final class PenaltyShootout {
    public record Result(int playerGoals, int opponentGoals, boolean playerWon, List<String> log) { }

    static final int OPPONENT_CHANCE = 72;
    private static final int MAX_SUDDEN_DEATH = 20;

    private PenaltyShootout() { }

    public static int playerChance(int shootingStat) {
        return Math.max(50, Math.min(92, 72 + (shootingStat - 50) / 3));
    }

    public static Result run(Random random, int shootingStat, String playerClub, String opponentClub) {
        List<String> log = new ArrayList<>();
        int playerChance = playerChance(shootingStat);
        int pg = 0, og = 0, pTaken = 0, oTaken = 0;

        // 5 tendangan pertama, berhenti lebih awal kalau hasil sudah pasti
        for (int i = 1; i <= 5; i++) {
            boolean p = random.nextInt(100) < playerChance;
            pTaken++;
            if (p) pg++;
            log.add("Penalti " + i + " - " + playerClub + ": " + (p ? "GOL" : "GAGAL"));
            if (decided(pg, og, pTaken, oTaken)) break;

            boolean o = random.nextInt(100) < OPPONENT_CHANCE;
            oTaken++;
            if (o) og++;
            log.add("Penalti " + i + " - " + opponentClub + ": " + (o ? "GOL" : "GAGAL"));
            if (decided(pg, og, pTaken, oTaken)) break;
        }

        // sudden death
        int round = 5;
        while (pg == og && round < 5 + MAX_SUDDEN_DEATH) {
            round++;
            boolean p = random.nextInt(100) < playerChance;
            boolean o = random.nextInt(100) < OPPONENT_CHANCE;
            if (p) pg++;
            if (o) og++;
            log.add("Sudden death " + (round - 5) + ": " + (p ? "GOL" : "GAGAL") + " / " + (o ? "GOL" : "GAGAL"));
        }
        if (pg == og) { // sangat jarang, pastikan selalu ada pemenang
            boolean playerWins = random.nextBoolean();
            if (playerWins) pg++; else og++;
        }
        return new Result(pg, og, pg > og, log);
    }

    private static boolean decided(int pg, int og, int pTaken, int oTaken) {
        int pLeft = 5 - pTaken;
        int oLeft = 5 - oTaken;
        return pg > og + oLeft || og > pg + pLeft;
    }
}
