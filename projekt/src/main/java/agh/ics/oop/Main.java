package agh.ics.oop;

import agh.ics.oop.model.Animal;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.util.Boundary;
import agh.ics.oop.model.util.SimulationConfig;

import java.util.ArrayList;
import java.util.List;

public class Main {
    static  void main(String[] args) {
        SimulationConfig simulationConfig = new SimulationConfig(
                new Boundary(new Vector2d(0,0),new Vector2d(10,10)),
                new Boundary(new Vector2d(0,4),new Vector2d(10,6)),
                5,
                3,
                10,
                10,
                3,
                5,
                8,
                1,
                2
        );

        Simulation simulation = new Simulation(simulationConfig);

        simulation.runSimulation();
    }
}
