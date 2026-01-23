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
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;

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
    private StatisticsLogger statisticsLogger;

    @FXML private ComboBox<String> statsComboBox;
    @FXML private LineChart<Number, Number> statsChart;

    @FXML private Canvas mapCanvas;
    @FXML private Label dayLabel;
    @FXML private Label infoLabel;

    @FXML private javafx.scene.control.Spinner<Integer> speedSpinner;

    private agh.ics.oop.presenter.renderers.MapRenderer mapRenderer;
    private StatisticsChartPresenter chartPresenter;

    @FXML
    public void initialize() {
        chartPresenter = new StatisticsChartPresenter(statsComboBox, statsChart);
        chartPresenter.initialize();
    }

    public void setSimulationConfig(SimulationConfig config) {
        String folderName = "stats";
        String fileName = "symulacja_" + System.currentTimeMillis() + ".csv";

        this.worldMap = new WorldMap(config.mapSize());
        this.statisticsEngine = new StatisticsEngine(worldMap);
        this.simulation = new Simulation(worldMap, config);
        this.statisticsLogger = new StatisticsLogger(folderName + File.separator + fileName);
        
        this.mapRenderer = new agh.ics.oop.presenter.renderers.MapRenderer(mapCanvas);

        simulation.addObserver(this);

        Platform.runLater(() -> {
            mapRenderer.drawMap(worldMap, mostPlacedGrass, topGenotype);
            mapChanged(worldMap, "Dzień: 0");
            
            speedSpinner.valueProperty().addListener((obs, oldVal, newVal) -> simulationDelay.set(newVal));
            simulationDelay.set(speedSpinner.getValue()); 
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

    @FXML
    private void onUndoClicked() {
        if (isSimulationRunning) {
            System.out.println("Zatrzymaj symulację, aby cofnąć!");
            return;
        }
        if (simulation != null) {
            new Thread(() -> {
                simulation.undo();
                Platform.runLater(() -> chartPresenter.decrementDay());
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

        Platform.runLater(() -> {
            mapRenderer.drawMap(worldMap, mostPlacedGrass, topGenotype); 
            dayLabel.setText(message);
            updateStatsLabel(stats);
            chartPresenter.update(stats);
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
}
