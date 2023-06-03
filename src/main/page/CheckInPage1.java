package main.page;

import com.alibaba.fastjson.JSONArray;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import main.callback.CommonCallback;
import main.entity.FlightInfo;
import main.entity.FlightListEntity;
import main.manager.AccountManager;
import main.manager.PageRouter;
import main.model.FlightModelData;
import main.utils.*;
import main.view.FlightBoard;
import main.view.FlightBoardChooseWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CheckInPage1 extends BasePageNode {
    private Button mNext;

    private ComboBox<FlightListEntity> mFlightQueryList;

    private Label mQueryTip;

    @FXML
    private VBox flightBoardContainer;

    private DatePicker mFromDate;

    private DatePicker mToDate;

    @FXML
    private FlightBoard flightBoard;

    @Override
    String getFxmLPath() {
        setId("tab_checking");
        return "checkInPage1";
    }

    @Override
    void initView() {
        mFlightQueryList = (ComboBox) mRoot.lookup("#queryFlight");
        mFromDate = (DatePicker) mRoot.lookup("#datePickFrom");
        mToDate = (DatePicker) mRoot.lookup("#datePickTo");
        mNext = (Button) mRoot.lookup("#nextStep");
        mQueryTip = (Label) mRoot.lookup("#queryResult");
        mNext.setOnAction(view -> {
            FlightInfo flightInfo = flightBoard.getFlightInfo();
            if (flightInfo == null) {
                ToastUtils.toast(ResourceUtils.getString("plsChooseFlight"));
                return;
            }
            FlightModelData.getInstance().setFlightInfo(flightInfo);
            PageRouter.jumpToPage(2);
        });
        Button query = (Button) mRoot.lookup("#query");
        query.setOnAction(event -> {
            queryFlightFromCloud();
        });
    }

    private void queryFlightFromCloud() {
        int errCode = checkSearchCondition();
        switch (errCode) {
            case 0:
                FlightModelData.getInstance().clear();
                flightBoard.clear();
                queryFromCloud();
                break;
            case -2:
                mQueryTip.setText(ResourceUtils.getString("plsEnterToDate"));
                break;
            case -3:
                mQueryTip.setText(ResourceUtils.getString("plsEnterFromDate"));
                break;
            default:
                mQueryTip.setText(ResourceUtils.getString("plsEnterFlightOrDate"));
        }
    }


    private void queryFromCloud() {
        String flightNumber = mFlightQueryList.getValue().getFlightNumber();
        CloudApi.getInstance().queryFlightInfos(AccountManager.getInstance().getCompany(), flightNumber, new CommonCallback() {
            @Override
            public void success(String data) {
                List<FlightInfo> flightInfoList = JSONArray.parseArray(data, FlightInfo.class);
                Platform.runLater(() -> {
                    if (flightInfoList == null || flightInfoList.isEmpty()) {
                        mQueryTip.setText(ResourceUtils.getString("queryFailed"));
                        System.out.println("flightInfo is null");
                        return;
                    }
                    if (flightInfoList.size() == 1) {
                        flightBoard.setFlightInfo(flightInfoList.get(0));
                    } else {
                        FlightBoardChooseWindow.showWindow(flightInfoList, flightBoard, null);
                    }
                    mQueryTip.setText(ResourceUtils.getString("querySuccess"));
                });
            }

            @Override
            public void fail(String fail) {
                System.out.println(fail);
            }
        });
    }

    private String getDateStr(DatePicker picker) {
        if (picker.getValue() == null) {
            return "";
        } else {
            return picker.getValue().toString();
        }
    }

    private int checkSearchCondition() {
        if (mFlightQueryList.getValue() == null) {
            return -1;
        }
        if (!TextUtils.isEmpty(mFlightQueryList.getValue().getFlightNumber())) {
            return 0;
        }
        if (!TextUtils.isEmpty(getDateStr(mFromDate)) && !TextUtils.isEmpty(getDateStr(mToDate))) {
            return 0;
        }
        if (!TextUtils.isEmpty(getDateStr(mFromDate)) && TextUtils.isEmpty(getDateStr(mToDate))) {
            return -2;
        }
        if (TextUtils.isEmpty(getDateStr(mFromDate)) && !TextUtils.isEmpty(getDateStr(mToDate))) {
            return -3;
        }
        return -1;
    }

    @Override
    void initData() {
        flightBoard.clear();
        mQueryTip.setText(ResourceUtils.getString("plsChooseFlight"));
        initFlightList();
    }


    private void initFlightList() {
        // 查询上传过的航班
        CloudApi.getInstance().queryAllFlightInfo(AccountManager.getInstance().getCompany(), new CommonCallback() {
            @Override
            public void success(String success) {
                ViewUtils.setFlightListView(mFlightQueryList, success);
            }

            @Override
            public void fail(String fail) {
            }
        });
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
