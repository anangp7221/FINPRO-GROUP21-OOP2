package com.finpro.frontend.command;

import com.finpro.frontend.model.Career;
import com.finpro.frontend.model.Player;
import com.finpro.frontend.model.Stat;

public class TrainCommand implements Command {
    static final int ENERGY_COST = 20;
    static final int GAIN = 2;

    private final Career career;
    private final Stat stat;

    private int appliedEnergy;
    private int appliedStat;

    public TrainCommand(Career career, Stat stat) {
        this.career = career;
        this.stat = stat;
    }

    @Override
    public String getName() { return "Latihan " + stat; }

    @Override
    public boolean canExecute() {
        return career.getPlayer().getEnergy() >= ENERGY_COST;
    }

    @Override
    public String execute() {
        Player p = career.getPlayer();
        appliedEnergy = p.changeEnergy(-ENERGY_COST);
        appliedStat = p.changeStat(stat, GAIN);
        return stat + " +" + appliedStat + " (energi " + p.getEnergy() + ")";
    }

    @Override
    public void undo() {
        Player p = career.getPlayer();
        p.changeEnergy(-appliedEnergy);
        p.changeStat(stat, -appliedStat);
    }
}
