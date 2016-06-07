package com.mygdx.game.sprites;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by joaopsilva on 30-05-2016.
 */
public class Cushion {
    private Body body;

    /**
     * Creates a Cushion object, made up of a Box2D body as well
     * as a sprite. Represents the cushion in a pool table.
     * @param vertices The vertices that make up the cushion shape. Must be provided in anti-clockwise order.
     * @param world The Box2D world to which the Cushion will be added
     */
    public Cushion(Vector2[] vertices, World world) {
        BodyDef bodyDef = new BodyDef();
        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.set(vertices);

        body.createFixture(shape, 0.0f);
        shape.dispose();
    }
}
