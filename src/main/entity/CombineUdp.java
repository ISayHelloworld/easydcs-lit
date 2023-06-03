package main.entity;

import main.parser.CustomerParser;
import main.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CombineUdp {
    private boolean mIsIntegrate = false;
    private int mErrorCode;
    private ArrayList<String> combineArr = new ArrayList<>();
    private static final String START_PATTERN = " PART\\d+";
    private static final String END_PATTERN = "ENDPART\\d+";
    private static final String FINAL_END = "ENDPNL";
    private static final String RBD = "RBD";
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");
    private static final String END = "END";
    private final String[] mLines;
    private List<PassportInfo> mPassports = new ArrayList<>();
    private FlightInfo mFlightInfo = new FlightInfo();
    private List<FlightUser> mFlightUsers = new ArrayList<>();

    // 仓位
    private String mCurrentPosition;
    private String mErrorRes = "";

    public CombineUdp(String[] lines) {
        mLines = lines;
    }

    public void fillFlightInfo() {
        if (mLines == null || mLines.length == 0) {
            return;
        }
        mFlightUsers.clear();
        StringBuilder customerStr = new StringBuilder();
        CustomerParser customerParser = new CustomerParser();
        int lineIndex = -1;
        int locationIndex = -1;
        for (String line : mLines) {
            lineIndex++;
            if (line == null || TextUtils.isEmpty(line)) {
                continue;
            }
            line = line.trim();

            // 通用数据预处理
            if (line.endsWith(" +") || line.endsWith(" -")) {
                // 删除udp报文中特殊的+ -结束符
                line = line.substring(0, line.length() - 1);
            }

            if (line.startsWith(RBD)) {
                mFlightInfo.setPositionSet(line.replace(RBD, ""));
            }

            // 处理航班信息数据
            if (line.contains("AVAIL")) {
                locationIndex = lineIndex + 1;
            } else if (line.endsWith(" PART1")) {
                String infoWord = line.split(" ")[0];
                String flightNumber = infoWord.split("/")[0];
                mFlightInfo.setFlightNumber(flightNumber);
                mFlightInfo.setCompany(flightNumber.substring(0, 2));
                mFlightInfo.setDate(infoWord.split("/")[1]);
            }
            if (lineIndex == locationIndex) {
                mFlightInfo.setFromLocation(line.split(" ")[0]);
                mFlightInfo.setToLocation(line.split(" ")[1]);
            }

            // 更新当前仓位信息
            if (line.matches("-" + mFlightInfo.getToLocation() + "\\d+([a-zA-Z])")) {
                Matcher matcher = NUMBER_PATTERN.matcher(line);
                if (matcher.find()) {
                    try {
                        if (Integer.parseInt(matcher.group()) > 0) {
                            mCurrentPosition = line.substring(line.length() - 1);
                        }
                    } catch (NumberFormatException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }

            // 收集用户原始数据
            if (line.startsWith("1")) {
                String res = customerStr.toString();
                if (!TextUtils.isEmpty(res) && res.startsWith("1")) {
                    setFlightInfo(customerParser, res);
                }
                customerStr = new StringBuilder();
            }
            customerStr.append(line);
            customerStr.append("\n");
        }

        // 处理最后一个人
        setFlightInfo(customerParser, customerStr.toString());
        mFlightInfo.setFlightUsers(mFlightUsers);
    }

    private void setFlightInfo(CustomerParser customerParser, String string) {
        FlightUser user = customerParser.parse(string);
        user.setFlightNumber(mFlightInfo.getFlightNumber());
        user.setCompany(mFlightInfo.getCompany());
        user.setPosition(mCurrentPosition);
        user.setFlightDate(mFlightInfo.getDate());
        mFlightUsers.add(user);
    }

    public void checkIntegrated() {
        if (mLines == null || mLines.length == 0) {
            return;
        }
        Pattern startP = Pattern.compile(START_PATTERN);
        Pattern endP = Pattern.compile(END_PATTERN);
        Stack<String> stack = new Stack<>();
        for (String line : mLines) {
            if (line == null) {
                continue;
            }
            line = line.trim();
            Matcher startMatcher = startP.matcher(line);
            if (startMatcher.find()) {
                stack.push(END + line.substring(startMatcher.start(), startMatcher.end()).trim());
            } else if (endP.matcher(line).find()) {
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            } else if (line.contains(FINAL_END)) {
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            } else {
                combineArr.add(line);
            }
        }
        StringBuilder builder = new StringBuilder("数据完整性校验结果:");
        if (stack.isEmpty()) {
            mErrorCode = 0;
            mIsIntegrate = true;
            builder.append("数据完整");
        } else {
            builder.append(System.lineSeparator());
            while (!stack.isEmpty()) {
                builder.append("报文缺失:");
                builder.append(stack.pop());
                builder.append(System.lineSeparator());
            }
            mErrorCode = -1;
        }
        mErrorRes = builder.toString();
        System.out.println(builder);
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public boolean isIsIntegrate() {
        return mIsIntegrate;
    }

    public List<PassportInfo> getPassports() {
        return mPassports;
    }

    public List<FlightUser> getCustomers() {
        return mFlightUsers;
    }

    public FlightInfo getFlightInfo() {
        return mFlightInfo;
    }

    public String getErrorRes() {
        return mErrorRes;
    }
}