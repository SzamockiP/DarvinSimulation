package agh.ics.oop.model.statistics;

import agh.ics.oop.model.Genotype;
import java.util.List;

public record SimulationStatistics(
        int animalCount,
        int plantCount,
        int parasiteCount,
        int freeFieldsCount,
        List<Genotype> mostPopularGenotypes, // Lista, bo może być remis
        double averageEnergy,
        double averageLifeSpan,
        double averageChildren
) {}