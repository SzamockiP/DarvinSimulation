package agh.ics.oop.model.util;

public record SimulationConfig(
        Boundary mapSize,
        Boundary jungleSize,
        int plantPerDay,
        int energyPerPlant,
        int startingPlants,
        int startingAnimals,
        int startingParasites,
        int startingEnergy,
        int genotypeLength,
        int energyLossDueParasite,
        int energyLossInPanic
) {
}