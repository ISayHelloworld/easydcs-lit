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
import main.utils.*;
import main.view.FlightBoard;
import main.view.FlightMoreStatusWindow;

import java.util.*;

public class BoardingPage2 extends BasePageNode {
    private FlightBoard mFlightBoard;

    private FlightInfo mFlightInfo;

    private TableView<FlightUser> mCustomerTableView;

    private FlightUser mFocusUser;

    private HBox mBackImg;

    private TextField mQueryName;

    @FXML
    private Button more;

    private Button mStatusBtn;

    private Label mBoardNum;

    private Label mNotBoardNum;

    private int mStatus = Constants.NOT_BOARD_NUM;

    private Button mBoard;

    private Button mSaveStatus;

    private Label mPeopleNum;

    private ComboBox<String> mBoardStatus;

    private boolean isConsumed = false;

    private static HashMap<Integer, String> mStatusMap = new HashMap<>();

    static {
        mStatusMap.put(Constants.NOT_BOARD_NUM, ResourceUtils.getString("notBoard"));
        mStatusMap.put(Constants.BOARD_NUM, ResourceUtils.getString("boarded"));
        mStatusMap.put(Constants.BOARD_ALL, ResourceUtils.getString("all"));
    }

    @Override
    String getFxmLPath() {
        return "boardingPage2";
    }

    @Override
    void initView() {
        mBackImg = (HBox) mRoot.lookup("#backImg");
        mBackImg.setOnMouseClicked(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            alert.setHeaderText(ResourceUtils.getString("confirmBack"));
            ok.addEventFilter(ActionEvent.ACTION, eventOk -> {
                PageRouter.jumpToPage(9);
            });
            alert.show();
        });
        mQueryName = (TextField) mRoot.lookup("#queryName");
        mBoardStatus = (ComboBox<String>) mRoot.lookup("#checkInStatus");
        mStatusBtn = (Button) mRoot.lookup("#statusBtn");
        mBoard = (Button) mRoot.lookup("#submitBoard");
        mSaveStatus = (Button) mRoot.lookup("#saveStatus");
        mPeopleNum = (Label) mRoot.lookup("#peopleNum");
        mBoardNum = (Label) mRoot.lookup("#boardNum");
        mNotBoardNum = (Label) mRoot.lookup("#notBoardNum");
        mStatusBtn.setOnAction(view -> {
            mStatus++;
            if (mStatus > 2) {
                mStatus = 0;
            }
            mStatusBtn.setText(mStatusMap.get(mStatus));
            refreshTableData(mCustomerTableView, getFilterData());
            mPeopleNum.setText(mStatusMap.get(mStatus) + " : " + mCustomerTableView.getItems().size() + ResourceUtils.getString("ppl"));
            if (mStatus == Constants.BOARD_NUM) {
                mBoard.setText(ResourceUtils.getString("delete"));
            } else {
                mBoard.setText(ResourceUtils.getString("board"));
            }
        });
        mFlightBoard = (FlightBoard) mRoot.lookup("#flightBoard");
        mCustomerTableView = (TableView) mRoot.lookup("#customerTableView");
        mCustomerTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        mBoard.setOnAction(view -> {
            if (mFocusUser == null) {
                ToastUtils.toast(ResourceUtils.getString("plsChooseUser"));
                return;
            }
            if (isNotValidBoardingStatus()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                String tip = "boardingNotStart";
                if (mFlightInfo.getBoardingStatus() == Constants.BOARDING_NOT_START_NUM) {
                    tip = "boardingNotStart";
                } else if (mFlightInfo.getBoardingStatus() == Constants.BOARDING_CLOSE_NUM) {
                    tip = "boardingClosed";
                } else if (mFlightInfo.getCheckInStatus() == Constants.FLIGHT_CLOSE_NUM) {
                    tip = "flightClosed";
                }
                alert.setHeaderText(ResourceUtils.getString(tip));
                alert.show();
                return;
            }
            if (TextUtils.equals(mBoard.getText(), ResourceUtils.getString("delete"))) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText(ResourceUtils.getString("deleteCheckInTip"));
                Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                ok.setOnAction(btn -> deleteBoard());
                alert.show();
            } else {
                board();
            }
        });
        mSaveStatus.setOnAction(view -> {
            // 状态切换
            String currentValue = mBoardStatus.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            Button cancel = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
            cancel.addEventFilter(ActionEvent.ACTION, eventCancel -> {
                isConsumed = false;
                String value = mFlightInfo.getBoardingStatus() == -1 ? ResourceUtils.getString("notStartBoarding") : ResourceUtils.getString(Constants.BOARDING_STATUS_MAP.get(mFlightInfo.getBoardingStatus()));
                mBoardStatus.getSelectionModel().select(value);
            });
            if (TextUtils.equals(currentValue, ResourceUtils.getString(Constants.BOARDING_CLOSE))) {
                alert.setTitle(ResourceUtils.getString("confirmCloseBoarding"));
                ok.addEventFilter(ActionEvent.ACTION, eventOk -> {
                    mFlightInfo.setBoardingStatus(Constants.BOARDING_CLOSE_NUM);
                    CloudApi.getInstance().uploadFlightInfo(mFlightInfo, new CommonCallback() {
                        @Override
                        public void success(String success) {
                            Platform.runLater(() -> {
                                ToastUtils.toast(ResourceUtils.getString("alreadyCloseBoarding"));
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
                alert.show();
            } else if (TextUtils.equals(currentValue, ResourceUtils.getString(Constants.BOARDING_START))) {
                alert.setTitle(ResourceUtils.getString("confirmStartBoarding"));
                ok.addEventFilter(ActionEvent.ACTION, eventOk -> {
                    mFlightInfo.setBoardingStatus(Constants.BOARDING_START_NUM);
                    CloudApi.getInstance().uploadFlightInfo(mFlightInfo, new CommonCallback() {
                        @Override
                        public void success(String success) {
                            Platform.runLater(() -> {
                                ToastUtils.toast(ResourceUtils.getString("alreadyStartBoarding"));
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
                alert.show();
            }
        });
        mQueryName.textProperty().addListener((observable, oldValue, newValue) -> {
            // 启动查询并刷新界面
            refreshTableData(mCustomerTableView, getFilterData());
        });
        addStatusListener();
        more.setOnAction(view -> {
            if (mFlightInfo == null) {
                return;
            }
            FlightMoreStatusWindow window = new FlightMoreStatusWindow(mFlightInfo);
            window.showWindow();
        });
    }

    private void deleteBoard() {
        // todo:删除之后需要滚轮刷新才会刷新数据，看下为啥
        mFocusUser.setBoardingStatus(Constants.NOT_BOARDED);
        CloudApi.getInstance().uploadFlightUser(mFocusUser, new CommonCallback() {
            @Override
            public void success(String success) {
                Platform.runLater(() -> {
                    ToastUtils.toast(ResourceUtils.getString("deleteSuccess"));
                    queryFlightUsers();
                });
            }

            @Override
            public void fail(String fail) {
                Platform.runLater(() -> {
                    ToastUtils.toast(ResourceUtils.getString("deleteFailed2") + fail);
                });
            }
        });
    }

    private void board() {
        mFocusUser.setBoardingStatus(Constants.BOARDED);
        CloudApi.getInstance().uploadFlightUser(mFocusUser, new CommonCallback() {
            @Override
            public void success(String success) {
                Platform.runLater(() -> {
                    ToastUtils.toast(ResourceUtils.getString("boardingSuccess"));
                    queryFlightUsers();
                });
            }

            @Override
            public void fail(String fail) {
                Platform.runLater(() -> {
                    ToastUtils.toast(ResourceUtils.getString("saveFailed2") + fail);
                });
            }
        });
    }

    private void addStatusListener() {
        mBoardStatus.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (TextUtils.equals(newValue, oldValue) || TextUtils.isEmpty(newValue)) {
                return;
            }
            if (!isConsumed) {
                isConsumed = true;
            }
        });
    }

    private boolean isNotValidBoardingStatus() {
        return mFlightInfo.getBoardingStatus() != Constants.BOARDING_START_NUM || mFlightInfo.getCheckInStatus() == Constants.FLIGHT_CLOSE_NUM;
    }

    private List<FlightUser> getFilterData() {
        // 循环过滤
        List<FlightUser> flightUsers = new ArrayList<>();
        Set<String> boardStatus = new HashSet<>();
        switch (mStatus) {
            case 0:
                boardStatus.add(Constants.NOT_BOARDED);
                break;
            case 1:
                boardStatus.add(Constants.BOARDED);
                break;
            case 2:
                boardStatus.add(Constants.BOARDED);
                boardStatus.add(Constants.NOT_BOARDED);
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
            if (boardStatus.contains(flightUser.getBoardingStatus())) {
                flightUsers.add(flightUser);
            }
        }
        return flightUsers;
    }

    private void initCustomerTableSchema() {
        //将表格的列和类的属性进行绑定
        mCustomerTableView.getItems().clear();
        mCustomerTableView.getColumns().clear();
        UsersChooseManager.getInstance().clear();
        List<String> key = new ArrayList<>(Arrays.asList("name", "ticketNumber", "position", "freeBudget", "recordNumber", "flight", "weight", "luggageNumber", "seatNumber", "order", "checkInStatus", "boardingStatus", "type", "country", "number", "gender", "birthDate", "validityPeriod"));
        List<String> keyName = ResourceUtils.getLangArray(key);
        for (int i = 0; i < keyName.size(); i++) {
            TableColumn<FlightUser, String> col = new TableColumn<>(keyName.get(i));
            col.setCellValueFactory(new PropertyValueFactory<>(key.get(i)));
            mCustomerTableView.getColumns().add(col);
        }
        mCustomerTableView.setRowFactory(tv -> {
            TableRow<FlightUser> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                mFocusUser = row.getItem();
                if (TextUtils.equals(mStatusBtn.getText(), ResourceUtils.getString("all"))) {
                    if (TextUtils.equals(mFocusUser.getBoardingStatus(), Constants.BOARDED)) {
                        mBoard.setText(ResourceUtils.getString("delete"));
                    } else {
                        mBoard.setText(ResourceUtils.getString("board"));
                    }
                }
            });
            return row;
        });
        for (TableColumn tableColumn : mCustomerTableView.getColumns()) {
            if (TextUtils.equals(tableColumn.getText(), ResourceUtils.getString("boardingStatus"))) {
                tableColumn.setCellFactory(column -> new TableCell<FlightUser, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? "" : getItem());
                        setGraphic(null);
                        TableRow<FlightUser> currentRow = getTableRow();
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
        List<String> statuses = Arrays.asList(Constants.BOARDING_START, Constants.BOARDING_CLOSE);
        List<String> langStatus = ResourceUtils.getLangArray(statuses);
        mBoardStatus.getItems().clear();
        mBoardStatus.getItems().addAll(langStatus);
        mFlightInfo = FlightModelData.getInstance().getFlightInfo();
        if (mFlightInfo == null) {
            return;
        }
        CloudApi.getInstance().queryFlightInfo(mFlightInfo.getCompany(), mFlightInfo.getFlightNumber(), mFlightInfo.getDate(), new CommonCallback() {
            @Override
            public void success(String data) {
                Platform.runLater(() -> {
                    System.out.println(data);
                    mFlightInfo = JSONObject.parseObject(data, FlightInfo.class);
                    if (mFlightInfo == null) {
                        return;
                    }
                    mFlightInfo.initFlightInfoDate();
                    FlightModelData.getInstance().setFlightInfo(mFlightInfo);
                    mFlightBoard.setFlightInfo(mFlightInfo);
                    isConsumed = false;
                    if (mFlightInfo.getBoardingStatus() == -1) {
                        mBoardStatus.setPromptText(ResourceUtils.getString("notStartBoarding"));
                    } else {
                        mBoardStatus.getSelectionModel().select(mFlightInfo.getBoardingStatus() - 1);
                    }
                    queryFlightUsers();
                });
            }

            @Override
            public void fail(String fail) {
                ToastUtils.toast(ResourceUtils.getString("queryFailed2") + fail);
            }
        });
    }

    private void queryFlightUsers() {
        CloudApi.getInstance().queryFlightBoardingUsers(mFlightInfo.getCompany(), mFlightInfo.getFlightNumber(), mFlightInfo.getDate(), new CommonCallback() {
            @Override
            public void success(String success) {
                List<FlightUser> users = JSONArray.parseArray(success, FlightUser.class);
                mFlightInfo.setFlightUsers(users);
                Platform.runLater(() -> {
                    if (users == null || users.isEmpty()) {
                        mBoardNum.setText("--");
                        mBoardNum.setText("--");
                    }
                    mStatusBtn.setText(mStatusMap.get(mStatus));
                    refreshTableData(mCustomerTableView, getFilterData());
                    mPeopleNum.setText(mStatusMap.get(mStatus) + " : " + mCustomerTableView.getItems().size() + ResourceUtils.getString("ppl"));
                    loopBoardNum(users);
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

    private void loopBoardNum(List<FlightUser> users) {
        int notBoarded = 0;
        int boarded = 0;
        if (users == null) {
            return;
        }
        for (FlightUser user : users) {
            if (user == null) {
                continue;
            }
            if (TextUtils.equals(user.getBoardingStatus(), Constants.BOARDED)) {
                boarded++;
            } else {
                notBoarded++;
            }
        }
        mBoardNum.setText(String.valueOf(boarded));
        mNotBoardNum.setText(String.valueOf(notBoarded));
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
