package agh.ics.oop.model;

import agh.ics.oop.model.util.Boundary;
import java.util.List;

public class WorldMap {
    private final LayerMap<Animal> animalMap;
    private final LayerMap<Plant> plantMap;
    private final LayerMap<Parasite> parasiteMap;
    private final List<Animal> deadAnimals = new java.util.ArrayList<>();
    private final Boundary boundary;
    private long totalDeadAnimalsAge = 0;
    private int deadAnimalsCount = 0;

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

    public void addDeadAnimalStats(int age) {
        totalDeadAnimalsAge += age;
        deadAnimalsCount++;
    }

    public double getAverageAnimalLifeSpan() {
        if (deadAnimalsCount == 0) return 0.0;
        return (double) totalDeadAnimalsAge / deadAnimalsCount;
    }

    public WorldMap copy() {
        WorldMap newMap = new WorldMap(this.boundary);
        
        // Kopiowanie prostych statystyk
        newMap.totalDeadAnimalsAge = this.totalDeadAnimalsAge;
        newMap.deadAnimalsCount = this.deadAnimalsCount;

        java.util.Map<java.util.UUID, Animal> idMap = new java.util.HashMap<>();

        // Kopiowanie zwierząt
        for (Animal animal : this.animalMap.getEntities()) {
            Animal copy = (Animal) animal.copy();
            idMap.put(animal.getId(), copy);
            newMap.getAnimals().addEntity(copy);
        }

        // Kopiowanie martwych zwierząt
        // (Ważne: martwe zwierzęta nie są na mapie, ale są w statystykach)
        for(Animal dead : this.deadAnimals) {
             newMap.getDeadAnimals().add((Animal) dead.copy());
        }

        // Kopiowanie roślin
        for (Plant plant : this.plantMap.getEntities()) {
            newMap.getPlants().addEntity((Plant) plant.copy());
        }

        // Kopiowanie pasożytów
        for (Parasite parasite : this.parasiteMap.getEntities()) {
            Parasite copy = (Parasite) parasite.copy();
            newMap.getParasites().addEntity(copy);
            
            // Relinkowanie
            // Musimy to zrobić PO skopiowaniu wszystkich zwierząt (hostów)
            copy.relink(idMap);
        }

        return newMap;
    }

    public void restore(WorldMap snapshot) {
        // 1. Tworzymy świeżą głęboką kopię snapshotu (aby nie modyfikować historii)
        WorldMap fresh = snapshot.copy();

        // 2. Czyścimy obecny stan
        this.animalMap.clear();
        this.plantMap.clear();
        this.parasiteMap.clear();
        this.deadAnimals.clear();

        // 3. Przenosimy zawartość ze świeżej kopii
        for (Animal a : fresh.animalMap.getEntities()) this.animalMap.addEntity(a);
        for (Plant p : fresh.plantMap.getEntities()) this.plantMap.addEntity(p);
        for (Parasite p : fresh.parasiteMap.getEntities()) this.parasiteMap.addEntity(p);
        this.deadAnimals.addAll(fresh.deadAnimals);

        this.totalDeadAnimalsAge = fresh.totalDeadAnimalsAge;
        this.deadAnimalsCount = fresh.deadAnimalsCount;
    }
}
