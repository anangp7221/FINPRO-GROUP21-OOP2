package com.finpro.frontend.match;

public class FirstHalfState extends PlayingState {
    @Override public String getName() { return "Babak 1"; }
    @Override protected int endMinute() { return Match.HALF_LENGTH; }
    @Override protected MatchState next() { return new HalfTimeState(); }
}
