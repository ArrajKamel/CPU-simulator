package stages;

public enum Operation {
    ADD(0),
    SUB(1),
    SLL(2),
    SRL(3),
    AND(4),
    OR(5),
    XOR(6),
    NON(7);


    private final int value;

    // Constructor for the enum
    Operation(int value) {
        this.value = value;
    }

    // Getter to access the associated integer value
    public int getValue() {
        return value;
    }
}
