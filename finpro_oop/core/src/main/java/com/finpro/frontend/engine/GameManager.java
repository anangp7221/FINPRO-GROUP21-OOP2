package com.finpro.frontend.engine;

import com.finpro.frontend.model.Career;
import com.finpro.frontend.model.Player;

public class GameManager {
    private static GameManager instance;

    private Career career;

    private GameManager() { }

    public static synchronized GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public Career newCareer(String playerName) {
        career = new Career(new Player(playerName));
        return career;
    }

    public boolean hasCareer() { return career != null; }

    public Career getCareer() {
        if (career == null) {
            throw new IllegalStateException("Belum ada karier. Panggil newCareer() dulu.");
        }
        return career;
    }

    public void reset() { career = null; }
}

