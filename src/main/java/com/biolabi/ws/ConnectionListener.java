package com.biolabi.ws;

public interface ConnectionListener {
    public void onConnect(WSConnection wsConnection);
    public void onClose(WSConnection wsConnection);

    public void onTextMessage(String payload,WSConnection wsConnection);

    public void onBinaryMessage(int[] data, WSConnection wsConnection);


}
