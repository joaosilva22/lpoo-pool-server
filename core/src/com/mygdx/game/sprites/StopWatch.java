package com.mygdx.game.sprites;

/**
 * Created by joaopsilva on 06-06-2016.
 */
public class StopWatch {
    private float elapsed;
    private boolean done;

    public StopWatch() {
        elapsed = 0.0f;
        done = true;
    }

    public void start() {
        elapsed = 0.0f;
        done = false;
    }

    public void update(float delta) {
        if (!done) elapsed += delta;
    }

    public void stop() {
        done = true;
    }

    public String toString() {
        int minutes = (int) (elapsed / 60);
        int seconds = (int) elapsed - (minutes * 60);
        return String.format("%02d:%02d", minutes, seconds);
    }
}
