package com.mygdx.game.sprites;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by joaopsilva on 06-06-2016.
 */
public class Hole {
    private Body body;
    private Fixture fixture;

    /**
     * Creates a Hole object.
     * Represents a hole on the billiards table.
     * @param x The x coordinate of the hole.
     * @param y The y coordinate of the hole.
     * @param radius The radius of the hole.
     * @param world The World this hole bellongs in.
     */
    public Hole(float x, float y, float radius, World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 0;
        fixtureDef.isSensor = true;
        fixtureDef.shape = shape;
        fixture = body.createFixture(fixtureDef);

        fixture.setUserData(this);
        shape.dispose();
    }
}
