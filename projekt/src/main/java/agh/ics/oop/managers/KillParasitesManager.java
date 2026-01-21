package agh.ics.oop.managers;

import agh.ics.oop.model.*;
import agh.ics.oop.model.util.SimulationConfig;

public class KillParasitesManager implements ISimulationManager {
    @Override
    public void step(WorldMap map, SimulationConfig config) {
        for(Parasite parasite : map.getParasites().getEntities()){
            if(parasite.getEnergy() <= 0){
                parasite.kill();
                map.getParasites().removeEntity(parasite);
            }
        }
    }
}
