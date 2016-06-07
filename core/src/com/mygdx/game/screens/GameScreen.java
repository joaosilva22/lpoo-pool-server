package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.mygdx.game.PoolGameServer;
import com.mygdx.game.manager.StateManager;
import com.mygdx.game.manager.states.GameStates;
import com.mygdx.game.manager.states.TableStates;
import com.mygdx.game.network.Message;
import com.mygdx.game.network.Server;
import com.mygdx.game.physics.CollisionListener;
import com.mygdx.game.sprites.Ball;
import com.mygdx.game.sprites.BallData;
import com.mygdx.game.sprites.Player;
import com.mygdx.game.sprites.StopWatch;
import com.mygdx.game.sprites.Table;

import java.util.ArrayList;

/**
 * Created by joaopsilva on 30-05-2016.
 */
public class GameScreen implements Screen {
    final PoolGameServer game;

    private OrthographicCamera camera;
    private OrthographicCamera uicam;

    private World world;
    private Box2DDebugRenderer debugRenderer;

    private StateManager<GameScreen, GameStates> stateManager;

    private Table table;
    private Server server;
    private Texture tableTexture;

    private BitmapFont font;
    private BitmapFont fontSmall;

    private StopWatch stopWatch;

    private Vector2 p1, p2, collision, normal, reflection, colscl;
    private RayCastCallback callback;
    private boolean debug;

    // Debugging --------------------------------
    // TODO: apagar quando nao for necessario
    private ShapeRenderer shapeRenderer;

    public GameScreen(final PoolGameServer gam) {
        game = gam;
        stateManager = new StateManager<GameScreen, GameStates>(this, GameStates.WAITING_FOR_PLAYERS);

        camera = new OrthographicCamera(PoolGameServer.VIEWPORT_WIDTH, PoolGameServer.VIEWPORT_HEIGHT);
        camera.position.set(PoolGameServer.VIEWPORT_WIDTH / 2, PoolGameServer.VIEWPORT_HEIGHT / 2, 0);
        camera.update();

        uicam = new OrthographicCamera(872, 578);
        uicam.position.set(872 / 2, 542 / 2, 0);
        uicam.update();

        Vector2 gravity = new Vector2(0, 0);
        world = new World(gravity, true);
        World.setVelocityThreshold(0);
        debugRenderer = new Box2DDebugRenderer();

        table = new Table(0, 0, PoolGameServer.VIEWPORT_WIDTH, 1.375f, world);
        server = new Server(4444, this);
        tableTexture = new Texture(Gdx.files.internal("pool-table.png"));

        int fontSize = (int) (Gdx.graphics.getHeight() * 0.05);
        font = game.generateFont(fontSize);
        int fontSizeSmall = (int) (Gdx.graphics.getHeight() * 0.03);
        fontSmall = game.generateFont(fontSizeSmall);

        stopWatch = new StopWatch();

        world.setContactListener(new CollisionListener(table));

        // Debugging --------------------------------
        // TODO: apagar quando nao for necessario
        shapeRenderer = new ShapeRenderer();

        // Desenhar o raio que preve o movimento da bola
        p1 = new Vector2(table.getCueBall().getPosition().x, table.getCueBall().getPosition().y);
        p2 = new Vector2();
        p2.x = table.getCueBall().getPosition().x + 10 * (float) Math.cos(table.getCueBall().getDirection());
        p2.y = table.getCueBall().getPosition().y + 10 * (float) Math.sin(table.getCueBall().getDirection());
        collision = new Vector2();
        normal = new Vector2();
        reflection = new Vector2();
        colscl = new Vector2();
        debug = false;

        callback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                collision = point;
                Vector2 s = (point.cpy().sub(p1.cpy()));
                s.setLength(s.len() - 0.03f);
                colscl.set(p1.cpy()).add(s);
                Vector2 n = normal.cpy().scl(-1);
                Vector2 d = colscl.cpy().sub(p1.cpy());
                //System.out.println(d.angle(n));
                reflection.set(d.cpy().sub(n.cpy().scl(2 * d.cpy().dot(n.cpy()))));
                reflection.setLength(0.1f).add(point);
                GameScreen.this.normal.set(normal).add(point);
                return fraction;
            }
        };

        // Opcoes super secretas
        // Para terminar o jogo mais rapido
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyUp(int keycode) {
                if (keycode == Input.Keys.SPACE) {
                    if ((stateManager.getState().equals(GameStates.PLAYER_1_TURN) || stateManager.getState().equals(GameStates.PLAYER_2_TURN))
                            && (table.getPocketedSolids().size != 7)) {
                        table.getPlayers().get(0).setType(BallData.Type.SOLID);
                        table.getPlayers().get(1).setType(BallData.Type.STRIPE);
                        for (Ball ball : table.getBalls()) {
                            BallData ballData = (BallData) ball.getFixture().getUserData();
                            if (!ballData.getType().equals(BallData.Type.BLACK)) {
                                table.scheduleToRelocate(ballData);
                                table.pocketBall(ballData);
                            }
                        }
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        camera.update();
        uicam.update();

        stateManager.update(delta);
        table.update(delta);
        stopWatch.update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(uicam.combined);
        game.batch.begin();
        // Desenha a mesa de jogo
        game.batch.draw(tableTexture, 0, -18);

        // Desenhar os scores dos jogadores
        // Para isto deve ser utilizada uma camara diferente (maior)
        if (!stateManager.getState().equals(GameStates.WAITING_FOR_PLAYERS)) {
            int offsetx = (int) (Gdx.graphics.getWidth() * 0.05);
            int offsety = Gdx.graphics.getHeight() - (int) (Gdx.graphics.getHeight() * 0.06);
            int width = (int) (Gdx.graphics.getWidth() * 0.3);
            int offsetySmall = Gdx.graphics.getHeight() - (int) (Gdx.graphics.getHeight() * 0.11);

            String player1 = table.getPlayers().get(0).getName();
            int player1Score = table.getPlayers().get(0).getPoints();

            String player2 = table.getPlayers().get(1).getName();
            int player2Score = table.getPlayers().get(1).getPoints();

            font.draw(game.batch, player1 + ": " + player1Score, offsetx, offsety, width, Align.left, true);
            font.draw(game.batch, player2 + ": " + player2Score, Gdx.graphics.getWidth() - offsetx - width, offsety, width, Align.right, true);

            if (table.getPlayers().get(0).getType() != null) {
                String player1Type = table.getPlayers().get(0).getType().toString();
                fontSmall.draw(game.batch, player1Type + "S", offsetx, offsetySmall, width, Align.left, true);
            }
            if (table.getPlayers().get(1).getType() != null) {
                String player1Type = table.getPlayers().get(1).getType().toString();
                fontSmall.draw(game.batch, player1Type + "S", Gdx.graphics.getWidth() - offsetx - width, offsetySmall, width, Align.right, true);
            }
        }

        // Desenhar o tempo decorrido desde o inicio da partida
        int offsety = Gdx.graphics.getHeight() - (int) (Gdx.graphics.getHeight() * 0.06);
        font.draw(game.batch, stopWatch.toString(), 0, offsety, Gdx.graphics.getWidth(), Align.center, true);

        // Desenhar a mensagem de espera
        // Antes de os jogadores se conectarem
        offsety = Gdx.graphics.getHeight() - (int) (Gdx.graphics.getHeight() * 0.12);
        if (stateManager.getState().equals(GameStates.WAITING_FOR_PLAYERS))
            font.draw(game.batch, "Waiting for players to connect . . .", 0, offsety, Gdx.graphics.getWidth(), Align.center, true);

        // Desenhar mensagem no final do jogo
        // Mostra o venceder e permite voltar ao inicio
        if (stateManager.getState().equals(GameStates.DONE)) {
            String winner = "";
            for (Player player : getPlayers())
                if (player.hasWon())
                    winner = player.getName();
            fontSmall.draw(game.batch, "Player " + winner + " has won. Click anywhere to quit.", 0, offsety, Gdx.graphics.getWidth(), Align.center, true);
        }

        game.batch.end();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        table.render(game.batch);
        game.batch.end();

        // Volta para o ecra inicial quando o jogo termina
        // E o utilizador clica para voltar
        if (Gdx.input.isTouched() && stateManager.getState().equals(GameStates.DONE)) {
            // Avisa os clients que o servidor vai fechar
            Message disconnect = new Message("disconnect");
            server.writeToAll(disconnect.toJson());

            // Disconecta o servidor e muda para ecra inicial
            server.disconnect();
            game.setScreen(new MainMenuScreen(game));
        }

        // Muda o estado do jogo para done
        // Se um ou mais clientes se disconectarem durante o jogo
        if (server.getDisconnectedClients().size() > 0 && !stateManager.getState().equals(GameStates.DONE) && !stateManager.getState().equals(GameStates.WAITING_FOR_PLAYERS)) {
            // O jogador que nao se desconectou e o vencedor
            int other = 0;
            if (other == server.getDisconnectedClients().get(0)) other = 1;
            table.getPlayers().get(other).setWon(true);
            stateManager.setState(GameStates.DONE);
        }

        // Debugging --------------------------------
        // TODO: apagar quando nao for necessario
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        if ((stateManager.getState().equals(GameStates.PLAYER_1_TURN) || stateManager.getState().equals(GameStates.PLAYER_2_TURN))
                && table.getStateManager().getState().equals(TableStates.BEFORE)) {
            world.rayCast(callback, p1, p2);
            p1.set(table.getCueBall().getPosition().x, table.getCueBall().getPosition().y);
            p2.x = table.getCueBall().getPosition().x + 10 * (float) Math.cos(table.getCueBall().getDirection());
            p2.y = table.getCueBall().getPosition().y + 10 * (float) Math.sin(table.getCueBall().getDirection());
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.line(p1, colscl);
            shapeRenderer.line(colscl, reflection);
            shapeRenderer.circle(colscl.x, colscl.y, 0.03f, 20);
            shapeRenderer.circle(table.getCueBall().getPosition().x, table.getCueBall().getPosition().y, 0.1f, 20);
        }

        shapeRenderer.end();
        // End debugging --------------------------------

        // Muda a posicao das bolas que foram embolsadas
        if (!world.isLocked())
            table.relocate();

        if (debug) debugRenderer.render(world, camera.combined);
        world.step(1/60f, 9, 3);
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

    public StateManager<GameScreen, GameStates> getStateManager() {
        return stateManager;
    }

    public Server getServer() {
        return server;
    }

    public Table getTable() {
        return table;
    }

    public void addPlayer(Player player) {
        table.addPlayer(player);
    }

    public ArrayList<Player> getPlayers() {
        return table.getPlayers();
    }

    public StopWatch getStopWatch() {
        return stopWatch;
    }
}
