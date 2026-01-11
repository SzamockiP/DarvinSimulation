package agh.isc.oop.model;

public class Vector2d {
    private final int x;
    private final int y;

    public Vector2d(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public String toString(){
        return "("+x+","+y+")";
    }

    public boolean precedes(Vector2d other){
        return x <= other.getX() && y <= other.getY();
    }

    public boolean follows(Vector2d other){
        return x >= other.getX() && y >= other.getY();
    }

    public Vector2d add(Vector2d other){
        Vector2d result = new Vector2d(x + other.x, y + other.y);
        return result;
    }

}
