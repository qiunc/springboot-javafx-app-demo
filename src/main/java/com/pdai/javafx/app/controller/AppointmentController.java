package com.pdai.javafx.app.controller;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.pdai.javafx.app.serialException.*;
import com.pdai.javafx.app.utils.JHex;
import com.pdai.javafx.app.utils.SerialTool;
import com.pdai.javafx.app.utils.TemperatureChart;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.ResourceBundle;

@Component
public class AppointmentController implements Initializable {
    @FXML
    public AnchorPane rootAnchorPane;
    @FXML
    private ImageView image1;
    @FXML
    private JFXComboBox<Object> bpsComboBox;
    @FXML
    private JFXComboBox<String> serialPortComboBox;
    @FXML
    private JFXButton openButton;
    @FXML
    private Label tmpLabel;
    @FXML
    private Label speedLabel;
    @FXML
    private SwingNode swingNode;
    @FXML
    private SwingNode spedSwingNode;
    // 保存可用端口号
    private List<String> commList;

    private boolean lineChartFlag = false;

    // 保存串口对象
    private SerialPort serialPort = null;
    //实例化图像
    TemperatureChart temperatureChart;
    // speedRealTimeChart = new SpeedChart("Speed", "Data Chart", "Speed(rpm/min)");


    /**
     * 初始化
     * 开启串口监听线程
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        commList = SerialTool.findPort();
//        if (commList.size() < 1) {
//            JOptionPane.showMessageDialog(null, "没有搜索到有效串口！", "错误", JOptionPane.INFORMATION_MESSAGE);
//        } else
//            JOptionPane.showMessageDialog(null, "已检测到有效串口", "提示",
//                    JOptionPane.INFORMATION_MESSAGE);


//        alert.titleProperty().set("信息");
//        alert.headerTextProperty().set("请先打开一个word文档再点编辑按钮");
//        alert.showAndWait();
        temperatureChart = new TemperatureChart("Temperature", "Speed", "Data Chart", "Temperature (℃)");

        new Thread(new SerialPortThread()).start();
    }

    /**
     * 利用javaFX自带的图库生成折线图
     *
     * @param lineChart  折线图
     * @param chartQueue 存储折线图的队列
     * @param lineName   图线名
     * @param xAxisName  x轴名
     * @param yAxisName  Y轴名
     * @return 返回折线图
     */
    public LineChart drawLineChart(LineChart lineChart, Queue<Double> chartQueue, String lineName, String xAxisName, String yAxisName) {

        final int[] t = {0};
        int seriesNum = 1;
        lineChart.getXAxis().setLabel(xAxisName);
        lineChart.getYAxis().setLabel(yAxisName);
        //创建列表存储曲线
        ObservableList<XYChart.Series<String, Double>> answer = FXCollections.observableArrayList();

        EventHandler<ActionEvent> eventHandler = e -> {

            //如果曲线的条数少于id的条数，则需要创建一条曲线到列表中
            if (answer.size() < seriesNum) {
                XYChart.Series<String, Double> series = new XYChart.Series<>();
                answer.addAll(series);
                lineChart.getData().addAll(series);

            }
            // 遍历列表，给曲线赋值
            if (!chartQueue.isEmpty()) {
                answer.get(0).getData().add(new XYChart.Data(Integer.toString(t[0]), chartQueue.poll()));
                answer.get(0).setName(lineName);
                answer.get(0).getNode().lookup(".chart-series-line").setStyle("-fx-stroke: blue;");
                t[0]++;
            }

        };

        Timeline animation = new Timeline(new KeyFrame(Duration.millis(1000), eventHandler));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.setAutoReverse(true);
        animation.play();
        lineChart.setVisible(true);
        return lineChart;
    }

    /**
     * 打开串口开关对应的事件监听方法
     * 在开关中启动画图线程
     * 正确打开时给串口添加串口监听器
     */
    public void openSerialButton() throws SerialPortParameterFailure, PortInUse, NotASerialPort, NoSuchPort, TooManyListeners {

        if (!lineChartFlag) {
            swingNode.setContent(temperatureChart);
            swingNode.requestFocus();
            swingNode.autosize();
            new Thread(temperatureChart).start();
            lineChartFlag = true;
        }
        // 获取串口名称
        String commName = String.valueOf(serialPortComboBox.getValue());
        // 获取波特率
        String bpsStr = String.valueOf(bpsComboBox.getValue());

        // 检查串口名称是否获取正确
        boolean flag = false;
        for (String s : commList) {
            if (s.equals(commName)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            JOptionPane.showMessageDialog(null, "当前串口不可用！", "错误", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // 检查波特率是否获取正确
            if (bpsStr == null || bpsStr.equals("")) {
                JOptionPane.showMessageDialog(null, "波特率获取错误！", "错误", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // 串口名、波特率均获取正确时
                int bps = Integer.parseInt(bpsStr);

                // 获取指定端口名及波特率的串口对象
                serialPort = SerialTool.openPort(commName, bps);
                // 在该串口对象上添加监听器
                SerialTool.addListener(serialPort, new SerialListener());
                // 监听成功进行提示
                JOptionPane.showMessageDialog(null, "监听成功，稍后将显示监测数据！", "提示",
                        JOptionPane.INFORMATION_MESSAGE);

            }
        }
    }

    /**
     * 开关线程实现的runnable接口
     * 扫描与管理可用串口
     */
    private class SerialPortThread implements Runnable {
        public void run() {
            // 扫描可用串口
            commList = SerialTool.findPort();
            if (commList.size() > 0) {

                // 添加新扫描到的可用串口
                for (String s : commList) {

                    // 该串口名是否已存在，初始默认为不存在（在commList里存在但在commChoice里不存在，则新添加）
                    boolean commExist = false;

                    for (int i = 0; i < serialPortComboBox.getItems().size(); i++) {
                        if (s.equals(serialPortComboBox.getItems().get(i))) {
                            // 当前扫描到的串口名已经在初始扫描时存在
                            commExist = true;
                            break;
                        }
                    }

                    if (commExist) {
                        // 当前扫描到的串口名已经在初始扫描时存在，直接进入下一次循环
                    } else {
                        // 若不存在则添加新串口名至可用串口下拉列表
                        serialPortComboBox.getItems().add(s);
                    }
                }

                // 移除已经不可用的串口
                for (int i = 0; i < serialPortComboBox.getItems().size(); i++) {

                    // 该串口是否已失效，初始默认为已经失效（在commChoice里存在但在commList里不存在，则已经失效）
                    boolean commNotExist = true;

                    for (String s : commList) {
                        if (s.equals(serialPortComboBox.getItems().get(i))) {
                            commNotExist = false;
                            break;
                        }
                    }

                    // System.out.println("remove" + commChoice.getItem(i));
                    if (commNotExist) serialPortComboBox.getItems().remove(i);
                }

            } else {
                // 如果扫描到的commList为空，则移除所有已有串口
                serialPortComboBox.getItems().clear();
            }

        }
    }

    /**
     * 串口监听类
     * 实现了串口的事件监听
     * 获取数据后进行数据处理
     */
    public class SerialListener implements SerialPortEventListener {

        @Override
        /*
         * 处理监控到的串口事件
         */
        public void serialEvent(SerialPortEvent serialPortEvent) {

            switch (serialPortEvent.getEventType()) {

                case SerialPortEvent.BI: // 10 通讯中断
                    JOptionPane.showMessageDialog(null, "与串口设备通讯中断", "错误", JOptionPane.INFORMATION_MESSAGE);
                    break;

                case SerialPortEvent.OE: // 7 溢位（溢出）错误

                case SerialPortEvent.FE: // 9 帧错误

                case SerialPortEvent.PE: // 8 奇偶校验错误

                case SerialPortEvent.CD: // 6 载波检测

                case SerialPortEvent.CTS: // 3 清除待发送数据

                case SerialPortEvent.DSR: // 4 待发送数据准备好了

                case SerialPortEvent.RI: // 5 振铃指示

                case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 2 输出缓冲区已清空
                    break;

                case SerialPortEvent.DATA_AVAILABLE: // 1 串口存在可用数据

                    try {
                        if (serialPort == null) {
                            JOptionPane.showMessageDialog(null, "串口对象为空！监听失败！", "错误", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            // 读取数据，存入字节数组
                            byte[] data = SerialTool.readFromPort(serialPort);
                            //将字节数组数据转换位为保存了原始数据的字符串
                            String receiveData = JHex.encodeString(data);
                            System.out.println("第一次"+receiveData);
                            // 自定义解析过程
                            // 检查数据是否读取正确
                            while (receiveData.length() < 14) {
//                                JOptionPane.showMessageDialog(null, "读取数据过程中未获取到有效数据！请检查设备或程序！", "错误",
//                                        JOptionPane.INFORMATION_MESSAGE);
//                                System.exit(0);
                                data = SerialTool.readFromPort(serialPort);
                                receiveData += JHex.encodeString(data);
                                System.out.println("后续" + receiveData);
                            }
                            {

                                String dataOriginal = receiveData;
                                receiveData = null;
                                        //截取转速
                                //System.out.println("原始数据: " + dataOriginal);
                                String[] dataArr = new String[6];
                                dataArr[0] = dataOriginal.substring(0, 2);
                                dataArr[1] = dataOriginal.substring(2, 4);
                                dataArr[2] = dataOriginal.substring(4, 6);
                                dataArr[3] = dataOriginal.substring(6, 8);
                                dataArr[4] = dataOriginal.substring(8, 10);
                                dataArr[5] = dataOriginal.substring(10, 12);
                                String resData = dataOriginal.substring(12, 14);
                                String srcData = "";
                                for (int i = 0; i < dataArr.length - 1; i++) {
                                    if (i == 0) {
                                        srcData = JHex.xor(dataArr[i], dataArr[i + 1]);
                                        // System.out.println("第一个数据："+dataArr[i]+"第二个数据"+dataArr[i+ 1]);
                                    } else {
                                        srcData = JHex.xor(srcData, dataArr[i + 1]);
                                        //  System.out.println("第一个数据："+resData+"第二个数据"+dataArr[i+ 1]);
                                    }
                                    //System.out.println("异或数据"+srcData);
                                }
                                //System.out.println("异或数据"+srcData);
                                //System.out.println("校验数据: " + resData);
                                if (srcData.length() <= 1)
                                    srcData = "0" + srcData;


                                if (srcData.toUpperCase().equals(resData)) {
                                    String dataValid = dataArr[0] + dataArr[1];
                                    String tmpData = dataArr[2] + dataArr[3];
                                    //16进制字符串转换为double数据
                                    int LastSpeed = Integer.parseInt(new BigInteger(dataValid, 16).toString());
                                    double speedQueueNum = Double.parseDouble(new BigInteger(dataValid, 16).toString());
                                    //转速加入转速队列
                                    TemperatureChart.SpeedQueue.offer(speedQueueNum);
                                    //截取温度
                                    int LastTmp = Integer.parseInt(new BigInteger(tmpData, 16).toString());
                                    int integerLastTmp = LastTmp / 10;
                                    int decimalsLastTmp = LastTmp % 10;

                                    double queueNum = Double.parseDouble(integerLastTmp + "." + decimalsLastTmp);
                                    //存入温度数据队列
                                    TemperatureChart.TemperatureQueue.offer(queueNum);

                                    //开启画温度曲线的线程
//                                    if (!tmpLineChartFlag) {
//                                        new Thread() {
//                                            public void run() {
//                                                Platform.runLater(() -> {
//                                                    lineChartPane.getChildren().add(drawLineChart(tempLineChart, tmpQueue, "Temperature", "Time(s)", "Temperature(℃)"));
//                                                });
//                                            }
//                                        }.start();
//                                        tmpLineChartFlag = true;
//                                    }
                                    try {

                                        Platform.runLater(() -> {
                                            tmpLabel.setText(integerLastTmp + "." + decimalsLastTmp + " ℃");
                                            speedLabel.setText(LastSpeed + " rpm/min");
                                        });
                                        //获取时间格式
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");// 格式化时间
                                        // 获取当前时间
                                        Date date = new Date();
                                        System.out.println("现在时间：" + sdf.format(date));
                                        //数据写入txt文件
                                        Writer writer = new FileWriter("src/main/resources/file/tmp.txt", true);
                                        writer.write(sdf.format(date) + "温度：" + integerLastTmp + "." + decimalsLastTmp +
                                                " ℃" + "  " + "转速：" + LastSpeed + " rpm/min" + "\r\n");
                                        writer.close();
                                    } catch (ArrayIndexOutOfBoundsException e) {
                                        JOptionPane.showMessageDialog(null, "数据解析过程出错，更新界面数据失败！请检查设备或程序！", "错误",
                                                JOptionPane.INFORMATION_MESSAGE);
                                        System.exit(0);
                                    }
                                }
                            }
                        }

                    } catch (IOException | ReadDataFromSerialPortFailure | SerialPortInputStreamCloseFailure e) {
                        JOptionPane.showMessageDialog(null, e, "错误", JOptionPane.INFORMATION_MESSAGE);
                        // 发生读取错误时显示错误信息后退出系统
                        System.exit(0);
                    }

                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + serialPortEvent.getEventType());
            }
        }

    }

}
