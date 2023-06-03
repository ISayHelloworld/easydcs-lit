package main.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.Main;
import main.callback.CommonCallback;
import main.entity.FlightInfo;
import main.entity.FlightUser;
import main.seats.GridSeats;
import main.utils.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class FlightMoreStatusWindow extends Pane {
    private Parent mNode;

    private FlightInfo flightInfo;

    @FXML
    private Label flightAndDate;

    @FXML
    private Label fromTo;

    @FXML
    private Label status;

    @FXML
    private Label statusChangeTime;

    @FXML
    private Label modelNumber;

    @FXML
    private Label airplaneNumber;

    @FXML
    private Label csn;

    @FXML
    private Label cas;

    @FXML
    private Label avs;

    @FXML
    private Label gate;

    @FXML
    private Label bdt;

    @FXML
    private Label edt;

    @FXML
    private Label eta;

    @FXML
    private Label remark;

    @FXML
    private Label ren;

    @FXML
    private Label childRen;

    @FXML
    private Label infRen;

    @FXML
    private Label ret;

    @FXML
    private Label childRet;

    @FXML
    private Label infRet;

    @FXML
    private Label ckn;

    @FXML
    private Label childCkn;

    @FXML
    private Label infCkn;

    @FXML
    private Label ckt;

    @FXML
    private Label childCkt;

    @FXML
    private Label infCkt;

    @FXML
    private Label bag;

    @FXML
    private Label bdn;

    @FXML
    private Label childBdn;

    @FXML
    private Label infBdn;


    public FlightMoreStatusWindow(FlightInfo flightInfo) {
        super();
        this.flightInfo = flightInfo;
        initView();
    }

    private void initView() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ResourceUtils.getXmlUrl("flightMoreStatusWindow"));
        fxmlLoader.setResources(ResourceBundle.getBundle("language"));
        fxmlLoader.setController(this);
        try {
            mNode = fxmlLoader.load();
            StyleUtils.addStyleNode(mNode);
            getChildren().add(mNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshView() {
        flightAndDate.setText(flightInfo.getFlightNumber() + "/" + flightInfo.getDate());
        fromTo.setText(flightInfo.getFromLocation() + "-" + flightInfo.getToLocation());
        status.setText(ResourceUtils.getString(Constants.CHECKING_STATUS_MAP.get(flightInfo.getCheckInStatus())));
        TextUtils.safeSetStr(statusChangeTime, flightInfo.getStatusChangeTime());
        if (flightInfo.getCustomModelObj() != null) {
            TextUtils.safeSetStr(modelNumber, " " + flightInfo.getCustomModelObj().getModelNumber());
        }
        if (!TextUtils.isEmpty(flightInfo.getPlaneNumber())) {
            airplaneNumber.setText(" " + flightInfo.getPlaneNumber());
        }
        setSeatNums();
        TextUtils.safeSetStr(gate, flightInfo.getBoardingGate());
        TextUtils.safeSetStr(bdt, flightInfo.getBoardingTime());
        TextUtils.safeSetStr(edt, flightInfo.getTakeoffTime());
        TextUtils.safeSetStr(eta, flightInfo.getLandTime());
        TextUtils.safeSetStr(remark, flightInfo.getRemark());
        setUserNums();
        queryRefreshPackages();

    }

    private void queryRefreshPackages() {
        CloudApi.getInstance().queryFlightPackageSum(flightInfo.getCompany(), flightInfo.getFlightNumber(), flightInfo.getDate(), new CommonCallback() {
            @Override
            public void success(String success) {
                Platform.runLater(() -> {
                    bag.setText(success);
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

    private void calculateFlightSeats() {
        String[] cockpitInputs = TextUtils.getSplitCockpit(flightInfo.getCustomLayout());
        flightInfo.clearGridSeats();
        if (cockpitInputs == null) {
            return;
        }
        for (int i = 0; i < cockpitInputs.length; i++) {
            String cockpitInput = cockpitInputs[i];
            GridSeats gridSeats = new GridSeats();
            gridSeats.initSeats(cockpitInput, flightInfo, i == 0, i == cockpitInputs.length - 1);
            gridSeats.setExtend(flightInfo.getCustomModelObj().getModelExtendObj());
            flightInfo.getGridSeatsList().add(gridSeats);
        }
    }

    private void setSeatNums() {
        if (flightInfo.getGridSeatsList() == null || flightInfo.getGridSeatsList().isEmpty()) {
            return;
        }
        StringBuilder csnStr = new StringBuilder();
        StringBuilder casStr = new StringBuilder();
        StringBuilder avsStr = new StringBuilder();
        for (GridSeats grid : flightInfo.getGridSeatsList()) {
            if (grid == null) {
                continue;
            }
            grid.calculateNums();
            csnStr.append(grid.getCockpit().getSubCockpitName());
            csnStr.append(grid.getAllSeatNumber());
            csnStr.append("/");

            casStr.append(grid.getCockpit().getSubCockpitName());
            casStr.append(grid.getAllSeatNumber() - grid.getSeatXNumber());
            casStr.append("/");

            avsStr.append(grid.getCockpit().getSubCockpitName());
            avsStr.append(grid.getAvsNumber());
            avsStr.append("/");
        }
        csn.setText(combineCockPitNum(csnStr.toString()));
        cas.setText(combineCockPitNum(casStr.toString()));
        avs.setText(combineCockPitNum(avsStr.toString()));
    }

    private String combineCockPitNum(String cockPitNums) {
        // G1/T2/C2 -> Y3/W2
        String[] split = cockPitNums.split("/");
        HashMap<String, Integer> numMap = new HashMap();
        for (String str : split) {
            if (TextUtils.isEmpty(str)) {
                continue;
            }
            String subName = str.substring(0, 1);
            String mainName = flightInfo.getCockPitNameMap().get(subName);
            int num = Integer.parseInt(str.substring(1));
            int sum = numMap.getOrDefault(mainName, 0);
            sum += num;
            numMap.put(mainName, sum);
        }
        StringBuilder res = new StringBuilder();
        for (Map.Entry entry : numMap.entrySet()) {
            res.append(entry.getKey());
            res.append(entry.getValue());
            res.append("/");
        }
        return res.substring(0, res.length() - 1);
    }

    private void setUserNums() {
        int renChildNum = 0;
        int renInfNum = 0;

        int retChildNum = 0;
        int retInfNum = 0;

        int cknChildNum = 0;
        int cknInfNum = 0;

        int cktChildNum = 0;
        int cktInfNum = 0;

        int bdnChildNum = 0;
        int bdnInfNum = 0;

        List<FlightUser> flightUsers = flightInfo.getFlightUsers();
        HashMap<String, Integer> cockPitNumRenMap = new HashMap<>();
        HashMap<String, Integer> cockPitNumRetMap = new HashMap<>();
        HashMap<String, Integer> cknNumMap = new HashMap<>();
        HashMap<String, Integer> cktNumMap = new HashMap<>();
        HashMap<String, Integer> boardedNumMap = new HashMap<>();
        for (FlightUser flightUser : flightUsers) {
            // ckn 已值机人数 ckt 已值机电子票人数
            if (TextUtils.equals(flightUser.getCheckInStatus(), Constants.CHECKED)) {
                int userNum = cknNumMap.getOrDefault(flightUser.getPosition(), 0);
                userNum++;
                cknNumMap.put(flightUser.getPosition(), userNum);
                if (flightUser.isChild()) {
                    cknChildNum++;
                }
                if (flightUser.hasInf()) {
                    cknInfNum++;
                }
                if (!TextUtils.isEmpty(flightUser.getTicketNumber())) {
                    int cktNum = cktNumMap.getOrDefault(flightUser.getPosition(), 0);
                    cktNum++;
                    cktNumMap.put(flightUser.getPosition(), cktNum);
                    if (flightUser.isChild()) {
                        cktChildNum++;
                    }
                    if (flightUser.hasInf()) {
                        cktInfNum++;
                    }
                }
            }

            // ren 总订票人数
            int userNum = cockPitNumRenMap.getOrDefault(flightUser.getPosition(), 0);
            userNum++;
            cockPitNumRenMap.put(flightUser.getPosition(), userNum);
            if (flightUser.isChild()) {
                renChildNum++;
                if (!TextUtils.isEmpty(flightUser.getTicketNumber())) {
                    retChildNum++;
                }
            }
            if (flightUser.hasInf()) {
                renInfNum++;
                if (!TextUtils.isEmpty(flightUser.getTicketNumber())) {
                    retInfNum++;
                }
            }

            // ret 总定电子票人数
            if (!TextUtils.isEmpty(flightUser.getTicketNumber())) {
                int retUserNum = cockPitNumRetMap.getOrDefault(flightUser.getPosition(), 0);
                retUserNum++;
                cockPitNumRetMap.put(flightUser.getPosition(), retUserNum);
                if (flightUser.isChild()) {
                    retChildNum++;
                    if (!TextUtils.isEmpty(flightUser.getTicketNumber())) {
                        retInfNum++;
                    }
                }
                if (flightUser.hasInf()) {
                    retInfNum++;
                    if (!TextUtils.isEmpty(flightUser.getTicketNumber())) {
                        retInfNum++;
                    }
                }
            }

            // bdn 已登机人数
            if (TextUtils.equals(flightUser.getBoardingStatus(), Constants.BOARDED)) {
                int boardedNum = boardedNumMap.getOrDefault(flightUser.getPosition(), 0);
                boardedNum++;
                boardedNumMap.put(flightUser.getPosition(), boardedNum);
                if (flightUser.isChild()) {
                    bdnChildNum++;
                }
                if (flightUser.hasInf()) {
                    bdnInfNum++;
                }
            }
        }
        StringBuilder renStr = new StringBuilder();
        StringBuilder retStr = new StringBuilder();
        StringBuilder cknStr = new StringBuilder();
        StringBuilder cktStr = new StringBuilder();
        StringBuilder bdnStr = new StringBuilder();
        for (Map.Entry entry : cockPitNumRenMap.entrySet()) {
            renStr.append(entry.getKey());
            renStr.append(entry.getValue());
            renStr.append("/");
        }
        for (Map.Entry entry : cockPitNumRetMap.entrySet()) {
            retStr.append(entry.getKey());
            retStr.append(entry.getValue());
            retStr.append("/");
        }
        for (Map.Entry entry : cknNumMap.entrySet()) {
            cknStr.append(entry.getKey());
            cknStr.append(entry.getValue());
            cknStr.append("/");
        }
        for (Map.Entry entry : cktNumMap.entrySet()) {
            cktStr.append(entry.getKey());
            cktStr.append(entry.getValue());
            cktStr.append("/");
        }
        for (Map.Entry entry : boardedNumMap.entrySet()) {
            bdnStr.append(entry.getKey());
            bdnStr.append(entry.getValue());
            bdnStr.append("/");
        }
        if (TextUtils.isEmpty(renStr.toString())) {
            ren.setText("0");
        } else {
            renStr = new StringBuilder(renStr.substring(0, renStr.length() - 1));
            ren.setText(combineCockPitNum(renStr.toString()));
        }
        childRen.setText(String.valueOf(renChildNum));
        infRen.setText(String.valueOf(renInfNum));

        if (TextUtils.isEmpty(retStr.toString())) {
            ret.setText("0");
        } else {
            retStr = new StringBuilder(retStr.substring(0, retStr.length() - 1));
            ret.setText(combineCockPitNum(retStr.toString()));
        }
        childRet.setText(String.valueOf(retChildNum));
        infRet.setText(String.valueOf(retInfNum));

        if (TextUtils.isEmpty(cknStr.toString())) {
            ckn.setText("0");
        } else {
            cknStr = new StringBuilder(cknStr.substring(0, cknStr.length() - 1));
            ckn.setText(combineCockPitNum(cknStr.toString()));
        }
        childCkn.setText(String.valueOf(cknChildNum));
        infCkn.setText(String.valueOf(cknInfNum));

        if (TextUtils.isEmpty(cktStr.toString())) {
            ckt.setText("0");
        } else {
            cktStr = new StringBuilder(cktStr.substring(0, cktStr.length() - 1));
            ckt.setText(combineCockPitNum(cktStr.toString()));
        }
        childCkt.setText(String.valueOf(cktChildNum));
        infCkt.setText(String.valueOf(cktInfNum));


        if (TextUtils.isEmpty(bdnStr.toString())) {
            bdn.setText("0");
        } else {
            bdnStr = new StringBuilder(bdnStr.substring(0, bdnStr.length() - 1));
            bdn.setText(combineCockPitNum(bdnStr.toString()));
        }
        childBdn.setText(String.valueOf(bdnChildNum));
        infBdn.setText(String.valueOf(bdnInfNum));
    }

    public void showWindow() {
        Stage stage = new Stage();
        stage.initOwner(Main.getStage());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        Scene scene = new Scene(this, 850, 200);
        stage.setScene(scene);
        stage.setTitle(ResourceUtils.getString("flightStatus"));
        stage.show();
        refreshView();
    }
}
