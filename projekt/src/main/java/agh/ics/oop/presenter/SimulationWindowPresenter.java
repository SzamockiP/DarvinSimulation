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

        double mapWidth = worldMap.getCurrentBoundary().upperRight().getX() + 1;
        double mapHeight = worldMap.getCurrentBoundary().upperRight().getY() + 1;

        double cellWidth = mapCanvas.getWidth() / Math.max(1, mapWidth);
        double cellHeight = mapCanvas.getHeight() / Math.max(1, mapHeight);

        drawJungle(gc, mapWidth, mapHeight, cellWidth, cellHeight);
        drawEntities(gc, worldMap.getPlants().getEntities(), Color.DARKGREEN, mapHeight, cellWidth, cellHeight);
        drawCreatures(gc, worldMap.getAnimals().getEntities(), Color.BROWN, Color.BLACK, mapHeight, cellWidth, cellHeight, 1.0);
        drawCreatures(gc, worldMap.getParasites().getEntities(), Color.BLACK, Color.BLUE, mapHeight, cellWidth, cellHeight, 0.5);
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
        
        // Inverted Y axis
        // Top visual Y = (mapHeight - (jungleStartY + jungleHeight)) * cellHeight
        double visualY = (mapHeight - jungleStartY - jungleHeight) * cellHeight;

        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(
                0,                              
                visualY,     
                mapCanvas.getWidth(),          
                jungleHeight * cellHeight       
        );
    }
    
    private void drawEntities(GraphicsContext gc, java.util.Collection<? extends Entity> entities,
                               Color color, double mapHeight, double cellWidth, double cellHeight) {
        gc.setFill(color);

        for (Entity entity : entities) {
            double logicX = entity.getPosition().getX();
            double logicY = entity.getPosition().getY();
            
            // Invert Y
            double visualY = mapHeight - 1 - logicY;
            
            double x = logicX * cellWidth;
            double y = visualY * cellHeight;

            gc.fillRect(x, y, cellWidth, cellHeight);
        }
    }

    private void drawCreatures(GraphicsContext gc, java.util.Collection<? extends Creature> creatures,
                               Color bodyColor, Color eyeColor, double mapHeight, double cellWidth, double cellHeight, double scale) {
        for (Creature creature : creatures) {
            double logicX = creature.getPosition().getX();
            double logicY = creature.getPosition().getY();
            
            // Invert Y
            double visualY = mapHeight - 1 - logicY;
            
            double x = logicX * cellWidth;
            double y = visualY * cellHeight;

            // Compute body size and positioning
            double width = cellWidth * scale;
            double height = cellHeight * scale;
            double offsetX = (cellWidth - width) / 2;
            double offsetY = (cellHeight - height) / 2;
            double centerX = x + cellWidth / 2;
            double centerY = y + cellHeight / 2;

            // Draw Body
            gc.setFill(bodyColor);
            gc.fillOval(x + offsetX, y + offsetY, width, height);

            // Draw Eyes (Directional)
            Vector2d dir = creature.getDirection().getUnitVector();
            // Invert Y in direction vector for visual rotation
            // Logic (0,1) -> Visual Up -> -90 deg
            double angle = Math.toDegrees(Math.atan2(-dir.getY(), dir.getX()));

            gc.save();
            gc.translate(centerX, centerY);
            gc.rotate(angle);

            // Draw eyes facing "Right" (0 deg) relative to rotated frame
            gc.setFill(eyeColor);
            double eyeSize = width * 0.20; // 20% size
            double eyeDist = width * 0.30; // 30% distance forward
            double eyeSep = width * 0.20;  // 20% distance sideways

            gc.fillOval(eyeDist - eyeSize / 2, -eyeSep - eyeSize / 2, eyeSize, eyeSize);
            gc.fillOval(eyeDist - eyeSize / 2, eyeSep - eyeSize / 2, eyeSize, eyeSize);

            gc.restore();
        }
    }

}
