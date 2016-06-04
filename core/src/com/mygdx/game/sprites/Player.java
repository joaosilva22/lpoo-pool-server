package com.mygdx.game.sprites;

/**
 * Created by joaopsilva on 04-06-2016.
 */
public class Player {
    String name;
    int points;

    public Player(String name) {
        this.name = name;
        points = 0;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
