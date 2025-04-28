package com.esprit.Controllers.Karim;

import com.esprit.Models.Category;
import com.esprit.Services.EventService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

import java.util.Map;

public class StatsController {
    @FXML private VBox chartContainer;
    @FXML private TabPane chartTabPane;
    @FXML private BarChart<String, Number> barChart;
    @FXML private PieChart pieChart;
    @FXML private AreaChart<String, Number> areaChart;

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

        // Bar Chart: Events by Location
        createLocationBarChart();

        // Pie Chart: Events by Category
        createCategoryPieChart();

        // Area Chart: Events by Month
        createMonthAreaChart();
    }

    private void createLocationBarChart() {
        Map<String, Long> locationCounts = eventService.getEventCountByLocation();
        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
        barSeries.setName("Events by Location");

        locationCounts.forEach((location, count) ->
                barSeries.getData().add(new XYChart.Data<>(location, count))
        );

        barChart.setTitle("Events by Location");
        barChart.getData().add(barSeries);
    }

    private void createCategoryPieChart() {
        Map<Category, Long> categoryCounts = eventService.getEventCountByCategory();
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        categoryCounts.forEach((category, count) ->
                pieData.add(new PieChart.Data(
                        category != null ? category.getName() : "No Category",
                        count)
                ));

        pieChart.setTitle("Events by Category");
        pieChart.setData(pieData);
    }

    private void createMonthAreaChart() {
        Map<Integer, Long> monthCounts = eventService.getEventCountByMonth();
        XYChart.Series<String, Number> areaSeries = new XYChart.Series<>();
        areaSeries.setName("Events by Month");

        // Month names for display
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        for (int i = 1; i <= 12; i++) {
            long count = monthCounts.getOrDefault(i, 0L);
            areaSeries.getData().add(new XYChart.Data<>(monthNames[i-1], count));
        }

        areaChart.setTitle("Events Trend by Month");
        areaChart.getData().add(areaSeries);
    }
}