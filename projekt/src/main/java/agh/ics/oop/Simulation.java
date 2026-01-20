package agh.ics.oop;

import agh.ics.oop.model.*;
import agh.ics.oop.model.util.SimulationConfig;

import java.util.ArrayList;
import java.util.List;

public class Simulation {
    SimulationConfig simulationConfig;

    WorldMap<Animal> animalMap;
    WorldMap<Plant> plantMap;
    WorldMap<Parasite> parasiteMap;

    List<Animal> deadAnimals;

    public Simulation(SimulationConfig simulationConfig) {
        this.simulationConfig = simulationConfig;
        animalMap = new WorldMap<Animal>(simulationConfig.mapSize());
        plantMap = new WorldMap<Plant>(simulationConfig.mapSize());
        parasiteMap = new WorldMap<Parasite>(simulationConfig.mapSize());

        deadAnimals = new ArrayList<Animal>();

        // dodaj zwierzęta
        for(int i = 0; i < this.simulationConfig.startingAnimals(); i++){
            animalMap.addEntity(
                    new Animal(
                            new Vector2d(0, 0),
                            10,
                            new Genotype(this.simulationConfig.genotypeLength())
                    )
            );
        }

        // dodaj rośliny
        for(int i = 0; i < this.simulationConfig.startingPlants(); i++){
            plantMap.addEntity(new Plant(new Vector2d(0, 0)));
        }

        // dodaj pasożyty
        for(int i = 0; i < this.simulationConfig.startingAnimals(); i++){
            parasiteMap.addEntity(
                    new Parasite(
                            new Vector2d(0, 0),
                            new Genotype(this.simulationConfig.genotypeLength()),
                            this.simulationConfig
                    )
            );

        }

    }

    public void runSimulation()
    {
        for(int i = 0; i < 10; i++){
            IO.println("Simulation "+i+" started");
            tickSimulation();
        }
    }

    // jak zabijasz wszystko co krok to działa :D
    private void tickSimulation(){
        setHosts();
        moveAnimals();

        killAnimals();
        killParasites();

        feedAnimals();
        moveParasites();

        killAnimals();
        killParasites();

        reproduceAnimals();
        reproduceParasites();

        killAnimals();
        killParasites();
    }


    private void moveAnimals(){
        for (Animal animal : animalMap.getEntities()){
            animalMap.move(animal);
        }
    }

    private void moveParasites(){
        for (Parasite parasite : parasiteMap.getEntities()){
            parasiteMap.move(parasite);
        }
    }

    private void feedAnimals(){
        // dla każdej kratki
        for(int y = 0; y < animalMap.getCurrentBoundary().upperRight().getY(); y++){
            for(int x = 0; x < animalMap.getCurrentBoundary().upperRight().getX(); x++){
                Vector2d pos = new Vector2d(x,y);
                List<Animal> animals = animalMap.getEntitiesAt(pos);
                List<Plant>  plants = plantMap.getEntitiesAt(pos);
                if(animals.isEmpty() || plants.isEmpty()) continue;

                // zwierze z największą energią
                Animal bestAnimal = animals.getFirst();
                for(Animal animal : animals){
                    if(animal.getEnergy() > bestAnimal.getEnergy()){
                        bestAnimal = animal;
                    }
                }
                Plant plant = plants.getFirst();

                // najlepsze zwierze je
                bestAnimal.addEnergy(this.simulationConfig.energyPerPlant());
                plantMap.getEntitiesAt(pos).remove(plant);
            }
        }
    }

    private void setHosts(){
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

    private void killAnimals() {
        for(Animal animal : animalMap.getEntities()){
            if(!animal.isAlive()){
                animalMap.getEntitiesAt(animal.getPosition()).remove(animal);
                deadAnimals.add(animal);
            }
        }
    }

    private void killParasites() {
        for(Parasite parasite : parasiteMap.getEntities()){
            if(parasite.getEnergy() <= 0){
                parasite.kill();
                parasiteMap.getEntitiesAt(parasite.getPosition()).remove(parasite);
            }
        }
    }

    private void reproduceAnimals() {
        // dla każdej kratki
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
                animalMap.getEntitiesAt(pos).add(newAnimal);
            }
        }
    }

    private void reproduceParasites() {
        // dla każdej kratki
        for(int y = 0; y < parasiteMap.getCurrentBoundary().upperRight().getY(); y++){
            for(int x = 0; x < parasiteMap.getCurrentBoundary().upperRight().getX(); x++){
                Vector2d pos = new Vector2d(x,y);
                List<Parasite> parasites = parasiteMap.getEntitiesAt(pos);
                if(parasites.isEmpty()) continue;

                // pasożyt z największą energią
                Parasite bestParasite = parasites.getFirst();

                for(Parasite parasite : parasites){
                    if(parasite.getEnergy() > bestParasite.getEnergy()){
                        bestParasite = parasite;
                    }
                }

                Parasite secondBestParasite = null;
                for(Parasite parasite : parasites){
                    if(parasite.getEnergy() <= bestParasite.getEnergy() && parasite != bestParasite){
                        secondBestParasite = parasite;
                    }
                }
                if(secondBestParasite == null) continue;

                Parasite newParasite = (Parasite) bestParasite.reproduce(secondBestParasite);
                parasiteMap.getEntitiesAt(pos).add(newParasite);
            }
        }
    }
}
