package agh.ics.oop.model;

public class Plant extends Entity {

    public Plant(Vector2d position) {
        super(position);
    }

    @Override
    public Entity copy() {
        return new Plant(this.getPosition());
    }
}
