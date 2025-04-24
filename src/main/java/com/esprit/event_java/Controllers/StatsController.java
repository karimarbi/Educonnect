package com.esprit.event_java.Controllers;

import com.esprit.event_java.Models.Category;
import com.esprit.event_java.Services.EventService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import java.util.Map;

public class StatsController {
    @FXML private VBox chartContainer;
    @FXML private TabPane chartTabPane;
    @FXML private BarChart<String, Number> barChart;
    @FXML private PieChart pieChart;
    @FXML private AreaChart<Number, Number> areaChart;

    private final EventService eventService = new EventService();

    @FXML
    public void initialize() {
        createCharts();
    }

    @FXML
    private void refreshCharts() {
        createCharts();
    }

    private void createCharts() {
        // Clear existing data
        barChart.getData().clear();
        pieChart.getData().clear();
        areaChart.getData().clear();

        // Get event counts by category
        Map<Category, Long> eventCounts = eventService.getEventCountByCategory();

        // Create bar chart data
        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
        barSeries.setName("Events");

        // Create pie chart data
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        // Create area chart data
        XYChart.Series<Number, Number> areaSeries = new XYChart.Series<>();
        areaSeries.setName("Events");

        int categoryIndex = 1;
        for (Map.Entry<Category, Long> entry : eventCounts.entrySet()) {
            String categoryName = entry.getKey() != null ? entry.getKey().getName() : "No Category";
            Long count = entry.getValue();

            // Add to bar chart
            barSeries.getData().add(new XYChart.Data<>(categoryName, count));

            // Add to pie chart
            pieData.add(new PieChart.Data(categoryName, count));

            // Add to area chart
            areaSeries.getData().add(new XYChart.Data<>(categoryIndex, count));
            categoryIndex++;
        }

        // Set data to charts
        barChart.getData().add(barSeries);
        pieChart.setData(pieData);
        areaChart.getData().add(areaSeries);
    }
}