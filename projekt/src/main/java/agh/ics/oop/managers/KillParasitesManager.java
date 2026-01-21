package agh.ics.oop.managers;

import agh.ics.oop.model.*;
import agh.ics.oop.model.util.SimulationConfig;
import java.util.ArrayList;
import java.util.List;

public class KillParasitesManager implements ISimulationManager {
    @Override
    public void step(WorldMap map, SimulationConfig config) {
        List<Parasite> deadParasites = new ArrayList<>();

        for(Parasite parasite : map.getParasites().getEntities()){
            // Sprawdzamy czy energia spadła do 0
            if(parasite.getEnergy() <= 0){
                deadParasites.add(parasite);
            }
        }


        for(Parasite parasite : deadParasites){
            map.getParasites().removeEntity(parasite);
        }
    }
}
