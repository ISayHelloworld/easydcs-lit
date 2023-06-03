package main.page;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import main.callback.CommonCallback;
import main.entity.*;
import main.manager.AccountManager;
import main.parser.DataInput;
import main.seats.GridSeats;
import main.utils.*;
import main.view.DateTimePicker;
import main.view.FlightBoard;
import main.view.FlightBoardChooseWindow;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ControlPage extends BasePageNode {
    private Button mUploadFlightInfoBtn;

    private FlightInfo mFlightInfo;

    @FXML
    private HBox typeSetting;

    @FXML
    private FlightBoard flightBoard;

    private ComboBox<String> mFlightModelList;

    private ComboBox<FlightListEntity> mFlightInfoList;

    private HashMap<String, FlightModel> modelMap = new HashMap<>();

    private HBox mSeatContainer;

    private Button mQueryBtn;

    private ComboBox<String> mCheckingStatus;

    private Button mSaveAll;

    private Button mSaveSeat;
    private TextField mAirplaneNumber;

    private TableView logTable;

    @FXML
    private TextField boardingHour;

    @FXML
    private TextField boardingMin;

    @FXML
    private Button controlLogBtn;

    private String boardingTime;

    @FXML
    private TextField remark;

    private TextField mBoardingGate;
    private DateTimePicker mTakeOffTime;
    private DateTimePicker mLandTime;

    private ComboBox<String> mSeatType;

    private TextField mSeatInput;

    private ArrayList<GridSeats> gridSeatsList;

    private boolean canChange = true;

    private CommonCallback mUploadCallback = new CommonCallback() {
        @Override
        public void success(String res) {
            Platform.runLater(() -> {
                if (res == null) {
                    return;
                }
                try {
                    if (Integer.parseInt(res) >= 0) {
                        ToastUtils.toast(ResourceUtils.getString("saveSuccess"));
                        initFlightList();
                        refreshSeatByModel(mFlightInfo.getCustomModelObj());
                    } else {
                        ToastUtils.toast(ResourceUtils.getString("saveFailed2") + res);
                    }
                } catch (NumberFormatException ex) {
                    ToastUtils.toast(ResourceUtils.getString("saveFailed2") + ex.getMessage());
                    ex.printStackTrace();
                }
            });
        }

        @Override
        public void fail(String fail) {
            Platform.runLater(() -> {
                ToastUtils.toast(ResourceUtils.getString("saveFailed2") + fail);
            });
        }
    };

    @Override
    String getFxmLPath() {
        setId("tab_control");
        return "controlPage";
    }

    @Override
    void initView() {
        gridSeatsList = new ArrayList<>();
        mUploadFlightInfoBtn = (Button) mRoot.lookup("#textInput");
        mFlightModelList = (ComboBox) mRoot.lookup("#queryModel");
        mFlightInfoList = (ComboBox) mRoot.lookup("#queryFlight");
        mSeatContainer = (HBox) mRoot.lookup("#seats");
        mQueryBtn = (Button) mRoot.lookup("#query");
        mCheckingStatus = (ComboBox) mRoot.lookup("#checkingStatus");
        mSaveAll = (Button) mRoot.lookup("#saveAll");
        mSaveSeat = (Button) mRoot.lookup("#saveSeat");
        mAirplaneNumber = (TextField) mRoot.lookup("#airplaneNumber");
        mBoardingGate = (TextField) mRoot.lookup("#boardingGate");
        mTakeOffTime = (DateTimePicker) mRoot.lookup("#takeOffTime");
        mLandTime = (DateTimePicker) mRoot.lookup("#landTime");
        mSeatType = (ComboBox<String>) mRoot.lookup("#type");
        mSeatInput = (TextField) mRoot.lookup("#seatInput");
        mQueryBtn.setOnAction(event -> {
            queryFromCloud();
        });
        mSaveAll.setOnAction(view -> {
            if (flightBoard.getFlightInfo() == null) {
                ToastUtils.toast(ResourceUtils.getString("plsChooseFlight"));
                return;
            }
            mFlightInfo = flightBoard.getFlightInfo();
            int status = mCheckingStatus.getSelectionModel().getSelectedIndex() + 1;
            if (canNotChangeState(status)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText(ResourceUtils.getString("checkingTip"));
                alert.show();
                return;
            }
            if (status == Constants.FLIGHT_CLOSE_NUM) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText(ResourceUtils.getString("closeWarning"));
                Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                Integer finalStatus = status;
                ok.addEventFilter(ActionEvent.ACTION, event -> {
                    saveAll(finalStatus);
                });
                alert.show();
                return;
            }
            saveAll(status);
        });
        mSaveSeat.setOnAction(view -> {
            // todo:bug获取失败 多语言问题
            int status = mCheckingStatus.getSelectionModel().getSelectedIndex() + 1;
            if (canNotChangeState(status)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText(ResourceUtils.getString("checkingTip"));
                alert.show();
                return;
            }
            FlightModel customModel = mFlightInfo.getCustomModelObj();
            FlightModel baseModel = modelMap.get(mFlightModelList.getSelectionModel().getSelectedItem());
            if (customModel == null && baseModel == null) {
                ToastUtils.toast(ResourceUtils.getString("seatLayoutTip"));
                return;
            }
            if (customModel == null || (baseModel != null && !TextUtils.equals(baseModel.getModelNumber(), customModel.getModelNumber()))) {
                // 基于baseModel来修改
                try {
                    customModel = baseModel.clone();
                    mFlightInfo.setCustomModelObj(customModel);
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
            }

            // merge修改
            mergeModelExtendObj(mFlightInfo.getCustomModelObj());
            uploadFlightInfo();
        });
        initInputListener();
        mFlightModelList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (mFlightInfoList.getSelectionModel().getSelectedItem() == null) {
                ToastUtils.toast(ResourceUtils.getString("plsChooseFlight"));
                return;
            }
            if (!TextUtils.isEmpty(newValue) && canChange) {
                // 标准model
                FlightModel model = modelMap.get(mFlightModelList.getSelectionModel().getSelectedItem());
                if (model == null) {
                    return;
                }
                refreshSeatByModel(model);
            }
            canChange = true;
            if (TextUtils.isEmpty(mFlightModelList.getSelectionModel().getSelectedItem())) {
                typeSetting.setVisible(false);
                typeSetting.managedProperty().bind(typeSetting.visibleProperty());
            } else {
                typeSetting.setVisible(true);
                typeSetting.managedProperty().bind(typeSetting.visibleProperty());
            }
        });
        mSeatType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            fillSeatInput();
        });

        TextFormatter<String> hourFormatter = new TextFormatter<>(change -> {
            if (!change.isContentChange()) {
                return change;
            }
            String text = change.getControlNewText();
            if (text.equals("--")) {
                return change;
            }
            if (!Constants.NUMBER_PATTERN.matcher(text).matches()) {
                return null;
            }
            if (text.length() == 1) {
                try {
                    Integer hour = Integer.parseInt(text);
                    if (hour < 0 || hour > 2) {
                        return null;
                    }
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
            if (boardingHour.getText().length() >= 2 && change.isAdded() && !change.isReplaced()) {
                boardingMin.requestFocus();
                return null;
            }
            if (text.length() == 2) {
                try {
                    Integer hour = Integer.parseInt(text);
                    if (hour >= 24) {
                        return null;
                    }
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
            return change;
        });
        TextFormatter<String> minFormatter = new TextFormatter<>(change -> {
            if (!change.isContentChange()) {
                return change;
            }
            String text = change.getControlNewText();
            if (text.equals("--")) {
                return change;
            }
            if (!Constants.NUMBER_PATTERN.matcher(text).matches()) {
                return null;
            }
            if (text.length() == 1) {
                try {
                    Integer hour = Integer.parseInt(text);
                    if (hour > 5) {
                        return null;
                    }
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
            if (boardingMin.getText().length() >= 2 && change.isAdded() && !change.isReplaced()) {
                return null;
            }
            if (text.length() == 2) {
                try {
                    Integer min = Integer.parseInt(text);
                    if (min >= 60) {
                        return null;
                    }
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
            return change;
        });
        boardingHour.setTextFormatter(hourFormatter);
        boardingMin.setTextFormatter(minFormatter);
        controlLogBtn.setOnAction(view -> openControlLog());
        if (TextUtils.isEmpty(mFlightModelList.getSelectionModel().getSelectedItem())) {
            typeSetting.setVisible(false);
            typeSetting.managedProperty().bind(typeSetting.visibleProperty());
        } else {
            typeSetting.setVisible(true);
            typeSetting.managedProperty().bind(typeSetting.visibleProperty());
        }
    }

    private boolean canNotChangeState(int status) {
        return mFlightInfo.getCheckInStatus() == Constants.CHECKING_START_NUM && status != Constants.CHECKING_PAUSE_NUM &&
                status != Constants.FLIGHT_CLOSE_NUM;
    }


    @FXML
    private void openControlLog() {
        if (mFlightInfo == null) {
            ToastUtils.toast(ResourceUtils.getString("plsChooseFlight"));
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ResourceUtils.getXmlUrl("controlLog"));
        fxmlLoader.setResources(ResourceBundle.getBundle("language"));
        Stage primaryStage = new Stage();
        try {
            Parent parent = fxmlLoader.load();
            logTable = (TableView) parent.lookup("#logTable");
            logTable.setEditable(true);
            logTable.getSelectionModel().setCellSelectionEnabled(true);
            logTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            Scene scene = new Scene(parent, 1200, 500);
            primaryStage.setTitle(ResourceUtils.getString("controlLog"));
            primaryStage.setScene(scene);
            initLogTable();
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initLogTable() {
        logTable.getColumns().clear();
        List<String> logKey = new ArrayList<>(Arrays.asList("account", "flightNumber", "event", "content", "time"));
        List<String> logKeyName = ResourceUtils.getLangArray(logKey);
        for (int i = 0; i < logKeyName.size(); i++) {
            TableColumn<Record, String> col = new TableColumn<>(logKeyName.get(i));
            col.setCellValueFactory(new PropertyValueFactory<>(logKey.get(i)));
            logTable.getColumns().add(col);
        }
        initLogTableData();
    }

    private void initLogTableData() {
        CloudApi.getInstance().queryFlightControlLog(mFlightInfo, new CommonCallback() {
            @Override
            public void success(String success) {
                Platform.runLater(() -> {
                    try {
                        List<Record> records = JSONArray.parseArray(success, Record.class);
                        if (records == null || records.isEmpty()) {
                            return;
                        }
                        refreshTableData(logTable, records);
                    } catch (JSONException EX) {
                        ToastUtils.toast(ResourceUtils.getString("refreshFailed"));
                    }
                });
            }

            @Override
            public void fail(String fail) {
                Platform.runLater(() -> {
                    ToastUtils.toast(ResourceUtils.getString("queryFailed2") + fail);
                });
            }
        });
    }

    private void refreshTableData(TableView tableView, List<Record> records) {
        if (tableView == null) {
            return;
        }
        ObservableList<Record> data = FXCollections.observableArrayList();
        data.addAll(records);
        tableView.setItems(data);
    }

    private void fillSeatInput() {
        String type = mSeatType.getSelectionModel().getSelectedItem();
        FlightModel model = modelMap.get(mFlightModelList.getSelectionModel().getSelectedItem());
        if (model == null || model.getModelExtend() == null) {
            return;
        }
        ModelExtend modelExtend = model.getModelExtendObj();
        if (!TextUtils.isEmpty(type)) {
            switch (type) {
                case "E":
                    mSeatInput.setText(modelExtend.getModelE());
                    break;
                case "C":
                    mSeatInput.setText(modelExtend.getModelC());
                    break;
                case "X":
                    mSeatInput.setText(modelExtend.getModelX());
                    break;
                default:
                    break;
            }
        }
    }


    private void refreshSeatByModel(FlightModel model) {
        if (model == null) {
            return;
        }
        String layout = model.getLayout();
        mSeatContainer.getChildren().clear();
        gridSeatsList.clear();

        // 将w3,3;1-3/ABCDEF;Y3,3;4-6/ABCDEF; -["w3,3;1-3/ABCDEF","Y3,3;4-6/ABCDEF;5-12/ABC"];
        String[] cockpitInputs = TextUtils.getSplitCockpit(layout);
        for (int i = 0; i < cockpitInputs.length; i++) {
            String cockpitInput = cockpitInputs[i];
            GridSeats gridSeats = new GridSeats();
            gridSeats.initSeats(cockpitInput, mFlightInfo, i == 0, i == cockpitInputs.length - 1);
            gridSeats.setAlignment(Pos.CENTER);
            mSeatContainer.getChildren().add(gridSeats);
            gridSeatsList.add(gridSeats);
        }
        refreshExtend(model.getModelExtendObj());
        canChange = false;
        mFlightModelList.getSelectionModel().select(model.getModelNumber());
    }

    private void refreshExtend(ModelExtend extend) {
        if (extend == null) {
            return;
        }

        // 每个仓位都设置一遍
        for (int i = 0; i < mSeatContainer.getChildren().size(); i++) {
            Node node = mSeatContainer.getChildren().get(i);
            if (!(node instanceof GridSeats)) {
                return;
            }
            GridSeats gridSeats = (GridSeats) node;
            gridSeats.setExtend(extend);
        }
    }

    private void saveAll(Integer status) {
        mFlightInfo.setCheckInStatus(status);
        mFlightInfo.setPlaneNumber(mAirplaneNumber.getText());
        mFlightInfo.setBoardingGate(mBoardingGate.getText());
        boardingTime = boardingHour.getText() + ":" + boardingMin.getText();
        if (TimeUtils.isValidTimeStr(boardingTime)) {
            mFlightInfo.setBoardingTime(boardingTime);
        }
        if (checkSetTime()) return;
        mFlightInfo.setRemark(remark.getText());
        FlightModel customModel = mFlightInfo.getCustomModelObj();
        FlightModel currentModel = modelMap.get(mFlightModelList.getSelectionModel().getSelectedItem());
        if (customModel == null) {
            try {
                if (currentModel != null) {
                    customModel = currentModel.clone();
                    mFlightInfo.setCustomModelObj(customModel);
                }
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
        mergeModelExtendObj(customModel);
        uploadFlightInfo();
    }

    private boolean checkSetTime() {
        if (TimeUtils.isValidTimeStr(mTakeOffTime.getDateTime()) && TimeUtils.isValidTimeStr(mLandTime.getDateTime())) {
            if (!TimeUtils.isTimeOrdered(mTakeOffTime.getDateTime(), mLandTime.getDateTime())) {
                ToastUtils.toast(ResourceUtils.getString("takeOffBeforeLandTime"));
                return true;
            }
            mFlightInfo.setTakeoffTime(mTakeOffTime.getDateTime());
            mFlightInfo.setLandTime(mLandTime.getDateTime());
        }
        if (TimeUtils.isValidTimeStr(mTakeOffTime.getDateTime()) && !TimeUtils.isValidTimeStr(mLandTime.getDateTime())) {
            mFlightInfo.setTakeoffTime(mTakeOffTime.getDateTime());
        }
        if (!TimeUtils.isValidTimeStr(mTakeOffTime.getDateTime()) && TimeUtils.isValidTimeStr(mLandTime.getDateTime())) {
            mFlightInfo.setLandTime(mLandTime.getDateTime());
        }
        return false;
    }

    private void mergeModelExtendObj(FlightModel customModel) {
        if (customModel == null) {
            return;
        }
        ModelExtend current = customModel.getModelExtendObj();
        String type = mSeatType.getSelectionModel().getSelectedItem();
        String input = mSeatInput.getText().trim();
        if (TextUtils.isEmpty(type) || TextUtils.isEmpty(input)) {
            return;
        }
        String[] modifySeats = TextUtils.splitSeatByExpression(input, gridSeatsList);
        switch (type) {
            case "*":
                current.addModelStar(modifySeats, false);
                break;
            case "C":
                current.addModelC(modifySeats);
                break;
            case "X":
                current.addModelX(modifySeats);
                break;
            default:
                break;
        }
    }

    private static void addModel(HashSet<String> currentModel, HashSet<String> customModel) {
        for (String tmp : customModel) {
            if (TextUtils.isEmpty(tmp)) {
                continue;
            }
            currentModel.add(tmp.trim());
        }
    }

    private void queryFromCloud() {
        if (mFlightInfoList.getValue() == null) {
            ToastUtils.toast(ResourceUtils.getString("plsChooseFlight"));
            return;
        }
        clearFlightInfoGroup();
        String flightNumber = mFlightInfoList.getValue().getFlightNumber();
        CloudApi.getInstance().queryFlightInfos(AccountManager.getInstance().getCompany(), flightNumber, new CommonCallback() {
            @Override
            public void success(String data) {
                List<FlightInfo> flightInfoList = JSONArray.parseArray(data, FlightInfo.class);
                Platform.runLater(() -> {
                    if (flightInfoList == null || flightInfoList.isEmpty()) {
                        return;
                    }
                    if (flightInfoList.size() == 1) {
                        mFlightInfo = flightInfoList.get(0);
                        flightBoard.setFlightInfo(flightInfoList.get(0));
                        setFlightInfoGroup(mFlightInfo);
                    } else {
                        FlightBoardChooseWindow.showWindow(flightInfoList, flightBoard, obj -> {
                            if (obj instanceof FlightInfo) {
                                mFlightInfo = (FlightInfo) obj;
                                setFlightInfoGroup(mFlightInfo);
                            }
                        });
                    }
                });
            }

            @Override
            public void fail(String fail) {
                System.out.println(fail);
            }
        });
    }

    private void setFlightInfoGroup(FlightInfo flightInfo) {
        mAirplaneNumber.setText(flightInfo.getPlaneNumber());
        if (!TextUtils.isEmpty(mFlightInfo.getBoardingTime())) {
            String[] times = mFlightInfo.getBoardingTime().split(":");
            boardingHour.setText(times[0].trim());
            boardingMin.setText(times[1].trim());
        }
        mBoardingGate.setText(flightInfo.getBoardingGate());
        if (TimeUtils.isValidTimeStr(flightInfo.getTakeoffTime())) {
            String time = flightInfo.getTakeoffTime();
            mTakeOffTime.setDateTime(time);
        }
        if (TimeUtils.isValidTimeStr(flightInfo.getLandTime())) {
            String time = flightInfo.getLandTime();
            mLandTime.setDateTime(time);
        }
        if (!TextUtils.isEmpty(flightInfo.getCustomLayout())) {
            refreshSeatByModel(flightInfo.getCustomModelObj());
        }
        if (flightInfo.getCheckInStatus() > 0) {
            mCheckingStatus.getSelectionModel().select(flightInfo.getCheckInStatus() - 1);
        } else {
            mCheckingStatus.getSelectionModel().select(-1);
            mCheckingStatus.setPromptText(ResourceUtils.getString("unChoose"));
        }
        remark.setText(flightInfo.getRemark());
    }

    private void clearFlightInfoGroup() {
        mAirplaneNumber.setText(null);
        mSeatInput.setText("");
        boardingHour.setText("--");
        boardingMin.setText("--");
        mBoardingGate.setText(null);
        mTakeOffTime.clear();
        mLandTime.clear();
        remark.setText("");
        mCheckingStatus.getSelectionModel().select(-1);
        mFlightModelList.getSelectionModel().select(-1);
        mSeatContainer.getChildren().clear();
        gridSeatsList.clear();
        flightBoard.clear();
    }

    @Override
    void initData() {
        clearFlightInfoGroup();
        initFlightList();
        initModelList();
        boardingHour.setText("--");
        boardingMin.setText("--");
        List<String> statuses = Arrays.asList(Constants.CHECKING_START, Constants.CHECKING_PAUSE, Constants.CHECKING_CLOSE_CHECKIN, Constants.CHECKING_CLOSE);
        List<String> langStatus = ResourceUtils.getLangArray(statuses);
        mCheckingStatus.getItems().clear();
        mCheckingStatus.getItems().addAll(langStatus);
    }

    private void initInputListener() {
        mUploadFlightInfoBtn.setOnAction(event -> {
            if (!AccountManager.getInstance().isManager()) {
                ToastUtils.toast(ResourceUtils.getString("noAuthority"));
                return;
            }
            File file = DataInput.openSingleFileChooser();
            String res = DataInput.readFileTxt(file);
            if (res == null) {
                return;
            }

            // 删除空白换行
            String[] lines = res.split("\r\n");
            CombineUdp combineUdp = new CombineUdp(lines);
            combineUdp.checkIntegrated();
            boolean isIntegrate = combineUdp.isIsIntegrate();
            if (isIntegrate) {
                combineUdp.fillFlightInfo();
                mFlightInfo = combineUdp.getFlightInfo();
                if (!TextUtils.equals(AccountManager.getInstance().getCompany(), mFlightInfo.getCompany())) {
                    ToastUtils.toast(ResourceUtils.getString("companyNotMatch"));
                    return;
                }
                uploadFlightInfoAndUsers();
            } else {
                ToastUtils.toast(ResourceUtils.getString("parseFailed2") + combineUdp.getErrorRes());
            }
        });
    }

    private void uploadFlightInfoAndUsers() {
        CloudApi.getInstance().uploadFlightInfoAndUsers(mFlightInfo, mUploadCallback);
    }

    private void uploadFlightInfo() {
        CloudApi.getInstance().uploadFlightInfo(mFlightInfo, mUploadCallback);
    }

    private void initFlightList() {
        CloudApi.getInstance().queryAllFlightInfo(AccountManager.getInstance().getCompany(), new CommonCallback() {
            @Override
            public void success(String success) {
                ViewUtils.setFlightListView(mFlightInfoList, success);
            }

            @Override
            public void fail(String fail) {
                ToastUtils.toast(ResourceUtils.getString("queryFailed2") + fail);
            }
        });
    }

    private void initModelList() {
        // 查询上传过的航班
        String company = "8L";
        CloudApi.getInstance().queryFlightModels(company, new CommonCallback() {
            @Override
            public void success(String data) {
                System.out.println(data);
                List<FlightModel> flightModels = JSONArray.parseArray(data, FlightModel.class);
                if (flightModels == null) {
                    return;
                }
                modelMap.clear();
                mFlightModelList.getItems().clear();
                List<String> arr = new ArrayList<>();
                for (FlightModel model : flightModels) {
                    if (!arr.contains(model.getModelNumber())) {
                        arr.add(model.getModelNumber());
                    }
                    modelMap.put(model.getModelNumber(), model);
                }
                Platform.runLater(() -> {
                    mFlightModelList.getItems().addAll(arr);
                });
            }

            @Override
            public void fail(String fail) {
                System.out.println(fail);
            }
        });
        mSeatType.getItems().clear();
        List<String> types = new ArrayList<>();
        types.add("*");
        types.add("C");
        types.add("X");
        mSeatType.getItems().addAll(types);
        mSeatType.setPromptText(ResourceUtils.getString("unChoose"));
    }

    @Override
    List<Pane> getRasterizeWidthPanes() {
        return new ArrayList<>(Arrays.asList(mRoot));
    }

    @Override
    List<Pane> getRasterizeHeightPanes() {
        return new ArrayList<>(Arrays.asList(mRoot));
    }

    @Override
    public void refresh() {
        initData();
    }
}
