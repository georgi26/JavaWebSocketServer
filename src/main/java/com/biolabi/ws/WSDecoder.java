package com.biolabi.ws;

public class WSDecoder {

   public static enum Opcode {

       CONT(0),
       TEXT(1),
       BINARY(2),
       CLOSE(8);
       private final int value;
       Opcode(int value){
           this.value = value;
       }
       public int getValue() {
           return value;
       }

       public static Opcode byOpcodeValue(int opcodeValue) throws WSException{
           Opcode [] opcodes = values();
           for (Opcode opcode : opcodes){
               if(opcode.value == opcodeValue){
                   return  opcode;
               }
           }
           throw new WSException("Invalid Opcode Value");
       }
   }
   private static final int SBYTE_TO_UBYTE_FACTOR = 255;
   private static final int XOR_KEYS_LENGTH = 4;

    private int [] frame;
    public WSDecoder(byte [] byteFrame){
        int [] intFrame = new int[byteFrame.length];
        for(int i = 0; i < intFrame.length;i++){
            intFrame[i] = byteFrame[i] & SBYTE_TO_UBYTE_FACTOR;
        }
        this.frame = intFrame;
    }

    public WSDecoder(int [] frame){
        this.frame = frame;
    }
    public int[] getFrame() {
        return frame;
    }

    public Opcode getOpcode() throws WSException {
        if (this.frame.length < 1){
            throw new WSException("Invalid frame with 0 bytes");
        }
        int first = this.frame[0];
        String binary = Integer.toBinaryString(first);
        String opCodeBinary = binary.substring(4);
        int opCode = Integer.parseInt(opCodeBinary,2);
        return Opcode.byOpcodeValue(opCode);
    }

    public int getLength() throws WSException {
        if (this.frame.length < 2){
            throw new WSException("There is no payload for this frame type");
        }
        int second = this.frame[1];
        String binary = Integer.toBinaryString(second);
        String binaryLength = binary.substring(1);
        return Integer.parseInt(binaryLength,2);
    }

    public int getMask() throws WSException{
        if (this.frame.length < 2){
            throw new WSException("There is no payload for this frame type");
        }
        int second = this.frame[1];
        String binary = Integer.toBinaryString(second);
        String binaryLength = binary.substring(0,1);
        return Integer.parseInt(binaryLength,2);
    }

    public int[] getBinaryData() throws WSException{
        int keysEnd = XOR_KEYS_LENGTH + 2;
        if (this.frame.length < keysEnd){
            throw new WSException("Invalid frame with data expect key before data");
        }
        int [] keys = new int[XOR_KEYS_LENGTH];
        int j = 0;

        for(int i = 2;i < keysEnd ; i++ ){
            keys[j] = this.frame[i];
            j++;
        }
        int dataLength = getLength();
        int dataEnd = keysEnd+dataLength;
        int [] data = new int[dataLength];
        int dataIndex = 0;
        for(int i = keysEnd; i< dataEnd; i ++){
            data[dataIndex] = this.frame[i] ^ keys[dataIndex & 0x3];
            dataIndex++;
        }
        return data;
    }

    public String getText() throws WSException {
        int [] data = getBinaryData();
        return new String(data,0,data.length);
    }
}
