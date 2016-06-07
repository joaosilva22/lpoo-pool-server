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

    public Type getType() {
        return type;
    }

    public int getNumber() {
        return number;
    }

    public boolean getDraw() {
        return draw;
    }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }

    public Queue<BallData> getCollisions() {
        return collisions;
    }

    public void addCollision(BallData ballData) {
        collisions.addLast(ballData);
    }

    public void clearCollision() {
        collisions.clear();
    }
}
