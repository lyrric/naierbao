package org.example.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import javafx.util.StringConverter;
import org.example.model.Area;
import org.example.model.entity.Config;
import org.example.util.AreaUtil;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class ConfigDialog extends Dialog<Config> {

    ChoiceBox<Pair<String, Area>> areaBox ;
    ChoiceBox<Pair<String, Area>> shopBox ;
    TextField phoneFiled ;
    TextField sptsFiled ;
    TextArea remarkArea;
    private Config config;


    public ConfigDialog(Config config) {
        this.config = config;
        initAreaChoiceBox(config);
        initShopChoiceBox(config);
        initTextField(config);
        VBox vbox = new VBox(
                new Label("门店"),
                new HBox(areaBox,shopBox),
                new Label("手机号码"), phoneFiled,
                new Label("spts"), sptsFiled,
                new Label("备注"), remarkArea
        );

        vbox.setSpacing( 10.0d );
        vbox.setPadding( new Insets(40.0d) );
        DialogPane dp = getDialogPane();
        setTitle( "编辑/新增" );

        ButtonType bt = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dp.getButtonTypes().addAll( bt, ButtonType.CANCEL );
        dp.setContent( vbox );
        setResultConverter( this::formResult );
    }

    private void initTextField(Config config) {
        phoneFiled = new TextField();
        sptsFiled = new TextField();
        remarkArea = new TextArea();
        phoneFiled.setText( config.getPhone());
        sptsFiled.setText( config.getSpts());
        remarkArea.setText( config.getRemark());
    }


    private void initAreaChoiceBox(Config config){
        areaBox = new ChoiceBox<>();
        areaBox.setPrefWidth(80);
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
        pairs.stream()
                .filter(t -> Objects.equals(t.getKey(), config.getAreaName()))
                .findFirst()
                .ifPresent(p->areaBox.setValue(p));

        areaBox.setOnAction((e)->{
            if (areaBox.getValue() == null) {
                shopBox.getItems().clear();
            }else{
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
    private void initShopChoiceBox(Config config){
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
        Pair<String, Area> areaPair = areaBox.getSelectionModel().getSelectedItem();
        if (areaPair != null) {
            List<Area> shops = AreaUtil.getShops(areaPair.getValue().getId());
            List<Pair<String, Area>> shopPairs = shops
                    .stream()
                    .map(t -> new Pair<>(t.getDictValue(),t))
                    .collect(Collectors.toList());
            shopPairs.add(0, new Pair<>("", null));
            shopBox.getItems().addAll(shopPairs);
            shopPairs.stream().filter(t -> t.getValue() != null && t.getValue().getRemark().equals(config.getShopId())).findFirst()
                    .ifPresent(t -> {
                        shopBox.setValue((t));
                    });
        }
    }

    private Config formResult(ButtonType bt) {
        if( bt.getButtonData() == ButtonBar.ButtonData.OK_DONE ) {
            config.setAreaName(areaBox.getValue().getValue().getDictValue());
            config.setShopId(shopBox.getValue().getValue().getRemark());
            config.setShopName(shopBox.getValue().getValue().getDictValue());
            config.setRemark(remarkArea.getText());
            config.setSpts(sptsFiled.getText());
            return config;
        }
        return null;
    }


}
