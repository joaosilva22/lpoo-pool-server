/**
 * Created by joaopsilva on 07-06-2016.
 */
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.PoolGameServer;
import com.mygdx.game.manager.states.TableStates;
import com.mygdx.game.sprites.Ball;
import com.mygdx.game.sprites.BallData;
import com.mygdx.game.sprites.Player;
import com.mygdx.game.sprites.Table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GdxTestRunner.class)
public class GameLogicTest {
    private World world;

    @Before
    public void setUp() throws Exception {
        world = new World(new Vector2(0,0), true);
    }

    @Test
    public void testTableCreate() {
        Table table = new Table(0, 0, 1, 1, world);

        assertEquals(table.getStateManager().getState(), TableStates.BEFORE);
    }

    @Test
    public void testTableShoot() {
        Table table = new Table(0, 0, 1, 1, world);
        table.shoot(1, (float) Math.PI, (float) Math.PI, 0);

        assertEquals(TableStates.RESOLVING, table.getStateManager().getState());
        assertEquals(table.getActivePlayerIndex(), 0);
    }

    @Test
    public void testTableAfter() {
        Table table = new Table(0, 0, 1, 1, world);
        table.shoot(0, 0, 0, 0);

        table.update(0);
        assertEquals(true, table.isTableStatic());
        assertEquals(TableStates.AFTER, table.getStateManager().getState());
    }

    @Test
    public void testTablePocketSolid() {
        Table table = new Table(0, 0, 1, 1, world);
        Ball toPocket = table.getBalls().get(0);
        BallData ballData = (BallData) toPocket.getFixture().getUserData();

        assertEquals(BallData.Type.SOLID, ballData.getType());
        assertEquals(0, table.getPocketedSolids().size);

        table.pocketBall(ballData);

        assertEquals(1, table.getPocketedSolids().size);
    }

    @Test
    public void testTablePocketStriped() {
        Table table = new Table(0, 0, 1, 1, world);
        Ball toPocket = table.getBalls().get(8);
        BallData ballData = (BallData) toPocket.getFixture().getUserData();

        assertEquals(BallData.Type.STRIPE, ballData.getType());
        assertEquals(0, table.getPocketedStripes().size);

        table.pocketBall(ballData);

        assertEquals(1, table.getPocketedStripes().size);
    }

    @Test
    public void testRelocateStriped() {
        Table table = new Table(0, 0, 1, 1, world);
        table.addPlayer(new Player("p1"));
        table.addPlayer(new Player("p2"));

        Ball toRelocate = table.getBalls().get(8);
        BallData ballData = (BallData) toRelocate.getFixture().getUserData();
        table.pocketBall(ballData);

        assertEquals(BallData.Type.STRIPE, ballData.getType());
        assertEquals(false, table.isRemoveStripe());
        assertEquals(1, table.getPocketedStripes().size);

        table.scheduleToRelocate(ballData);
        table.relocate();

        assertTrue((new Vector2(0.84f, 1.0649999f)).equals(toRelocate.getPosition()));
    }

    @Test
    public void testRelocateSolid() {
        Table table = new Table(0, 0, 1, 1, world);
        table.addPlayer(new Player("p1"));
        table.addPlayer(new Player("p2"));

        Ball toRelocate = table.getBalls().get(0);
        BallData ballData = (BallData) toRelocate.getFixture().getUserData();
        table.pocketBall(ballData);

        assertEquals(BallData.Type.SOLID, ballData.getType());
        assertEquals(false, table.isRemoveSolid());
        assertEquals(1, table.getPocketedSolids().size);

        table.scheduleToRelocate(ballData);
        table.relocate();

        assertTrue((new Vector2(0.84f, 1.0649999f)).equals(toRelocate.getPosition()));
    }

    @Test
    public void testRelocateBlack() {
        Table table = new Table(0, 0, 1, 1, world);
        table.addPlayer(new Player("p1"));
        table.addPlayer(new Player("p2"));

        Ball toRelocate = table.getBalls().get(7);
        BallData ballData = (BallData) toRelocate.getFixture().getUserData();
        table.pocketBall(ballData);

        assertEquals(BallData.Type.BLACK, ballData.getType());
        assertEquals(true, table.getHasPocketedBlackBall());

        table.scheduleToRelocate(ballData);
        table.relocate();

        assertTrue((new Vector2(-1, -1)).equals(toRelocate.getPosition()));
    }

    @Test
    public void testRelocateCueball() {
        Table table = new Table(0, 0, 1, 1, world);
        table.addPlayer(new Player("p1"));
        table.addPlayer(new Player("p2"));

        Ball toRelocate = table.getCueBall();
        BallData ballData = (BallData) toRelocate.getFixture().getUserData();
        table.pocketBall(ballData);

        assertEquals(BallData.Type.CUEBALL, ballData.getType());
        assertEquals(true, table.getHasPocketedCueball());

        table.scheduleToRelocate(ballData);
        table.relocate();

        assertTrue((new Vector2(-1, -1)).equals(toRelocate.getPosition()));
    }

    @Test
    public void testUpdateScores() {
        Table table = new Table(0, 0, 1, 1, world);
        table.addPlayer(new Player("p1"));
        table.addPlayer(new Player("p2"));

        table.getPlayers().get(0).setType(BallData.Type.SOLID);
        table.getPlayers().get(1).setType(BallData.Type.STRIPE);

        Ball solidToPocket = table.getBalls().get(0);
        BallData solidData = (BallData) solidToPocket.getFixture().getUserData();
        Ball stripedToPocket = table.getBalls().get(8);
        BallData stripedData = (BallData) stripedToPocket.getFixture().getUserData();

        assertEquals(BallData.Type.SOLID, solidData.getType());
        assertEquals(BallData.Type.STRIPE, stripedData.getType());

        table.pocketBall(solidData);
        table.pocketBall(stripedData);

        assertEquals(1, table.getPocketedSolids().size);
        assertEquals(1, table.getPocketedStripes().size);

        table.updateScores();

        assertEquals(1, table.getPlayers().get(0).getPoints());
        assertEquals(1, table.getPlayers().get(1).getPoints());
    }

    @Test
    public void testPlaysAgain() {
        Table table = new Table(0, 0, 1, 1, world);
        table.addPlayer(new Player("p1"));
        table.addPlayer(new Player("p2"));

        table.getPlayers().get(0).setType(BallData.Type.SOLID);
        table.shoot(0, 0, 0, 0);

        assertEquals(0, table.getActivePlayerIndex());
        assertEquals(BallData.Type.SOLID, table.getActivePlayer().getType());

        Ball toPocket = table.getBalls().get(8);
        BallData ballData = (BallData) toPocket.getFixture().getUserData();

        assertEquals(BallData.Type.STRIPE, ballData.getType());

        table.setFirstPocket(false);

        assertTrue(!table.playerPlaysAgain());
    }

    @Test
    public void testPlayerWon() {
        Table table = new Table(0, 0, 0, 1, world);
        table.addPlayer(new Player("p1"));
        table.addPlayer(new Player("p2"));

        table.shoot(0, 0, 0, 0);

        assertEquals(0, table.getActivePlayerIndex());

        Ball black = table.getBalls().get(7);
        BallData ballData = (BallData) black.getFixture().getUserData();

        assertEquals(BallData.Type.BLACK, ballData.getType());

        table.pocketBall(ballData);
        table.getActivePlayer().setPoints(8);

        assertEquals(true, table.getHasPocketedBlackBall());
        assertEquals(8, table.getActivePlayer().getPoints());
        assertEquals(false, table.getActivePlayer().hasWon());

        table.updateScores();

        assertEquals(true, table.getActivePlayer().hasWon());
    }
}
