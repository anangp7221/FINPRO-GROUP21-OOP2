package com.finpro.frontend.command;

import com.finpro.frontend.model.Career;
import com.finpro.frontend.model.MatchStats;
import com.finpro.frontend.model.Player;
import com.finpro.frontend.model.Stat;
import java.util.Random;

public class PassCommand implements Command {
    static final int ENERGY_COST = 5;

    private final Career career;
    private final Random random;

    private int appliedEnergy;
    private int passAdded;
    private boolean previousPending;

    public PassCommand(Career career, Random random) {
        this.career = career;
        this.random = random;
    }

    @Override
    public String getName() { return "Umpan"; }

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

        boolean success = random.nextInt(100) < p.getStat(Stat.PASSING);
        passAdded = success ? 1 : 0;
        ms.addPasses(passAdded);
        ms.setPendingPass(success);

        return success ? "Umpan berhasil!" : "Umpan terpotong lawan.";
    }

    @Override
    public void undo() {
        MatchStats ms = career.getMatchStats();
        career.getPlayer().changeEnergy(-appliedEnergy);
        ms.addPasses(-passAdded);
        ms.setPendingPass(previousPending);
    }
}
