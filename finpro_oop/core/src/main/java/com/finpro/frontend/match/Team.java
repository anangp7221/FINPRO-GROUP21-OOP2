package com.finpro.frontend.match;

public class Team {
    private final String name;
    private MatchStrategy strategy;
    private int goals;

    public Team(String name, MatchStrategy strategy) {
        this.name = name;
        setStrategy(strategy);
    }

    public String getName() { return name; }
    public MatchStrategy getStrategy() { return strategy; }
    public int getGoals() { return goals; }

    public void setStrategy(MatchStrategy strategy) {
        if (strategy == null) throw new IllegalArgumentException("Strategi tidak boleh null");
        this.strategy = strategy;
    }

    void addGoals(int n) { goals += n; }
}
