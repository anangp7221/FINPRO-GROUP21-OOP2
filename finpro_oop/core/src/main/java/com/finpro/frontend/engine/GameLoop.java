package com.finpro.frontend.engine;

public class GameLoop {
    public static final float MAX_DT = 0.1f;

    private final Updatable target;
    private boolean paused;

    public GameLoop(Updatable target) {
        this.target = target;
    }

    public void update(float rawDt) {
        if (paused) {
            return;
        }
        target.update(Math.min(rawDt, MAX_DT));
    }

    public void pause() { paused = true; }
    public void resume() { paused = false; }
    public boolean isPaused() { return paused; }
}
