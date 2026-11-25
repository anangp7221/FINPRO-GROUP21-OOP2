package com.finpro.frontend.match;

import com.finpro.frontend.event.GameEventType;
import com.finpro.frontend.model.Player;
import com.finpro.frontend.model.Stat;

public class HalfTimeState implements MatchState {
    @Override public String getName() { return "Turun minum"; }

    @Override
    public void onEnter(Match m) {
        m.publish(GameEventType.HALF_TIME, "Turun minum. " + m.scoreLine(),
                m.getHome().getGoals() - m.getAway().getGoals());
    }

    /** Selama turun minum, UI boleh ganti strategi: match.getHome().setStrategy(...). */
    @Override
    public TurnResult onTurn(Match m) {
        Player p = m.getCareer().getPlayer();
        int gained = p.changeEnergy(10 + p.getStat(Stat.STAMINA) / 5);
        m.setState(new SecondHalfState());
        return new TurnResult(m.getMinute(), TurnType.BREAK,
                "Babak kedua dimulai. Energi pulih +" + gained + ".");
    }
}
