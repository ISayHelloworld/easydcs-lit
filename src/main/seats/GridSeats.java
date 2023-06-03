package main.seats;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import main.callback.SeatCallback;
import main.entity.FlightInfo;
import main.entity.ModelExtend;
import main.utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

// 一个cockpit加上所有标题
public class GridSeats extends GridPane {
    private static final int WIDTH = 30;

    private static final int HEIGHT = 25;

    private GridPane mSeatPane;

    private Cockpit cockpit;

    private HashMap<String, SeatButton> map = new HashMap<>();

    private String eta;

    private SeatCallback seatCallback;

    private int allSeatNumber = 0;

    private int seatXNumber = 0;

    private int seatAvNumber = 0;

    private int checkedNumber = 0;

    // textFiled:用于显示点击座位
    public void initSeats(String input, FlightInfo flightInfo, boolean isFirst, boolean isLast) {
        if (TextUtils.isEmpty(input)) {
            this.clear();
            return;
        }
        String[] usedSeats = null;
        if (flightInfo != null) {
            usedSeats = flightInfo.getUsedSeatsArr();
        }
        cockpit = new Cockpit();
        cockpit.setFirstPit(true);
        cockpit.setLastPit(true);
        cockpit.parseInput(input, isFirst, isLast);

        // 添加座位
        addSeat(cockpit, getHashSet(usedSeats));

        // 左右动态添加
        addLeftRightTitles(cockpit);

        // 顶部动态添加标题
        addTopTitles(cockpit);
    }

    private HashSet<String> getHashSet(String[] usedSeats) {
        HashSet<String> set = new HashSet<>();
        if (usedSeats == null) {
            return set;
        }
        set.addAll(Arrays.asList(usedSeats));
        return set;
    }

    private void addSeat(Cockpit cockpit, HashSet<String> usedSeats) {
        String[][] seatStr = cockpit.getSeatStr();
        mSeatPane = new GridPane();
        int[] colNumbers = cockpit.getCombineTitle2Str();
        String[] rowNames = cockpit.getCompleteRowTitles();
        for (int i = 0; i < seatStr.length; i++) {
            for (int j = 0; j < seatStr[0].length; j++) {
                String str = seatStr[i][j];
                if (str == null) {
                    str = "";
                }
                if (str.contains("=")) {
                    Label label = new Label(str);
                    label.setPrefHeight(HEIGHT);
                    label.setPrefWidth(WIDTH);
                    label.setAlignment(Pos.CENTER);
                    label.setStyle("-fx-text-fill:" + StyleUtils.getReverseColor());
                    mSeatPane.add(label, j, i);
                } else if (str.contains("&&")) {
                    // 不可见
                    Label label = new Label(str);
                    label.setPrefHeight(HEIGHT);
                    label.setPrefWidth(WIDTH);
                    label.setAlignment(Pos.CENTER);
                    label.setVisible(false);
                    mSeatPane.add(label, j, i);
                } else if (TextUtils.isEmpty(str)) {
                    // do Nothing
                } else {
                    int finalI = i;
                    int finalJ = j;
                    int colName = colNumbers[finalJ];
                    String rowName = rowNames[finalI];
                    Seat seat = new Seat();
                    if (usedSeats.contains(colName + rowName)) {
                        seat.setAvailable(false);
                    }
                    seat.setOnMouseClicked(event -> {
                        if (seatCallback != null) {
                            if (!seat.isAvailable()) {
                                ToastUtils.toast(ResourceUtils.getString("invalidSeat"));
                                return;
                            }
                            seatCallback.choose(colName + rowName);
                        }
                    });
                    mSeatPane.add(seat, j, i);
                }
            }
        }
        this.add(mSeatPane, 1, 1);
    }

    private void addLeftRightTitles(Cockpit cockpit) {
        String[] left = cockpit.getLeftColTitles();
        String[] right = cockpit.getRightColTitles();
        GridPane leftPane = new GridPane();
        for (int i = 0; i < left.length; i++) {
            Label title = new Label(left[i]);
            title.setPrefHeight(HEIGHT);
            title.setPrefWidth(WIDTH);
            title.setAlignment(Pos.CENTER);
            title.setStyle("-fx-text-fill:" + StyleUtils.getReverseColor());
            leftPane.add(title, 0, i);
        }
        GridPane rightPane = new GridPane();
        for (int i = 0; i < right.length; i++) {
            Label title = new Label(right[i]);
            title.setPrefHeight(HEIGHT);
            title.setPrefWidth(WIDTH);
            title.setAlignment(Pos.CENTER);
            title.setTextFill(Color.BLACK);
            title.setStyle("-fx-text-fill:" + StyleUtils.getReverseColor());
            rightPane.add(title, 0, i);
        }
        leftPane.setAlignment(Pos.CENTER);
        rightPane.setAlignment(Pos.CENTER);
        this.add(leftPane, 0, 1);
        this.add(rightPane, 2, 1);
    }

    private void addTopTitles(Cockpit cockpit) {
        String title1 = cockpit.getTitle1Str();
        int[] title2 = cockpit.getCombineTitle2Str();
        GridPane gridPane = new GridPane();
        // w
        Label text = new Label(title1);
        text.setPrefWidth(WIDTH);
        text.setAlignment(Pos.CENTER);
        text.setStyle("-fx-text-fill:" + StyleUtils.getReverseColor() + ";-fx-font-weight:bold;");
        gridPane.add(text, 0, 0);
        gridPane.setAlignment(Pos.CENTER);

        // 1,2,3
        for (int i = 0; i < title2.length; i++) {
            Label title;
            if (title2[i] == -1) {
                // 横向分仓空行
                title = new Label(" ");
            } else {
                title = new Label(String.valueOf(title2[i]));
            }
            title.setPrefHeight(HEIGHT);
            title.setPrefWidth(WIDTH);
            title.setAlignment(Pos.CENTER);
            title.setStyle("-fx-text-fill:" + StyleUtils.getReverseColor() + ";-fx-font-weight:bold;");
            gridPane.add(title, i, 1);
        }
        this.add(gridPane, 1, 0);
    }

    public GridSeats() {
        this.setHgap(2);
        this.setVgap(2);
    }

    public HashMap<String, SeatButton> getMap() {
        return map;
    }

    public void setExtend(ModelExtend modelExtend) {
        if (modelExtend == null) {
            return;
        }
        parseModelCXE(modelExtend, Constants.SEAT_EMERGENCY);
        parseModelCXE(modelExtend, Constants.SEAT_X);
        parseModelCXE(modelExtend, Constants.SEAT_SUB_PRIORITY);
    }

    private void parseModelCXE(ModelExtend extend, String type) {
        // “1A，2B，3C;”
        String model = extend.getModelByType(type);
        if (model == null || !TextUtils.isValidSeatArr(model)) {
            return;
        }
        model = model.replaceAll(";", "").trim();
        String[] arr = model.split(",");
        for (int i = 0; i < arr.length; i++) {
            // 1A,3B
            if (TextUtils.isEmpty(arr[i])) {
                continue;
            }
            String rowName = arr[i].trim().substring(arr[i].length() - 1, arr[i].length());
            int col = Integer.parseInt(arr[i].replaceAll(rowName, "").trim());
            int row = getRowFromName(rowName);
            int minCol = cockpit.getStartCols().get(0);
            int maxCol = cockpit.getEndCols().get(cockpit.getEndCols().size() - 1);
            if (col > maxCol || col < minCol || row < 0 || row >= cockpit.getCompleteRowTitles().length) {
                continue;
            }

            int colIndex = findRealColIndex(minCol, col, cockpit.getXSplit());
            Node node = getNodeFromGridPane(mSeatPane, colIndex, row);
            if (node instanceof Seat) {
                Seat seat = (Seat) node;
                seat.setType(type);
            }
        }
    }

    private int findRealColIndex(int minCol, int colNumber, ArrayList<Integer> xSplits) {
        int spaceNum = 0;
        for (Integer split: xSplits) {
            if (split < colNumber) {
                spaceNum++;
            } else {
                break;
            }
        }
        int colIndex = colNumber - minCol + spaceNum;
        return colIndex;
    }

    private int getRowFromName(String name) {
        int i = 0;
        for (String rowName : cockpit.getCompleteRowTitles()) {
            if (TextUtils.equals(rowName, name)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private void parseModelE(ModelExtend modelExtend) {
        String modelE = modelExtend.getModelE();
        if (TextUtils.isEmpty(modelE)) {
            return;
        }
        if (TextUtils.isValidColArr(modelE)) {
            refreshByCol(modelE);
        }
    }

    private void refreshByCol(String modelE) {
        modelE = modelE.replaceAll(";", "").trim();
        String[] arr = modelE.split(",");
        for (int i = 0; i < arr.length; i++) {
            Integer col = Integer.parseInt(arr[i].trim());
            int minCol = cockpit.getStartCols().get(0);
            int maxCol = cockpit.getEndCols().get(cockpit.getEndCols().size() - 1);
            if (col > maxCol || col < minCol) {
                continue;
            }

            // 找到数组列index
            int colIndex = col - minCol;
            for (int row = 0; row < mSeatPane.impl_getRowCount(); row++) {
                Node node = getNodeFromGridPane(mSeatPane, colIndex, row);
                if (node instanceof Seat) {
                    Seat seat = (Seat) node;
                    seat.setType(Constants.SEAT_SUB_PRIORITY);
                } else if (node instanceof Label) {
                    Label pathWay = (Label) node;
                    pathWay.setText("E");
                }
            }
        }
    }

    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    public void clear() {
        this.getChildren().clear();
        map.clear();
    }

    public Cockpit getCockpit() {
        return cockpit;
    }

    public void setSeatCallback(SeatCallback callback) {
        seatCallback = callback;
    }

    public int getAllSeatNumber() {
        return allSeatNumber;
    }

    public int getAvsNumber() {
        return seatAvNumber;
    }

    public int getCheckedNumber() {
        return checkedNumber;
    }

    public int getSeatXNumber() {
        return seatXNumber;
    }

    public void calculateNums() {
        for (int row = 0; row < mSeatPane.impl_getRowCount(); row++) {
            for (int col = 0; col < mSeatPane.impl_getColumnCount(); col++) {
                Node node = getNodeFromGridPane(mSeatPane, col, row);
                if (node instanceof Seat) {
                    Seat seat = (Seat) node;
                    if (TextUtils.equals(seat.getType(), Constants.SEAT_X)) {
                        seatXNumber++;
                    }
                    if (seat.isUsed()) {
                        checkedNumber++;
                    }
                    allSeatNumber++;
                }
            }
        }
        seatAvNumber = allSeatNumber - seatXNumber;
    }
}
