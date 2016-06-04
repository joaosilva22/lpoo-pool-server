package com.mygdx.game.sprites;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by joaopsilva on 30-05-2016.
 */
public class Ball {
    private Body body;

    /**
     * Creates a Ball object, made up of a Box2D body as well
     * as a sprite. Represents a pool ball.
     *
     * @param x The x coordinate of the Ball
     * @param y The y coordinate of the Ball
     * @param radius The radius of the Ball
     * @param world The Box2D world to which the Ball will be added
     */
    public Ball(float x, float y, float radius, World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.linearDamping = 0.6f;
        bodyDef.angularDamping = 0.6f;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 4f;
        fixtureDef.restitution = 0.8f;
        body.createFixture(fixtureDef);
        shape.dispose();
    }

    /**
     * Returns the Box2D body associated with the Ball.
     * @return Box2D body
     */
    public Body getBody() {
        return body;
    }

    /**
     * Returns a Vector2 holding the ball's position.
     * @return position Vector2 holding the position
     */
    public Vector2 getPosition() {
        return body.getPosition();
    }
}
