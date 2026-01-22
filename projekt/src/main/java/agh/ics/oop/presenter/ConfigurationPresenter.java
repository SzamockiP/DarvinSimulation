package agh.ics.oop.presenter;

import agh.ics.oop.model.*;
import agh.ics.oop.model.util.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.List;

public class ConfigurationPresenter {

    @FXML
    private javafx.scene.control.Spinner<Integer> widthField;
    @FXML
    private javafx.scene.control.Spinner<Integer> heightField;
    @FXML
    private javafx.scene.control.Spinner<Integer> animalCount;
    @FXML
    private javafx.scene.control.Spinner<Integer> parasiteCount;
    @FXML
    private javafx.scene.control.Spinner<Integer> startAnimalEnergy;
    @FXML
    private javafx.scene.control.Spinner<Integer> geneLength;
    @FXML
    private javafx.scene.control.Spinner<Integer> plantCount;
    @FXML
    private javafx.scene.control.Spinner<Integer> dailyPlantCount;
    @FXML
    private javafx.scene.control.Spinner<Integer> plantsEnergy;
    @FXML
    private javafx.scene.control.Spinner<Integer> energyLossDay;
    @FXML
    private javafx.scene.control.Spinner<Integer> energyReproduce;
    @FXML
    private javafx.scene.control.Spinner<Integer> minEnergyReproduce;
    @FXML
    private javafx.scene.control.Spinner<Integer> minMutation;
    @FXML
    private javafx.scene.control.Spinner<Integer> maxMutation;
    @FXML
    private Label errorLabel;
    @FXML
    private javafx.scene.control.CheckBox parasiteToggle;
    @FXML
    private javafx.scene.control.Spinner<Integer> energyPanickingParasite;
    @FXML
    private javafx.scene.control.Spinner<Integer> energyTakenParasite;
    @FXML
    private javafx.scene.control.Spinner<Integer> startParasiteEnergy;
    @FXML private TextField newPresetName;
    @FXML private ComboBox<String> presetComboBox;

    @FXML
    private void onParasiteToggle() {
        boolean enabled = parasiteToggle.isSelected();
        parasiteCount.setDisable(!enabled);
        energyPanickingParasite.setDisable(!enabled);
        energyTakenParasite.setDisable(!enabled);
        startParasiteEnergy.setDisable(!enabled);
    }

    @FXML
    private void onSimulationStartClicked() {
        try {
            int width = widthField.getValue();
            int height = heightField.getValue();

            if (width < 1 || height < 1) {
                errorLabel.setText("Błąd: Minimalny rozmiar mapy to 1x1!");
                return;
            }

            Boundary mapBoundary = new Boundary(new Vector2d(0, 0), new Vector2d(width - 1, height - 1));
            // Dżungla
            int jungleHeight = Math.max(1, height / 5); // Zabezpieczenie, żeby wysokość była min 1
            int jungleStartY = (height - jungleHeight) / 2;
            int jungleEndY = jungleStartY + jungleHeight;
            Boundary jungleBoundary = new Boundary(new Vector2d(0, jungleStartY), new Vector2d(width - 1, jungleEndY));

            // 2. Tworzymy SimulationConfig ze WSZYSTKIMI parametrami
            SimulationConfig config = new SimulationConfig(
                    mapBoundary,
                    jungleBoundary,
                    dailyPlantCount.getValue(),   // plantPerDay
                    plantsEnergy.getValue(),      // energyPerPlant
                    plantCount.getValue(),        // startingPlants
                    animalCount.getValue(),       // startingAnimals
                    parasiteToggle.isSelected() ? parasiteCount.getValue() : 0,     // startingParasites
                    startAnimalEnergy.getValue(), // startingEnergy
                    geneLength.getValue(),        // genotypeLength
                    energyTakenParasite.getValue(), // energyLossDueParasite
                    energyPanickingParasite.getValue(), // energyLossInPanic

                    // --- NOWE POLA ---
                    energyLossDay.getValue(),      // dailyEnergyLoss
                    minEnergyReproduce.getValue(), // reproductionMinEnergy
                    energyReproduce.getValue(),    // reproductionCost
                    minMutation.getValue(),        // mutationMin

                    maxMutation.getValue(),        // mutationMax
                    startParasiteEnergy.getValue() // startingParasiteEnergy
            );

            spawnSimulationWindow(config);

        } catch (NumberFormatException e) {
            errorLabel.setText("Błąd: Wpisz poprawne liczby!");
        } catch (Exception e) {
            errorLabel.setText("Błąd: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void spawnSimulationWindow(SimulationConfig config) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/simulation_window.fxml"));
        Parent root = loader.load();

        SimulationWindowPresenter presenter = loader.getController();
        presenter.setSimulationConfig(config);

        Stage stage = new Stage();
        stage.setTitle("Symulacja - Okno Symulacji");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private final PresetManager presetManager = new PresetManager(); // helper do plików

    @FXML
    public void initialize() {
        // Ta metoda uruchamia się sama przy starcie okna
        refreshPresets();
    }

    private void refreshPresets() {
        presetComboBox.getItems().clear();
        List<String> names = presetManager.loadPresetNames();
        presetComboBox.getItems().addAll(names);
    }

    @FXML
    private void onSavePresetClicked() {
        String name = newPresetName.getText();
        if (name == null || name.trim().isEmpty()) {
            errorLabel.setText("Podaj nazwę presetu!");
            return;
        }

        // Zbieramy wszystkie liczby w jeden długi ciąg znaków (String)
        String data = String.format("%d;%d;%d;%d;%d;%d;%d;%d;%d;%d;%d;%d;%d;%d;%d;%d;%d;%b",
                widthField.getValue(),
                heightField.getValue(),
                animalCount.getValue(),
                parasiteCount.getValue(),
                startAnimalEnergy.getValue(),
                geneLength.getValue(),
                plantCount.getValue(),
                dailyPlantCount.getValue(),
                plantsEnergy.getValue(),
                energyLossDay.getValue(),
                energyReproduce.getValue(),
                minEnergyReproduce.getValue(),
                minMutation.getValue(),
                maxMutation.getValue(),
                energyPanickingParasite.getValue(),
                energyTakenParasite.getValue(),
                startParasiteEnergy.getValue(),
                parasiteToggle.isSelected()
        );

        try {
            presetManager.savePreset(name, data);
            refreshPresets(); // Odświeżamy listę
            newPresetName.clear();
            errorLabel.setText("Zapisano preset: " + name);
        } catch (IOException e) {
            errorLabel.setText("Błąd zapisu pliku!");
        }
    }

    @FXML
    private void onPresetSelected() {
        String selectedName = presetComboBox.getValue();
        if (selectedName == null) return;

        String[] parts = presetManager.loadPresetData(selectedName);
        if (parts != null) {
            try {
                // parts[0] to nazwa, więc dane zaczynają się od indeksu 1
                widthField.getValueFactory().setValue(Integer.parseInt(parts[1]));
                heightField.getValueFactory().setValue(Integer.parseInt(parts[2]));
                animalCount.getValueFactory().setValue(Integer.parseInt(parts[3]));
                parasiteCount.getValueFactory().setValue(Integer.parseInt(parts[4]));
                startAnimalEnergy.getValueFactory().setValue(Integer.parseInt(parts[5]));
                geneLength.getValueFactory().setValue(Integer.parseInt(parts[6]));
                plantCount.getValueFactory().setValue(Integer.parseInt(parts[7]));
                dailyPlantCount.getValueFactory().setValue(Integer.parseInt(parts[8]));
                plantsEnergy.getValueFactory().setValue(Integer.parseInt(parts[9]));
                energyLossDay.getValueFactory().setValue(Integer.parseInt(parts[10]));
                energyReproduce.getValueFactory().setValue(Integer.parseInt(parts[11]));
                minEnergyReproduce.getValueFactory().setValue(Integer.parseInt(parts[12]));
                minMutation.getValueFactory().setValue(Integer.parseInt(parts[13]));
                maxMutation.getValueFactory().setValue(Integer.parseInt(parts[14]));
                energyPanickingParasite.getValueFactory().setValue(Integer.parseInt(parts[15]));
                energyTakenParasite.getValueFactory().setValue(Integer.parseInt(parts[16]));
                startParasiteEnergy.getValueFactory().setValue(Integer.parseInt(parts[17]));

                boolean pToggle = Boolean.parseBoolean(parts[18]);
                parasiteToggle.setSelected(pToggle);
                onParasiteToggle(); // Odśwież widoczność pól zależnych od checkboxa

                errorLabel.setText("Wczytano: " + selectedName);
            } catch (Exception e) {
                errorLabel.setText("Błąd wczytywania danych presetu.");
                e.printStackTrace();
            }
        }
    }
}
