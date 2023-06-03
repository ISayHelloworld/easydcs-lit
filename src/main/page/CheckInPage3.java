package main.page;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import main.callback.CommonCallback;
import main.callback.WindowCallback;
import main.entity.AirPackage;
import main.entity.FlightInfo;
import main.entity.FlightUser;
import main.entity.Record;
import main.manager.PageRouter;
import main.manager.UsersChooseManager;
import main.model.FlightModelData;
import main.seats.GridSeats;
import main.utils.*;
import main.view.FlightBoard;
import main.view.InfInfoWindow;
import main.view.PackageAddWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class CheckInPage3 extends BasePageNode {
    private Button mSubmit;

    private Button mLast;

    private Button mSaveSp;

    private ComboBox<String> mSpStatus;

    private FlightBoard mFlightBoard;

    private FlightInfo mFlightInfo;

    private TableView<FlightUser> mCustomRegisterTable;

    private TableView<Record> mLogTable;

    private VBox mSpServiceArea;

    private Pane mRegisterArea;

    private HBox mSeatsContainer;

    @FXML
    private Button infWindow;

    // API信息区字段
    private TextField mLastName;

    private TextField mFirstName;

    @FXML
    private TextField middleName;

    private TextField mGender;
    private TextField mCountry;
    private TextField mBirthday;

    private TextField mPassportNumber;
    private TextField mIssueDate;
    private TextField mExpireDate;
    private VBox mPackageContainer;

    private Button mSaveApi;

    private Label mSeatTip;

    private Button mDeletePackage;

    private List<AirPackage> mPackageList = new ArrayList<>();

    private TableView<AirPackage> mPackageTable;

    private TableRow<AirPackage> mFocusChooseRow;

    private PackageAddWindow mPackageWindow;

    private WindowCallback mWindowCallback;

    // 座位分配
    private TextField mAssignSeatInput;

    private FlightUser mCurrentFlightUser;

    private TextArea mPnl;

    @FXML
    private TabPane tabPaneArea;

    private List<AirPackage> mUserPackages = new ArrayList<>();

    @FXML
    private Button addBoard;

    @FXML
    private Button addPackageList;

    @Override
    String getFxmLPath() {
        return "checkInPage3";
    }

    @Override
    void initView() {
        HBox buttonArea = (HBox) mRoot.lookup("#buttonArea");
//        ViewUtils.setBorder(buttonArea);
        mLast = (Button) mRoot.lookup("#lastStep");
        mLast.setOnAction(view -> {
            PageRouter.jumpToPage(2);
        });
        mSubmit = (Button) mRoot.lookup("#assignSeat");
        mFlightBoard = (FlightBoard) mRoot.lookup("#flightBoard");
        mCustomRegisterTable = (TableView) mRoot.lookup("#customRegisterTable");
        mRegisterArea = (Pane) mRoot.lookup("#registerInfoArea");

        // API
        mLastName = (TextField) mRoot.lookup("#lastName");
        mFirstName = (TextField) mRoot.lookup("#firstName");
        mGender = (TextField) mRoot.lookup("#gender");
        mCountry = (TextField) mRoot.lookup("#country");
        mBirthday = (TextField) mRoot.lookup("#birthday");
        mPassportNumber = (TextField) mRoot.lookup("#passportNumber");
        mIssueDate = (TextField) mRoot.lookup("#issueDate");
        mExpireDate = (TextField) mRoot.lookup("#expireDate");
        mSaveApi = (Button) mRoot.lookup("#saveApi");
        mSaveApi.setOnAction(view -> {
            if (isNotValidCheckInStatus()) {
                showWarning();
                return;
            }
            setSaveApi(mCurrentFlightUser);
        });
        infWindow.setOnAction(view -> {
            InfInfoWindow window = new InfInfoWindow(mCurrentFlightUser);
            window.showWindow();
        });

        // SP
        mSpServiceArea = (VBox) mRoot.lookup("#specialService");
        mSaveSp = (Button) mRoot.lookup("#saveSp");
        mSpStatus = (ComboBox<String>) mRoot.lookup("#spComboBox");
        mSaveSp.setOnAction(view -> {
            String sp = mSpStatus.getSelectionModel().getSelectedItem();
            if (sp == null) {
                sp = "";
            }
            mCurrentFlightUser.setSpService(sp.trim());
            CloudApi.getInstance().uploadFlightUser(mCurrentFlightUser, new CommonCallback() {
                @Override
                public void success(String success) {
                    Platform.runLater(() -> {
                        ToastUtils.toast(ResourceUtils.getString("saveSuccess"));
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

        // package
        mPackageContainer = (VBox) mRoot.lookup("#packageEntity");
        mPackageTable = (TableView) mRoot.lookup("#packageTable");
        mDeletePackage = (Button) mRoot.lookup("#deleteBoard");
        mPackageWindow = (PackageAddWindow) mRoot.lookup("#packageWindow");
        ViewUtils.setTableEmpty(mPackageTable);
        addBoard.setOnAction(view -> {
            ToastUtils.toast(ResourceUtils.getString("toBeDevelop"));
        });
        addPackageList.setOnAction(view -> {
            ToastUtils.toast(ResourceUtils.getString("toBeDevelop"));
        });
        mWindowCallback = obj -> {
            if (isNotValidCheckInStatus() || !(obj instanceof AirPackage)) {
                showWarning();
                return;
            }
            AirPackage airPackage = (AirPackage) obj;
            airPackage.setFlightNumber(mFlightInfo.getFlightNumber());
            airPackage.setFlightDate(mFlightInfo.getDate());
            airPackage.setTicketNumber(mCurrentFlightUser.getTicketNumber());
            CloudApi.getInstance().uploadPackageInfo(airPackage, new CommonCallback() {
                @Override
                public void success(String success) {
                    Platform.runLater(() -> {
                        addTableData(mPackageTable, getAirPackages(airPackage));
                    });
                }

                @Override
                public void fail(String fail) {
                    Platform.runLater(() -> {
                        ToastUtils.toast(ResourceUtils.getString("saveFailed2") + fail);
                    });
                }
            });
        };
        mPackageWindow.setCallback(mWindowCallback);

        // seat
        mSeatsContainer = (HBox) mRoot.lookup("#seats");
        mAssignSeatInput = (TextField) mRoot.lookup("#assignSeatInput");
        mSeatTip = (Label) mRoot.lookup("#seatTip");

        // pnl
        mPnl = (TextArea) mRoot.lookup("#pnl");

        // log
        mLogTable = (TableView<Record>) mRoot.lookup("#userLog");
        ViewUtils.setTableEmpty(mLogTable);
        mSubmit.setOnAction(btn -> {
            if (isNotValidCheckInStatus()) {
                showWarning();
                return;
            }

            // 保存当前用户信息
            setGroupInfo(mCurrentFlightUser);
            submit();
        });
    }

    private void submit() {
        if (checkCondition()) {
            CountDownLatch countDownLatch = new CountDownLatch(UsersChooseManager.getInstance().getCurrentUsers().size());
            int errCode = checkSeatValid();
            if (errCode == -1) {
                // 有人没选座
                ToastUtils.toast(ResourceUtils.getString("seatNotSelect"));
                return;
            } else if (errCode == -2) {
                ToastUtils.toast(ResourceUtils.getString("duplicateSeat"));
                return;
            }
            for (FlightUser user : UsersChooseManager.getInstance().getCurrentUsers()) {
                saveUsers(countDownLatch, user);
            }
        }
    }

    private int checkSeatValid() {
        HashSet<String> chosenSeats = new HashSet<>();
        for (FlightUser user : UsersChooseManager.getInstance().getCurrentUsers()) {
            if (TextUtils.isEmpty(user.getSeatNumber())) {
                return -1;
            } else {
                if (chosenSeats.contains(user.getSeatNumber())) {
                    return -2;
                }
                chosenSeats.add(user.getSeatNumber());
            }
        }
        return 0;
    }

    private void setSaveApi(FlightUser user) {
        setGroupInfo(user);
        CloudApi.getInstance().uploadFlightUser(user, new CommonCallback() {
            @Override
            public void success(String success) {
                Platform.runLater(() -> {
                    ToastUtils.toast(ResourceUtils.getString("saveSuccess"));
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

    private void saveUsers(CountDownLatch countDownLatch, FlightUser user) {
        CloudApi.getInstance().uploadFlightUser(user, new CommonCallback() {
            @Override
            public void success(String success) {
                countDownLatch.countDown();
                if (countDownLatch.getCount() == 0) {
                    Platform.runLater(() -> {
                        ToastUtils.toast(ResourceUtils.getString("saveSuccess"));
                        PageRouter.jumpToPage(2);
                    });
                }
            }

            @Override
            public void fail(String fail) {
                countDownLatch.countDown();
                Platform.runLater(() -> {
                    if (TextUtils.equals(fail, "-2")) {
                        ToastUtils.toast(ResourceUtils.getString("seatAlreadyChosen"));
                        return;
                    }
                    ToastUtils.toast(ResourceUtils.getString("saveFailed2") + fail);
                });
            }
        });
    }

    private void setGroupInfo(FlightUser user) {
        user.setMiddleName(middleName.getText());
        user.setName(mLastName.getText() + "/" + mFirstName.getText());
        user.setGender(user.getGender());
        user.setIssueDate(mIssueDate.getText());
        user.setValidityPeriod(mExpireDate.getText());
        user.setBirthDate(mBirthday.getText());
        user.setCountry(mCountry.getText());
    }

    private boolean checkCondition() {
        TextField[] checkList = new TextField[]{
                mFirstName, mLastName, mGender, mCountry, mBirthday, mPassportNumber, mAssignSeatInput
        };
        for (TextField textField : checkList) {
            if (textField == null) {
                ToastUtils.toast(ResourceUtils.getString("invalidFormat"));
                return false;
            }
            if (TextUtils.isEmpty(textField.getText())) {
                ToastUtils.toast(ResourceUtils.getString("completeTip"));
                return false;
            }
        }
        return true;
    }

    private void refreshFlightInfo() {
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
                Platform.runLater(() -> {
                    calculateFlightSeats();
                    FlightModelData.getInstance().setFlightInfo(mFlightInfo);
                    refreshAfterInfo();
                });
            }

            @Override
            public void fail(String fail) {
                System.out.println(fail);
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

    @Override
    void initData() {
        tabPaneArea.getSelectionModel().select(0);
        refreshFlightInfo();
    }

    private void refreshAfterInfo() {
        if (mFlightInfo == null) {
            return;
        }
        mFlightBoard.setFlightInfo(mFlightInfo);
        initTableSchema();
        refreshCustomTableData(UsersChooseManager.getInstance().getCurrentUsers());
        initPackageData();
        initSpService();
        initSeats();

        // 默认选中第一个
        mCustomRegisterTable.getSelectionModel().select(0);
        mCurrentFlightUser = mCustomRegisterTable.getSelectionModel().getSelectedItem();
        if (mCurrentFlightUser == null) {
            return;
        }
        onUserChosen();
    }

    private void onUserChosen() {
        refreshApi(mCurrentFlightUser);
        refreshPackage(mCurrentFlightUser);
        refreshSeat(mCurrentFlightUser);
        refreshSp(mCurrentFlightUser);
        refreshPnl(mCurrentFlightUser);
        refreshLog(mCurrentFlightUser);
    }

    private void refreshSp(FlightUser flightUser) {
        if (flightUser == null || TextUtils.isEmpty(flightUser.getSpService())) {
            mSpStatus.getSelectionModel().select(-1);
            mSpStatus.setPromptText(ResourceUtils.getString("unChoose"));
            return;
        }
        mSpStatus.getSelectionModel().select(flightUser.getSpService());
    }

    private void refreshLog(FlightUser flightUser) {
        if (flightUser == null) {
            return;
        }
        mLogTable.getItems().clear();
        CloudApi.getInstance().queryUserLog(flightUser, new CommonCallback() {
            @Override
            public void success(String success) {
                Platform.runLater(() -> {
                    try {
                        List<Record> records = JSONArray.parseArray(success, Record.class);
                        if (records == null || records.isEmpty()) {
                            return;
                        }
                        refreshTableData(mLogTable, records);
                    } catch (JSONException EX) {
                        ToastUtils.toast(ResourceUtils.getString("refreshFailed"));
                    }
                });
            }

            @Override
            public void fail(String fail) {
                Platform.runLater(() -> {
                    ToastUtils.toast(ResourceUtils.getString("refreshFailed2") + fail);
                });
            }
        });
    }

    private void refreshPnl(FlightUser flightUser) {
        if (flightUser == null) {
            mPnl.setText("");
            return;
        }
        mPnl.setText(flightUser.getPnl());
    }

    private void initSeats() {
        // 将w3,3;1-3/ABCDEF;Y3,3;4-6/ABCDEF; -["w3,3;1-3/ABCDEF","Y3,3;4-6/ABCDEF"];
        mSeatsContainer.getChildren().clear();
        if (mFlightInfo.getCustomLayout() == null) {
            mSeatTip.setText(ResourceUtils.getString("noLayoutNoBoarding"));
            return;
        } else {
            mSeatTip.setText(ResourceUtils.getString("plsChooseSeat"));
        }
        mFlightInfo.setSeatCallback(seatNumber -> {
            mAssignSeatInput.setText(seatNumber);
            mCurrentFlightUser.setSeatNumber(seatNumber);
        });
        mSeatsContainer.getChildren().addAll(mFlightInfo.getGridSeatsList());
    }

    private void refreshCustomTableData(List<FlightUser> flightUsers) {
        if (flightUsers == null || flightUsers.isEmpty()) {
            return;
        }
        ObservableList<FlightUser> data = FXCollections.observableArrayList();
        data.addAll(flightUsers);
        mCustomRegisterTable.setItems(data);
    }

    private void initPackageData() {
        initPackageTableColumns();
        mPackageTable.setRowFactory(tv -> {
            TableRow<AirPackage> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                mFocusChooseRow = row;
            });
            return row;
        });
        mDeletePackage.setOnAction(view -> {
            if (isNotValidCheckInStatus()) {
                showWarning();
                return;
            }
            deletePackage();
        });
    }

    private void showWarning() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(ResourceUtils.getString("warning"));
        alert.setHeaderText(ResourceUtils.getString(Constants.CHECKING_STATUS_MAP.get(mFlightInfo.getCheckInStatus())) + ", " + ResourceUtils.getString("saveFailed"));
        alert.show();
    }

    private void deletePackage() {
        if (mFocusChooseRow == null) {
            ToastUtils.toast(ResourceUtils.getString("plsChoosePackage"));
            return;
        }
        if (mFocusChooseRow != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(ResourceUtils.getString("deletePackageConfirm"));
            AirPackage airPackage = mFocusChooseRow.getItem();
            Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            VBox container = new VBox();

            HBox deleteNumber = new HBox();
            Label text1 = new Label(ResourceUtils.getString("plsEnterNumber"));
            Label red1 = new Label("*");
            red1.setStyle("-fx-text-fill:red");
            TextField textField1 = new TextField();
            textField1.setPromptText(ResourceUtils.getString("number"));
            deleteNumber.getChildren().addAll(text1, red1, textField1);

            HBox deleteWeight = new HBox();
            Label text2 = new Label(ResourceUtils.getString("plsEnterWeight"));
            Label red2 = new Label("*");
            red2.setStyle("-fx-text-fill:red");
            TextField textField2 = new TextField();
            textField2.setPromptText(ResourceUtils.getString("weight"));
            deleteWeight.getChildren().addAll(text2, red2, textField2);

            if (airPackage.getWeight() > 0) {
                textField2.setText(String.valueOf(airPackage.getWeight()));
            }
            container.getChildren().addAll(deleteNumber, deleteWeight);
            alert.setGraphic(container);
            ok.addEventFilter(ActionEvent.ACTION, event -> {
                String number = textField1.getText() == null ? null : textField1.getText().trim();
                if (TextUtils.isEmpty(number)) {
                    ToastUtils.toast(ResourceUtils.getString("plsEnterNumber"));
                    event.consume();
                    return;
                }
                if (mUserPackages == null || mUserPackages.isEmpty()) {
                    return;
                }
                if (!TextUtils.equals(mFocusChooseRow.getItem().getNumber(), number)) {
                    ToastUtils.toast(ResourceUtils.getString("wrongNumber"));
                    event.consume();
                    return;
                }
                if (TextUtils.isEmpty(textField2.getText())) {
                    ToastUtils.toast(ResourceUtils.getString("plsEnterWeight"));
                    event.consume();
                    return;
                }
                CloudApi.getInstance().deletePackage(mFocusChooseRow.getItem(), new CommonCallback() {
                    @Override
                    public void success(String success) {
                        Platform.runLater(() -> {
                            ToastUtils.toast(ResourceUtils.getString("deleteSuccess"));
                            mPackageTable.getItems().remove(mFocusChooseRow.getIndex());
                        });

                    }

                    @Override
                    public void fail(String fail) {
                        Platform.runLater(() -> {
                            ToastUtils.toast(ResourceUtils.getString("deleteFailed2") + fail);
                        });
                    }
                });

            });
            alert.show();
        }
    }

    private void initPackageTableColumns() {
        mPackageTable.getColumns().clear();
        List<String> key = new ArrayList<>(Arrays.asList("number", "weight", "toLocation"));
        List<String> keyName = ResourceUtils.getLangArray(key);
        for (int i = 0; i < keyName.size(); i++) {
            TableColumn<AirPackage, String> col = new TableColumn<>(keyName.get(i));
            col.setCellValueFactory(new PropertyValueFactory<>(key.get(i)));
            mPackageTable.getColumns().add(col);
        }
    }

    private void refreshTableData(TableView tableView, List items) {
        if (tableView == null) {
            return;
        }
        tableView.getItems().clear();
        ObservableList<AirPackage> data = FXCollections.observableArrayList();
        data.addAll(items);
        tableView.getItems().addAll(data);
    }

    private void addTableData(TableView<AirPackage> tableView, List<AirPackage> airPackages) {
        if (tableView == null) {
            return;
        }
        ObservableList<AirPackage> data = FXCollections.observableArrayList();
        data.addAll(airPackages);
        tableView.getItems().addAll(data);
    }

    private List<AirPackage> getAirPackages(AirPackage airPackage) {
        List<AirPackage> airPackages = new ArrayList<>();
        if (airPackage.getNum() > 1) {
            airPackages = splitAddPackage(airPackage);
        } else {
            airPackages.add(airPackage);
        }
        return airPackages;
    }

    private List<AirPackage> splitAddPackage(AirPackage airPackage) {
        ArrayList<AirPackage> list = new ArrayList<>();
        if (airPackage.getNum() > 1) {
            for (int i = 0; i < airPackage.getNum(); i++) {
                AirPackage splitPackage = null;
                try {
                    splitPackage = airPackage.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                splitPackage.setNumber(airPackage.getNumber() + ":" + i);
                splitPackage.setNum(1);
                list.add(splitPackage);
            }
        } else {
            list.add(airPackage);
        }
        return list;
    }

    private void initSpService() {
        mSpStatus.getItems().clear();
        String[] spServices = new String[]{
                "WCHR",
                "SPML",
                "DEPA",
                "DEPU",
                "VGML",
                "UMNR",
                "WCHC",
                "WCHS",
                "BSCT",
                "BLND",
                "AVML",
                "DBML"
        };
        for (String sp : spServices) {
            mSpStatus.getItems().add(sp);
        }
    }

    private void initTableSchema() {
        //将表格的列和类的属性进行绑定
        mCustomRegisterTable.getColumns().clear();
        List<String> key = new ArrayList<>(Arrays.asList("name", "ticketNumber", "position", "freeBudget", "recordNumber", "flight", "weight", "luggageNumber", "seatNumber", "order", "checkInStatus", "type", "country", "number", "gender", "birthDate", "validityPeriod"));
        List<String> keyName = ResourceUtils.getLangArray(key);
        for (int i = 0; i < keyName.size(); i++) {
            TableColumn<FlightUser, String> col = new TableColumn<>(keyName.get(i));
            col.setCellValueFactory(new PropertyValueFactory<>(key.get(i)));
            mCustomRegisterTable.getColumns().add(col);
        }
        mCustomRegisterTable.setRowFactory(tv -> {
            TableRow<FlightUser> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                // 先保存上一个用户的信息
                setGroupInfo(mCurrentFlightUser);
                mCurrentFlightUser = row.getItem();
                onUserChosen();
            });
            return row;
        });

        mLogTable.getColumns().clear();
        List<String> logKey = new ArrayList<>(Arrays.asList("account", "name", "flightNumber", "event", "ticketNumber", "content", "time"));
        List<String> logKeyName = ResourceUtils.getLangArray(logKey);
        for (int i = 0; i < logKeyName.size(); i++) {
            TableColumn<Record, String> col = new TableColumn<>(logKeyName.get(i));
            col.setCellValueFactory(new PropertyValueFactory<>(logKey.get(i)));
            mLogTable.getColumns().add(col);
        }
    }

    private void refreshSeat(FlightUser flightUser) {
        if (flightUser == null) {
            mAssignSeatInput.setText("");
            return;
        }
        if (TextUtils.isEmpty(flightUser.getSeatNumber())) {
            mAssignSeatInput.setText("");
            return;
        }
        mAssignSeatInput.setText(flightUser.getSeatNumber());
    }

    private void refreshPackage(FlightUser flightUser) {
        mPackageTable.getItems().clear();
        mPackageWindow.clear(mFlightInfo);
        mUserPackages.clear();
        if (flightUser == null) {
            return;
        }
        CloudApi.getInstance().queryPackages(flightUser.getCompany(), flightUser.getTicketNumber(), new CommonCallback() {
            @Override
            public void success(String data) {
                List<AirPackage> packages = JSONArray.parseArray(data, AirPackage.class);
                if (packages == null || packages.isEmpty()) {
                    return;
                }
                Platform.runLater(() -> {
                    mUserPackages = packages;
                    refreshTableData(mPackageTable, packages);
                });
            }

            @Override
            public void fail(String fail) {
                ToastUtils.toast(ResourceUtils.getString("queryFailed2") + fail);
            }
        });
    }

    private void refreshApi(FlightUser flightUser) {
        if (flightUser == null) {
            return;
        }
        middleName.setText("");
        if (flightUser.getName().contains("/")) {
            String[] names = flightUser.getName().split("/");
            mFirstName.setText(names[1].trim());
            mLastName.setText(names[0].trim());
        }
        middleName.setText(flightUser.getMiddleName());
        mCountry.setText(flightUser.getCountry());
        mGender.setText(flightUser.getGender());
        mExpireDate.setText(flightUser.getValidityPeriod());
        mIssueDate.setText(flightUser.getIssueDate());
        mPassportNumber.setText(flightUser.getNumber());
        mBirthday.setText(flightUser.getBirthDate());
        mAssignSeatInput.setText(flightUser.getSeatNumber());
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

    private boolean isNotValidCheckInStatus() {
        return mFlightInfo.getCheckInStatus() == -1 ||
                mFlightInfo.getCheckInStatus() == Constants.FLIGHT_CLOSE_NUM ||
                mFlightInfo.getCheckInStatus() == Constants.CHECKING_PAUSE_NUM ||
                mFlightInfo.getCheckInStatus() == Constants.CLOSE_CHECKING_NUM;
    }
}
