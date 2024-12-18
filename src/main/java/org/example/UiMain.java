package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.example.ui.HistoryUi;
import org.example.ui.ConfigUi;

@Slf4j
public class UiMain extends Application {

    private App app;
    Label statusLabel;
    @Override
    public void start(Stage primaryStage){
        Button start = new Button("停止");
        start.setPadding( new Insets(20,40,20,20) );
        start.setOnMouseClicked(event -> {
            if (onStart()) {
                start.setText("停止");
            }else{
                start.setText("开始");
            }
        });
        Button config = new Button("配置");
        config.setPadding( new Insets(20,40,20,20) );
        config.setOnMouseClicked(event -> {
            showConfig();
        });
        Button history = new Button("记录");
        history.setPadding( new Insets(20,40,20,20) );
        history.setOnMouseClicked(event -> showHistory());
        HBox hBox = new HBox(start, config, history);
        hBox.setSpacing(40);
        hBox.setPadding(new Insets(0,20,20,20));
        hBox.setAlignment(javafx.geometry.Pos.CENTER);
        statusLabel = new Label("未开始");
        statusLabel.setPadding( new Insets(20,0,10,20));
        VBox vBox = new VBox(statusLabel, hBox);
        Scene scene = new Scene(vBox);
        primaryStage.setTitle("Main");
        primaryStage.setScene( scene );
        primaryStage.show();
        app = new App(statusLabel);
        onStart();
        primaryStage.setOnCloseRequest(event ->   System.exit(0));
    }

    private void showConfig(){
        // 创建一个新的Stage
        Stage newStage = new Stage();
        newStage.setTitle("配置");
        Scene scene = new ConfigUi().getScene();
        newStage.setScene(scene);
        // 显示新的Stage，不会关闭主Stage
        newStage.show();
    }

    private void showHistory(){
        // 创建一个新的Stage
        Stage newStage = new Stage();
        newStage.setTitle("预约记录");
        Scene scene = new HistoryUi().getScene();
        newStage.setScene(scene);
        // 显示新的Stage，不会关闭主Stage
        newStage.show();
    }

    private boolean onStart(){
        if (app.getStatus()) {
            app.stop();
            statusLabel.setText("未开始");
        }else{
            app.start();
        }
        return app.getStatus();

    }


    public static void show() {
        launch();
    }

    public void setStatusLabel(String text) {
        statusLabel.setText(text);
    }
}