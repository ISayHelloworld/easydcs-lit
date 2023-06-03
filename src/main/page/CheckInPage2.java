package main.page;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import main.callback.CommonCallback;
import main.entity.FlightInfo;
import main.entity.FlightUser;
import main.manager.PageRouter;
import main.manager.UsersChooseManager;
import main.model.FlightModelData;
import main.seats.GridSeats;
import main.utils.*;
import main.view.FlightBoard;
import main.view.FlightMoreStatusWindow;
import main.view.UresWindow;

import java.util.*;

public class CheckInPage2 extends BasePageNode {
    private Button mNext;

    @FXML
    private Button ures;

    private FlightBoard mFlightBoard;

    private FlightInfo mFlightInfo;
    private TableView<FlightUser> mCustomerTableView;
    private TableView<FlightUser> mCustomRegisterTable;

    private FlightUser mFocusUser;

    private TableRow<FlightUser> mFocusChooseRow;

    private Button mUp;

    private Button mDown;

    @FXML
    private Button more;

    private Button mDeleteCheckIn;

    private HBox mBack;

    private TextField mQueryName;

    private Button mStatusBtn;

    private int mStatus = Constants.NOT_BOARD_NUM;

    private Label mPeopleNum;

    private static HashMap<Integer, String> mStatusMap = new HashMap<>();

    static {
        mStatusMap.put(Constants.NOT_BOARD_NUM, "notChecked");
        mStatusMap.put(Constants.BOARD_NUM, "checked");
        mStatusMap.put(Constants.BOARD_ALL, "all");
    }

    @Override
    String getFxmLPath() {
        return "checkInPage2";
    }

    @Override
    void initView() {
        mBack = (HBox) mRoot.lookup("#backImg");
        mBack.setOnMouseClicked(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            alert.setHeaderText(ResourceUtils.getString("confirmBack"));
            ok.addEventFilter(ActionEvent.ACTION, eventOk -> {
                PageRouter.jumpToPage(1);
            });
            alert.show();
        });
        mNext = (Button) mRoot.lookup("#nextStep");
        mUp = (Button) mRoot.lookup("#moveUp");
        mDeleteCheckIn = (Button) mRoot.lookup("#deleteBoard");
        mQueryName = (TextField) mRoot.lookup("#queryName");
        mStatusBtn = (Button) mRoot.lookup("#statusBtn");
        mPeopleNum = (Label) mRoot.lookup("#peopleNum");
        mStatusBtn.setOnAction(view -> {
            mStatus++;
            if (mStatus > 2) {
                mStatus = 0;
            }
            if (mStatus == Constants.NOT_BOARD_NUM) {
                ures.setVisible(true);
            } else {
                ures.setVisible(false);
            }
            mStatusBtn.setText(ResourceUtils.getString(mStatusMap.get(mStatus)));
            refreshTableData(mCustomerTableView, getFilterData());
            setPeopleNum();
        });
        mUp.setOnAction(view -> {
            moveUp();
        });
        mDown = (Button) mRoot.lookup("#moveDown");
        mDown.setOnAction(view -> {
            moveDown();
        });
        mFlightBoard = (FlightBoard) mRoot.lookup("#flightBoard");
        mCustomerTableView = (TableView) mRoot.lookup("#customerTableView");
        mCustomerTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        mCustomRegisterTable = (TableView) mRoot.lookup("#customRegisterTable");
        ViewUtils.setTableEmpty(mCustomRegisterTable);
        mNext.setOnAction(view -> {
            if (UsersChooseManager.getInstance().getCurrentUsers().isEmpty()) {
                ToastUtils.toast(ResourceUtils.getString("plsChooseUser"));
                return;
            }
            if (isNotValidBoardingStatus()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                alert.setTitle(ResourceUtils.getString("warning"));
                alert.setHeaderText(ResourceUtils.getString("already") + ResourceUtils.getString(Constants.CHECKING_STATUS_MAP.get(mFlightInfo.getCheckInStatus())) + "," + ResourceUtils.getString("confirmNext"));
                ok.addEventFilter(ActionEvent.ACTION, event -> {
                    PageRouter.jumpToPage(3);
                });
                alert.show();
                return;
            }
            PageRouter.jumpToPage(3);
        });
        mQueryName.textProperty().addListener((observable, oldValue, newValue) -> {
            // 启动查询并刷新界面
            refreshTableData(mCustomerTableView, getFilterData());
        });
        mDeleteCheckIn.setOnAction(view -> {
            if (mFocusUser == null) {
                ToastUtils.toast(ResourceUtils.getString("plsChooseUser"));
                return;
            }
            if (isNotValidBoardingStatus()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle(ResourceUtils.getString("warning"));
                alert.setHeaderText(ResourceUtils.getString("already") + ResourceUtils.getString(Constants.CHECKING_STATUS_MAP.get(mFlightInfo.getCheckInStatus())) + "," + ResourceUtils.getString("saveFailed"));
                alert.show();
                return;
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText(ResourceUtils.getString("deleteCheckInTip"));
            Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            ok.setOnAction(btn -> deleteCheckIn());
            alert.show();
        });
        more.setOnAction(view -> {
            if (mFlightInfo == null) {
                return;
            }
            FlightMoreStatusWindow window = new FlightMoreStatusWindow(mFlightInfo);
            window.showWindow();
        });
        ures.setOnAction(view -> {
            UresWindow window = new UresWindow(Alert.AlertType.CONFIRMATION);
            window.showWindow(mFlightInfo, obj -> {
                if (!(obj instanceof FlightUser)) {
                    return;
                }
                CloudApi.getInstance().uploadFlightUser((FlightUser) obj, new CommonCallback() {
                    @Override
                    public void success(String success) {
                        Platform.runLater(() -> {
                            ToastUtils.toast(ResourceUtils.getString("saveSuccess"));
                            refresh();
                        });
                    }

                    @Override
                    public void fail(String fail) {
                        Platform.runLater(() -> {
                            ToastUtils.toast(ResourceUtils.getString("saveFailed2") + fail);
                        });
                    }
                });
            });
        });
    }

    private boolean isNotValidBoardingStatus() {
        return mFlightInfo.getCheckInStatus() == Constants.FLIGHT_CLOSE_NUM ||
                mFlightInfo.getCheckInStatus() == Constants.CHECKING_PAUSE_NUM ||
                mFlightInfo.getCheckInStatus() == Constants.CLOSE_CHECKING_NUM;
    }

    private void deleteCheckIn() {
        if (TextUtils.equals(mFocusUser.getCheckInStatus(), Constants.CHECKED)) {
            mFocusUser.setCheckInStatus(Constants.NOT_CHECKED);
            CloudApi.getInstance().deleteBoardFlightUser(mFocusUser, new CommonCallback() {
                @Override
                public void success(String success) {
                    Platform.runLater(() -> {
                        ToastUtils.toast(ResourceUtils.getString("deleteSuccess"));
                        mFocusUser.setSeatNumber("");
                        refreshTableData(mCustomerTableView, getFilterData());
                    });
                }

                @Override
                public void fail(String fail) {
                    Platform.runLater(() -> {
                        ToastUtils.toast(ResourceUtils.getString("deleteFailed2") + fail);
                        refreshTableData(mCustomerTableView, getFilterData());
                    });
                }
            });
        } else {
            ToastUtils.toast(ResourceUtils.getString("userNotCheck"));
        }
    }

    private List<FlightUser> getFilterData() {
        // 循环过滤
        List<FlightUser> flightUsers = new ArrayList<>();
        Set<String> boardStatus = new HashSet<>();
        switch (mStatus) {
            case 0:
                boardStatus.add(Constants.NOT_CHECKED);
                break;
            case 1:
                boardStatus.add(Constants.CHECKED);
                break;
            case 2:
                boardStatus.add(Constants.CHECKED);
                boardStatus.add(Constants.NOT_CHECKED);
                break;
            default:
                break;
        }
        if (mFlightInfo.getFlightUsers() == null || mFlightInfo.getFlightUsers().isEmpty()) {
            return new ArrayList<>();
        }
        for (FlightUser flightUser : mFlightInfo.getFlightUsers()) {
            if (!TextUtils.isEmpty(mQueryName.getText())) {
                String name = flightUser.getName();
                String upName = name.toUpperCase();
                if (!upName.startsWith(mQueryName.getText().toUpperCase())) {
                    continue;
                }
            }
            if (boardStatus.contains(flightUser.getCheckInStatus())) {
                flightUsers.add(flightUser);
            }
        }
        return flightUsers;
    }

    private void moveDown() {
        if (mFocusUser == null) {
            return;
        }
        UsersChooseManager.getInstance().addCurrentUser(mFocusUser);
        refreshTableData(mCustomRegisterTable, UsersChooseManager.getInstance().getCurrentUsers());
    }

    private void moveUp() {
        if (mFocusChooseRow == null) {
            return;
        }
        UsersChooseManager.getInstance().removeByIndex(mFocusChooseRow.getIndex());
        if (mFocusChooseRow.getIndex() >= mCustomRegisterTable.getItems().size()) {
            return;
        }
        mCustomRegisterTable.getItems().remove(mFocusChooseRow.getIndex());
    }

    private void initCustomerTableSchema() {
        //将表格的列和类的属性进行绑定
        mCustomerTableView.getColumns().clear();
        mCustomRegisterTable.getColumns().clear();
        mCustomRegisterTable.getItems().clear();
        UsersChooseManager.getInstance().clear();
        List<String> key = new ArrayList<>(Arrays.asList("name", "ticketNumber", "position", "freeBudget", "recordNumber", "flight", "weight", "luggageNumber", "seatNumber", "order", "checkInStatus", "type", "country", "number", "gender", "birthDate", "validityPeriod"));
        List<String> keyName = ResourceUtils.getLangArray(key);
        for (int i = 0; i < keyName.size(); i++) {
            TableColumn<FlightUser, String> col = new TableColumn<>(keyName.get(i));
            col.setCellValueFactory(new PropertyValueFactory<>(key.get(i)));
            mCustomerTableView.getColumns().add(col);
        }
        for (int i = 0; i < keyName.size(); i++) {
            TableColumn<FlightUser, String> col = new TableColumn<>(keyName.get(i));
            col.setCellValueFactory(new PropertyValueFactory<>(key.get(i)));
            mCustomRegisterTable.getColumns().add(col);
        }
        for (TableColumn tableColumn : mCustomerTableView.getColumns()) {
            if (TextUtils.equals(tableColumn.getText(), ResourceUtils.getString("checkInStatus"))) {
                tableColumn.setCellFactory(column -> new TableCell<FlightUser, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? "" : getItem());
                        setGraphic(null);
                        TableRow<FlightUser> currentRow = getTableRow();
                        if (currentRow == null) {
                            return;
                        }
                        if (TextUtils.equals(item, "1")) {
                            currentRow.setStyle("-fx-background-color:#eca6a6");
                        } else if (TextUtils.equals(item, "0")) {
                            currentRow.setStyle("-fx-background-color:lightgreen");
                        } else {
                            currentRow.setStyle("-fx-background-color:white");
                        }
                    }
                });
            }
        }
        mCustomerTableView.setRowFactory(tv -> {
            TableRow<FlightUser> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                mFocusUser = row.getItem();
                if (mFocusUser == null) {
                    return;
                }
                if (TextUtils.equals(mFocusUser.getCheckInStatus(), Constants.CHECKED)) {
                    mDeleteCheckIn.setOpacity(1F);
                } else {
                    mDeleteCheckIn.setOpacity(0.4F);
                }
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    moveDown();
                }
            });
            return row;
        });
        mCustomRegisterTable.setRowFactory(tv -> {
            TableRow<FlightUser> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                mFocusChooseRow = row;
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    moveUp();
                }
            });
            return row;
        });
    }

    private void refreshTableData(TableView tableView, List<FlightUser> flightUsers) {
        if (tableView == null) {
            return;
        }
        ObservableList<FlightUser> data = FXCollections.observableArrayList();
        data.addAll(flightUsers);
        tableView.setItems(data);
    }

    @Override
    void initData() {
        initCustomerTableSchema();
        mQueryName.setText("");

        // 用于界面传递参数
        mFlightInfo = FlightModelData.getInstance().getFlightInfo();
        if (mFlightInfo == null) {
            return;
        }
        CloudApi.getInstance().queryFlightInfo(mFlightInfo.getCompany(), mFlightInfo.getFlightNumber(), mFlightInfo.getDate(), new CommonCallback() {
            @Override
            public void success(String data) {
                mFlightInfo = JSONObject.parseObject(data, FlightInfo.class);
                if (mFlightInfo == null) {
                    System.out.println("flightInfo is null");
                    return;
                }
                mFlightInfo.initFlightInfoDate();
                calculateFlightSeats();
                Platform.runLater(() -> {
                    FlightModelData.getInstance().setFlightInfo(mFlightInfo);
                    mFlightBoard.setFlightInfo(mFlightInfo);
                    queryFlightUsers();
                });
            }

            @Override
            public void fail(String fail) {
                ToastUtils.toast(ResourceUtils.getString("queryFailed2") + fail);
            }
        });
    }

    private void calculateFlightSeats() {
        String[] cockpitInputs = TextUtils.getSplitCockpit(mFlightInfo.getCustomLayout());
        mFlightInfo.clearGridSeats();
        if (cockpitInputs == null) {
            return;
        }
        for (int i = 0; i < cockpitInputs.length; i++) {
            String cockpitInput = cockpitInputs[i];
            GridSeats gridSeats = new GridSeats();
            gridSeats.initSeats(cockpitInput, mFlightInfo, i == 0, i == cockpitInputs.length - 1);
            gridSeats.setExtend(mFlightInfo.getCustomModelObj().getModelExtendObj());
            mFlightInfo.getGridSeatsList().add(gridSeats);
        }
    }

    private void queryFlightUsers() {
        CloudApi.getInstance().queryFlightUsers(mFlightInfo.getCompany(), mFlightInfo.getFlightNumber(), mFlightInfo.getDate(), new CommonCallback() {
            @Override
            public void success(String success) {
                List<FlightUser> users = JSONArray.parseArray(success, FlightUser.class);
                mFlightInfo.setFlightUsers(users);
                Platform.runLater(() -> {
                    mStatusBtn.setText(ResourceUtils.getString(mStatusMap.get(mStatus)));
                    refreshTableData(mCustomerTableView, getFilterData());
                    setPeopleNum();
                });
            }

            @Override
            public void fail(String fail) {

            }
        });
    }

    private void setPeopleNum() {
        String paxStr = mCustomerTableView.getItems().size() + " " + ResourceUtils.getString("pax");
        String infStr = getInfNum() + " " + ResourceUtils.getString("inf");
        mPeopleNum.setText(paxStr + " " + infStr);
    }

    private int getInfNum() {
        int infNum = 0;
        for (FlightUser flightUser : mCustomerTableView.getItems()) {
            if (flightUser.hasInf()) {
                infNum++;
            }
        }
        return infNum;
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
        initView();
        initData();
    }
}
