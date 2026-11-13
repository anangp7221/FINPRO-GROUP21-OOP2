package com.finpro.frontend.command;

import com.finpro.frontend.model.Career;
import com.finpro.frontend.model.MatchStats;
import com.finpro.frontend.model.Player;
import com.finpro.frontend.model.Stat;
import java.util.Random;

public class ShootCommand implements Command {
    static final int ENERGY_COST = 10;

    private final Career career;
    private final Random random;

    private int appliedEnergy;
    private int goalAdded;
    private int assistAdded;
    private boolean previousPending;

    public ShootCommand(Career career, Random random) {
        this.career = career;
        this.random = random;
    }

    @Override
    public String getName() { return "Tembak"; }

    @Override
    public boolean canExecute() {
        return career.getPlayer().getEnergy() >= ENERGY_COST;
    }

    @Override
    public String execute() {
        Player p = career.getPlayer();
        MatchStats ms = career.getMatchStats();

        appliedEnergy = p.changeEnergy(-ENERGY_COST);
        previousPending = ms.isPendingPass();

        boolean goal = random.nextInt(100) < p.getStat(Stat.SHOOTING);
        goalAdded = goal ? 1 : 0;
        assistAdded = (goal && previousPending) ? 1 : 0;

        ms.addGoals(goalAdded);
        ms.addAssists(assistAdded);
        ms.setPendingPass(false);

        return goal ? "GOL!" : "Tembakan melenceng.";
    }

    @Override
    public void undo() {
        MatchStats ms = career.getMatchStats();
        career.getPlayer().changeEnergy(-appliedEnergy);
        ms.addGoals(-goalAdded);
        ms.addAssists(-assistAdded);
        ms.setPendingPass(previousPending);
    }
}
