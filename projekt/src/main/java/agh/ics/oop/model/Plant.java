package agh.ics.oop.model;

import agh.ics.oop.model.base.Entity;
import agh.ics.oop.model.base.Vector2d;

public class Plant extends Entity {

    public Plant(Vector2d position) {
        super(position);
    }

    @Override
    public Entity copy() {
        return new Plant(this.getPosition());
    }
}
