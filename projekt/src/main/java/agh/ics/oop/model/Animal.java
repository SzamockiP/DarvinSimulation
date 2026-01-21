package agh.ics.oop.model;

import java.util.Comparator;
import agh.ics.oop.model.Genotype;
import agh.ics.oop.model.util.SimulationConfig;

public class Animal extends Creature {
    int childrenAmount;
    int age;
    //private final SimulationConfig simulationConfig;

    public Animal(Vector2d position, Genotype genotype, SimulationConfig simulationConfig) {
        super(position, simulationConfig.startingEnergy(), genotype, simulationConfig);
        age = 0;
        childrenAmount = 0;
    }

    public Animal(Vector2d position, Genotype genotype, int energy, SimulationConfig simulationConfig) {
        super(position, energy, genotype, simulationConfig);
        age = 0;
        childrenAmount = 0;
    }

    public void makeOlder(){
        this.age++;
    }
    public int  getAge(){
        return age;
    }
    public int getChildrenAmount(){
        return childrenAmount;
    }

    public void addEnergy(int delta){
        this.setEnergy( this.getEnergy() + delta);
    }

    @Override
    public void move(LayerMap map){
        if(!this.isAlive()) return;

        super.move(map);

        this.addEnergy(-getSimulationConfig().dailyEnergyLoss());
        this.makeOlder();
    }

    @Override
    protected Creature createChild(Vector2d position, Genotype genotype, int energy) {
        // Zwierzę tworzy małe Zwierzę
        return new Animal(position, genotype, energy, getSimulationConfig());
    }

    /*@Override
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
    }*/
}
