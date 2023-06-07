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
                connections.add(wsConnection);
                new Thread(wsConnection).start();
            }
        } catch (IOException e) {
            throw new WSException(e);
        }
    }


}
