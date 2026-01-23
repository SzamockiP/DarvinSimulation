package agh.ics.oop.model.util;

import agh.ics.oop.model.base.Boundary;

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
        int energyLossInPanic,
        // --- NOWE POLA Z GUI ---
        int dailyEnergyLoss,      // Koszt życia (energyLossDay)
        int reproductionMinEnergy,// Min energia do rozmnażania
        int reproductionCost,     // Koszt rozmnażania (ile energii tracą rodzice)
        int mutationMin,          // Min liczba mutacji
        int mutationMax,          // Max liczba mutacji
        int startingParasiteEnergy // NOWE: Startowa energia pasożytów
) {
}