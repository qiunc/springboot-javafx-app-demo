package com.pdai.javafx.app.controller;

import com.jfoenix.controls.JFXButton;
import com.pdai.javafx.app.utils.DialogBuilder;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.SetChangeListener;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.*;

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
    public JFXButton openButton;
    @FXML
    public JFXButton closeButton;

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

    public void closeSerialPort(ActionEvent actionEvent) {
        Dialog<HashMap<String, String>> dialog = new Dialog<>();
        dialog.setTitle("参数设置");
        dialog.setHeaderText("请设置以下参数值");
        dialog.setHeight(500);
        dialog.setWidth(500);
// Set the icon (must be included in the project).
        dialog.setGraphic(new ImageView(this.getClass().getResource("/images/add.png").toString()));

// Set the button types.
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

// Create the username and password labels and fields.
        GridPane grid = new GridPane();
        //设置水平间距
        grid.setHgap(10);
        //设置垂直间距
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 10));

        //设置提示文字
        TextField ksp = new TextField();
        ksp.setPrefWidth(400);
        ksp.setPromptText("升速参数, 范围5~250, 默认220");
        TextField ksi = new TextField();
        ksi.setPromptText("升速参数 范围5~80, 默认20");
        TextField doorLockType = new TextField();
        doorLockType.setPromptText("0为电磁或牵引门锁，1为带反馈信号门锁 默认0");
        TextField DoorLockTime = new TextField();
        DoorLockTime.setPromptText("开门时门锁通电时间, 单位0.1s, 范围1~200, 默认2");

        grid.add(new Label("ksp:"), 0, 0);
        grid.add(ksp, 1, 0);
        grid.add(new Label("ksi:"), 0, 1);
        grid.add(ksi, 1, 1);
        grid.add(new Label("门锁类型"), 0, 2);
        grid.add(doorLockType, 1, 2);
        grid.add(new Label("开锁时间"), 0, 3);
        grid.add(DoorLockTime, 1, 3);

// Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
        ksp.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
        //强制输入
        Platform.runLater(() -> ksp.requestFocus());

// Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("ksp", ksp.getText());
                hashMap.put("ksi", ksi.getText());
                hashMap.put("doorLockType",doorLockType.getText());
                hashMap.put("doorLockTime", DoorLockTime.getText());
                return hashMap;
            }
            return null;
        });

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        // Add a custom icon.
        stage.getIcons().add(new Image(this.getClass().getResource("/images/add.png").toString()));

        Optional<HashMap<String, String>> result = dialog.showAndWait();

        result.ifPresent(setValue -> {
            System.out.println("ksp=" + setValue.get("ksp") + ", ksi=" + setValue.get("ksi")+", doorlocktype = "
            +setValue.get("doorLockType") + ", doorLockTime=" + setValue.get("doorLockTime"));
        });

    }

    public void openSerialPort(ActionEvent actionEvent) {
        HashMap<String, String> hashMap = new HashMap<>();

        new DialogBuilder(openButton)
                .setTitle("参数设置")
//                .setMessage("KSP")
//                .setTextFieldText("升速参数, 范围5~250, 默认220", new DialogBuilder.OnInputListener() {
//                    @Override
//                    public void onGetText(String result) {
//                        hashMap.put("KSP", result);
//                    }
//                }).setMessage("KSI")
//                .setTextFieldText("升速参数, 范围5~80, 默认20", new DialogBuilder.OnInputListener() {
//                    @Override
//                    public void onGetText(String result) {
//                        hashMap.put("KSi", result);
//                    }
//                }).setMessage("门锁类型")
//                .setTextFieldText("升速参数, 范围5~80, 默认20", new DialogBuilder.OnInputListener() {
//                    @Override
//                    public void onGetText(String result) {
//                        hashMap.put("1", result);
//                    }
//                })
                .setMessage("开锁时间")
                .setTextFieldText("升速参数, 范围5~80, 默认20", new DialogBuilder.OnInputListener() {
                    @Override
                    public void onGetText(String result) {
                        //System.out.println("12"+result);
                    }
                }).setPositiveBtn("确定").setNegativeBtn("取消").create();
//                .setPositiveBtn("确定", new DialogBuilder.OnClickListener() {
//                    @Override
//                    public void onClick() {
//                        System.out.println("KSP = " +hashMap.get("KSP")+ "   "+"KSI = "+hashMap.get("KSI")
//                        +"     " + "开锁类型 = "+ hashMap.get("1")+ "   " +"开锁时间" + hashMap.get("2"));
//                    }
//                }).setNegativeBtn("取消").create();
    }
}
