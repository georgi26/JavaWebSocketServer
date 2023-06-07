package com.biolabi.ws;

public class WSException extends Exception{

    public WSException(String message){
        super(message);
    }

    public WSException(Exception e){
        super(e);
    }

}
