package org.example.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.example.db.ConfigBiz;
import org.example.model.entity.Config;

import java.util.List;

@Slf4j
public class ConfigUi extends Application {


    TableView<Config> tableView ;


    public ConfigUi() {
        initTableView();
    }


    private void initTableView(){
        tableView = new TableView<>();
        TableColumn<Config, Integer> idColumn = new TableColumn<>("id");
        TableColumn<Config, String> areaNameColumn = new TableColumn<>("区域");
        TableColumn<Config, String> shopIdColumn = new TableColumn<>("门店Id");
        TableColumn<Config, String> shopNameColumn = new TableColumn<>("门店");
        TableColumn<Config, Integer> maxCountPerDayColumn = new TableColumn<>("每日数量");
        TableColumn<Config, String> remarkColumn = new TableColumn<>("备注");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        areaNameColumn.setCellValueFactory(new PropertyValueFactory<>("areaName"));
        shopIdColumn.setCellValueFactory(new PropertyValueFactory<>("shopId"));
        shopNameColumn.setCellValueFactory(new PropertyValueFactory<>("shopName"));
        maxCountPerDayColumn.setCellValueFactory(new PropertyValueFactory<>("maxCountPerDay"));
        remarkColumn.setCellValueFactory(new PropertyValueFactory<>("remark"));
        // 创建上下文菜单
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("Edit");
        MenuItem newItem = new MenuItem("New");
        // 为菜单项添加动作处理器
        editItem.setOnAction(event -> {
            onEditConfig();
        });
        newItem.setOnAction(event -> {
            onNewConfig();
        });
        // 将菜单项添加到上下文菜单中
        contextMenu.getItems().addAll(newItem,editItem);
        tableView.setOnContextMenuRequested(event -> {
            // 确保在选择模型中有选中的项
            if (tableView.getSelectionModel().getSelectedItem() != null) {
                //菜单
                contextMenu.show(tableView, event.getScreenX(), event.getScreenY());
            }
        });
        tableView.getColumns().addAll(
                idColumn,
                areaNameColumn,
                shopIdColumn,
                shopNameColumn
                , maxCountPerDayColumn
                , remarkColumn
        );
        refresh();
    }

    @Override
    public void start(Stage primaryStage) {
        VBox vbox = new VBox(tableView);
        Scene scene = new Scene(vbox);
        primaryStage.setTitle("设置");
        primaryStage.setScene( scene );
        primaryStage.show();
    }
    private void refresh(){
        tableView.getItems().clear();
        List<Config> Configs = ConfigBiz.selectList();
        tableView.getItems().addAll(Configs);
    }
    public static void main(String[] args) {
        launch(args);
    }

    private void onNewConfig(){
        Config config = new Config();
        ConfigDialog dialog = new ConfigDialog(config);
        dialog.setResultConverter(bt -> {
            if(bt.getButtonData() == ButtonBar.ButtonData.OK_DONE ) {
                ConfigBiz.insert(config);
            }
            return config;
        });
        dialog.showAndWait();
    }

    private void onEditConfig(){
        Config config = tableView.getSelectionModel().getSelectedItem();
        ConfigDialog dialog = new ConfigDialog(config);
        dialog.setResultConverter(bt -> {
            if(bt.getButtonData() == ButtonBar.ButtonData.OK_DONE ) {
               ConfigBiz.update(config);
            }
            return config;
        });
        dialog.showAndWait();
    }


}
