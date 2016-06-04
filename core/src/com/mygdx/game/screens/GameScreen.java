package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.PoolGameServer;
import com.mygdx.game.network.Server;
import com.mygdx.game.sprites.Table;

/**
 * Created by joaopsilva on 30-05-2016.
 */
public class GameScreen implements Screen {
    final PoolGameServer game;

    private OrthographicCamera camera;
    private World world;
    private Box2DDebugRenderer debugRenderer;

    private Table table;
    private Server server;

    public GameScreen(final PoolGameServer gam) {
        game = gam;

        camera = new OrthographicCamera(PoolGameServer.VIEWPORT_WIDTH, PoolGameServer.VIEWPORT_HEIGHT);
        camera.position.set(PoolGameServer.VIEWPORT_WIDTH / 2, PoolGameServer.VIEWPORT_HEIGHT / 2, 0);
        camera.update();

        Vector2 gravity = new Vector2(0, 0);
        world = new World(gravity, true);
        world.setVelocityThreshold(0);
        debugRenderer = new Box2DDebugRenderer();

        table = new Table(0, 0, PoolGameServer.VIEWPORT_WIDTH, PoolGameServer.VIEWPORT_HEIGHT, world);
        server = new Server(4444);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        camera.update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.end();

        debugRenderer.render(world, camera.combined);
        world.step(1/60f, 6, 2);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
