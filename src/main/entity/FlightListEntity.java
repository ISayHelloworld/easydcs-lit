package main.entity;

import main.utils.Constants;
import main.utils.ResourceUtils;

// 该方案只能处理已关闭的情况，因为str和entity需要建立强关联可互换关系
public class FlightListEntity {
    private String mFlightNumber;

    private int mFlightBoardStatus = -1;

    public String getFlightWithStatus() {
        if (mFlightBoardStatus == Constants.FLIGHT_CLOSE_NUM) {
            return mFlightNumber + ResourceUtils.getString("closed");
        }
        return mFlightNumber;
    }

    public String getFlightNumber() {
        return mFlightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.mFlightNumber = flightNumber;
    }

    public int getFlightBoardStatus() {
        return mFlightBoardStatus;
    }

    public void setFlightBoardStatus(int flightBoardStatus) {
        this.mFlightBoardStatus = flightBoardStatus;
    }

}
