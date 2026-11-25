package com.finpro.frontend.match;

import java.util.Random;

/** Tiga strategi bawaan. Mau tambah strategi baru? Cukup bikin class lain yang implement MatchStrategy. */
public enum Tactic implements MatchStrategy {
    DEFENSIVE("Bertahan", 3, 20, 10),
    BALANCED("Seimbang", 5, 30, 5),
    ATTACKING("Menyerang", 7, 40, 0);

    private final String label;
    private final int attackWeight;
    private final int scoreChance;
    private final int defenseRating;

    Tactic(String label, int attackWeight, int scoreChance, int defenseRating) {
        this.label = label;
        this.attackWeight = attackWeight;
        this.scoreChance = scoreChance;
        this.defenseRating = defenseRating;
    }

    @Override public String getName() { return label; }
    @Override public int attackWeight() { return attackWeight; }
    @Override public int scoreChance() { return scoreChance; }
    @Override public int defenseRating() { return defenseRating; }

    public static Tactic random(Random random) {
        Tactic[] all = values();
        return all[random.nextInt(all.length)];
    }
}
