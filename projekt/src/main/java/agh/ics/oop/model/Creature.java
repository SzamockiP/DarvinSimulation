package agh.ics.oop.model;

public abstract class Creature extends Entity implements IAlive{
    private MapDirection direction;
    private int energy;
    private boolean dead;
    Genotype genotype;

    public Creature(Vector2d position, int energy) {
        super(position);
        this.energy = energy;
    }

    public MapDirection getDirection() {
        return direction;
    }

    public void setDirection(MapDirection direction) {
        this.direction = direction;
    }

    public Genotype getGenotype() {
        return genotype;
    }

    @Override
    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    @Override
    public boolean isDead() {
        return dead;
    }
}
