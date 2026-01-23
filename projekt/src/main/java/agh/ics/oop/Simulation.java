package agh.ics.oop;

import agh.ics.oop.managers.*;
import agh.ics.oop.model.*;
import agh.ics.oop.model.util.SimulationConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulation {
    private final SimulationConfig simulationConfig;
    private final WorldMap worldMap;
    private final List<ISimulationManager> managers;
    private final List<MapChangeListener> observers = new ArrayList<>();

    public Simulation(WorldMap worldMap, SimulationConfig simulationConfig) {
        this.simulationConfig = simulationConfig;
        this.worldMap = worldMap;
        this.managers = new ArrayList<>();

        initializeEntities();
        initializeManagers();
    }

    private void initializeEntities() {
        // dodaj zwierzęta
        for(int i = 0; i < this.simulationConfig.startingAnimals(); i++){
            Vector2d position = getRandomPosition();
            worldMap.getAnimals().addEntity(
                    new Animal(
                            position,
                            new Genotype(this.simulationConfig.genotypeLength()),
                            this.simulationConfig.startingEnergy(),
                            this.simulationConfig
                    )
            );
        }

        // dodaj rośliny
        // Optymalizacja: nie próbuj stawiać więcej roślin niż jest miejsc
        int width = simulationConfig.mapSize().upperRight().getX();
        int height = simulationConfig.mapSize().upperRight().getY();
        int maxPlants = (width + 1) * (height + 1);
        int plantLimit = Math.min(this.simulationConfig.startingPlants(), maxPlants);

        for(int i = 0; i < plantLimit; i++){
            Vector2d position = getRandomPlantPosition();

            if (position != null) {
                worldMap.getPlants().addEntity(new Plant(position));
            } else {
                break; // Brak miejsc
            }
        }

        // dodaj pasożyty
        for(int i = 0; i < this.simulationConfig.startingParasites(); i++){
            Vector2d position = getRandomPosition();
            worldMap.getParasites().addEntity(
                    new Parasite(
                            position,
                            new Genotype(this.simulationConfig.genotypeLength()),
                            this.simulationConfig.startingParasiteEnergy(),
                            this.simulationConfig
                    )
            );
        }
    }

    private Vector2d getRandomPosition() {
        Random random = new Random();
        int x = random.nextInt(simulationConfig.mapSize().upperRight().getX() + 1);
        int y = random.nextInt(simulationConfig.mapSize().upperRight().getY() + 1);
        return new Vector2d(x, y);
    }

    private Vector2d getRandomPlantPosition() {
        // Wymiary mapy
        int width = simulationConfig.mapSize().upperRight().getX();
        int height = simulationConfig.mapSize().upperRight().getY();

        // granice "Dżungli" (środkowego paska - 1/5 wysokości)
        int jungleStartY = simulationConfig.jungleSize().lowerLeft().getY();
        int jungleEndY = simulationConfig.jungleSize().upperRight().getY();

        // 3. Tworzymy dwie listy: wolne pola w dżungli i wolne pola na stepie
        List<Vector2d> freeJungleSpots = new ArrayList<>();
        List<Vector2d> freeSteppeSpots = new ArrayList<>();

        // Przechodzimy po całej mapie i sprawdzamy, gdzie nie ma trawy
        for (int x = 0; x <= width; x++) {
            for (int y = 0; y <= height; y++) {
                Vector2d pos = new Vector2d(x, y);

                if (!worldMap.getPlants().isOccupied(pos)) {// Jeśli na danej pozycji NIE MA ROŚLINY
                    if (y >= jungleStartY && y < jungleEndY) {
                        freeJungleSpots.add(pos); // To jest środek (Dżungla)
                    } else {
                        freeSteppeSpots.add(pos); // To jest góra lub dół (Step)
                    }}}}

        // Losujemy, gdzie chcemy postawić trawę (80% szans na Dżunglę)
        boolean preferJungle = Math.random() < 0.8; // 80% szans na prawdę

        Random random = new Random();

        if (preferJungle) {
            // Chcemy postawić w Dżungli (80% przypadków)
            if (!freeJungleSpots.isEmpty()) {// Jest miejsce w dżungli -> losujemy stamtąd
                return freeJungleSpots.get(random.nextInt(freeJungleSpots.size()));
            } else if (!freeSteppeSpots.isEmpty()) {// Dżungla pełna -> bierzemy z reszty obszaru
                return freeSteppeSpots.get(random.nextInt(freeSteppeSpots.size()));
            }
        } else {
            // Chcemy postawić na Stepie (20% przypadków)
            if (!freeSteppeSpots.isEmpty()) {// Jest miejsce na reszcie mapie -> losujemy stamtąd
                return freeSteppeSpots.get(random.nextInt(freeSteppeSpots.size()));
            } else if (!freeJungleSpots.isEmpty()) {// Reszta mapy pełna -> bierzemy z Dżungli
                return freeJungleSpots.get(random.nextInt(freeJungleSpots.size()));
            }
        }

        return null;
    }

    private void initializeManagers() {
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

        managers.add(new AddPlantsManager());
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

    public void addObserver(MapChangeListener observer) {// Metoda do rejestrowania obserwatorów
        observers.add(observer);
    }

    public void removeObserver(MapChangeListener observer) {// Metoda do usuwania
        observers.remove(observer);
    }

    private void notifyObservers(String message) { // Metoda wywołuje mapChanged u każdego obserwatora
        for (MapChangeListener observer : observers) {
            observer.mapChanged(worldMap, message);
        }
    }

    private int day = 0;
    private final java.util.Stack<WorldMap> history = new java.util.Stack<>();

    public void step() {
        // Zapisz stan przed krokiem
        history.push(worldMap.copy());

        tickSimulation();
        day++;
        notifyObservers("Dzień: " + day);
    }
    
    public void undo() {
        if (history.isEmpty()) return;

        WorldMap previousState = history.pop();
        worldMap.restore(previousState);
        day--;
        notifyObservers("Cofnięto (Dzień: " + day + ")");
    }
}
