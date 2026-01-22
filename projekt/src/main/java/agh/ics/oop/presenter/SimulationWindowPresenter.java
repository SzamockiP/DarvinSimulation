package agh.ics.oop.presenter;

import agh.ics.oop.model.*;
import agh.ics.oop.model.statistics.*;
import agh.ics.oop.model.util.*;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import agh.ics.oop.Simulation;
import javafx.application.Platform;

public class SimulationWindowPresenter implements MapChangeListener {
    private WorldMap worldMap;
    private StatisticsEngine statisticsEngine;
    private Simulation simulation;
    private volatile boolean isSimulationRunning = false;
    private Thread activeThread;

    @FXML
    private Canvas mapCanvas;
    @FXML
    private Label dayLabel;
    @FXML
    private Label infoLabel;

    @FXML
    private javafx.scene.control.Spinner<Integer> speedSpinner;

    public void setSimulationConfig(SimulationConfig config) {
        this.worldMap = new WorldMap(config.mapSize());
        this.statisticsEngine = new StatisticsEngine(worldMap);
        this.simulation = new Simulation(worldMap, config);

        simulation.addObserver(this);

        // Initial draw
        Platform.runLater(() -> {
            drawMap();
            mapChanged(worldMap, "Dzień: 0");
        });
        
        startSimulationThread();
    }

    private void startSimulationThread() {
        activeThread = new Thread(() -> {
            while (true) {
                try {
                    // Spinner might be updated on FX thread, reading it from background thread might be unsafe or throw exception?
                    // Usually properties are better access via Platform.runLater or binding, but for simple int value strictly speaking FX controls are not thread safe.
                    // However, getValue() usually just reads a property. Let's try. If it fails, we wrap in FutureTask or similar.
                    // Safer: Read value in atomic variable updated by listener?
                    // Or simply access it cautiously.
                    // Simplest: Use Platform.runLater to read it into a volatile/atomic or just read it (often works for read).
                    // Correct way:
                    /*
                    final java.util.concurrent.atomic.AtomicLong delayRef = new java.util.concurrent.atomic.AtomicLong(300);
                    Platform.runLater(() -> delayRef.set(speedSpinner.getValue()));
                     */
                    // But we want it inside the loop.
                    
                    final java.util.concurrent.FutureTask<Integer> query = new java.util.concurrent.FutureTask<>(() -> speedSpinner.getValue());
                    Platform.runLater(query);
                    long delay = 300;
                    try {
                        delay = query.get();
                    } catch (Exception e) {
                       // ignore
                    }

                    if (isSimulationRunning && simulation != null) {
                        simulation.step();
                    }
                    Thread.sleep(delay);
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

    @FXML
    private void onNextDayClicked() {
        if (simulation != null) {
             new Thread(() -> {
                 simulation.step();
             }).start();
        }
    }

    @Override
    public void mapChanged(WorldMap worldMap, String message) {
        SimulationStatistics stats = statisticsEngine.calculate();

        // Wysyłamy do GUI tylko GOTOWY WYNIK.
        Platform.runLater(() -> {
            drawMap(); 
            dayLabel.setText(message); // Update day label
            updateStatsLabel(stats);
        });
    }

    private void updateStatsLabel(SimulationStatistics stats) {
        String topGenotype = stats.mostPopularGenotypes().isEmpty() ? "-" : stats.mostPopularGenotypes().get(0).toString();

        infoLabel.setText(String.format(
                """
                Statystyki:
                Zwierząt: %d
                Roślin: %d
                Pasożytów: %d (Przyczepione: %d, Panikujące: %d)
                Wolne pola: %d
                Śr. energia: %.2f
                Śr. długość życia (martwe): %.2f
                Śr. liczba dzieci (żywe): %.2f
                Top Genotyp: 
                %s
                """,
                stats.animalCount(),
                stats.plantCount(),
                stats.parasiteCount(),
                stats.attachedParasiteCount(),
                stats.panickingParasiteCount(),
                stats.freeFieldsCount(),
                stats.averageEnergy(),
                stats.averageLifeSpan(),
                stats.averageChildren(),
                topGenotype
        ));
    }

    private void drawMap() {
        if (worldMap == null) return;

        GraphicsContext gc = mapCanvas.getGraphicsContext2D();

        gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());

        gc.setFill(Color.ANTIQUEWHITE);
        gc.fillRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());

        double mapWidth = worldMap.getCurrentBoundary().upperRight().getX();
        double mapHeight = worldMap.getCurrentBoundary().upperRight().getY();

        double cellWidth = mapCanvas.getWidth() / Math.max(1, mapWidth);
        double cellHeight = mapCanvas.getHeight() / Math.max(1, mapHeight);

        drawJungle(gc, mapWidth, mapHeight, cellWidth, cellHeight);
        drawEntities(gc, worldMap.getPlants().getEntities(), Color.DARKGREEN, cellWidth, cellHeight, true, 1.0);
        drawEntities(gc, worldMap.getAnimals().getEntities(), Color.RED, cellWidth, cellHeight, false, 1.0);
        drawEntities(gc, worldMap.getParasites().getEntities(), Color.BLACK, cellWidth, cellHeight, false, 0.5);
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
                0,                              
                jungleStartY * cellHeight,     
                mapCanvas.getWidth(),          
                jungleHeight * cellHeight       
        );
    }
    
    private void drawEntities(GraphicsContext gc, java.util.Collection<? extends Entity> entities,
                              Color color, double cellWidth, double cellHeight, boolean isSquare, double scale) {
        gc.setFill(color);

        for (Entity entity : entities) {
            double x = entity.getPosition().getX() * cellWidth;
            double y = entity.getPosition().getY() * cellHeight;

            if (isSquare) {
                gc.fillRect(x, y, cellWidth, cellHeight);
            } else {
                if (scale < 1.0) {
                    double width = cellWidth * scale;
                    double height = cellHeight * scale;
                    double offsetX = (cellWidth - width) / 2;
                    double offsetY = (cellHeight - height) / 2;
                    gc.fillOval(x + offsetX, y + offsetY, width, height);
                } else {
                    gc.fillOval(x, y, cellWidth, cellHeight);
                }
            }
        }
    }

}
