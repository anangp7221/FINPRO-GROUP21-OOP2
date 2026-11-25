package com.finpro.frontend.match;

public class SecondHalfState extends PlayingState {
    @Override public String getName() { return "Babak 2"; }
    @Override protected int endMinute() { return Match.HALF_LENGTH * 2; }
    @Override protected MatchState next() { return new FullTimeState(); }
}
