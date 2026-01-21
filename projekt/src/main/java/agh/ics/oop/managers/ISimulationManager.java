package agh.ics.oop.managers;

import agh.ics.oop.model.WorldMap;
import agh.ics.oop.model.util.SimulationConfig;

public interface ISimulationManager {
    void step(WorldMap map, SimulationConfig config);
}
