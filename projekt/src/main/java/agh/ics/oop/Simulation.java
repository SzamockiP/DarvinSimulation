package agh.ics.oop;

import agh.ics.oop.managers.*;
import agh.ics.oop.model.*;
import agh.ics.oop.model.util.SimulationConfig;

import java.util.ArrayList;
import java.util.List;

public class Simulation {
    private final SimulationConfig simulationConfig;
    private final WorldMap worldMap;
    private final List<ISimulationManager> managers;

    public Simulation(SimulationConfig simulationConfig) {
        this.simulationConfig = simulationConfig;
        this.worldMap = new WorldMap(simulationConfig.mapSize());
        this.managers = new ArrayList<>();

        initializeEntities();
        initializeManagers();
    }

    private void initializeEntities() {
        // dodaj zwierzęta
        for(int i = 0; i < this.simulationConfig.startingAnimals(); i++){
            worldMap.getAnimals().addEntity(
                    new Animal(
                            new Vector2d(0, 0),
                            10,
                            new Genotype(this.simulationConfig.genotypeLength())
                    )
            );
        }

        // dodaj rośliny
        for(int i = 0; i < this.simulationConfig.startingPlants(); i++){
            worldMap.getPlants().addEntity(new Plant(new Vector2d(0, 0)));
        }

        // dodaj pasożyty
        for(int i = 0; i < this.simulationConfig.startingAnimals(); i++){
            worldMap.getParasites().addEntity(
                    new Parasite(
                            new Vector2d(0, 0),
                            new Genotype(this.simulationConfig.genotypeLength()),
                            this.simulationConfig
                    )
            );
        }
    }

    private void initializeManagers() {
        // Based on original tickSimulation order
        managers.add(new SetHostsManager());
        managers.add(new MoveAnimalsManager());
        
        managers.add(new KillAnimalsManager());
        managers.add(new KillParasitesManager());
        
        managers.add(new FeedAnimalsManager());
        managers.add(new MoveParasitesManager());
        
        managers.add(new KillAnimalsManager());
        managers.add(new KillParasitesManager());
        
        managers.add(new ReproduceAnimalsManager());
        managers.add(new ReproduceParasitesManager());
        
        managers.add(new KillAnimalsManager());
        managers.add(new KillParasitesManager());
    }

    public void runSimulation() {
        for(int i = 0; i < 10; i++){
            System.out.println("Simulation "+i+" started");
            tickSimulation();
        }
    }

    private void tickSimulation(){
        for (ISimulationManager manager : managers) {
            manager.step(worldMap, simulationConfig);
        }
    }
}
