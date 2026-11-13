package com.finpro.frontend.model;

import java.util.EnumMap;
import java.util.Map;

public class Player {
    public static final int MIN_VALUE = 0;
    public static final int MAX_STAT = 100;
    public static final int MAX_ENERGY = 100;
    private static final int START_STAT = 50;

    private final String name;
    private final Map<Stat, Integer> stats = new EnumMap<>(Stat.class);
    private int energy = MAX_ENERGY;

    public Player(String name) {
        this.name = name;
        for (Stat s : Stat.values()) {
            stats.put(s, START_STAT);
        }
    }

    public String getName() { return name; }

    public int getStat(Stat stat) { return stats.get(stat); }

    public int getEnergy() { return energy; }


    public int changeStat(Stat stat, int delta) {
        int before = stats.get(stat);
        int after = clamp(before + delta, MIN_VALUE, MAX_STAT);
        stats.put(stat, after);
        return after - before;
    }


    public int changeEnergy(int delta) {
        int before = energy;
        energy = clamp(before + delta, MIN_VALUE, MAX_ENERGY);
        return energy - before;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
