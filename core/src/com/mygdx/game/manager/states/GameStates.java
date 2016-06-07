package com.mygdx.game.manager.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.mygdx.game.network.Message;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.sprites.Player;

/**
 * Created by joaopsilva on 04-06-2016.
 */
public enum GameStates implements State<GameScreen> {
    WAITING_FOR_PLAYERS() {
        @Override
        public void enter(GameScreen entity) {

        }

        @Override
        public void update(GameScreen entity, float delta) {

        }

        @Override
        public void exit(GameScreen entity) {
            // Informa os clientes que o jogo comecou
            Message ready = new Message("ready");
            entity.getServer().writeToAll(ready.toJson());

            // Comeca o relogio
            entity.getStopWatch().start();
        }

        @Override
        public void handleMessage(GameScreen entity, Message message) {
            // Recebe a mensagem quando o servidor se liga a um cliente
            // Muda o estado para 'PLAYER_1_TURN'
            if (message.getTag().equals("connect")) {
                // Adiciona o jogador a lista de jogadores
                String name = (String) message.getValue("name");
                entity.addPlayer(new Player(name));
                // Se estiverem dois jogadores conectados:
                // Muda o estado para 'PLAYER_1_TURN'
                if (entity.getPlayers().size() == 2)
                    entity.getStateManager().setState(PLAYER_1_TURN);
            }
        }
    },

    PLAYER_1_TURN() {
        @Override
        public void enter(GameScreen entity) {
            // Muda a mesa para o estado inicial 'BEFORE' ou o jogo para o estado final 'DONE'
            if (!entity.getTable().getHasPocketedBlackBall())
                entity.getTable().getStateManager().setState(TableStates.BEFORE);
            else {
                entity.getTable().getStateManager().setState(TableStates.DONE);
                entity.getStateManager().setState(DONE);
            }


            // Informa o jogador 1 que e a sua vez de jogar
            Message beginTurn = new Message("begin-turn");
            entity.getServer().write(0, beginTurn.toJson());
        }

        @Override
        public void update(GameScreen entity, float delta) {
            // Muda o estado de acordo com o resultado da jogada
            // Se embolsou, muda de novo para este estado
            // Caso contrario muda para 'PLAYER_2_TURN'
            if (entity.getTable().getStateManager().getState().equals(TableStates.AFTER)) {
                // TODO: verificar e atualizar o score
                if (entity.getTable().playerPlaysAgain())
                    entity.getStateManager().setState(PLAYER_1_TURN);
                else
                    entity.getStateManager().setState(PLAYER_2_TURN);
            }
        }

        @Override
        public void exit(GameScreen entity) {

        }

        @Override
        public void handleMessage(GameScreen entity, Message message) {
            // Atualiza a direcao da tacada
            if (message.getTag().equals("aim")) {
                float direction = (Float) message.getValue("direction");
                entity.getTable().getCueBall().setDirection(direction);
            }

            // Faz a jogada do jogador 1
            if (message.getTag().equals("play")) {
                float impulse = (Float) message.getValue("impulse");
                float direction = (Float) message.getValue("direction");
                float spin = (Float) message.getValue("spin");
                entity.getTable().shoot(impulse, direction, spin, 0);
            }
        }
    },

    PLAYER_2_TURN() {
        @Override
        public void enter(GameScreen entity) {
            // Muda a mesa para o estado inicial 'BEFORE' ou o jogo para o estado final 'DONE'
            if (!entity.getTable().getHasPocketedBlackBall())
                entity.getTable().getStateManager().setState(TableStates.BEFORE);
            else {
                entity.getTable().getStateManager().setState(TableStates.DONE);
                entity.getStateManager().setState(DONE);
            }

            // Informa o jogador 2 que e a sua vez de jogar
            Message beginTurn = new Message("begin-turn");
            entity.getServer().write(1, beginTurn.toJson());
        }

        @Override
        public void update(GameScreen entity, float delta) {
            // Muda o estado de acordo com o resultado da jogada
            // Se embolsou, muda de novo para este estado
            // Caso contrario muda para 'PLAYER_1_TURN'
            if (entity.getTable().getStateManager().getState().equals(TableStates.AFTER)) {
                // TODO: verificar e atualizar o score
                if (entity.getTable().playerPlaysAgain())
                    entity.getStateManager().setState(GameStates.PLAYER_2_TURN);
                else
                    entity.getStateManager().setState(GameStates.PLAYER_1_TURN);
            }
        }

        @Override
        public void exit(GameScreen entity) {
        }

        @Override
        public void handleMessage(GameScreen entity, Message message) {
            // Atualiza a direcao da tacada
            if (message.getTag().equals("aim")) {
                float direction = (Float) message.getValue("direction");
                entity.getTable().getCueBall().setDirection(direction);
            }

            // Faz a jogada do jogador 2
            // Muda o estado para 'PLAYER_1_TURN'
            if (message.getTag().equals("play")) {
                float impulse = (Float) message.getValue("impulse");
                float direction = (Float) message.getValue("direction");
                float spin = (Float) message.getValue("spin");
                entity.getTable().shoot(impulse, direction, spin, 1);
            }
        }
    },

    DONE() {
        @Override
        public void enter(GameScreen entity) {
            System.out.println("Game over");
            // Avisa os jogadores que ganharam :D
            // (Ou que perderam :'<)
            for (int i = 0; i < entity.getPlayers().size(); i++)
                if (entity.getPlayers().get(i).hasWon()) {
                    Message won = new Message("won");
                    if (entity.getServer().isConnected(i))
                        entity.getServer().write(i, won.toJson());
                } else {
                    Message lost = new Message("lost");
                    if (entity.getServer().isConnected(i))
                        entity.getServer().write(i, lost.toJson());
                }

            // Para o relogio
            entity.getStopWatch().stop();

            // Escreve na leaderboard
            FileHandle file = Gdx.files.local("data/leaderboards.txt");
            String winner = "";
            for (Player player : entity.getPlayers())
                if (player.hasWon())
                    winner = player.getName();
            file.writeString(winner + " - " + entity.getStopWatch().toString() + "\n", true);
        }

        @Override
        public void update(GameScreen entity, float delta) {

        }

        @Override
        public void exit(GameScreen entity) {

        }

        @Override
        public void handleMessage(GameScreen entity, Message message) {

        }
    }
}
