package com.finpro.frontend.match;

/** State pattern: tiap fase match punya perilaku sendiri. */
public interface MatchState {
    String getName();

    TurnResult onTurn(Match match);

    default void onEnter(Match match) { }

    /** Dipanggil setelah sebuah giliran (peluang pemain / serangan lawan) selesai. */
    default void onTurnClosed(Match match) { }

    default boolean isFinished() { return false; }
}
