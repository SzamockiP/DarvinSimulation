package agh.ics.oop.managers;

import agh.ics.oop.model.*;
import agh.ics.oop.model.base.*;
import agh.ics.oop.model.map.*;
import agh.ics.oop.model.interfaces.*;
import agh.ics.oop.model.util.SimulationConfig;

public class MoveAnimalsManager implements ISimulationManager {
    @Override
    public void step(WorldMap map, SimulationConfig config) {
        for (Animal animal : map.getAnimals().getEntities()){
            map.getAnimals().move(animal);
        }
    }
}
