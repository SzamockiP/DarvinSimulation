package agh.ics.oop.presenter;

import agh.ics.oop.model.*;
import agh.ics.oop.model.util.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

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
}
