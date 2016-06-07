package com.mygdx.game.sprites;

/**
 * Created by joaopsilva on 04-06-2016.
 */
public class Player {
    private String name;
    private int points;
    private BallData.Type type;
    public boolean won;

    /**
     * Creates a Player object.
     * Holds information about a player.
     * @param name The player's name.
     */
    public Player(String name) {
        this.name = name;
        points = 0;
        type = null;
        won = false;
    }

    /**
     * Returns the player's score.
     * @return The score of the player.
     */
    public int getPoints() {
        return points;
    }

    /**
     * Sets the player's score.
     * @param points The new score.
     */
    public void setPoints(int points) {
        this.points = points;
    }

    /**
     * Gets the ball type this player is tryinh to pocket.
     * @return The player's ball type.
     */
    public BallData.Type getType() {
        return type;
    }

    /**
     * Set's the player's ball type.
     * @param type The new type;
     */
    public void setType(BallData.Type type) {
        this.type = type;
    }

    /**
     * Returns the player's name.
     * @return The player's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the player's won flag.
     * @param won The new flag value.
     */
    public void setWon(boolean won) {
        this.won = won;
    }

    /**
     * Returns the value of the won flag.
     * @return True if player has won.
     */
    public boolean hasWon() {
        return won;
    }
}
