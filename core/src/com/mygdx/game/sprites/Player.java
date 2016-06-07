package com.mygdx.game.sprites;

/**
 * Created by joaopsilva on 04-06-2016.
 */
public class Player {
    private String name;
    private int points;
    private BallData.Type type;
    public boolean won;

    public Player(String name) {
        this.name = name;
        points = 0;
        type = null;
        won = false;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public BallData.Type getType() {
        return type;
    }

    public void setType(BallData.Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setWon(boolean won) {
        this.won = won;
    }

    public boolean hasWon() {
        return won;
    }
}
