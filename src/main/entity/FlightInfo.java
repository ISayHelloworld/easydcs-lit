package main.entity;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import main.callback.SeatCallback;
import main.seats.GridSeats;
import main.utils.Log;
import main.utils.TextUtils;
import main.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Data
public class FlightInfo {
    private List<FlightUser> flightUsers = new ArrayList<>();

    private String company;

    private String flightNumber;

    private String fromLocation;

    private String toLocation;

    private String date;

    private String planeNumber;

    private String boardingTime;

    private String boardingGate;

    private String takeoffTime;

    private String landTime;

    private int checkInStatus = -1;

    private int boardingStatus = -1;

    private String statusChangeTime;

    private String locationSetStr;

    private String layout;

    private String closeTime;

    private String remark;

    // ["A1","B3",...]
    private String usedSeats;

    // 基于模板合并的自定义数据状态
    private FlightModel customModelObj;

    private String customModel;

    private HashMap<String, ArrayList<String>> locationSetMap = new HashMap<>();

    private String csn;

    private List<GridSeats> gridSeatsList = new ArrayList<>();

    // 子仓->主仓映射表
    private HashMap<String, String> cockPitNameMap = new HashMap<>();

    public void initFlightInfoDate() {
        if (!TextUtils.isEmpty(locationSetStr)) {
            setPositionSet(locationSetStr);
        }
        String[] cockpitInputs = TextUtils.getSplitCockpit(getCustomLayout());
        clearGridSeats();
        if (cockpitInputs == null) {
            return;
        }
        for (int i = 0; i < cockpitInputs.length; i++) {
            String cockpitInput = cockpitInputs[i];
            GridSeats gridSeats = new GridSeats();
            gridSeats.initSeats(cockpitInput, this, i == 0, i == cockpitInputs.length - 1);
            gridSeats.setExtend(getCustomModelObj().getModelExtendObj());
            getGridSeatsList().add(gridSeats);
        }
    }

    public void setPositionSet(String words) {
        locationSetStr = words;
        String[] segment = words.split(" ");
        for (String seg : segment) {
            if (TextUtils.isEmpty(seg)) {
                continue;
            }
            String mainPosition = seg.split("/")[0];
            String subPositions = seg.split("/")[1];
            splitSubPositions(mainPosition, subPositions);
            ArrayList<String> arrayList = new ArrayList<>();
            for (Character character : subPositions.toCharArray()) {
                arrayList.add(String.valueOf(character));
            }
            locationSetMap.put(mainPosition, arrayList);
        }
    }

    private void splitSubPositions(String mainPosition, String subPositions) {
        for (Character chr : subPositions.toCharArray()) {
            cockPitNameMap.put(String.valueOf(chr), mainPosition);
        }
    }

    // 从model中获取自定义布局
    public String getCustomLayout() {
        if (customModelObj == null) {
            return null;
        }
        layout = customModelObj.getLayout();
        if (!TextUtils.isEmpty(layout) && !layout.endsWith(";")) {
            layout += ";";
        }
        return layout;
    }

    public String getPlaneNumber() {
        return planeNumber;
    }

    public void setPlaneNumber(String planeNumber) {
        this.planeNumber = planeNumber;
    }

    public String getBoardingTime() {
        return boardingTime;
    }

    public String[] getUsedSeatsArr() {
        if (TextUtils.isEmpty(usedSeats)) {
            return null;
        }
        String res = usedSeats.replaceAll("]", "").replaceAll("\\[", "").trim();
        String[] arr = res.split(",");
        for (int i = 0; i < arr.length; i++) {
            arr[i] = arr[i].trim();
        }
        return arr;
    }

    public void setCustomModelObj(FlightModel customModelObj) {
        this.customModelObj = customModelObj;
        customModel = JSON.toJSONString(customModelObj);
        Log.info(customModel);
    }

    public void setCustomModel(String customModelStr) {
        customModel = customModelStr;
        Log.info(customModel);
        this.customModelObj = JSON.parseObject(customModelStr, FlightModel.class);
    }

    public void setBoardingStatus(int status) {
        if (boardingStatus != status) {
            statusChangeTime = TimeUtils.getHourMinNow();
        }
        boardingStatus = status;
    }

    public void setCheckInStatus(int status) {
        if (checkInStatus != status) {
            statusChangeTime = TimeUtils.getHourMinNow();
        }
        checkInStatus = status;
    }

    public void clearGridSeats() {
        gridSeatsList.clear();
    }

    public void setSeatCallback(SeatCallback callback) {
        if (gridSeatsList == null || gridSeatsList.isEmpty()) {
            return;
        }
        for (GridSeats seats : gridSeatsList) {
            if (seats == null) {
                continue;
            }
            seats.setSeatCallback(callback);
        }
    }

    public ArrayList<String> getCockpitNames() {
        ArrayList<String> names = new ArrayList<>();
        for (GridSeats grid:gridSeatsList) {
            names.add(grid.getCockpit().getSubCockpitName());
        }
        return names;
    }
}