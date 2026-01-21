package agh.ics.oop.presenter;

import agh.ics.oop.model.*;
import agh.ics.oop.model.util.*;
import javafx.fxml.FXML;
import agh.ics.oop.model.util.Boundary;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import agh.ics.oop.Simulation;

public class SimulationPresenter {
    private WorldMap worldMap;

    @FXML
    private Canvas mapCanvas;
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
    private Label infoLabel;
    @FXML
    private TextField energyPanickingParasite;
    @FXML
    private TextField energyTakenParasite;

    public void initialize() {
        // Ta metoda uruchamia się sama po załadowaniu okna
        System.out.println("Okno załadowane poprawnie!");
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
                    Integer.parseInt(plantCount.getText()),        // plantPerDay
                    Integer.parseInt(plantsEnergy.getText()),      // energyPerPlant
                    Integer.parseInt(dailyPlantCount.getText()),        // startingPlants (zakładam to samo co plantPerDay na start)
                    Integer.parseInt(animalCount.getText()),       // startingAnimals
                    Integer.parseInt(parasiteCount.getText()),     // startingParasites
                    Integer.parseInt(startAnimalEnergy.getText()), // startingEnergy
                    Integer.parseInt(geneLength.getText()),        // genotypeLength
                    10,                                            // energyLossDueParasite (Domyślne - brak w GUI)
                    5,                                             // energyLossInPanic (Domyślne - brak w GUI)

                    // --- NOWE POLA ---
                    Integer.parseInt(energyLossDay.getText()),      // dailyEnergyLoss
                    Integer.parseInt(minEnergyReproduce.getText()), // reproductionMinEnergy
                    Integer.parseInt(energyReproduce.getText()),    // reproductionCost
                    Integer.parseInt(minMutation.getText()),        // mutationMin
                    Integer.parseInt(maxMutation.getText())         // mutationMax
            );


            // Tworzymy mapę o wybranych wymiarach
            this.worldMap = new WorldMap(config.mapSize());

            Simulation simulation = new Simulation(worldMap, config);

            System.out.println("Uruchamiam mapę: " + width + "x" + height);
            drawMap();
            infoLabel.setText("Mapa utworzona: " + width + "x" + height);

        } catch (NumberFormatException e) {
            // jak ktoś wpisze "ala" zamiast liczby
            infoLabel.setText("Błąd: Wpisz poprawne liczby!");
        }
    }

    private void drawMap() {
        if (worldMap == null) return;// Jeśli mapa nie istnieje, nie rysujemy nic

        GraphicsContext gc = mapCanvas.getGraphicsContext2D();

        // Czyścimy całe płótno
        gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());

        // 2. Rysujemy tło (Ziemię)
        gc.setFill(Color.ANTIQUEWHITE);
        gc.fillRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());

        // Obliczamy rozmiar jednej kratki
        // Musimy wiedzieć, jak duża jest mapa. Pobieramy to z Boundary.
        // Używamy Math.max(1, ...), żeby uniknąć dzielenia przez zero
        double mapWidth = worldMap.getCurrentBoundary().upperRight().getX();
        double mapHeight = worldMap.getCurrentBoundary().upperRight().getY();

        double cellWidth = mapCanvas.getWidth() / Math.max(1, mapWidth);
        double cellHeight = mapCanvas.getHeight() / Math.max(1, mapHeight);

        // Rysowanie dżungli
        int height = (int) mapHeight;
        int jungleHeight = Math.max(1, height / 5);
        int jungleStartY = (height - jungleHeight) / 2;
        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(
                0,                              // x (od lewej)
                jungleStartY * cellHeight,      // y (początek dżungli w pikselach)
                mapCanvas.getWidth(),           // szerokość (na całą mapę)
                jungleHeight * cellHeight       // wysokość paska w pikselach
        );

        // Rysowanie Zwierząt
        // Twoja WorldMap ma warstwy, więc musimy pobrać zwierzęta z animalMap
        for (Animal animal : worldMap.getAnimals().getEntities()) {
            gc.setFill(Color.RED); // Zwierzaki na czerwono
            gc.fillOval(
                    animal.getPosition().getX() * cellWidth,
                    animal.getPosition().getY() * cellHeight,
                    cellWidth, cellHeight
            );
        }

        // Rysowanie Roślin (jeśli są)
        for (Plant plant : worldMap.getPlants().getEntities()) {
            gc.setFill(Color.DARKGREEN); // Rośliny na ciemnozielono
            // Rośliny zazwyczaj są kwadratowe
            gc.fillRect(
                    plant.getPosition().getX() * cellWidth,
                    plant.getPosition().getY() * cellHeight,
                    cellWidth, cellHeight
            );
        }

        // Rysowanie Pasożytów (jeśli są)
        for (Parasite parasite : worldMap.getParasites().getEntities()) {
            gc.setFill(Color.BLACK);
            gc.fillOval(
                    parasite.getPosition().getX() * cellWidth,
                    parasite.getPosition().getY() * cellHeight,
                    cellWidth, cellHeight
            );
        }

        // Rysowanie siatki (grid)
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(0.2);
        for (int x = 0; x <= mapWidth; x++) {
            gc.strokeLine(x * cellWidth, 0, x * cellWidth, mapCanvas.getHeight());
        }
        for (int y = 0; y <= mapHeight; y++) {
            gc.strokeLine(0, y * cellHeight, mapCanvas.getWidth(), y * cellHeight);
        }
    }
}
