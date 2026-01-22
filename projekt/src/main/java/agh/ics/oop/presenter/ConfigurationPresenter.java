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
    private TextField widthField;
    @FXML
    private TextField heightField;
    @FXML
    private TextField animalCount;
    @FXML
    private TextField parasiteCount;
    @FXML
    private TextField startAnimalEnergy;
    @FXML
    private TextField geneLength;
    @FXML
    private TextField plantCount;
    @FXML
    private TextField dailyPlantCount;
    @FXML
    private TextField plantsEnergy;
    @FXML
    private TextField energyLossDay;
    @FXML
    private TextField energyReproduce;
    @FXML
    private TextField minEnergyReproduce;
    @FXML
    private TextField minMutation;
    @FXML
    private TextField maxMutation;
    @FXML
    private Label errorLabel;
    @FXML
    private javafx.scene.control.CheckBox parasiteToggle;
    @FXML
    private TextField energyPanickingParasite;
    @FXML
    private TextField energyTakenParasite;
    @FXML
    private TextField startParasiteEnergy;

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
            int width = Integer.parseInt(widthField.getText());
            int height = Integer.parseInt(heightField.getText());
            Boundary mapBoundary = new Boundary(new Vector2d(0, 0), new Vector2d(width, height));
            // Dżungla
            int jungleHeight = Math.max(1, height / 5); // Zabezpieczenie, żeby wysokość była min 1
            int jungleStartY = (height - jungleHeight) / 2;
            int jungleEndY = jungleStartY + jungleHeight;
            Boundary jungleBoundary = new Boundary(new Vector2d(0, jungleStartY), new Vector2d(width, jungleEndY));

            // 2. Tworzymy SimulationConfig ze WSZYSTKIMI parametrami
            SimulationConfig config = new SimulationConfig(
                    mapBoundary,
                    jungleBoundary,
                    Integer.parseInt(dailyPlantCount.getText()),   // plantPerDay
                    Integer.parseInt(plantsEnergy.getText()),      // energyPerPlant
                    Integer.parseInt(plantCount.getText()),        // startingPlants
                    Integer.parseInt(animalCount.getText()),       // startingAnimals
                    parasiteToggle.isSelected() ? Integer.parseInt(parasiteCount.getText()) : 0,     // startingParasites
                    Integer.parseInt(startAnimalEnergy.getText()), // startingEnergy
                    Integer.parseInt(geneLength.getText()),        // genotypeLength
                    Integer.parseInt(energyTakenParasite.getText()), // energyLossDueParasite
                    Integer.parseInt(energyPanickingParasite.getText()), // energyLossInPanic

                    // --- NOWE POLA ---
                    Integer.parseInt(energyLossDay.getText()),      // dailyEnergyLoss
                    Integer.parseInt(minEnergyReproduce.getText()), // reproductionMinEnergy
                    Integer.parseInt(energyReproduce.getText()),    // reproductionCost
                    Integer.parseInt(minMutation.getText()),        // mutationMin

                    Integer.parseInt(maxMutation.getText()),        // mutationMax
                    Integer.parseInt(startParasiteEnergy.getText()) // startingParasiteEnergy
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
