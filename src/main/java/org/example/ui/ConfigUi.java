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
import java.util.Optional;

@Slf4j
public class ConfigUi {


    TableView<Config> tableView ;

    Scene scene;


    public ConfigUi() {
        initTableView();
        initScene();
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
        MenuItem delItem = new MenuItem("Del");
        // 为菜单项添加动作处理器
        editItem.setOnAction(event -> {
            onEditConfig();
        });
        newItem.setOnAction(event -> {
            onNewConfig();
        });
        delItem.setOnAction(event -> {
            onDelConfig();
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

    private void onDelConfig() {
        Config config = tableView.getSelectionModel().getSelectedItem();
        ConfigBiz.deleteById(config.getId());
        refresh();
        final Alert alert = new Alert(Alert.AlertType.INFORMATION, "操作成功", ButtonType.OK);
        alert.setTitle("提示");
        alert.setHeaderText("");
        alert.showAndWait();
    }

    public void initScene() {
        VBox vbox = new VBox(tableView);
        scene = new Scene(vbox);
    }
    private void refresh(){
        tableView.getItems().clear();
        List<Config> Configs = ConfigBiz.selectList();
        tableView.getItems().addAll(Configs);
    }
    public Scene getScene(){
        return scene;
    }

    private void onNewConfig(){
        Config config = new Config();
        ConfigDialog dialog = new ConfigDialog(config);
        Optional<Config> optional = dialog.showAndWait();
        optional.ifPresent(c -> {
            ConfigBiz.insert(config);
            refresh();
            final Alert alert = new Alert(Alert.AlertType.INFORMATION, "操作成功", ButtonType.OK);
            alert.setTitle("提示");
            alert.setHeaderText("");
            alert.showAndWait();
        });
    }

    private void onEditConfig(){
        Config config = tableView.getSelectionModel().getSelectedItem();
        ConfigDialog dialog = new ConfigDialog(config);
        Optional<Config> optional = dialog.showAndWait();
        optional.ifPresent(c -> {
            ConfigBiz.update(config);
            refresh();
            final Alert alert = new Alert(Alert.AlertType.INFORMATION, "操作成功", ButtonType.OK);
            alert.setTitle("提示");
            alert.setHeaderText("");
            alert.showAndWait();
        });
    }


}
