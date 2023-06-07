package com.biolabi.ws;

public enum Opcode {

    CONT(0),
    TEXT(1),
    BINARY(2),
    CLOSE(8);
    private final int value;

    Opcode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Opcode byOpcodeValue(int opcodeValue) throws WSException {
        Opcode[] opcodes = values();
        for (Opcode opcode : opcodes) {
            if (opcode.value == opcodeValue) {
                return opcode;
            }
        }
        throw new WSException("Invalid Opcode Value");
    }
}
