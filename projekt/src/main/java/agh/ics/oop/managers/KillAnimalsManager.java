package agh.ics.oop.managers;

import agh.ics.oop.model.*;
import agh.ics.oop.model.util.SimulationConfig;

public class KillAnimalsManager implements ISimulationManager {
    @Override
    public void step(WorldMap map, SimulationConfig config) {
        for(Animal animal : map.getAnimals().getEntities()){
            if(!animal.isAlive()){
                map.getAnimals().removeEntity(animal);
                map.getDeadAnimals().add(animal);
            }
        }
    }
}
