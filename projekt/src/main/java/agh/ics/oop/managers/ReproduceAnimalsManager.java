package agh.ics.oop.managers;

import agh.ics.oop.model.*;
import agh.ics.oop.model.util.SimulationConfig;
import java.util.List;

public class ReproduceAnimalsManager implements ISimulationManager {
    @Override
    public void step(WorldMap map, SimulationConfig config) {
        LayerMap<Animal> animalMap = map.getAnimals();

        for(int y = 0; y < animalMap.getCurrentBoundary().upperRight().getY(); y++){
            for(int x = 0; x < animalMap.getCurrentBoundary().upperRight().getX(); x++){
                Vector2d pos = new Vector2d(x,y);
                List<Animal> animals = animalMap.getEntitiesAt(pos);
                if(animals.isEmpty()) continue;

                // zwierze z największą energią
                Animal bestAnimal = animals.getFirst();

                for(Animal animal : animals){
                    if(animal.getEnergy() > bestAnimal.getEnergy()){
                        bestAnimal = animal;
                    }
                }

                Animal secondBestAnimal = null;
                for(Animal animal : animals){
                    if(animal.getEnergy() <= bestAnimal.getEnergy() && animal != bestAnimal){
                        secondBestAnimal = animal;
                    }
                }
                if(secondBestAnimal == null) continue;

                Animal newAnimal = (Animal) bestAnimal.reproduce(secondBestAnimal);
                animalMap.addEntity(newAnimal);
            }
        }
    }
}
