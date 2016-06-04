package com.mygdx.game.manager.states;

import com.mygdx.game.network.Message;
import com.mygdx.game.screens.GameScreen;

/**
 * Created by joaopsilva on 04-06-2016.
 */
public enum GameStates implements State<GameScreen> {
    WAITING_FOR_PLAYERS() {
        @Override
        public void enter(GameScreen entity) {
            System.out.println("Entered WAITING_FOR_PLAYERS...");
        }

        @Override
        public void update(GameScreen entity, float delta) {

        }

        @Override
        public void exit(GameScreen entity) {
            System.out.println("Left WAITING_FOR_PLAYERS...");
        }

        @Override
        public void handleMessage(GameScreen entity, Message message) {
            System.out.println("WAITING_FOR_PLAYERS received a message:");
            System.out.println(message.toJson());
        }
    },

    PLAYER_1_TURN() {
        @Override
        public void enter(GameScreen entity) {
            System.out.println("Entered PLAYER_1_TURN...");
        }

        @Override
        public void update(GameScreen entity, float delta) {

        }

        @Override
        public void exit(GameScreen entity) {
            System.out.println("Left PLAYER_1_TURN...");
        }

        @Override
        public void handleMessage(GameScreen entity, Message message) {
            System.out.println("PLAYER_1_TURN received a message:");
            System.out.println(message.toJson());
        }
    }
}
