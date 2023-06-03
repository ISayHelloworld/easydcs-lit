package main.page;

import com.alibaba.fastjson.JSONArray;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import main.callback.CommonCallback;
import main.entity.FlightModel;
import main.entity.ModelExtend;
import main.seats.GridSeats;
import main.utils.CloudApi;
import main.utils.ResourceUtils;
import main.utils.TextUtils;
import main.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ManagerPage extends BasePageNode {
    @FXML
    private HBox typeSetting;

    private ComboBox<String> mFlightModelList;

    private TextField mLayoutInput;

    private Button mUploadLayout;

    private HBox mGridSeatContainer;

    private TextField mExtendInput;

    private ComboBox<String> mType;

    private HashMap<String, FlightModel> modelMap = new HashMap<>();

    private FlightModel currentModel;

    private ArrayList<String> modelList = new ArrayList<>();

    ArrayList<GridSeats> gridSeatsList = new ArrayList<>();

    @Override
    String getFxmLPath() {
        setId("tab_manager");
        return "managerPage";
    }

    @Override
    void initView() {
        mFlightModelList = (ComboBox) mRoot.lookup("#queryFlight");
        mLayoutInput = (TextField) mRoot.lookup("#layoutInput");
        mUploadLayout = (Button) mRoot.lookup("#uploadLayout");
        mGridSeatContainer = (HBox) mRoot.lookup("#seats");
        mExtendInput = (TextField) mRoot.lookup("#seatInput");
        mType = (ComboBox<String>) mRoot.lookup("#type");
        mLayoutInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (TextUtils.isEmpty(mLayoutInput.getText())) {
                typeSetting.setVisible(false);
                typeSetting.managedProperty().bind(typeSetting.visibleProperty());
            } else {
                typeSetting.setVisible(true);
                typeSetting.managedProperty().bind(typeSetting.visibleProperty());
            }
            refreshSeat();
        });
        mFlightModelList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!TextUtils.isEmpty(newValue)) {
                currentModel = modelMap.get(newValue);
                mLayoutInput.setText(currentModel.getLayout());
            }
        });
        mUploadLayout.setOnAction(view-> {
            if (!modelList.contains(mFlightModelList.getSelectionModel().getSelectedItem())) {
                addFlightSetting(true);
                // 不包含，说明新创建的
            } else {
                // 修改当前布局配置
                addFlightSetting(false);
            }
        });
        if (TextUtils.isEmpty(mLayoutInput.getText())) {
            typeSetting.setVisible(false);
            typeSetting.managedProperty().bind(typeSetting.visibleProperty());
        } else {
            typeSetting.setVisible(true);
            typeSetting.managedProperty().bind(typeSetting.visibleProperty());
        }
    }

    private void addFlightSetting(boolean isNew) {
        String type = mType.getSelectionModel().getSelectedItem();
        String input = mExtendInput.getText().trim();
        if (isNew) {
            currentModel = new FlightModel();
            currentModel.setCompany("8L");
        }
        if (!TextUtils.isEmpty(type)) {
            if (currentModel == null) {
                return;
            }
            if (!TextUtils.isValidSeatArr(input) && !TextUtils.isValidColArr(input)) {
                ToastUtils.toast(ResourceUtils.getString("invalidFormat"));
                return;
            }
            ModelExtend extend = currentModel.getModelExtendObj();
            if (extend == null) {
                extend = new ModelExtend();
            }
            int res = -1;
            String[] modifySeats = TextUtils.splitSeatByExpression(input, gridSeatsList);
            switch (type) {
                case "*":
                    res = extend.addModelStar(modifySeats, true);
                    break;
                case "C":
                    res = extend.addModelC(modifySeats);
                    break;
                case "E":
                    res = extend.addModelE(modifySeats);
                    break;
                case "X":
                    res = extend.addModelX(modifySeats);
                    break;
                default:
                    break;
            }
            if (res != 0) {
                return;
            }
            currentModel.setModelExtendObj(extend);
        }
        String number = mFlightModelList.getSelectionModel().getSelectedItem().trim();
        if (!TextUtils.isEmpty(number)) {
            currentModel.setModelNumber(number);
        }
        if (!TextUtils.isEmpty(mLayoutInput.getText())) {
            currentModel.setLayout(mLayoutInput.getText());
        }
        CloudApi.getInstance().uploadFlightModel(currentModel, new CommonCallback() {
            @Override
            public void success(String success) {
                Platform.runLater(()-> {
                    ToastUtils.toast(ResourceUtils.getString("saveSuccess"));
                    refreshSeat();
                    queryFlightModel();
                });
            }

            @Override
            public void fail(String fail) {
                Platform.runLater(()->{
                    ToastUtils.toast(ResourceUtils.getString("saveFailed2") + fail);
                });
            }
        });
    }

    private void refreshSeat() {
        mGridSeatContainer.getChildren().clear();
        gridSeatsList.clear();

        // 将input拆分成不同的仓位
        String[] cockpitInputs = TextUtils.getSplitCockpit(mLayoutInput.getText());

        // 每个仓单独生成
        for (int i = 0; i < cockpitInputs.length; i++) {
            String cockpitInput = cockpitInputs[i];
            GridSeats gridSeats = new GridSeats();
            gridSeats.initSeats(cockpitInput, null, i == 0, i == cockpitInputs.length - 1);
            gridSeats.setAlignment(Pos.CENTER);
            mGridSeatContainer.getChildren().add(gridSeats);
            gridSeatsList.add(gridSeats);
        }
        refreshE(currentModel);
    }

    private void refreshE(FlightModel flightModel) {
        if (flightModel == null) {
            return;
        }
        ModelExtend extend = flightModel.getModelExtendObj();
        if (extend == null) {
            return;
        }

        // 每个仓位都设置一遍
        for (int i = 0; i < mGridSeatContainer.getChildren().size(); i++) {
            Node node = mGridSeatContainer.getChildren().get(i);
            if (!(node instanceof GridSeats)) {
                return;
            }
            GridSeats gridSeats = (GridSeats) node;
            gridSeats.setExtend(extend);
        }
    }

    private void queryFlightModel() {
        String company = "8L";
        CloudApi.getInstance().queryFlightModels(company, new CommonCallback() {
            @Override
            public void success(String data) {
                List<FlightModel> flightModels = JSONArray.parseArray(data, FlightModel.class);
                if (flightModels == null) {
                    return;
                }
                modelMap.clear();
                modelList.clear();
                mFlightModelList.getItems().clear();
                for (FlightModel model : flightModels) {
                    if (!modelList.contains(model.getModelNumber())) {
                        modelList.add(model.getModelNumber());
                    }
                    modelMap.put(model.getModelNumber(), model);
                }
                Platform.runLater(() -> {
                    mFlightModelList.getItems().addAll(modelList);
                });
            }

            @Override
            public void fail(String fail) {
                System.out.println(fail);
            }
        });
    }

    @Override
    void initData() {
        mLayoutInput.setText("");
        queryFlightModel();
        List<String> types = Arrays.asList("E", "*");
        mType.getItems().clear();
        mType.getItems().addAll(types);
        mType.setPromptText(ResourceUtils.getString("unChoose"));
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
