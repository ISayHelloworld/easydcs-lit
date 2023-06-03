package main.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import main.entity.FlightInfo;
import main.entity.FlightListEntity;

import java.util.HashSet;

public class ViewUtils {
    public static void setBorder(Pane node) {
        BorderStroke borderStroke = new BorderStroke(null, null, Color.BLACK, null, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT, new Insets(0));
        node.setBorder(new Border(borderStroke));
    }

    public static void clearBoard(Pane node) {
        node.setBorder(null);
    }

    public static void setFlightListView(ComboBox<FlightListEntity> flightQueryList, String res) {
        HashSet<String> flightNumberSet = new HashSet<>();
        Platform.runLater(() -> {
            JSONArray jsonArray = JSONArray.parseArray(res);
            flightQueryList.converterProperty().set(new StringConverter<FlightListEntity>() {
                @Override
                public String toString(FlightListEntity object) {
                    if (object == null) {
                        return null;
                    }
                    return object.getFlightWithStatus();
                }

                @Override
                public FlightListEntity fromString(String string) {
                    FlightListEntity entity = new FlightListEntity();
                    if (string.contains("(")) {
                        String flightNumber = string.split("\\(")[0];
                        entity.setFlightNumber(flightNumber);
                        entity.setFlightBoardStatus(Constants.FLIGHT_CLOSE_NUM);
                    } else {
                        entity.setFlightNumber(string);
                    }
                    return entity;
                }
            });
            ObservableList<FlightListEntity> comboBoxItems = FXCollections.observableArrayList();
            for (Object flightInfo : jsonArray) {
                FlightInfo tmp = JSONObject.parseObject(flightInfo.toString(), FlightInfo.class);
                if (tmp == null) {
                    ToastUtils.toast(ResourceUtils.getString("parseFailed"));
                    continue;
                }
                FlightListEntity listEntity = new FlightListEntity();
                listEntity.setFlightNumber(tmp.getFlightNumber());
                listEntity.setFlightBoardStatus(tmp.getCheckInStatus());
                if (!flightNumberSet.contains(listEntity.getFlightNumber())) {
                    flightNumberSet.add(listEntity.getFlightNumber());
                    comboBoxItems.add(listEntity);
                }
            }
            flightQueryList.setItems(comboBoxItems);
        });
    }

    public static void setTableEmpty(TableView tableView) {
        Label label = new Label(ResourceUtils.getString("emtpyData"));
        label.setStyle("-fx-text-fill:black;-fx-text-fill:gray;");
        tableView.setPlaceholder(label);
    }

    public static void setOutShadow(Node node) {
        if (StyleUtils.isDarkMode()) {
            node.setEffect(null);
            return;
        }
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(8);
        // 水平方向，0则向两侧，正则向右，负则向左
        dropShadow.setOffsetX(1);
        // 竖直方向，0则向两侧，正则向下，负则向上
        // 颜色变淡的程度
        dropShadow.setSpread(0.001);
        dropShadow.setColor(Color.rgb(220,220,220));
        node.setEffect(dropShadow);
    }

    public static void setInnerShadow(Node node) {
        if (StyleUtils.isDarkMode()) {
            node.setEffect(null);
            return;
        }
        InnerShadow dropShadow = new InnerShadow();
        dropShadow.setRadius(8);
        // 水平方向，0则向两侧，正则向右，负则向左
        // 竖直方向，0则向两侧，正则向下，负则向上
        dropShadow.setOffsetY(5);

        // 颜色变淡的程度
        dropShadow.setColor(Color.rgb(240,240,240));
        node.setEffect(dropShadow);
    }
}

