package agh.ics.oop.managers;

import agh.ics.oop.model.*;
import agh.ics.oop.model.base.*;
import agh.ics.oop.model.map.*;
import agh.ics.oop.model.interfaces.*;
import agh.ics.oop.model.util.SimulationConfig;
import java.util.List;

public class SetHostsManager implements ISimulationManager {
    @Override
    public void step(WorldMap map, SimulationConfig config) {
        LayerMap<Parasite> parasiteMap = map.getParasites();
        LayerMap<Animal> animalMap = map.getAnimals();

        for(int y = 0; y < parasiteMap.getCurrentBoundary().upperRight().getY(); y++){
            for(int x = 0; x < parasiteMap.getCurrentBoundary().upperRight().getX(); x++){
                Vector2d pos = new Vector2d(x,y);
                List<Animal> animals = animalMap.getEntitiesAt(pos);
                Animal unluckyAnimal = null;
                if(!animals.isEmpty())
                    unluckyAnimal = animals.getFirst();

                for(Parasite parasite : parasiteMap.getEntitiesAt(pos)){
                    parasite.setHost(unluckyAnimal);
                }
            }
        }
    }
}
