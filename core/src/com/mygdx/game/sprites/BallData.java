package com.mygdx.game.sprites;

import com.badlogic.gdx.utils.Queue;

/**
 * Created by joaopsilva on 05-06-2016.
 */
public class BallData {
    public enum Type {
        CUEBALL, SOLID, STRIPE, BLACK
    }
    private Type type;
    private int number;
    private boolean draw;
    private Queue<BallData> collisions;

    /**
     * Creates a BallData object.
     * Holds the information of a Ball.
     * @param number The ball's number.
     */
    public BallData(int number) {
        this.number = number;
        draw = true;
        collisions = new Queue<BallData>();

        // Coloca o tipo devido na bola
        // De acordo com o numero providenciado
        this.type = Type.CUEBALL;
        if (number > 0 && number < 8) this.type = Type.SOLID;
        if (number == 8) this.type = Type.BLACK;
        if (number > 8 && number < 16) this.type = Type.STRIPE;
    }

    /**
     * Returns the ball's type.
     * @return The ball's type.
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the ball's number.
     * @return The number of the ball.
     */
    public int getNumber() {
        return number;
    }

    /**
     * Returns the draw flag.
     * @return The draw flag.
     */
    public boolean getDraw() {
        return draw;
    }

    /**
     * Sets the ball's draw flag.
     * @param draw The new draw flag value.
     */
    public void setDraw(boolean draw) {
        this.draw = draw;
    }

    /**
     * Returns the collision queue of the ball.
     * @return The ball's collision queue.
     */
    public Queue<BallData> getCollisions() {
        return collisions;
    }

    /**
     * Adds a collision to the back of the queue.
     * @param ballData The ball it collided with.
     */
    public void addCollision(BallData ballData) {
        collisions.addLast(ballData);
    }

    /**
     * Clears the collisions queue.
     */
    public void clearCollision() {
        collisions.clear();
    }
}
