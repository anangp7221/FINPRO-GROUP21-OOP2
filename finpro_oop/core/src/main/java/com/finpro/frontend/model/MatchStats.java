package com.finpro.frontend.model;

public class MatchStats {
    private int goals;
    private int assists;
    private int passes;
    private boolean pendingPass;

    public int getGoals() { return goals; }
    public int getAssists() { return assists; }
    public int getPasses() { return passes; }
    public boolean isPendingPass() { return pendingPass; }


    public void addGoals(int delta) { goals += delta; }
    public void addAssists(int delta) { assists += delta; }
    public void addPasses(int delta) { passes += delta; }
    public void setPendingPass(boolean value) { pendingPass = value; }
}