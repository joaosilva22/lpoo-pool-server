package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.PoolGameServer;
import com.mygdx.game.manager.StateManager;
import com.mygdx.game.manager.states.GameStates;
import com.mygdx.game.network.Message;
import com.mygdx.game.network.Server;
import com.mygdx.game.sprites.Player;
import com.mygdx.game.sprites.Table;

import java.util.ArrayList;

/**
 * Created by joaopsilva on 30-05-2016.
 */
public class GameScreen implements Screen {
    final PoolGameServer game;

    private OrthographicCamera camera;
    private World world;
    private Box2DDebugRenderer debugRenderer;

    private ArrayList<Player> players;

    private StateManager<GameScreen, GameStates> stateManager;

    private Table table;
    private Server server;

    // Debugging --------------------------------
    // TODO: apagar quando nao for necessario
    private ShapeRenderer shapeRenderer;

    public GameScreen(final PoolGameServer gam) {
        game = gam;
        stateManager = new StateManager<GameScreen, GameStates>(this, GameStates.WAITING_FOR_PLAYERS);

        camera = new OrthographicCamera(PoolGameServer.VIEWPORT_WIDTH, PoolGameServer.VIEWPORT_HEIGHT);
        camera.position.set(PoolGameServer.VIEWPORT_WIDTH / 2, PoolGameServer.VIEWPORT_HEIGHT / 2, 0);
        camera.update();

        Vector2 gravity = new Vector2(0, 0);
        world = new World(gravity, true);
        world.setVelocityThreshold(0);
        debugRenderer = new Box2DDebugRenderer();

        table = new Table(0, 0, PoolGameServer.VIEWPORT_WIDTH, PoolGameServer.VIEWPORT_HEIGHT, world);
        server = new Server(4444, this);



        // Debugging --------------------------------
        // TODO: apagar quando nao for necessario
        shapeRenderer = new ShapeRenderer();

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        camera.update();
        stateManager.update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.end();

        // Debugging --------------------------------
        // TODO: apagar quando nao for necessario
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        Vector2 target = new Vector2();
        target.x = table.getCueBall().getPosition().x + 10 * (float) Math.cos(table.getCueBall().getDirection());
        target.y = table.getCueBall().getPosition().y + 10 * (float) Math.sin(table.getCueBall().getDirection());
        shapeRenderer.line(table.getCueBall().getPosition().x, table.getCueBall().getPosition().y, target.x, target.y);

        shapeRenderer.end();

        if (Gdx.input.isTouched() && stateManager.getState().equals(GameStates.WAITING_FOR_PLAYERS)) {
            stateManager.setState(GameStates.PLAYER_1_TURN);
        }
        // End debugging --------------------------------

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

    public void handleMessage(Message message) {
        stateManager.handleMessage(message);
    }

    public Table getTable() {
        return table;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }
}
