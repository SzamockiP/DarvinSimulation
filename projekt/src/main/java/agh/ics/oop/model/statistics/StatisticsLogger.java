package agh.ics.oop.presenter;

import agh.ics.oop.model.statistics.SimulationStatistics;
import java.io.*;

public class StatisticsLogger {
    private final String fileName;
    private static final String SEPARATOR = ";";
    private int rowNumber = 0;

    public StatisticsLogger(String fileName) {
        this.fileName = fileName;

        File file = new File(fileName);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        createHeader(); // Tworzymy nagłówek przy starcie
    }

    private void createHeader() {
        File file = new File(fileName);
        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
                writer.write("Day;Animals;Plants;Parasites;FreeFields;AvgEnergy;AvgLifeSpan;AvgChildren\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void log(SimulationStatistics stats) {
        // Formatujemy dane do formatu CSV
        String line = String.format("%d%s%d%s%d%s%d%s%d%s%.2f%s%.2f%s%.2f\n",
                rowNumber++, SEPARATOR,
                stats.animalCount(), SEPARATOR,
                stats.plantCount(), SEPARATOR,
                stats.parasiteCount(), SEPARATOR,
                stats.freeFieldsCount(), SEPARATOR,
                stats.averageEnergy(), SEPARATOR,
                stats.averageLifeSpan(), SEPARATOR,
                stats.averageChildren()
                // Możesz dodać więcej pól jeśli potrzebujesz
        );

        // Dopisywanie do pliku (tryb append = true)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(line.replace(",", ".")); // Zamiana przecinków na kropki w liczbach, żeby Excel nie świrował
        } catch (IOException e) {
            System.out.println("Błąd zapisu statystyk: " + e.getMessage());
        }
    }
}