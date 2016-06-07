package com.mygdx.game.sprites;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Queue;
//TODO: eliminar esta classe quando eu tiver a certeza que nao preciso mais
public class CueBall extends Ball {
    private float direction;
    private Queue<Ball> collisions;

    public CueBall(float x, float y, float radius, World world) {
        super(x, y, radius, world, 0);

        // Direcao do impulso aplicado a bola
        // Controlado pelo utilizador
        direction = (float) Math.PI;

        // Fila que guarda as colisoes ocorridas nume jogada
        // Permite ver se a bola branca acertou na bola correta
        collisions = new Queue<Ball>();
    }

    public float getDirection() {
        return direction;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

    public void addCollision(Ball ball) {
        collisions.addLast(ball);
    }

    public Queue<Ball> getCollisions() {
        return collisions;
    }

    public void clearCollisions() {
        collisions.clear();
    }
}
