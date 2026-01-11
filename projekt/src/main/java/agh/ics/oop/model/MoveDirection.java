package agh.ics.oop.model;

public enum MoveDirection {
    FRONT (0, new Vector2d(0, 1)),
    FRONT_RIGHT (1, new Vector2d(1, 1)),
    RIGHT (2, new Vector2d(1, 0)),
    BACK_RIGHT (3, new Vector2d(1, -1)),
    BACK (4, new Vector2d(0, -1)),
    BACK_LEFT (5, new Vector2d(-1, -1)),
    LEFT (6, new Vector2d(-1, 0)),
    FRONT_LEFT (6, new Vector2d(-1, 0));

    private final int value;
    private final Vector2d unitVector;

    MoveDirection(int value, Vector2d unitVector) {
        this.value = value;
        this.unitVector = unitVector;
    }
    public Vector2d toUnitVector() {
        return unitVector;
    }



}
