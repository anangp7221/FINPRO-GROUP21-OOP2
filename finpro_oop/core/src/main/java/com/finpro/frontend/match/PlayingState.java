package com.finpro.frontend.match;

/** Kerangka satu babak (Template Method): subclass cuma menentukan menit akhir dan state berikutnya. */
public abstract class PlayingState implements MatchState {
    protected abstract int endMinute();

    protected abstract MatchState next();

    @Override
    public TurnResult onTurn(Match m) {
        m.advanceMinute();
        int homeW = m.getHome().getStrategy().attackWeight();
        int awayW = m.getAway().getStrategy().attackWeight();
        boolean homeAttacks = m.getRandom().nextInt(homeW + awayW) < homeW;

        TurnResult result = homeAttacks ? m.openPlayerChance() : m.resolveOpponentAttack();
        if (!m.isAwaitingPlayer()) {
            checkEnd(m);
        }
        return result;
    }

    @Override
    public void onTurnClosed(Match m) {
        checkEnd(m);
    }

    private void checkEnd(Match m) {
        if (m.getMinute() >= endMinute()) {
            m.setState(next());
        }
    }
}
