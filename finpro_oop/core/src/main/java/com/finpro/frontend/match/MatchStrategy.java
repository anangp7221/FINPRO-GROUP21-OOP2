package com.finpro.frontend.match;

/** Cara sebuah tim "berpikir" di match. Bisa diganti kapan saja lewat Team.setStrategy(). */
public interface MatchStrategy {
    String getName();

    /** Bobot peluang tim ini menguasai giliran serang. */
    int attackWeight();

    /** Persen peluang serangan tim ini jadi gol (sebelum dikurangi pertahanan lawan). */
    int scoreChance();

    /** Pengurang peluang gol lawan. */
    int defenseRating();
}
