package com.finpro.frontend.engine;

/** Target GameLoop: menambah waktu main karier tiap frame (delta time). */
public class CareerClock implements Updatable {
    @Override
    public void update(float dt) {
        GameManager gm = GameManager.getInstance();
        if (gm.hasCareer()) {
            gm.getCareer().addPlayTime(dt);
        }
    }
}
