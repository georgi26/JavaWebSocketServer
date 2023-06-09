package com.biolabi.ws;

public class WSDecoder {
    public static final int SBYTE_TO_UBYTE_FACTOR = 255;
    public static final int XOR_KEYS_LENGTH = 4;

    private int[] frame;

    public WSDecoder(byte[] byteFrame) {
        int[] intFrame = new int[byteFrame.length];
        for (int i = 0; i < intFrame.length; i++) {
            intFrame[i] = byteFrame[i] & SBYTE_TO_UBYTE_FACTOR;
        }
        this.frame = intFrame;
    }

    public WSDecoder(int[] frame) {
        this.frame = frame;
    }

    public int[] getFrame() {
        return frame;
    }

    public Opcode getOpcode() throws WSException {
        if (this.frame.length < 1) {
            throw new WSException("Invalid frame with 0 bytes");
        }
        int first = this.frame[0];
        String binary = Integer.toBinaryString(first);
        String opCodeBinary = binary.substring(4);
        int opCode = Integer.parseInt(opCodeBinary, 2);
        return Opcode.byOpcodeValue(opCode);
    }

    public int getLength() throws WSException {
        if (this.frame.length < 2) {
            throw new WSException("There is no payload for this frame type");
        }
        int second = this.frame[1];
        String binary = Integer.toBinaryString(second);
        int result = second;
        if(binary.length() > 7){
            String binaryLength = binary.substring(1);
            result = Integer.parseInt(binaryLength, 2);
        }
        return result;
    }

    public int getMask() throws WSException {
        if (this.frame.length < 2) {
            throw new WSException("There is no payload for this frame type");
        }
        int second = this.frame[1];
        String binary = Integer.toBinaryString(second);
        int result = 0;
        if(binary.length() == 8) {
            String binaryLength = binary.substring(0, 1);
            result = Integer.parseInt(binaryLength, 2);
        }
        return result;
    }

    public int[] getBinaryData() throws WSException {
        if(getMask() == 1){
            return getBinaryDataMasked();
        }else{
            return getBinaryDataUnMasked();
        }
    }

    public int [] getBinaryDataMasked()  throws WSException{
        int keysEnd = XOR_KEYS_LENGTH + 2;
        if (this.frame.length < keysEnd) {
            throw new WSException("Invalid frame with data expect key before data");
        }
        int[] keys = new int[XOR_KEYS_LENGTH];
        int j = 0;

        for (int i = 2; i < keysEnd; i++) {
            keys[j] = this.frame[i];
            j++;
        }
        int dataLength = getLength();
        int dataEnd = keysEnd + dataLength;
        int[] data = new int[dataLength];
        int dataIndex = 0;
        for (int i = keysEnd; i < dataEnd; i++) {
            data[dataIndex] = this.frame[i] ^ keys[dataIndex & 0x3];
            dataIndex++;
        }
        return data;
    }

    public int [] getBinaryDataUnMasked()  throws WSException{
        int keysEnd = 2;
        if (this.frame.length < keysEnd) {
            throw new WSException("Invalid frame with data expect key before data");
        }
        int dataLength = getLength();
        int dataEnd = keysEnd + dataLength;
        int[] data = new int[dataLength];
        for (int i = keysEnd; i < dataEnd; i++) {
            data[i-keysEnd] = this.frame[i];
        }
        return data;
    }

    public String getText() throws WSException {
        int[] data = getBinaryData();
        return new String(data, 0, data.length);
    }
}
