package stages;

public enum Operation {
    ADD(0),
    SUB(1),
    SLL(2),
    SRL(3),
    AND(4),
    OR(5),
    XOR(6),
    SLT(7),
    BEQ(8),
    BNQ(9),
    NON(10);


    private final int value;

    Operation(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
