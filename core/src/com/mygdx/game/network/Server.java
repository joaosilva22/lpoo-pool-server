package com.mygdx.game.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.screens.GameScreen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by joaopsilva on 04-06-2016.
 */
public class Server {
    private ArrayList<ClientConnection> clients;
    private LinkedBlockingQueue<String> messages;
    private ServerSocket serverSocket;

    public Server(int port, final GameScreen gameScreen) {
        clients = new ArrayList<ClientConnection>();
        messages = new LinkedBlockingQueue<String>();

        ServerSocketHints hints = new ServerSocketHints();
        // TODO: ver se isto e necessario
        hints.acceptTimeout = 0;
        serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, port, hints);

        // Thread para aceitar as conexoes dos clients
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (clients.size() < 2) {
                        Socket client = serverSocket.accept(null);
                        clients.add(new ClientConnection(client));
                        System.out.println("A client has connected...");
                    }
                }
            }
        }).start();

        // Thread para fazer o handling das mensagens recebidas
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Json json = new Json();
                        Message message = json.fromJson(Message.class, messages.take());
                        // TODO: handling das mensagens
                        gameScreen.handleMessage(message);
                        /*
                        if (message.getTag().equals("play")) {
                            float impulse = (Float) message.getValue("impulse");
                            float direction = (Float) message.getValue("direction");
                            float spin = (Float) message.getValue("spin");
                            gameScreen.getTable().shoot(impulse, direction, spin);
                        }

                        if (message.getTag().equals("aim")) {
                            float direction = (Float) message.getValue("direction");
                            gameScreen.getTable().getCueBall().setDirection(direction);
                        }
                        System.out.println(message.toJson());*/
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void write(int index, String message) {
        clients.get(index).write(message);
    }

    public void writeToAll(String message) {
        for (ClientConnection client : clients)
            client.write(message);
    }

    private class ClientConnection {
        private Socket client;
        private BufferedReader in;

        public ClientConnection(Socket socket) {
            client = socket;
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            // Thread para ler as mensagens enviadas pelo cliente
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            messages.put(in.readLine());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        public void write(String message) {
            try {
                String toSend = message + "\n";
                client.getOutputStream().write(toSend.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
