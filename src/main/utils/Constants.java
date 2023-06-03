package main.utils;

import java.util.HashMap;
import java.util.regex.Pattern;

public class Constants {
    public static final String VERSION = "1.1.4";
    public static final String CHECKED = "1";

    public static final String NOT_CHECKED = "0";

    public static final String BOARDED = "1";

    public static final String NOT_BOARDED = "0";

    public static final String MANAGER = "1";

    public static final int AUTO = 0;

    public static final int MANUAL = 1;

    public static final int NOT_BOARD_NUM = 0;
    public static final int BOARD_NUM = 1;
    public static final int BOARD_ALL = 2;

    // 值机
    public static final int CHECKING_START_NUM = 1;
    public static final int CHECKING_PAUSE_NUM = 2;
    public static final int CLOSE_CHECKING_NUM = 3;
    public static final int FLIGHT_CLOSE_NUM = 4;

    public static final String CHECKING_START = "startCheckIn";
    public static final String CHECKING_PAUSE = "pauseCheckIn";
    public static final String CHECKING_CLOSE_CHECKIN = "closeCheckIn";
    public static final String CHECKING_CLOSE = "closeFlight";

    // 登机
    public static final int BOARDING_NOT_START_NUM = -1;
    public static final int BOARDING_START_NUM = 1;
    public static final int BOARDING_CLOSE_NUM = 2;

    public static final String BOARDING_NOT_START = "notStartBoarding";
    public static final String BOARDING_START = "startBoarding";
    public static final String BOARDING_CLOSE = "closeBoarding";

    public static final HashMap<Integer, String> CHECKING_STATUS_MAP = new HashMap<>();

    public static final HashMap<String, Integer> CHECKING_STATUS_MAP2 = new HashMap<>();

    public static final HashMap<Integer, String> BOARDING_STATUS_MAP = new HashMap<>();

    public static final Pattern NUMBER_PATTERN = Pattern.compile("[\\d]*$");

    static {
        CHECKING_STATUS_MAP.put(-1, "notCheckinStart");
        CHECKING_STATUS_MAP.put(0, "notCheckinStart");
        CHECKING_STATUS_MAP.put(CHECKING_START_NUM, CHECKING_START);
        CHECKING_STATUS_MAP.put(CHECKING_PAUSE_NUM, CHECKING_PAUSE);
        CHECKING_STATUS_MAP.put(CLOSE_CHECKING_NUM, CHECKING_CLOSE_CHECKIN);
        CHECKING_STATUS_MAP.put(FLIGHT_CLOSE_NUM, CHECKING_CLOSE);

        CHECKING_STATUS_MAP2.put(CHECKING_START, CHECKING_START_NUM);
        CHECKING_STATUS_MAP2.put(CHECKING_PAUSE, CHECKING_PAUSE_NUM);
        CHECKING_STATUS_MAP2.put(CHECKING_CLOSE_CHECKIN, CLOSE_CHECKING_NUM);
        CHECKING_STATUS_MAP2.put(CHECKING_CLOSE, FLIGHT_CLOSE_NUM);

        BOARDING_STATUS_MAP.put(BOARDING_NOT_START_NUM, BOARDING_NOT_START);
        BOARDING_STATUS_MAP.put(BOARDING_START_NUM, BOARDING_START);
        BOARDING_STATUS_MAP.put(BOARDING_CLOSE_NUM, BOARDING_CLOSE);
    }


    // 优先座位
    public static final String SEAT_PRIORITY = "seatPriority";

    public static final String SEAT_SUB_PRIORITY = "seatC";

    // 紧急出口座位
    public static final String SEAT_EMERGENCY = "seatE";
    // 次优先座位(C)
    // 禁止座位
    public static final String SEAT_X = "seatX";

    // 孩子
    public static final String CHILD = "child";

    // 婴儿
    public static final String INF = "inf";
}
