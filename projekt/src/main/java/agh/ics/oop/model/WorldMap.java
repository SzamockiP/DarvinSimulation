package agh.ics.oop.model;

import agh.ics.oop.model.util.Boundary;
import java.util.List;

public class WorldMap {
    private final LayerMap<Animal> animalMap;
    private final LayerMap<Plant> plantMap;
    private final LayerMap<Parasite> parasiteMap;
    private final List<Animal> deadAnimals = new java.util.ArrayList<>();
    private final Boundary boundary;

    public WorldMap(Boundary boundary) {
        this.boundary = boundary;
        this.animalMap = new LayerMap<>(boundary);
        this.plantMap = new LayerMap<>(boundary);
        this.parasiteMap = new LayerMap<>(boundary);
    }
    
    public List<Animal> getDeadAnimals() {
        return deadAnimals;
    }

    public LayerMap<Animal> getAnimals() {
        return animalMap;
    }

    public LayerMap<Plant> getPlants() {
        return plantMap;
    }

    public LayerMap<Parasite> getParasites() {
        return parasiteMap;
    }

    public Boundary getCurrentBoundary() {
        return boundary;
    }
}
