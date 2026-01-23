package agh.ics.oop.presenter;

import agh.ics.oop.model.statistics.SimulationStatistics;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;

public class StatisticsChartPresenter {
    private final ComboBox<String> statsComboBox;
    private final LineChart<Number, Number> statsChart;
    private XYChart.Series<Number, Number> currentSeries;
    private int currentDay = 0;

    public StatisticsChartPresenter(ComboBox<String> statsComboBox, LineChart<Number, Number> statsChart) {
        this.statsComboBox = statsComboBox;
        this.statsChart = statsChart;
    }

    public void initialize() {
        statsComboBox.getItems().addAll(
                "Liczba zwierząt",
                "Liczba roślin",
                "Liczba pasożytów",
                "Wolne pola",
                "Średnia energia",
                "Średnia długość życia",
                "Średnia liczba dzieci",
                "Przyczepione pasożyty",
                "Panikujące pasożyty"
        );
        statsComboBox.getSelectionModel().select(0);

        currentSeries = new XYChart.Series<>();
        currentSeries.setName(statsComboBox.getValue());
        statsChart.getData().add(currentSeries);

        statsComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            statsChart.getData().clear();
            currentSeries = new XYChart.Series<>();
            currentSeries.setName(newVal);
            statsChart.getData().add(currentSeries);
        });
    }

    public void update(SimulationStatistics stats) {
        currentDay++;
        if (currentSeries != null && statsComboBox.getValue() != null) {
            double value = 0.0;
            String selected = statsComboBox.getValue();
            switch (selected) {
                case "Liczba zwierząt" -> value = stats.animalCount();
                case "Liczba roślin" -> value = stats.plantCount();
                case "Liczba pasożytów" -> value = stats.parasiteCount();
                case "Wolne pola" -> value = stats.freeFieldsCount();
                case "Średnia energia" -> value = stats.averageEnergy();
                case "Średnia długość życia" -> value = stats.averageLifeSpan();
                case "Średnia liczba dzieci" -> value = stats.averageChildren();
                case "Przyczepione pasożyty" -> value = stats.attachedParasiteCount();
                case "Panikujące pasożyty" -> value = stats.panickingParasiteCount();
            }
            currentSeries.getData().add(new XYChart.Data<>(currentDay, value));
        }
    }

    public void decrementDay() {
        if(currentDay > 0) currentDay--;
    }
}
