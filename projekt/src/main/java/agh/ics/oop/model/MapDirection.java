package agh.ics.oop.model;

public enum MapDirection {
    NORTH (0),
    NORTHEAST (1),
    EAST (2),
    SOUTHEAST (3),
    SOUTH (4),
    SOUTHWEST (5),
    WEST (6),
    NORTHWEST (7);

    private final int value;


    MapDirection(int value) {
        this.value = value;
    }


    public int getValue() {
        return value;
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

    public MapDirection rotate(int rotation) {
        int newIndex = (this.value + rotation) % MapDirection.values().length;
        return MapDirection.fromInt(newIndex);
    }
}
