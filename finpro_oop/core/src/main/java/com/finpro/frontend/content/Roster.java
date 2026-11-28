package com.finpro.frontend.content;

import com.finpro.frontend.match.ScaledStrategy;
import com.finpro.frontend.match.Tactic;
import java.util.List;

/** Nama klub, karakter, dan lawan tiap fase. Ganti isinya di sini kalau mau ubah karakter/lawan. */
public final class Roster {
    public static final String CLUB = "Liverpool";
    public static final String COACH = "Coach Bima";
    public static final String JOURNALIST = "Maya";
    public static final String CAPTAIN = "Rafi";

    private static final List<Opponent> OPPONENTS = List.of(
            new Opponent("Bayern Munich", new ScaledStrategy(Tactic.BALANCED, -2, 0),
                    "Raksasa Jerman, disiplin dan rapi, tapi kurang tajam di depan gawang."),
            new Opponent("Barcelona", new ScaledStrategy(Tactic.ATTACKING, -7, 0),
                    "Tim Spanyol yang gemar menyerang. Pertahanannya sering terbuka."),
            new Opponent("Inter Milan", new ScaledStrategy(Tactic.BALANCED, 2, 2),
                    "Juara bertahan dari Italia. Lawan terkuat di turnamen ini."));

    private Roster() { }

    /** stageIndex: 0 = perempat final, 1 = semi final, 2 = final. */
    public static Opponent opponentFor(int stageIndex) {
        return OPPONENTS.get(stageIndex);
    }
}