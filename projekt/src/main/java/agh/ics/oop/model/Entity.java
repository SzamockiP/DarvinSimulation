package agh.ics.oop.model;

public abstract class Entity implements IEntity {
    private Vector2d position;

    public Entity(Vector2d position) {
        this.position = position;
    }

    @Override
    public Vector2d getPosition() {
        return position;
    }

    @Override
    public void setPosition(Vector2d p) {
        position = p;
    }
}
