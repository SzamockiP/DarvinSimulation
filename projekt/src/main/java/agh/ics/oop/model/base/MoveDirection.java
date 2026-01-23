package agh.ics.oop.model.base;

public enum MoveDirection {
    FRONT (0),
    FRONT_RIGHT (1),
    RIGHT (2),
    BACK_RIGHT (3),
    BACK (4),
    BACK_LEFT (5),
    LEFT (6),
    FRONT_LEFT (7);

    private final int value;
    public int getValue() {
        return value;
    }

    MoveDirection(int value) {
        this.value = value;
    }
}
