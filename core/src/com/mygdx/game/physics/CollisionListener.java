package com.mygdx.game.physics;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.game.sprites.BallData;
import com.mygdx.game.sprites.Hole;
import com.mygdx.game.sprites.Table;

/**
 * Created by joaopsilva on 05-06-2016.
 */
public class CollisionListener implements ContactListener {
    private Table table;

    public CollisionListener(Table table) {
        this.table = table;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        // Colisao entre bola branca e bola normal
        if (fixtureA.getUserData() instanceof BallData && fixtureB.getUserData() instanceof BallData) {
            table.getBallsfx().play(1.0f);
            // Se uma das bolas for a bola branca
            // Adiciona bola a fila de colisoes da bola branca
            BallData userDataA = (BallData) fixtureA.getUserData();
            BallData userDataB = (BallData) fixtureB.getUserData();
            if (userDataA.getType().equals(BallData.Type.CUEBALL))
                userDataA.addCollision(userDataB);
            if (userDataB.getType().equals(BallData.Type.CUEBALL))
                userDataB.addCollision(userDataA);
        }

        // Colisao entre buraco e bola normal
        if ((fixtureA.getUserData() instanceof BallData && fixtureB.getUserData() instanceof Hole)
                || (fixtureA.getUserData() instanceof Hole && fixtureB.getUserData() instanceof BallData)) {
            // Se a fixture A for a bola
            // A fixture B e o buraco
            if (fixtureA.getUserData() instanceof BallData) {
                BallData ballData = (BallData) fixtureA.getUserData();
                // Se for a primeira bola que o jogador embolsa muda o tipo
                if (table.getActivePlayer().getType() == null) {
                    if (ballData.getType().equals(BallData.Type.SOLID) || ballData.getType().equals(BallData.Type.STRIPE))
                        table.getActivePlayer().setType(ballData.getType());

                    int other = 0;
                    if (table.getActivePlayerIndex() == other) other++;

                    if (ballData.getType().equals(BallData.Type.SOLID))
                        table.getPlayers().get(other).setType(BallData.Type.STRIPE);
                    if (ballData.getType().equals(BallData.Type.STRIPE))
                        table.getPlayers().get(other).setType(BallData.Type.SOLID);
                }
                System.out.println("Pocketed ball number " + ballData.getNumber() + " type " + ballData.getType());
                table.pocketBall(ballData);

                table.scheduleToRelocate(ballData);
            }
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
