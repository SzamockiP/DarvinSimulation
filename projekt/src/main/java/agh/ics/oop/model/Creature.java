package agh.ics.oop.model;

public abstract class Creature implements IAlive {
    private MapDirection direction;
    private int energy;
    private boolean dead;
    Genotype genotype;

    public  Creature(MapDirection direction, int energy) {
        this.direction = direction;
    }

    @Override
    public int getEnergy() {
        return energy;
    }

    @Override
    public boolean isDead() {
        return dead;
    }
}
