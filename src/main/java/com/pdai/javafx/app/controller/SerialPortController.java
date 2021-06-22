package com.pdai.javafx.app.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author QIU
 * @create 2021-06-22
 */
@Component
public class SerialPortController extends BaseController implements Initializable {
    //动态图像结点
    @FXML
    public SwingNode swingNode;

    @FXML
    private AreaChart<String, Number> areaChart;
    //饼状图
    @FXML
    private PieChart pieChart;

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Sun", Math.random()),
                new PieChart.Data("IBM", Math.random()),
                new PieChart.Data("HP", Math.random()),
                new PieChart.Data("Dell", Math.random()),
                new PieChart.Data("Apple", Math.random())
        );
        pieChart.setData(pieChartData);
        pieChart.setClockwise(false);

//        XYChart.Series<String, Number> series = new XYChart.Series<>();
//        series.setName("测试柱状图数据");
//        series.getData().add(new XYChart.Data<>("0", 2D*Math.random()));
//        series.getData().add(new XYChart.Data<>("1", 8D*Math.random()));
//        series.getData().add(new XYChart.Data<>("2", 5D*Math.random()));
//        series.getData().add(new XYChart.Data<>("3", 3D*Math.random()));
//        series.getData().add(new XYChart.Data<>("4", 6D*Math.random()));
//        series.getData().add(new XYChart.Data<>("5", 8D*Math.random()));
//        series.getData().add(new XYChart.Data<>("6", 5D*Math.random()));
//        series.getData().add(new XYChart.Data<>("7", 6D*Math.random()));
//        series.getData().add(new XYChart.Data<>("8", 5D*Math.random()));
//
//        areaChart.getData().setAll(series);
//        areaChart.setCreateSymbols(true);
    }
}
