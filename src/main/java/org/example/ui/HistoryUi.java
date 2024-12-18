package org.example.ui;

import cn.hutool.core.date.DateUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;
import org.example.db.AppointHistoryBiz;
import org.example.model.Area;
import org.example.model.entity.AppointHistory;
import org.example.util.AreaUtil;
import org.example.util.ChangePhoneUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
public class HistoryUi {


    ChoiceBox<Pair<String, Area>> areaBox ;
    ChoiceBox<Pair<String, Area>> shopBox ;
    ChoiceBox<Pair<String, Integer>> statusBox ;
    ChoiceBox<Pair<String, Integer>> typeBox ;
    TextField phoneFiled ;
    TableView<AppointHistory> tableView ;
    Scene scene;

    public HistoryUi() {
        initAreaChoiceBox();
        initShopChoiceBox();
        initStatusBox();
        initTypeBox();
        initPhoneFiled();
        initTableView();
        initScene();
    }

    private void initStatusBox() {
        statusBox = new ChoiceBox<>();
        statusBox.getItems().add(new Pair<>("", null));
        Pair<String, Integer> defaultItem = new Pair<>("待使用", 1);
        statusBox.getItems().add(defaultItem);
        statusBox.getItems().add(new Pair<>("已取消", 2));
        statusBox.getItems().add(new Pair<>("已使用", 3));
        statusBox.setConverter(new StringConverter<Pair<String, Integer>>() {
            @Override
            public String toString(Pair<String, Integer> object) {
                return object.getKey();
            }
            @Override
            public Pair<String, Integer> fromString(String string) {return null;
            }}
        );
        statusBox.setValue(defaultItem);
    }
    private void initTypeBox() {
        typeBox = new ChoiceBox<>();
        typeBox.getItems().add(new Pair<>("", null));
        Pair<String, Integer> defaultItem = new Pair<>("默认", 1);
        typeBox.getItems().add(defaultItem);
        typeBox.getItems().add(new Pair<>("旧数据", 2));
        typeBox.getItems().add(new Pair<>("新数据", 3));
        typeBox.setConverter(new StringConverter<Pair<String, Integer>>() {
            @Override
            public String toString(Pair<String, Integer> object) {
                return object.getKey();
            }
            @Override
            public Pair<String, Integer> fromString(String string) {return null;
            }}
        );
        typeBox.setValue(defaultItem);
    }
    private void initPhoneFiled() {
        phoneFiled = new TextField();
    }

    private void initScene(){
        Label label = new Label("选择门店信息：");
        Button queryBtn = new Button("Query");
        HBox hbox1 = new HBox(
                label,
                areaBox,
                shopBox);
        HBox hbox2 = new HBox(
                new Label("状态："),
                statusBox,
                new Label("类型："),
                typeBox,
                new Label("手机号："),
                phoneFiled,
                queryBtn);
        hbox1.setSpacing( 10.0d );
        hbox2.setSpacing( 10.0d );
        VBox vbox = new VBox(hbox1, hbox2, tableView);
        hbox1.setAlignment(Pos.CENTER_LEFT );
        hbox2.setAlignment(Pos.CENTER_LEFT );
        hbox1.setPadding( new Insets(40,0,0,40) );
        hbox2.setPadding( new Insets(20,0,20,40) );


        queryBtn.setOnMouseClicked(event -> {
            refresh();
        });
        scene = new Scene(vbox);
    }

    public Scene getScene(){
        return scene;
    }

    private void refresh(){
        List<String> shopIds = Collections.emptyList();
        Pair<String, Area> value = shopBox.getValue();
        Pair<String, Area> areaBoxValue = areaBox.getValue();
        if (value != null && value.getValue() != null) {
            shopIds = Collections.singletonList(value.getValue().getRemark());
        }else if(areaBoxValue != null){
            shopIds = AreaUtil.getShops(areaBoxValue.getValue().getId()).stream().map(Area::getRemark).collect(Collectors.toList());
        }
        Pair<String, Integer> selectedItem = statusBox.getValue();
        Integer status = null;
        if (selectedItem != null) {
            status = selectedItem.getValue();
        }
        selectedItem = typeBox.getValue();
        Integer type = null;
        if (selectedItem != null) {
            type = selectedItem.getValue();
        }
        List<AppointHistory> appointHistories = AppointHistoryBiz.selectList(shopIds,
                phoneFiled.getText(),
                status,
                type);
        tableView.getItems().clear();
        tableView.getItems().addAll(appointHistories);
    }


    private void initTableView(){
        tableView = new TableView<>();
        TableColumn<AppointHistory, String> shopIdColumn = new TableColumn<>("门店Id");
        TableColumn<AppointHistory, String> shopNameColumn = new TableColumn<>("门店");
        TableColumn<AppointHistory, String> dateColumn = new TableColumn<>("日期");
        TableColumn<AppointHistory, String> phoneColumn = new TableColumn<>("手机号");
        TableColumn<AppointHistory, Integer> stateColumn = new TableColumn<>("状态");
        TableColumn<AppointHistory, Integer> typeColumn = new TableColumn<>("类型");
        TableColumn<AppointHistory, Date> createTimeColumn = new TableColumn<>("创建时间");
        shopIdColumn.setCellValueFactory(new PropertyValueFactory<>("shopId"));
        shopNameColumn.setCellValueFactory(new PropertyValueFactory<>("shopName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        stateColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        createTimeColumn.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        // 创建上下文菜单
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("EditPhone");
        // 为菜单项添加动作处理器
        editItem.setOnAction(event -> {
            changePhone();
        });


        // 将菜单项添加到上下文菜单中
        contextMenu.getItems().addAll(editItem);

        tableView.setOnContextMenuRequested(event -> {
            // 确保在选择模型中有选中的项
            if (tableView.getSelectionModel().getSelectedItem() != null) {
                // 显示上下文菜单
                contextMenu.show(tableView, event.getScreenX(), event.getScreenY());
            }
        });
        // 自定义单元格工厂
        stateColumn.setCellFactory(col -> new TableCell<AppointHistory, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                }else{
                    switch (item){
                        case 1:
                            setText("待使用");
                            break;
                        case 2:
                            setText("已取消");
                            break;
                        case 3:
                            setText("已使用");
                            break;
                        default:
                            setText("异常");
                            break;
                    }
                }
            }
        });
        // 自定义单元格工厂
        typeColumn.setCellFactory(col -> new TableCell<AppointHistory, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                }else{
                    switch (item){
                        case 1:
                            setText("默认");
                            break;
                        case 2:
                            setText("旧数据");
                            break;
                        case 3:
                            setText("新数据");
                            break;
                        default:
                            setText("异常");
                            break;
                    }
                }
            }
        });
        createTimeColumn.setCellFactory(col -> new TableCell<AppointHistory, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                }else {
                    setText(DateUtil.format(item, "yyyy-MM-dd HH:mm:ss"));
                }
            }
        });
        tableView.getColumns().addAll(
                shopIdColumn,
                shopNameColumn,
                dateColumn,
                phoneColumn
                , stateColumn
                , typeColumn
                , createTimeColumn
        );
        refresh();
    }

    private void initAreaChoiceBox(){
        areaBox = new ChoiceBox<>();
        areaBox.setPrefWidth(80);
        areaBox.setPadding( new Insets(0,40,0,0) );
        List<Pair<String, Area>> pairs = AreaUtil
                .getAreas()
                .stream()
                .map(t -> new Pair<>(t.getDictValue(),t))
                .collect(Collectors.toList());
        pairs.add(0, new Pair<>("", null));
        areaBox.getItems().addAll(pairs);
        areaBox.setConverter(new StringConverter<Pair<String,Area>>() {
            @Override
            public String toString(Pair<String, Area> pair) {
                return pair.getKey();
            }
            @Override
            public Pair<String, Area> fromString(String string) {return null;}
        });
        areaBox.setOnAction((e)->{
            if (areaBox.getValue() == null) {
                shopBox.getItems().clear();
            }else{
                shopBox.getItems().clear();
                List<Area> shops = AreaUtil.getShops(areaBox.getValue().getValue().getId());
                List<Pair<String, Area>> shopPairs = shops
                        .stream()
                        .map(t -> new Pair<>(t.getDictValue(),t))
                        .collect(Collectors.toList());
                shopPairs.add(0, new Pair<>("", null));
                shopBox.getItems().addAll(shopPairs);
            }

        });
    }

    private void changePhone(){
        // 在这里添加编辑逻辑
        AppointHistory selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("请输入手机号");
            dialog.setContentText(null);
            dialog.setHeaderText(null);
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                // 用户点击了确认按钮
                String newPhone = result.get();
                if (newPhone.length() < 10) {
                    final Alert alert2 = new Alert(Alert.AlertType.INFORMATION, "手机号格式错误", ButtonType.OK); // 实体化Alert对话框对象，并直接在建构子设置对话框的消息类型
                    alert2.setTitle("提示");
                    alert2.setHeaderText("");
                    alert2.showAndWait();
                }else{
                    try {
                        AppointHistory change = ChangePhoneUtils.change(selectedItem, newPhone);
                        selectedItem.setStatus(2);
                        selectedItem.setType(2);
                        change.setType(3);
                        AppointHistoryBiz.updateAptHist(selectedItem.getId(), 2,2);
                        AppointHistoryBiz.insert(change);
                        final Alert alert2 = new Alert(Alert.AlertType.INFORMATION,"操作成功", ButtonType.OK); // 实体化Alert对话框对象，并直接在建构子设置对话框的消息类型
                        alert2.setTitle("操作成功");
                        alert2.setHeaderText("");
                        alert2.showAndWait();
                        refresh();
                    }catch (Exception e){
                        final Alert alert2 = new Alert(Alert.AlertType.INFORMATION, e.getMessage(), ButtonType.OK); // 实体化Alert对话框对象，并直接在建构子设置对话框的消息类型
                        alert2.setTitle("切换手机号失败");
                        alert2.setHeaderText("");
                        alert2.showAndWait();
                    }

                }
            }
        }
    }
    private void initShopChoiceBox(){
        shopBox = new ChoiceBox<>();
        shopBox.setPrefWidth(120);
        shopBox.setConverter(new StringConverter<Pair<String,Area>>() {
            @Override
            public String toString(Pair<String, Area> pair) {
                return pair.getKey();
            }
            @Override
            public Pair<String, Area> fromString(String string) {return null;}
        });
    }
}
