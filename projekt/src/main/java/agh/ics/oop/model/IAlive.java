package agh.ics.oop.model;

public interface IAlive {
    int getEnergy();
    void setEnergy(int energy);

    void addEnergy(int delta);

    boolean isAlive();
    void kill();
}
