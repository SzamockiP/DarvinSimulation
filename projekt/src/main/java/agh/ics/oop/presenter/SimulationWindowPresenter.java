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
import javafx.scene.paint.Paint;

import java.io.File;
import java.util.Set;
import agh.ics.oop.presenter.StatisticsLogger;

public class SimulationWindowPresenter implements MapChangeListener {
    private WorldMap worldMap;
    private StatisticsEngine statisticsEngine;
    private Simulation simulation;
    private volatile boolean isSimulationRunning = false;
    private Thread activeThread;
    private final java.util.concurrent.atomic.AtomicInteger simulationDelay = new java.util.concurrent.atomic.AtomicInteger(300);
    String topGenotype;
    Set<Vector2d> mostPlacedGrass;
    private StatisticsLogger statisticsLogger; //

    @FXML private Canvas mapCanvas;
    @FXML private Label dayLabel;
    @FXML private Label infoLabel;

    @FXML private javafx.scene.control.Spinner<Integer> speedSpinner;

    public void setSimulationConfig(SimulationConfig config) {
        String folderName = "stats";
        String fileName = "symulacja_" + System.currentTimeMillis() + ".csv";

        this.worldMap = new WorldMap(config.mapSize());
        this.statisticsEngine = new StatisticsEngine(worldMap);
        this.simulation = new Simulation(worldMap, config);
        this.statisticsLogger = new StatisticsLogger(folderName + File.separator + fileName);

        simulation.addObserver(this);

        // Initial draw
        Platform.runLater(() -> {
            drawMap();
            mapChanged(worldMap, "Dzień: 0");
            
            // gdy zmienia się wartość w okienku, zmienna w tle sama się zaktualizuje
            speedSpinner.valueProperty().addListener((obs, oldVal, newVal) -> simulationDelay.set(newVal));
            simulationDelay.set(speedSpinner.getValue()); // Ustawienie początkowe
        });
        
        startSimulationThread();
    }

    private void startSimulationThread() {
        activeThread = new Thread(() -> {
            while (true) {
                try {
                    if (isSimulationRunning && simulation != null) {
                        simulation.step();
                    }
                    // Czytamy bezpieczną stałą, którą aktualizuje listener
                    Thread.sleep(simulationDelay.get());
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
        statisticsEngine.update();
        SimulationStatistics stats = statisticsEngine.calculate();
        this.mostPlacedGrass = statisticsEngine.getFieldsWithMostPlantGrowth();

        if (!stats.mostPopularGenotypes().isEmpty()) {
            this.topGenotype = stats.mostPopularGenotypes().get(0).toString();
        } else {
            this.topGenotype = null;
        }

        if (statisticsLogger != null) {
            statisticsLogger.log(stats);
        }

        // Wysyłamy do GUI tylko gotowy wynik
        Platform.runLater(() -> {
            drawMap(); 
            dayLabel.setText(message);
            updateStatsLabel(stats);
        });
    }

    private void updateStatsLabel(SimulationStatistics stats) {
        this.topGenotype = stats.mostPopularGenotypes().isEmpty() ? "-" : stats.mostPopularGenotypes().get(0).toString();

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
        drawGoodSpots(gc, mapWidth, mapHeight, cellWidth, cellHeight);
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
        
        double visualY = (mapHeight - jungleStartY - jungleHeight) * cellHeight;

        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(
                0,                              
                visualY,     
                mapCanvas.getWidth(),          
                jungleHeight * cellHeight       
        );
    }
    public void drawGoodSpots(GraphicsContext gc, double mapWidth, double mapHeight,
                         double cellWidth, double cellHeight) {
        if (mostPlacedGrass == null || mostPlacedGrass.isEmpty()) return;

        gc.setFill(Color.LIGHTYELLOW);// Ustawiamy kolor wyróżnienia na półprzezroczysty czerwony

        for (Vector2d position : mostPlacedGrass) {
            double logicX = position.getX();
            double logicY = position.getY();

            double visualY = mapHeight - 1 - logicY;
            double x = logicX * cellWidth;
            double y = visualY * cellHeight;

            gc.fillRect(x, y, cellWidth, cellHeight);
        }
    }
    
    private void drawEntities(GraphicsContext gc, java.util.Collection<? extends Entity> entities,
                               Color color, double mapHeight, double cellWidth, double cellHeight) {
        gc.setFill(color);

        for (Entity entity : entities) {
            gc.setFill(color);
            double logicX = entity.getPosition().getX();
            double logicY = entity.getPosition().getY();

            Vector2d position = entity.getPosition();
            if (mostPlacedGrass != null && mostPlacedGrass.contains(position)) {
                gc.setFill(Color.LAWNGREEN);
            }
            
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
            
            double visualY = mapHeight - 1 - logicY;
            
            double x = logicX * cellWidth;
            double y = visualY * cellHeight;

            // Rozmiar ciała i pozycja
            double width = cellWidth * scale;
            double height = cellHeight * scale;
            double offsetX = (cellWidth - width) / 2;
            double offsetY = (cellHeight - height) / 2;
            double centerX = x + cellWidth / 2;
            double centerY = y + cellHeight / 2;

            // Rysowanie ciała
            String currentGenotype = creature.getGenotype().toString();
            if (topGenotype != null && topGenotype.equals(currentGenotype)) {
                gc.setFill(Color.BLUEVIOLET);
            } else {
                gc.setFill(bodyColor);
            }
            gc.fillOval(x + offsetX, y + offsetY, width, height);

            // Rysowanie oczu
            Vector2d dir = creature.getDirection().getUnitVector();

            // (0,1) -> Góra -> -90 stopni
            double angle = Math.toDegrees(Math.atan2(-dir.getY(), dir.getX()));

            gc.save();
            gc.translate(centerX, centerY);
            gc.rotate(angle);

            gc.setFill(eyeColor);
            double eyeSize = width * 0.20;
            double eyeDist = width * 0.30;
            double eyeSep = width * 0.20; 

            gc.fillOval(eyeDist - eyeSize / 2, -eyeSep - eyeSize / 2, eyeSize, eyeSize);
            gc.fillOval(eyeDist - eyeSize / 2, eyeSep - eyeSize / 2, eyeSize, eyeSize);

            gc.restore();

            int energy = creature.getEnergy(); // Zakładam, że klasa Creature ma tę metodę publiczną
            SimulationConfig config = creature.getSimulationConfig();

            // 2. Dobieramy kolor
            if (energy <= config.dailyEnergyLoss() * 2) {
                gc.setFill(Color.RED);
            } else if (energy <= config.dailyEnergyLoss() * 4) {
                gc.setFill(Color.YELLOW);
            } else {
                gc.setFill(Color.GREEN);
            }

            double barWidth = cellWidth * 0.8;       // Pasek na 80% szerokości pola
            double barHeight = Math.max(2, cellHeight * 0.15); // Pasek ma 15% wysokości pola (min 2px)
            double barX = x + (cellWidth - barWidth) / 2; // Centrujemy pasek w poziomie względem pola

            // Rysujemy pasek na samej górze pola (lub lekko nad nim, np. y - barHeight)
            // Tutaj rysuję go na górze wewnątrz pola, żeby nie nachodził na sąsiadów wyżej
            double barY = y;
            gc.fillRect(barX, barY, barWidth, barHeight);

            gc.restore();
        }
    }

}
