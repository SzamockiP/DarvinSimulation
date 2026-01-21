package agh.ics.oop.managers;

import agh.ics.oop.model.*;
import agh.ics.oop.model.util.SimulationConfig;

public class MoveParasitesManager implements ISimulationManager {
    @Override
    public void step(WorldMap map, SimulationConfig config) {
        for (Parasite parasite : map.getParasites().getEntities()){
            map.getParasites().move(parasite);
        }
    }
}
