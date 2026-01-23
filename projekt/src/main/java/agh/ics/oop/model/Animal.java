package agh.ics.oop.model;

import java.util.Comparator;
import agh.ics.oop.model.Genotype;
import agh.ics.oop.model.util.SimulationConfig;

public class Animal extends Creature {
    private final java.util.List<Animal> children = new java.util.ArrayList<>();
    int childrenAmount;
    int age;

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
    public int getLivingChildrenAmount() {
        return (int) children.stream().filter(Creature::isAlive).count();
    }
    public void addChild(Animal child) {
        this.childrenAmount++;
        this.children.add(child);
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

    public Animal(Animal other) {
        super(other);
        this.age = other.age;
        this.childrenAmount = other.childrenAmount;
    }

    @Override
    public Entity copy() {
        return new Animal(this);
    }

    @Override
    protected Creature createChild(Vector2d position, Genotype genotype, int energy) {
        // Zwierzę tworzy małe Zwierzę
        return new Animal(position, genotype, energy, getSimulationConfig());
    }
}
