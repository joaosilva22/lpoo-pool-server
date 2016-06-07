package com.mygdx.game.sprites;

/**
 * Created by joaopsilva on 06-06-2016.
 */
public class StopWatch {
    private float elapsed;
    private boolean done;

    /**
     * Creates a StopWatch object.
     * Useful to time events.
     */
    public StopWatch() {
        elapsed = 0.0f;
        done = true;
    }

    /**
     * Start the stopwatch count.
     */
    public void start() {
        elapsed = 0.0f;
        done = false;
    }

    /**
     * Update the stopwatch.
     * @param delta Time interval between update calls.
     */
    public void update(float delta) {
        if (!done) elapsed += delta;
    }

    /**
     * Stops the stopwatch.
     */
    public void stop() {
        done = true;
    }

    /**
     * Returns the stopwatch's time in a human-friendly format.
     * @return A time string.
     */
    public String toString() {
        int minutes = (int) (elapsed / 60);
        int seconds = (int) elapsed - (minutes * 60);
        return String.format("%02d:%02d", minutes, seconds);
    }
}
