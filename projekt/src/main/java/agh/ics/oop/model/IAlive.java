package agh.ics.oop.model;

public interface IAlive {
    int getEnergy();

    boolean isDead();

    void addEnergy(int delta);
}
