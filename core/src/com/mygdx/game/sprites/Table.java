package com.mygdx.game.sprites;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.PoolGameServer;

import java.util.ArrayList;

/**
 * Created by joaopsilva on 30-05-2016.
 */
public class Table {
    private static final float WALL_WIDTH = 0.04f;

    private static final float HOLE_OFFSET_CORNER = 0.04f;
    private static final float HOLE_RADIUS_CORNER = 0.089f;
    private static final float HOLE_OFFSET_MIDDLE = 0.006f;
    private static final float HOLE_RADIUS_MIDDLE = 0.076f;

    private static final float CUSHION_HEIGHT = 0.085f;
    private static final float CUSHION_OFFSET_LOW_CORNER = 0.11f;
    private static final float CUSHION_OFFSET_HIGH_CORNER = 0.18f;
    private static final float CUSHION_OFFSET_LOW_MIDDLE = 0.06f;
    private static final float CUSHION_OFFSET_HIGH_MIDDLE = 0.11f;

    private static final float BALL_RADIUS = 0.030f;

    private World world;
    private float width;
    private float height;
    private Vector2 position;

    private ArrayList<Ball> balls;
    private ArrayList<Cushion> cushions;
    private Ball cueBall;

    /**
     * Creates a Table object, representing a pool Table.
     * @param x The x coordinate of the table
     * @param y The y coordinate of the table
     * @param w Width of the table
     * @param h Height of the table
     * @param wld Box2D world in which the simulation will take place
     */
    public Table(float x, float y, float w, float h, World wld) {
        position = new Vector2(x, y);
        width = w; height = h;
        world = wld;

        cueBall = new Ball(width / 2, height / 2, BALL_RADIUS, world);
        balls = new ArrayList<Ball>();

        // region Adding the table's cushions
        cushions = new ArrayList<Cushion>();
        Vector2 vertices[] = new Vector2[6];

        vertices[0] = new Vector2(position.x + WALL_WIDTH, position.y + WALL_WIDTH + CUSHION_OFFSET_LOW_CORNER);
        vertices[1] = new Vector2(position.x + WALL_WIDTH + CUSHION_HEIGHT / 3, position.y +  WALL_WIDTH + CUSHION_OFFSET_LOW_CORNER);
        vertices[2] = new Vector2(position.x + WALL_WIDTH + CUSHION_HEIGHT, position.y +  WALL_WIDTH + CUSHION_OFFSET_HIGH_CORNER);
        vertices[3] = new Vector2(position.x + WALL_WIDTH + CUSHION_HEIGHT, position.y +  height - WALL_WIDTH - CUSHION_OFFSET_HIGH_CORNER);
        vertices[4] = new Vector2(position.x + WALL_WIDTH + CUSHION_HEIGHT / 3, position.y +  height - WALL_WIDTH - CUSHION_OFFSET_LOW_CORNER);
        vertices[5] = new Vector2(position.x + WALL_WIDTH, position.y +  height - WALL_WIDTH - CUSHION_OFFSET_LOW_CORNER);
        cushions.add(new Cushion(vertices, world));

        vertices[0] = (new Vector2(position.x + width - WALL_WIDTH - CUSHION_HEIGHT, position.y +  WALL_WIDTH + CUSHION_OFFSET_HIGH_CORNER));
        vertices[1] = (new Vector2(position.x + width - WALL_WIDTH - CUSHION_HEIGHT / 3, position.y +  WALL_WIDTH + CUSHION_OFFSET_LOW_CORNER));
        vertices[2] = (new Vector2(position.x + width - WALL_WIDTH, position.y +  WALL_WIDTH + CUSHION_OFFSET_LOW_CORNER));
        vertices[3] = (new Vector2(position.x + width - WALL_WIDTH, position.y +  height - WALL_WIDTH - CUSHION_OFFSET_LOW_CORNER));
        vertices[4] = (new Vector2(position.x + width - WALL_WIDTH - CUSHION_HEIGHT / 3, position.y +  height - WALL_WIDTH - CUSHION_OFFSET_LOW_CORNER));
        vertices[5] = (new Vector2(position.x + width - WALL_WIDTH - CUSHION_HEIGHT, position.y +  height - WALL_WIDTH - CUSHION_OFFSET_HIGH_CORNER));
        cushions.add(new Cushion(vertices, world));

        vertices[0] = (new Vector2(position.x + WALL_WIDTH + CUSHION_OFFSET_LOW_CORNER, position.y +  WALL_WIDTH));
        vertices[1] = (new Vector2(position.x + width / 2 - CUSHION_OFFSET_LOW_MIDDLE, position.y +  WALL_WIDTH));
        vertices[2] = (new Vector2(position.x + width / 2 - CUSHION_OFFSET_LOW_MIDDLE, position.y +  WALL_WIDTH + CUSHION_HEIGHT / 3));
        vertices[3] = (new Vector2(position.x + width / 2 - CUSHION_OFFSET_HIGH_MIDDLE, position.y +  WALL_WIDTH + CUSHION_HEIGHT));
        vertices[4] = (new Vector2(position.x + WALL_WIDTH + CUSHION_OFFSET_HIGH_CORNER, position.y +  WALL_WIDTH + CUSHION_HEIGHT));
        vertices[5] = (new Vector2(position.x + WALL_WIDTH + CUSHION_OFFSET_LOW_CORNER, position.y +  WALL_WIDTH + CUSHION_HEIGHT / 3));
        cushions.add(new Cushion(vertices, world));

        vertices[0] = (new Vector2(position.x + width / 2 + CUSHION_OFFSET_LOW_MIDDLE, position.y +  WALL_WIDTH));
        vertices[1] = (new Vector2(position.x + width - CUSHION_OFFSET_LOW_CORNER - WALL_WIDTH, position.y +  WALL_WIDTH));
        vertices[2] = (new Vector2(position.x + width - CUSHION_OFFSET_LOW_CORNER - WALL_WIDTH, position.y +  WALL_WIDTH + CUSHION_HEIGHT / 3));
        vertices[3] = (new Vector2(position.x + width - CUSHION_OFFSET_HIGH_CORNER - WALL_WIDTH, position.y +  WALL_WIDTH + CUSHION_HEIGHT));
        vertices[4] = (new Vector2(position.x + width / 2 + CUSHION_OFFSET_HIGH_MIDDLE, position.y +  WALL_WIDTH + CUSHION_HEIGHT));
        vertices[5] = (new Vector2(position.x + width / 2 + CUSHION_OFFSET_LOW_MIDDLE, position.y +  WALL_WIDTH + CUSHION_HEIGHT / 3));
        cushions.add(new Cushion(vertices, world));

        vertices[0] = (new Vector2(position.x + WALL_WIDTH + CUSHION_OFFSET_LOW_CORNER, position.y +  height - WALL_WIDTH));
        vertices[1] = (new Vector2(position.x + width / 2 - CUSHION_OFFSET_LOW_MIDDLE, position.y +  height - WALL_WIDTH));
        vertices[2] = (new Vector2(position.x + width / 2 - CUSHION_OFFSET_LOW_MIDDLE, position.y +  height - WALL_WIDTH - CUSHION_HEIGHT / 3));
        vertices[3] = (new Vector2(position.x + width / 2 - CUSHION_OFFSET_HIGH_MIDDLE, position.y +  height - WALL_WIDTH - CUSHION_HEIGHT));
        vertices[4] = (new Vector2(position.x + WALL_WIDTH + CUSHION_OFFSET_HIGH_CORNER, position.y +  height - WALL_WIDTH - CUSHION_HEIGHT));
        vertices[5] = (new Vector2(position.x + WALL_WIDTH + CUSHION_OFFSET_LOW_CORNER, position.y +  height - WALL_WIDTH - CUSHION_HEIGHT / 3));
        cushions.add(new Cushion(vertices, world));

        vertices[0] = (new Vector2(position.x + width / 2 + CUSHION_OFFSET_LOW_MIDDLE, position.y +  height - WALL_WIDTH));
        vertices[1] = (new Vector2(position.x + width - CUSHION_OFFSET_LOW_CORNER - WALL_WIDTH, position.y +  height - WALL_WIDTH));
        vertices[2] = (new Vector2(position.x + width - CUSHION_OFFSET_LOW_CORNER - WALL_WIDTH, position.y +  height - WALL_WIDTH - CUSHION_HEIGHT / 3));
        vertices[3] = (new Vector2(position.x + width - CUSHION_OFFSET_HIGH_CORNER - WALL_WIDTH, position.y +  height - WALL_WIDTH - CUSHION_HEIGHT));
        vertices[4] = (new Vector2(position.x + width / 2 + CUSHION_OFFSET_HIGH_MIDDLE, position.y +  height - WALL_WIDTH - CUSHION_HEIGHT));
        vertices[5] = (new Vector2(position.x + width / 2 + CUSHION_OFFSET_LOW_MIDDLE, position.y +  height - WALL_WIDTH - CUSHION_HEIGHT / 3));
        cushions.add(new Cushion(vertices, world));
        // endregion
    }

    /**
     * Handles the user input.
     */
    private void handleInput() {

    }

    /**
     * Updates the table.
     * @param delta Time interval.
     */
    public void update(float delta) {
        handleInput();
    }

    /**
     * Shoots the cue ball in the given direction and with the given
     * force.
     * @param force The force to apply to the ball in Newtons
     * @param direction The angle to shoot the ball at (radians)
     * @param spin The angle that the cue makes with the ball (radians)
     */
    public boolean shoot(float force, float direction, float spin) {
        Vector2 impulse = new Vector2( (float) Math.cos(direction), (float) Math.sin(direction));
        impulse.setLength(force);

        Vector2 hitPos = new Vector2();
        hitPos.x = cueBall.getPosition().x;
        // TODO: terminar o que eu estava a fazer aqui...
        return false;
    }

}
