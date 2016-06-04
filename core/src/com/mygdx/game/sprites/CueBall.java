package com.mygdx.game.sprites;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class CueBall extends Ball {
    /**
     * Creates a CueBall object, a specific type of Ball that
     * allows the application of user define forces. Represents
     * the cue ball in a game of pool.*
     * @param x The x coordinate of the Ball
     * @param y The y coordinate of the Ball
     * @param radius The radius of the Ball
     * @param world The Box2D world to which the Ball will be added
     */
    public CueBall(float x, float y, float radius, World world) {
        super(x, y, radius, world);
    }
}
