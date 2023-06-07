package com.biolabi.ws;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

public class WSConnection implements Runnable{

    private static final String SEC_WEB_SOCKET_KEY="Sec-WebSocket-Key:";
    private static final String MAGIC_WS_STRING="258EAFA5-E914-47DA-95CA-C5AB0DC85B11";


    private Socket socket;
    private String socketKey;

    private boolean closed = false;

    public WSConnection(Socket socket){
        this.socket = socket;
    }


    @Override
    public void run() {
        try {
            this.socketKey = readSecWebSocketKey(socket.getInputStream());
            sendHandShake(socket.getOutputStream());
            readDecodeBytes(socket.getInputStream());
        } catch (WSException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readSecWebSocketKey(InputStream is) throws WSException {
        String data = null;
        try {
            data = readStream(is);
        } catch (IOException e) {
            throw new WSException(e);
        }
        String [] lines = data.split("\n");

        for(String line : lines){
            line = line.trim();
            if(line.startsWith(SEC_WEB_SOCKET_KEY)){
                String [] secKeyLines =  line.split(":");
                if(secKeyLines.length == 2){
                    return secKeyLines[1].trim();
                }
            }
        }
        throw new WSException("Sec-WebSocket-Key not found");
    }

    public void readDecodeBytes(InputStream is) throws IOException, WSException {
        byte [] buffer = new byte[1000];
        int read = -1;
        while(!closed && (read = is.read(buffer)) > 0){
            System.out.println("\n");
            WSDecoder decoder = new WSDecoder((buffer));
            if(decoder.getOpcode() == Opcode.CLOSE){
                this.close();
                break;
            }
            int [] frame = decoder.getFrame();
            System.out.println(decoder.getText());
            for(int i = 0 ; i < read; i++){
                int uByte = frame[i];
                System.out.print(" "+uByte+" ");
            }
        }
    }

    public void close() throws WSException{
        this.closed = true;
        try {
            this.socket.close();
        } catch (IOException e) {
            throw new WSException(e);
        }
    }

    private void decodeFirst(int first){

    }

    private String readStream(InputStream in) throws IOException {
        Scanner scanner = new Scanner(in,"UTF-8");
        String data = scanner.useDelimiter("\\r\\n\\r\\n").next();
        System.out.println(data);
        return data;
    }

    private void sendHandShake(OutputStream out) throws WSException{
        try{
            byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                    + "Connection: Upgrade\r\n"
                    + "Upgrade: websocket\r\n"
                    + "Sec-WebSocket-Accept: "
                    + Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((this.socketKey + MAGIC_WS_STRING).getBytes("UTF-8")))
                    + "\r\n\r\n").getBytes("UTF-8");
            out.write(response, 0, response.length);
        }catch(IOException | NoSuchAlgorithmException e){
            throw new WSException(e);
        }

    }
}
