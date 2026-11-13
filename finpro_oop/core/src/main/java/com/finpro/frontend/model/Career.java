package com.finpro.frontend.model;


public class Career {
    private final Player player;
    private final MatchStats matchStats = new MatchStats();
    private int week = 1;
    private double playTimeSeconds;

    public Career(Player player) {
        this.player = player;
    }

    public Player getPlayer() { return player; }
    public MatchStats getMatchStats() { return matchStats; }
    public int getWeek() { return week; }
    public double getPlayTimeSeconds() { return playTimeSeconds; }

    public void nextWeek() { week++; }
    public void addPlayTime(float dt) { playTimeSeconds += dt; }
}
