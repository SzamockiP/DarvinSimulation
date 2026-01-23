package agh.ics.oop.managers;

import agh.ics.oop.model.*;
import agh.ics.oop.model.base.*;
import agh.ics.oop.model.map.*;
import agh.ics.oop.model.interfaces.*;
import agh.ics.oop.model.util.SimulationConfig;
import java.util.ArrayList;
import java.util.List;

public class KillAnimalsManager implements ISimulationManager {
    @Override
    public void step(WorldMap map, SimulationConfig config) {
        List<Animal> deadAnimals = new ArrayList<>();

        // Znajdź martwe
        for(Animal animal : map.getAnimals().getEntities()){
            if(animal.getEnergy() <= 0 || !animal.isAlive()){ // Upewnij się co do warunku
                animal.kill(); // Na wszelki wypadek
                deadAnimals.add(animal);
            }
        }

        // Usuń je bezpiecznie
        for(Animal animal : deadAnimals){
            map.getAnimals().removeEntity(animal);
            map.addDeadAnimalStats(animal.getAge());
            map.getDeadAnimals().add(animal);
        }
    }
}
