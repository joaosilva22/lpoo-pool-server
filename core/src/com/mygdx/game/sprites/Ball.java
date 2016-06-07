package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by joaopsilva on 30-05-2016.
 */
public class Ball {
    private Body body;
    private Fixture fixture;
    private BallData userData;
    private Texture texture;
    private Sprite sprite;
    private float direction;

    /**
     * Creates a Ball object, representing a pool ball.
     * @param x The x coordinate of the ball.
     * @param y The y coordinate of the ball.
     * @param radius The radius of the ball.
     * @param world The world to which the ball bellongs.
     * @param number The ball's number.
     */
    public Ball(float x, float y, float radius, World world, int number) {
        // Cria o body do Box2D
        // Utilizado na simulacao da fisica
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.linearDamping = 0.7f;
        bodyDef.angularDamping = 0.7f;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 5f;
        fixtureDef.restitution = 0.8f;
        fixture = body.createFixture(fixtureDef);

        // Cria a user data do corpo
        // Utilizada na detecao de colisoes com sensors
        userData = new BallData(number);
        fixture.setUserData(userData);
        shape.dispose();

        // Cria a sprite associada com a bola
        // Escolhe a imagem correta a partir do numero da bola
        // TODO: mudar a spritesheet
        // TODO: incluir animacoes (?)
        texture = new Texture(Gdx.files.internal("balls.png"));
        int spritex = (number % 4) * 64;
        int spritey = (number / 4) * 64;
        sprite = new Sprite(texture, spritex, spritey, 64, 64);
        sprite.setSize(radius * 2, radius * 2);

        // Direcao do impulso aplicado a bola
        // Controlado pelo utilizador
        direction = (float) Math.PI;
    }

    /**
     * Updates the ball.
     * @param delta The time interval between update calls.
     */
    public void update(float delta) {
        sprite.setCenter(body.getPosition().x, body.getPosition().y);
    }

    /**
     * Renders the ball.
     * @param batch The SpriteBatch to render the ball.
     */
    public void render(SpriteBatch batch) {
        if (userData.getDraw())
            sprite.draw(batch);
    }

    /**
     * Returns the ball's Box2D body.
     * @return The ball's Box2D body.
     */
    public Body getBody() {
        return body;
    }

    /**
     * Returns the ball's position.
     * @return Position of the ball.
     */
    public Vector2 getPosition() {
        return body.getPosition();
    }

    /**
     * Returns the ball's direction.
     * @return The direction of the ball.
     */
    public float getDirection() {
        return direction;
    }

    /**
     * Sets the ball's direction.
     * @param direction The new direction.
     */
    public void setDirection(float direction) {
        this.direction = direction;
    }

    /**
     * Returns the ball's Box2D fixture.
     * @return The ball's fixture.
     */
    public Fixture getFixture() {
        return fixture;
    }
}
