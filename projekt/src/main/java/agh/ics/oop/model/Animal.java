package agh.ics.oop.model;

import java.util.Comparator;
import agh.ics.oop.model.Genotype;

public class Animal extends Creature {
    int childrenAmount;
    int age;

    public Animal(Vector2d position, int initialEnergy) {
        super(position, initialEnergy);
    }

    // Konstruktor dla dzieci (z dziedziczeniem)
    public Animal(Vector2d position, int initialEnergy, Genotype genotype) {
        super(position, initialEnergy, genotype);
    }

    public void makeOlder(){
        this.age++;
    }

    public void addEnergy(int delta){
        this.setEnergy( this.getEnergy() + delta);
    }

    @Override
    public void move(WorldMap map){
        if(this.isAlive()) return;

        super.move(map);

        this.addEnergy(-1);
        this.makeOlder();
    }

    @Override
    public Creature reproduce(Creature other) {
        if(!other.getClass().equals(this.getClass())){
            throw new ClassCastException("Can't reproduce different class creatures");
        }

        Genotype newGenotype = getGenotype().cross(other.getGenotype(), getEnergy(), other.getEnergy());

        int newEnergy = getEnergy()/2 +  other.getEnergy()/2;
        this.setEnergy(getEnergy()/2);
        other.setEnergy(other.getEnergy()/2);

        Vector2d newPosition = this.getPosition();

        return new Animal(newPosition, newEnergy, newGenotype);
    }
}
