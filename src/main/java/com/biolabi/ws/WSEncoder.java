package com.biolabi.ws;

public class WSEncoder {

    public static final String DEFAULT_MASK = "0";

    public byte[] encodeText(String text) {
        byte[] header = new byte[2];
        header[0] = Integer.valueOf(createFirstTextOpcodeByte()).byteValue();
        header[1] = Integer.valueOf(createMaskAndLengthByte(text)).byteValue();
//        int [] key = generateKey();
//        for (int i = 2; i < header.length ; i++){
//            header[i] = Integer.valueOf(key[i-2]).byteValue();
//        }
        byte[] payload = text.getBytes();//encodePayload(text,key);
        byte[] result = new byte[header.length + payload.length];
        for (int i = 0; i < header.length; i++) {
            result[i] = header[i];
        }
        for (int i = header.length; i < result.length; i++) {
            result[i] = payload[i - header.length];
        }
        return result;
    }

    public int createFirstTextOpcodeByte() {
        String start = "1000";
        StringBuilder sb = new StringBuilder(Integer.toBinaryString(Opcode.TEXT.getValue()));
        while (sb.length() < 4) {
            sb.insert(0, '0');
        }
        sb.insert(0, start);
        return Integer.parseInt(sb.toString(), 2);
    }

    public int createMaskAndLengthByte(String text) {
        int size = text.length();
        StringBuilder sb = new StringBuilder(Integer.toBinaryString(size));
        while (sb.length() < 7) {
            sb.insert(0, '0');
        }
        sb.insert(0, DEFAULT_MASK);
        return Integer.parseInt(sb.toString(), 2);
    }

    public int[] generateKey() {
        int[] result = new int[4];
        for (int i = 0; i < result.length; i++) {
            result[i] = Double.valueOf(Math.random() * 1000).intValue() & 255;
        }
        return result;
    }

    public byte[] encodePayload(String text, int[] key) {
        byte[] bytes = text.getBytes();
        byte[] encoded = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            int value = bytes[i] ^ key[i & (key.length - 1)];
            encoded[i] = Integer.valueOf(value).byteValue();
        }
        return encoded;
    }
}
