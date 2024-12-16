package org.example.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.example.db.DBUtil;
import org.example.model.Config;

import java.util.Optional;

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
        TableColumn<Config, String> xianyuColumn = new TableColumn<>("闲鱼昵称");
        TableColumn<Config, String> phoneColumn = new TableColumn<>("手机号");
        TableColumn<Config, String> sptsColumn = new TableColumn<>("spts");
        TableColumn<Config, Integer> maxCountPerDayColumn = new TableColumn<>("每日数量");
        TableColumn<Config, String> remarkColumn = new TableColumn<>("备注");

        // 创建上下文菜单
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("Edit");
        MenuItem newItem = new MenuItem("New");
        // 为菜单项添加动作处理器
        editItem.setOnAction(event -> {
            //onEditConfig();
        });
        newItem.setOnAction(event -> {
            //onNewConfig();
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
                , xianyuColumn
                , phoneColumn
                , sptsColumn
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
        tableView.getItems().addAll(DBUtil.getConfigs());
    }
    public static void main(String[] args) {
        launch(args);
    }

    private void onNewConfig(){
        ConfigDialog dialog = new ConfigDialog(new Config());
        Optional<Config> config = dialog.showAndWait();
        config.ifPresent(c->{
           /* DBUtil.inser(c);
            refresh();*/
        });
    }


}
