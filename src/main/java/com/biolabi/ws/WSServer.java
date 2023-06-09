package com.biolabi.ws;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class WSServer {


    private int port;
    private ServerSocket serverSocket;

    private List<WSConnection> connections = new LinkedList<WSConnection>();

    public WSServer(int port) {
        this.port = port;
    }

    public void start() throws WSException {
        try {
            serverSocket = new ServerSocket(this.port);
            Socket socket = null;
            while ((socket = serverSocket.accept()) != null) {
                WSConnection wsConnection = new WSConnection(socket);
                wsConnection.addConnectionListener(new ConnectionListener() {
                    @Override
                    public void onConnect(WSConnection wsConnection) {
                        System.out.println("Connected");
                    }

                    @Override
                    public void onClose(WSConnection wsConnection) {
                        System.out.println("ConnectionClosed");
                        removeConnection((wsConnection));
                    }

                    @Override
                    public void onTextMessage(String payload, WSConnection wsConnection) {
                        System.out.println(payload);
                        try {
                            wsConnection.sendText("Hi We Reseived your message");
                        } catch (WSException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onBinaryMessage(int[] data, WSConnection wsConnection) {
                        System.out.println(data);
                    }
                });
                connections.add(wsConnection);
                new Thread(wsConnection).start();
            }
        } catch (IOException e) {
            throw new WSException(e);
        }
    }

    public synchronized void removeConnection(WSConnection connection) {
        connections.remove(connection);
    }


}
