package com.finpro.frontend.model;

/** Statistik karier jangka panjang (disimpan ke file). Di-update lewat Observer. */
public class CareerRecord {
    private int matches, wins, draws, losses, goals, assists;

    public void addResult(int sign) {
        matches++;
        if (sign > 0) wins++;
        else if (sign < 0) losses++;
        else draws++;
    }

    public void addGoals(int n) { goals += n; }
    public void addAssists(int n) { assists += n; }

    public int getMatches() { return matches; }
    public int getWins() { return wins; }
    public int getDraws() { return draws; }
    public int getLosses() { return losses; }
    public int getGoals() { return goals; }
    public int getAssists() { return assists; }

    public void restore(int matches, int wins, int draws, int losses, int goals, int assists) {
        this.matches = matches;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.goals = goals;
        this.assists = assists;
    }
}
