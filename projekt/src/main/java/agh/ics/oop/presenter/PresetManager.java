package agh.ics.oop.presenter; // Lub inny pakiet, w którym trzymasz pomocnicze klasy

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PresetManager {

    private static final String PRESET_FILE = "presets.csv";
    private static final String SEPARATOR = ";";

    // Zapisuje linię z danymi do pliku
    public void savePreset(String name, String dataLine) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRESET_FILE, true))) {
            writer.write(name + SEPARATOR + dataLine + "\n");
        }
    }

    // Zwraca listę samych nazw presetów (do wyświetlenia w ComboBox)
    public List<String> loadPresetNames() {
        List<String> names = new ArrayList<>();
        File file = new File(PRESET_FILE);

        if (!file.exists()) return names;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(SEPARATOR);
                if (parts.length > 0) {
                    names.add(parts[0]); // Pierwszy element to nazwa
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return names;
    }

    // Szuka konkretnego presetu po nazwie i zwraca tablicę parametrów
    public String[] loadPresetData(String presetName) {
        File file = new File(PRESET_FILE);
        if (!file.exists()) return null;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(SEPARATOR);
                // Sprawdzamy czy nazwa się zgadza (parts[0])
                if (parts.length > 0 && parts[0].equals(presetName)) {
                    return parts; // Zwracamy całą linię rozbitą na tablicę
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Nie znaleziono
    }
}