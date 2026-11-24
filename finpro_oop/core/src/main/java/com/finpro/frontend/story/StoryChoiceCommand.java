package com.finpro.frontend.story;

import com.finpro.frontend.command.Command;
import com.finpro.frontend.model.Career;
import com.finpro.frontend.model.Player;

/** Pilihan cerita sebagai Command, jadi ikut sistem pilihan yang sama (log + undo). */
public class StoryChoiceCommand implements Command {
    private final Career career;
    private final StoryChoice choice;

    private int appliedStat;
    private int appliedEnergy;

    public StoryChoiceCommand(Career career, StoryChoice choice) {
        this.career = career;
        this.choice = choice;
    }

    @Override
    public String getName() { return "Cerita: " + choice.label(); }

    @Override
    public boolean canExecute() {
        Player p = career.getPlayer();
        if (choice.requiredStat() != null && p.getStat(choice.requiredStat()) < choice.requiredValue()) {
            return false;
        }
        return p.getEnergy() + choice.energyDelta() >= 0;
    }

    @Override
    public String execute() {
        Player p = career.getPlayer();
        appliedEnergy = p.changeEnergy(choice.energyDelta());
        appliedStat = choice.stat() == null ? 0 : p.changeStat(choice.stat(), choice.statDelta());
        return choice.resultText();
    }

    @Override
    public void undo() {
        Player p = career.getPlayer();
        p.changeEnergy(-appliedEnergy);
        if (choice.stat() != null) {
            p.changeStat(choice.stat(), -appliedStat);
        }
    }
}
