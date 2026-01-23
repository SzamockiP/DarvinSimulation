package agh.ics.oop.model;

public abstract class Entity implements IEntity {
    private Vector2d position;
    private final java.util.UUID id = java.util.UUID.randomUUID();

    public Entity(Vector2d position) {
        this.position = position;
    }

    public java.util.UUID getId() {
        return id;
    }

    @Override
    public Vector2d getPosition() {
        return position;
    }

    @Override
    public void setPosition(Vector2d p) {
        position = p;
    }

    public abstract Entity copy();
}
