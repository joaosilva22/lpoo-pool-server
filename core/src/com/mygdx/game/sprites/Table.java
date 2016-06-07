package com.mygdx.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.game.PoolGameServer;
import com.mygdx.game.manager.StateManager;
import com.mygdx.game.manager.states.GameStates;
import com.mygdx.game.manager.states.TableStates;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by joaopsilva on 30-05-2016.
 */
public class Table {
    private static final float WALL_WIDTH = 0.04f;

    // region Declaracao das constantes relativas aos buracos
    private static final float HOLE_OFFSET_CORNER = 0.05f;
    private static final float HOLE_RADIUS_CORNER = 0.089f / 2;
    private static final float HOLE_OFFSET_MIDDLE = 0.006f;
    private static final float HOLE_RADIUS_MIDDLE = 0.086f / 2;
    // endregion

    // region Declaracao das constantes relativas as almofadas
    private static final float CUSHION_HEIGHT = 0.085f;
    private static final float CUSHION_OFFSET_LOW_CORNER = 0.11f;
    private static final float CUSHION_OFFSET_HIGH_CORNER = 0.18f;
    private static final float CUSHION_OFFSET_LOW_MIDDLE = 0.06f;
    private static final float CUSHION_OFFSET_HIGH_MIDDLE = 0.11f;
    // endregion

    private static final float BALL_RADIUS = 0.030f;
    private static final float INIT_LINE = 0.372f;
    private static final float MAX_IMPULSE = 0.1f;

    private World world;
    private float width;
    private float height;
    private Vector2 position;

    private StateManager<Table, TableStates> stateManager;

    private ArrayList<Ball> balls;
    private ArrayList<Cushion> cushions;
    private ArrayList<Hole> holes;
    private Ball cueBall;

    private ArrayList<Player> players;

    private Queue<Integer> pocketedStripes;
    private Queue<Integer> pocketedSolids;
    private boolean hasPocketedStripe;
    private boolean hasPocketedSolid;
    private boolean hasPocketedCueball;
    private boolean hasPocketedBlackBall;
    private int activePlayerIndex;

    private boolean removeSolid;
    private boolean removeStripe;

    private boolean firstPocket;

    private ArrayList<Ball> scheduledToRelocate;
    private ArrayList<Vector2> relocatePositions;

    public Table(float x, float y, float w, float h, World wld) {
        position = new Vector2(x, y);
        width = w; height = h;
        world = wld;

        cueBall = new Ball(width - WALL_WIDTH - 2 * INIT_LINE, height / 2, BALL_RADIUS, world, 0);
        balls = new ArrayList<Ball>();
        // region Disposicao inicial das bolas
        for (int i = 1; i < 16; i++) {
            float xx = 0, yy = 0;
            // Bolas de 1 a 5
            if (i < 6) {
                xx = INIT_LINE;
                yy = ((height / 2) - 6 * BALL_RADIUS) + i * 2 * BALL_RADIUS;
            }
            // Bolas 6 e 7
            if (i >= 6 && i < 8) {
                xx = INIT_LINE + 2 * BALL_RADIUS;
                yy = ((height / 2) + BALL_RADIUS) + (i - 6) * 2 * BALL_RADIUS;
            }
            // Bola 8
            if (i == 8) {
                xx = INIT_LINE + 4 * BALL_RADIUS;
                yy = height / 2;
            }
            // Bolas 9 e 10
            if (i > 8 && i < 11) {
                xx = INIT_LINE + 2 * BALL_RADIUS;
                yy = ((height / 2) - 3 * BALL_RADIUS) + (i - 9) * 2 * BALL_RADIUS;
            }
            // Bola 11
            if (i == 11) {
                xx = INIT_LINE + 4 * BALL_RADIUS;
                yy = height / 2 + 2 * BALL_RADIUS;
            }
            // Bola 12
            if (i == 12) {
                xx = INIT_LINE + 4 * BALL_RADIUS;
                yy = height / 2 - 2 * BALL_RADIUS;
            }
            // Bolas 13 e 14
            if (i > 12 && i < 15) {
                xx = INIT_LINE + 6 * BALL_RADIUS;
                yy = ((height / 2) - BALL_RADIUS) + (i - 13) * 2 * BALL_RADIUS;
            }
            // Bola 15
            if (i == 15) {
                xx = INIT_LINE + 8 * BALL_RADIUS;
                yy = height / 2;
            }
            balls.add(new Ball(xx, yy, BALL_RADIUS, world, i));
        }
        // endregion

        activePlayerIndex = -1;
        stateManager = new StateManager<Table, TableStates>(this, TableStates.BEFORE);

        players = new ArrayList<Player>();

        pocketedStripes = new Queue<Integer>();
        pocketedSolids = new Queue<Integer>();
        hasPocketedSolid = false;
        hasPocketedStripe = false;
        hasPocketedCueball = false;
        hasPocketedBlackBall = false;

        scheduledToRelocate = new ArrayList<Ball>();
        relocatePositions = new ArrayList<Vector2>();

        firstPocket = true;

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

        // region Criacao dos buracos da mesa
        holes = new ArrayList<Hole>();
        holes.add(new Hole(position.x + WALL_WIDTH + HOLE_OFFSET_CORNER, position.y + WALL_WIDTH + HOLE_OFFSET_CORNER, HOLE_RADIUS_CORNER, world));
        holes.add(new Hole(position.x + WALL_WIDTH + HOLE_OFFSET_CORNER, position.y + height - WALL_WIDTH - HOLE_OFFSET_CORNER, HOLE_RADIUS_CORNER, world));
        holes.add(new Hole(position.x + width - WALL_WIDTH - HOLE_OFFSET_CORNER, position.y + WALL_WIDTH + HOLE_OFFSET_CORNER, HOLE_RADIUS_CORNER, world));
        holes.add(new Hole(position.x + width - WALL_WIDTH - HOLE_OFFSET_CORNER, position.y + height - WALL_WIDTH - HOLE_OFFSET_CORNER, HOLE_RADIUS_CORNER, world));
        holes.add(new Hole(position.x + width / 2, WALL_WIDTH + HOLE_OFFSET_MIDDLE, position.y + HOLE_RADIUS_MIDDLE, world));
        holes.add(new Hole(position.x + width / 2, height - WALL_WIDTH - HOLE_OFFSET_MIDDLE, position.y + HOLE_RADIUS_MIDDLE, world));
        // endregion
    }

    public void update(float delta) {
        stateManager.update(delta);
        cueBall.update(delta);
        for (Ball ball : balls)
            ball.update(delta);
    }

    public void render(SpriteBatch batch) {
        cueBall.render(batch);
        for (Ball ball : balls)
            ball.render(batch);
    }

    public boolean shoot(float mult, float direction, float spin, int index) {
        activePlayerIndex = index;

        Vector2 impulse = new Vector2( (float) Math.cos(direction), (float) Math.sin(direction));
        impulse.setLength(mult * MAX_IMPULSE);

        Vector2 hitPos = new Vector2();
        hitPos.x = cueBall.getPosition().x + (float) Math.cos(spin) * BALL_RADIUS;
        hitPos.y = cueBall.getPosition().y + (float) Math.sin(spin) * BALL_RADIUS;

        if (stateManager.getState().equals(TableStates.BEFORE)) cueBall.getBody().applyLinearImpulse(impulse, hitPos, true);
        stateManager.setState(TableStates.RESOLVING);
        return false;
    }

    public boolean isTableStatic() {
        boolean ret = true;
        for (Ball ball : balls)
            if (ball.getBody().getLinearVelocity().len2() != 0 && ball.getBody().getAngularVelocity() != 0)
                ret = false;
        if (cueBall.getBody().getLinearVelocity().len2() != 0 && cueBall.getBody().getAngularVelocity() != 0)
            ret = false;
        return ret;
    }

    public void pocketBall(BallData userData) {
        // Embolsar uma bola do tipo 'SOLID'
        if (userData.getType().equals(BallData.Type.SOLID)) {
            hasPocketedSolid = true;
            //userData.setDraw(false);
            boolean pocket = true;
            for (int num : pocketedSolids)
                if (num == userData.getNumber())
                    pocket = false;
            if (pocket) pocketedSolids.addLast(userData.getNumber());
        }

        // Embolsar uma bola do tipo 'STRIPED'
        if (userData.getType().equals(BallData.Type.STRIPE)) {
            hasPocketedStripe = true;
            //userData.setDraw(false);
            boolean pocket = true;
            for (int num : pocketedStripes)
                if (num == userData.getNumber())
                    pocket = false;
            if (pocket) pocketedStripes.addLast(userData.getNumber());
        }

        // Embolsar uma bola do tipo 'CUEBALL'
        if (userData.getType().equals(BallData.Type.CUEBALL))
            hasPocketedCueball = true;

        // Embolsar uma bola do tipo 'BLACK'
        if (userData.getType().equals(BallData.Type.BLACK))
            hasPocketedBlackBall = true;
    }

    public boolean playerPlaysAgain() {
        // Descobrir o tipo de bolas a que o jogador esta a jogar
        BallData.Type type = getActivePlayer().getType();

        if (type == null) return false;

        // Se estiver a jogar as 'SOLIDS'
        // E embolsou uma 'SOLID'
        if (type.equals(BallData.Type.SOLID)) {
            BallData ballData = (BallData) cueBall.getFixture().getUserData();
            if (ballData.getCollisions().size == 0) return false;

            if (!ballData.getCollisions().first().getType().equals(BallData.Type.SOLID) && !firstPocket)
                return false;

            if (hasPocketedSolid) return true;
        }

        // Se estiver a jogar as 'STRIPES'
        // E embolsou uma 'STRIPED'
        if (type.equals(BallData.Type.STRIPE)) {
            BallData ballData = (BallData) cueBall.getFixture().getUserData();
            if (ballData.getCollisions().size == 0) return false;

            if (!ballData.getCollisions().first().getType().equals(BallData.Type.STRIPE) && !firstPocket)
                return false;

            if (hasPocketedStripe) return true;
        }

        return false;
    }

    public void updateScores() {
        // Atualizar score do jogador 1
        if (players.get(0).getType() != null) {
            if (players.get(0).getType().equals(BallData.Type.SOLID))
                players.get(0).setPoints(pocketedSolids.size);
            if (players.get(0).getType().equals(BallData.Type.STRIPE))
                players.get(0).setPoints(pocketedStripes.size);
        }

        // Atualizar score do jogador 2
        if (players.get(1).getType() != null) {
            if (players.get(1).getType().equals(BallData.Type.SOLID))
                players.get(1).setPoints(pocketedSolids.size);
            if (players.get(1).getType().equals(BallData.Type.STRIPE))
                players.get(1).setPoints(pocketedStripes.size);
        }

        // Se a bola preta for embolsada
        // Verifica quem ganhou e quem perdeu
        if (hasPocketedBlackBall) {
            if (getActivePlayer().getPoints() == 8) {
                getActivePlayer().setWon(true);
            } else {
                int other = 0;
                if (activePlayerIndex == other) other = 1;
                players.get(other).setWon(true);
            }
        }
    }

    public void scheduleToRelocate(BallData ballData) {
        int index = ballData.getNumber();
        Ball toRelocate;
        if (index > 0)
            toRelocate = balls.get(index - 1);
        else
            toRelocate = cueBall;
        Vector2 relocatePos = new Vector2();

        // Reposicionar bolas do tipo 'STRIPED'
        // Debaixo do nome do jogador que esta a jogar a este tipo
        if (ballData.getType().equals(BallData.Type.STRIPE) && !removeStripe)
            if (players.get(0).getType() == null) {
                relocatePos = new Vector2(width - 0.09f - (BALL_RADIUS * 2 + 0.01f) * pocketedStripes.size, height + BALL_RADIUS + 0.035f);
            } else {
                if (players.get(0).getType().equals(BallData.Type.STRIPE)) {
                    relocatePos = new Vector2(0.09f + (BALL_RADIUS * 2 + 0.01f) * pocketedStripes.size, height + BALL_RADIUS + 0.035f);
                } else {
                    relocatePos = new Vector2(width - 0.09f - (BALL_RADIUS * 2 + 0.01f) * pocketedStripes.size, height + BALL_RADIUS + 0.035f);
                }
            }

        // Reposicionar bolas do tipo 'STRIPED'
        // De volta a mesa, por falta do jogador
        if (ballData.getType().equals(BallData.Type.STRIPE) && removeStripe) {
            Random rand = new Random();
            relocatePos = new Vector2(rand.nextFloat() * (width - WALL_WIDTH * 2 - CUSHION_HEIGHT * 2) + WALL_WIDTH * 2 + CUSHION_HEIGHT * 2, rand.nextFloat() * (height - WALL_WIDTH * 2 - CUSHION_HEIGHT * 2) + WALL_WIDTH * 2 + CUSHION_HEIGHT * 2);
            removeStripe = false;
        }

        // Reposicionar bolas do tipo 'SOLID'
        // Debaixo do nome do jogador que esta a jogar a este tipo
        if (ballData.getType().equals(BallData.Type.SOLID))
            if (players.get(0).getType() == null) {
                relocatePos = new Vector2(width - 0.09f - (BALL_RADIUS * 2 + 0.01f) * pocketedSolids.size, height + BALL_RADIUS + 0.035f);
            } else {
                if (players.get(0).getType().equals(BallData.Type.SOLID)) {
                    relocatePos = new Vector2(0.09f + (BALL_RADIUS * 2 + 0.01f) * pocketedSolids.size, height + BALL_RADIUS + 0.035f);
                } else {
                    relocatePos = new Vector2(width - 0.09f - (BALL_RADIUS * 2 + 0.01f) * pocketedSolids.size, height + BALL_RADIUS + 0.035f);
                }
            }

        // Reposicionar bolas do tipo 'SOLID'
        // De volta a mesa, por falta do jogador
        if (ballData.getType().equals(BallData.Type.SOLID) && removeSolid) {
            Random rand = new Random();
            relocatePos = new Vector2(rand.nextFloat() * (width - WALL_WIDTH * 2 - CUSHION_HEIGHT * 2) + WALL_WIDTH * 2 + CUSHION_HEIGHT * 2, rand.nextFloat() * (height - WALL_WIDTH * 2 - CUSHION_HEIGHT * 2) + WALL_WIDTH * 2 + CUSHION_HEIGHT * 2);
            removeSolid = false;
        }

        // Reposicionar bola do tipo 'CUEBALL'
        // Na posicao inicial da bola
        if (ballData.getType().equals(BallData.Type.CUEBALL))
            if (hasPocketedCueball) {
                relocatePos = new Vector2(-1, -1);
            } else {
                relocatePos = new Vector2(width - WALL_WIDTH - 2 * INIT_LINE, height / 2);
            }

        // Reposicionar bola do tipo 'BLACK'
        // Fora de vista do jogador
        if (ballData.getType().equals(BallData.Type.BLACK))
            relocatePos = new Vector2(-1, -1);

        scheduledToRelocate.add(toRelocate);
        relocatePositions.add(relocatePos);
    }

    public void relocate() {
        for (int i = 0; i < scheduledToRelocate.size(); i++) {
            scheduledToRelocate.get(i).getBody().setAngularVelocity(0);
            scheduledToRelocate.get(i).getBody().setLinearVelocity(0, 0);
            scheduledToRelocate.get(i).getBody().setTransform(relocatePositions.get(i), 0);
        }
        scheduledToRelocate.clear();
        relocatePositions.clear();
    }

    public void removeSolid() {
        BallData.Type playerType = getActivePlayer().getType();
        BallData ballData = (BallData) cueBall.getFixture().getUserData();

        if (playerType == null) return;
        if (firstPocket) return;

        if (playerType.equals(BallData.Type.SOLID)) {
            if (ballData.getCollisions().size == 0) {
                if (pocketedSolids.size > 0) {
                    Ball toRemove = balls.get(pocketedSolids.last() - 1);
                    pocketedSolids.removeLast();
                    removeSolid = true;
                    scheduleToRelocate((BallData) toRemove.getFixture().getUserData());
                }
            } else if (!ballData.getCollisions().first().getType().equals(BallData.Type.SOLID)) {
                if (ballData.getCollisions().first().getType().equals(BallData.Type.BLACK)) {
                    if (pocketedSolids.size != 7) {
                        if (pocketedSolids.size > 0) {
                            Ball toRemove = balls.get(pocketedSolids.last() - 1);
                            pocketedSolids.removeLast();
                            removeSolid = true;
                            scheduleToRelocate((BallData) toRemove.getFixture().getUserData());
                        }
                    }
                } else {
                    if (pocketedSolids.size > 0) {
                        Ball toRemove = balls.get(pocketedSolids.last() - 1);
                        pocketedSolids.removeLast();
                        removeSolid = true;
                        scheduleToRelocate((BallData) toRemove.getFixture().getUserData());
                    }
                }
            }
        }
    }

    public void removeStripe() {
        BallData.Type playerType = getActivePlayer().getType();
        BallData ballData = (BallData) cueBall.getFixture().getUserData();

        if (playerType == null) return;
        if (firstPocket) return;

        if (playerType.equals(BallData.Type.STRIPE)) {
            if (ballData.getCollisions().size == 0) {
                if (pocketedStripes.size > 0) {
                    Ball toRemove = balls.get(pocketedStripes.last() - 1);
                    pocketedStripes.removeLast();
                    removeStripe = true;
                    scheduleToRelocate((BallData) toRemove.getFixture().getUserData());
                }
            } else if (!ballData.getCollisions().first().getType().equals(BallData.Type.STRIPE)) {
                if (ballData.getCollisions().first().getType().equals(BallData.Type.BLACK)) {
                    if (pocketedStripes.size != 7) {
                        if (pocketedStripes.size > 0) {
                            Ball toRemove = balls.get(pocketedStripes.last() - 1);
                            pocketedStripes.removeLast();
                            removeStripe = true;
                            scheduleToRelocate((BallData) toRemove.getFixture().getUserData());
                        }
                    }
                } else {
                    if (pocketedStripes.size > 0) {
                        Ball toRemove = balls.get(pocketedStripes.last() - 1);
                        pocketedStripes.removeLast();
                        removeStripe = true;
                        scheduleToRelocate((BallData) toRemove.getFixture().getUserData());
                    }
                }
            }
        }
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public StateManager<Table, TableStates> getStateManager() {
        return stateManager;
    }

    public Ball getCueBall() {
        return cueBall;
    }

    public void setHasPocketedSolid(boolean hasPocketedSolid) {
        this.hasPocketedSolid = hasPocketedSolid;
    }

    public void setHasPocketedStripe(boolean hasPocketedStripe) {
        this.hasPocketedStripe = hasPocketedStripe;
    }

    public Player getActivePlayer() {
        return players.get(activePlayerIndex);
    }

    public int getActivePlayerIndex() {
        return activePlayerIndex;
    }

    public boolean getHasPocketedBlackBall() {
        return hasPocketedBlackBall;
    }

    public void setHasPocketedBlackBall(boolean hasPocketedBlackBall) {
        this.hasPocketedBlackBall = hasPocketedBlackBall;
    }

    public boolean getHasPocketedCueball() {
        return hasPocketedCueball;
    }

    public void setHasPocketedCueball(boolean hasPocketedCueball) {
        this.hasPocketedCueball = hasPocketedCueball;
    }

    public boolean isRemoveSolid() {
        return removeSolid;
    }

    public void setRemoveSolid(boolean removeSolid) {
        this.removeSolid = removeSolid;
    }

    public boolean isRemoveStripe() {
        return removeStripe;
    }

    public void setRemoveStripe(boolean removeStripe) {
        this.removeStripe = removeStripe;
    }

    public void setFirstPocket(boolean firstPocket) {
        this.firstPocket = firstPocket;
    }

    public ArrayList<Ball> getBalls() {
        return balls;
    }

    public Queue<Integer> getPocketedSolids() {
        return pocketedSolids;
    }

    public Queue<Integer> getPocketedStripes() {
        return pocketedStripes;
    }
}
