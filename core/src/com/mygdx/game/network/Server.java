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
import java.net.SocketException;
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
                        gameScreen.handleMessage(message);
                    } catch (InterruptedException e) {
                        return;
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

    public void disconnect() {
        for (ClientConnection client : clients)
            client.disconnect();
        serverSocket.dispose();
    }

    public boolean isConnected(int index) {
        return clients.get(index).isConnected();
    }

    public ArrayList<Integer> getDisconnectedClients() {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        for (int i = 0; i < clients.size(); i++)
            if (!clients.get(i).isConnected())
                ret.add(i);
        return ret;
    }

    private class ClientConnection {
        private Socket client;
        private BufferedReader in;
        private Thread read;
        private boolean connected;

        public ClientConnection(Socket socket) {
            client = socket;
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            connected = true;

            // Thread para ler as mensagens enviadas pelo cliente
            read = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            String toRead = in.readLine();
                            if (toRead == null) {
                                connected = false;
                                return;
                            }
                            messages.put(toRead);
                        }
                    } catch (IOException e) {
                        connected = false;
                        return;
                    } catch (InterruptedException e) {
                        connected = false;
                        return;
                    }
                }
            });
            read.start();
        }

        public void write(String message) {
            try {
                String toSend = message + "\n";
                client.getOutputStream().write(toSend.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean isConnected() {
            return connected;
        }

        public void disconnect() {
            read.interrupt();
            client.dispose();
        }
    }
}
