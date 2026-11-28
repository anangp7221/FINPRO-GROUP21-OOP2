package com.finpro.frontend.tournament;

import java.util.List;

public record TieOutcome(Type type, String summary, int aggregateFor, int aggregateAgainst,
                         List<String> penaltyLog) {

    public enum Type { NEXT_LEG, ADVANCED, CHAMPION, ELIMINATED }

    public boolean isGameOver() { return type == Type.CHAMPION || type == Type.ELIMINATED; }
}
