package agh.isc.oop.model;

public enum MapDirection {
    NORTH (0, new Vector2d(0, 1)),
    NORTHEAST(1, new Vector2d(1, 1)),
    EAST(2, new Vector2d(1, 0)),
    SOUTHEAST (3, new Vector2d(1, -1)),
    SOUTH(4, new Vector2d(0, -1)),
    SOUTHWEST (5, new Vector2d(-1, -1)),
    WEST (6, new Vector2d(-1, 0)),
    NORTHWEST (7, new Vector2d(-1, 1));

    private final int value;
    private final Vector2d unitVector;


    MapDirection(int value, Vector2d unitVector) {
        this.value = value;
        this.unitVector = unitVector;
    }
    public Vector2d toUnitVector() {
        return unitVector;
    }

    public static MapDirection fromInt(int value) {
        return switch (value) {
            case 0 -> NORTH;
            case 1 -> NORTHEAST;
            case 2 -> EAST;
            case 3 -> SOUTHEAST;
            case 4 -> SOUTH;
            case 5 -> SOUTHWEST;
            case 6 -> WEST;
            case 7 -> NORTHWEST;

            default -> throw new IllegalArgumentException("Incorrect direction: " + value);
        };
    }

    public MapDirection next(){
        return fromInt((this.value + 1) % MapDirection.values().length);
    }

    public MapDirection previous(){
        return fromInt((this.value+MapDirection.values().length - 1) % MapDirection.values().length);
    }

    @Override
    public String toString(){
        return switch(this){
            case NORTH -> "Polnoc";
            case NORTHEAST -> "Polnocny-wschod";
            case SOUTH -> "Poludnie";
            case SOUTHEAST ->  "Poludniowy-wschod";
            case EAST -> "Wschod";
            case SOUTHWEST -> "Poludniowy-zachod";
            case WEST -> "Zachod";
            case NORTHWEST -> "Polnocny-zachod";

            default -> "Brak danych";
        };
    }
}
