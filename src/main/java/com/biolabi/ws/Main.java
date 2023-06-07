package com.biolabi.ws;

import java.io.IOException;
import java.net.ServerSocket;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        try {
            new WSServer(8080).start();
        } catch (WSException e) {
            throw new RuntimeException(e);
        }
    }
}