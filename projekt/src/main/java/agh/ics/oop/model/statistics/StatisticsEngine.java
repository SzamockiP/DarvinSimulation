package agh.ics.oop.model.statistics;

import agh.ics.oop.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class StatisticsEngine {
    private final WorldMap map;

    public StatisticsEngine(WorldMap map) {
        this.map = map;
    }

    public SimulationStatistics calculate() {
        Collection<Animal> animals = map.getAnimals().getEntities();
        Collection<Plant> plants = map.getPlants().getEntities();
        Collection<Parasite> parasites = map.getParasites().getEntities();

        int animalCount = animals.size();
        int plantCount = plants.size();
        int parasiteCount = parasites.size();

        double avgEnergy = 0.0;
        if (!animals.isEmpty()) {
            avgEnergy = animals.stream()
                    .mapToInt(Animal::getEnergy)
                    .average()
                    .orElse(0.0);
        }


        double avgLifeSpan = map.getAverageAnimalLifeSpan();

        double avgChildren = 0.0;
        if (!animals.isEmpty()) {
            avgChildren = animals.stream()
                    .mapToInt(Animal::getLivingChildrenAmount)
                    .average()
                    .orElse(0.0);
        }

        int freeFields = calculateFreeFields(); //wolne pola

        List<Genotype> popularGenotypes = calculatePopularGenotypes(animals);

        long attachedCount = parasites.stream().filter(p -> p.getHost() != null).count();
        int panickingCount = parasiteCount - (int) attachedCount;

        return new SimulationStatistics(
                animalCount,
                plantCount,
                parasiteCount,
                freeFields,
                popularGenotypes,
                avgEnergy,
                avgLifeSpan,
                avgChildren,
                (int) attachedCount,
                panickingCount
        );
    }

    private int calculateFreeFields() {
        int width = map.getCurrentBoundary().upperRight().getX();
        int height = map.getCurrentBoundary().upperRight().getY();

        // 1. Pobieramy zajęte pozycje z każdej warstwy
        Set<Vector2d> occupiedPositions = new HashSet<>();
        
        occupiedPositions.addAll(map.getAnimals().getOccupiedPositions());
        occupiedPositions.addAll(map.getPlants().getOccupiedPositions());
        occupiedPositions.addAll(map.getParasites().getOccupiedPositions());

        // Wszystkie pola (width i height to indeksy, więc rozmiar to +1)
        int totalFields = (width + 1) * (height + 1); 

        return totalFields - occupiedPositions.size();
    }

    private List<Genotype> calculatePopularGenotypes(Collection<Animal> animals) {
        if (animals.isEmpty()) return List.of();

        // Mapa: Genotyp -> Liczba wystąpień
        Map<Genotype, Integer> counts = new HashMap<>();
        for (Animal a : animals) {
            counts.put(a.getGenotype(), counts.getOrDefault(a.getGenotype(), 0) + 1);
        }

        // Znajdź maksymalną liczbę wystąpień
        int maxCount = counts.values().stream().max(Integer::compareTo).orElse(0);

        // Zwróć wszystkie genotypy, które mają ten max (może być remis)
        return counts.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}