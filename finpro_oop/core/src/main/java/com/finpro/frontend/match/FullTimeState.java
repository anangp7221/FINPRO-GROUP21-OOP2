package com.finpro.frontend.match;

import com.finpro.frontend.event.GameEventType;
import com.finpro.frontend.model.Career;
import com.finpro.frontend.model.Player;

public class FullTimeState implements MatchState {
    private String summary = "";

    @Override public String getName() { return "Selesai"; }
    @Override public boolean isFinished() { return true; }

    @Override
    public void onEnter(Match m) {
        int sign = Integer.compare(m.getHome().getGoals(), m.getAway().getGoals());
        String verdict = sign > 0 ? "MENANG" : sign < 0 ? "KALAH" : "SERI";
        summary = "Peluit panjang! " + m.scoreLine() + " (" + verdict + ")";
        m.publish(GameEventType.FULL_TIME, summary, sign);

        Career career = m.getCareer();
        career.getPlayer().changeEnergy(Player.MAX_ENERGY); // pulih sebelum pekan berikutnya
        career.nextWeek();
    }

    @Override
    public TurnResult onTurn(Match m) {
        return new TurnResult(m.getMinute(), TurnType.FULL_TIME, summary);
    }
}
