package agh.ics.oop.presenter;

import agh.ics.oop.model.*;
import agh.ics.oop.model.statistics.*;
import agh.ics.oop.model.util.*;
import javafx.fxml.FXML;
import agh.ics.oop.model.util.Boundary;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import agh.ics.oop.Simulation;
import javafx.application.Platform;

public class SimulationPresenter implements MapChangeListener{
    private WorldMap worldMap;
    private StatisticsEngine statisticsEngine;
    private volatile boolean isSimulationRunning = false;

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
    @FXML
    private TextField startParasiteEnergy;

    public void initialize() {
        // Ta metoda uruchamia się sama po załadowaniu okna
        System.out.println("Okno załadowane poprawnie!");
    }

    @FXML
    private void onStopClicked() {
        isSimulationRunning = false;
        System.out.println("Symulacja zatrzymana.");
    }

    @FXML
    private void onResumeClicked() {
        isSimulationRunning = true;
        System.out.println("Symulacja wznowiona.");
    }

    @Override
    public void mapChanged(WorldMap worldMap, String message) {
        SimulationStatistics stats = statisticsEngine.calculate();

        // Wysyłamy do GUI tylko GOTOWY WYNIK.
        Platform.runLater(() -> {
            drawMap(); // Rysowanie nadal musi być w runLater
            updateStatsLabel(stats, message);
        });
    }
    private void updateStatsLabel(SimulationStatistics stats, String message) {
        String topGenotype = stats.mostPopularGenotypes().isEmpty() ? "-" : stats.mostPopularGenotypes().get(0).toString();

        infoLabel.setText(String.format(
                """
                %s
                Zwierząt: %d
                Roślin: %d
                Pasożytów: %d
                Wolne pola: %d
                Śr. energia: %.2f
                Śr. długość życia (martwe): %.2f
                Śr. liczba dzieci (żywe): %.2f
                Top Genotyp: 
                %s
                """,
                message,
                stats.animalCount(),
                stats.plantCount(),
                stats.parasiteCount(),
                stats.freeFieldsCount(),
                stats.averageEnergy(),
                stats.averageLifeSpan(),
                stats.averageChildren(),
                topGenotype
        ));
    }

    private Simulation simulation;

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

                    Integer.parseInt(maxMutation.getText()),        // mutationMax
                    Integer.parseInt(startParasiteEnergy.getText()) // startingParasiteEnergy
            );


            // Tworzymy mapę o wybranych wymiarach
            this.worldMap = new WorldMap(config.mapSize());
            this.statisticsEngine = new StatisticsEngine(worldMap);
            this.simulation = new Simulation(worldMap, config);

            simulation.addObserver(this);

            isSimulationRunning = false; // Start paused

            // Don't start a new thread every time we click generate
            // Ideally we should manage the thread better, but for now let's just create a new one if not exists or if we want to restart logic
            // But since "Generate" implies reset, we are fine creating new objects.
            // The thread logic below needs to be careful not to spawn multiple threads running in background for old simulations.
            // For simplicity in this lab scope, let's assume one simulation at a time or we just spawn a thread that checks 'isRunning' and 'simulation' object.
            
            // To be safe and simple: Let's create a thread that runs WHATEVER simulation is current
            if (activeThread == null || !activeThread.isAlive()) {
                 activeThread = new Thread(() -> {
                    while (true) {
                        try {
                            if (isSimulationRunning && simulation != null) {
                                simulation.step();
                            }
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                activeThread.setDaemon(true);
                activeThread.start();
            }
            
            // Force initial draw
            Platform.runLater(() -> {
                drawMap(); 
                // Initial stats might be empty or initial state
                mapChanged(worldMap, "Mapa wygenerowana");
            });

        } catch (NumberFormatException e) {
            infoLabel.setText("Błąd: Wpisz poprawne liczby!");
        }
    }
    
    private Thread activeThread;

    @FXML
    private void onNextDayClicked() {
        if (simulation != null) {
            // Run single step in background to not freeze UI, but it's fast enough usually
            // Better to run on thread or just call step (it's synchronized usually or fast)
            // step() calls notifyObservers which does Platform.runLater, so it is safe to call from here roughly
             new Thread(() -> {
                 simulation.step();
             }).start();
        }
    }

    private void drawMap() {
        if (worldMap == null) return;// Jeśli mapa nie istnieje, nie rysujemy nic

        GraphicsContext gc = mapCanvas.getGraphicsContext2D();

        gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());// Czyścimy całe płótno

        gc.setFill(Color.ANTIQUEWHITE);// Rysujemy tło (Ziemię)
        gc.fillRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());

        // Obliczamy rozmiar jednej kratki
        double mapWidth = worldMap.getCurrentBoundary().upperRight().getX();
        double mapHeight = worldMap.getCurrentBoundary().upperRight().getY();

        double cellWidth = mapCanvas.getWidth() / Math.max(1, mapWidth);// Używamy Math.max(1, ...), żeby uniknąć dzielenia przez zero
        double cellHeight = mapCanvas.getHeight() / Math.max(1, mapHeight);

        // Rysowanie dżungli, trawy, zwierząt, pasożytów i statki
        drawJungle(gc, mapWidth, mapHeight, cellWidth, cellHeight);
        drawEntities(gc, worldMap.getPlants().getEntities(), Color.DARKGREEN, cellWidth, cellHeight);
        drawEntities(gc, worldMap.getParasites().getEntities(), Color.BLACK, cellWidth, cellHeight);
        drawEntities(gc, worldMap.getAnimals().getEntities(), Color.RED, cellWidth, cellHeight);
        drawGrid(gc, mapWidth, mapHeight, cellWidth, cellHeight);
    }
    public void drawGrid(GraphicsContext gc, double mapWidth, double mapHeight,
                         double cellWidth, double cellHeight) {
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(0.2);
        for (int x = 0; x <= mapWidth; x++) {
            gc.strokeLine(x * cellWidth, 0, x * cellWidth, mapCanvas.getHeight());
        }
        for (int y = 0; y <= mapHeight; y++) {
            gc.strokeLine(0, y * cellHeight, mapCanvas.getWidth(), y * cellHeight);
        }
    }
    public void drawJungle(GraphicsContext gc, double mapWidth, double mapHeight,
                         double cellWidth, double cellHeight) {
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
    }
    private void drawEntities(GraphicsContext gc, java.util.Collection<? extends Entity> entities,
                              Color color, double cellWidth, double cellHeight) {
        gc.setFill(color);

        for (Entity entity : entities) {
            double x = entity.getPosition().getX() * cellWidth;
            double y = entity.getPosition().getY() * cellHeight;

            gc.fillOval(x, y, cellWidth, cellHeight);
        }
    }

}
