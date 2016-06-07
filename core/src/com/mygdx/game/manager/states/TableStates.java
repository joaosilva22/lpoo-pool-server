package com.mygdx.game.manager.states;

import com.mygdx.game.network.Message;
import com.mygdx.game.sprites.BallData;
import com.mygdx.game.sprites.Table;

/**
 * Created by joaopsilva on 05-06-2016.
 */
public enum TableStates implements State<Table> {
    BEFORE() {
        @Override
        public void enter(Table entity) {
            // Reset das variaveis de controlo do jogo
            entity.setHasPocketedSolid(false);
            entity.setHasPocketedStripe(false);
            entity.setHasPocketedCueball(false);
            ((BallData)entity.getCueBall().getFixture().getUserData()).clearCollision();
            System.out.println(entity.getActivePlayerIndex());
            if (entity.getActivePlayerIndex() != -1)
                if (entity.getActivePlayer().getType() == null)
                    entity.setFirstPocket(true);
                else
                    entity.setFirstPocket(false);
        }

        @Override
        public void update(Table entity, float delta) {

        }

        @Override
        public void exit(Table entity) {

        }

        @Override
        public void handleMessage(Table entity, Message message) {

        }
    },

    RESOLVING() {
        @Override
        public void enter(Table entity) {

        }

        @Override
        public void update(Table entity, float delta) {
            if (entity.isTableStatic()) {
                entity.getStateManager().setState(AFTER);
            }
        }

        @Override
        public void exit(Table entity) {

        }

        @Override
        public void handleMessage(Table entity, Message message) {

        }
    },

    AFTER() {
        @Override
        public void enter(Table entity) {

        }

        @Override
        public void update(Table entity, float delta) {

        }

        @Override
        public void exit(Table entity) {
            entity.removeSolid();
            entity.removeStripe();
            entity.updateScores();
            if (entity.getHasPocketedCueball()) {
                entity.setHasPocketedCueball(false);
                entity.scheduleToRelocate((BallData) entity.getCueBall().getFixture().getUserData());
            }
        }

        @Override
        public void handleMessage(Table entity, Message message) {

        }
    },

    DONE() {
        @Override
        public void enter(Table entity) {

        }

        @Override
        public void update(Table entity, float delta) {

        }

        @Override
        public void exit(Table entity) {

        }

        @Override
        public void handleMessage(Table entity, Message message) {

        }
    }
}

