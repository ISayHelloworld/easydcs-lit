package main.entity;

import lombok.Data;
import main.utils.Constants;
import main.utils.TextUtils;

@Data
public class FlightUser {
    // 1.姓名 2.年龄段(inf,child,adult) 2.中名 2.票号 3.仓位 4.免费行李额 5.记录编码 6.航段 7.证件信息 8.托运行李重量 9.行李牌号 10.座位号 11.是否为紧急出口（1为真 0位假）12.值机序号 13.值机状态表示（1.已登记，0.未登机） 14.特殊服务
    private String company;
    private String name;
    private String ageType;
    private String middleName;
    private String ticketNumber;
    private String position;
    private String freeBudget;
    private String recordNumber;
    private String flight;
    private String weight;
    private String luggageNumber;
    private String seatNumber;
    private int isEmergency;
    private String orderId;

    // 0未值机， 1已值机
    private String checkInStatus = "0";

    // 0未登机， 1已登机
    private String boardingStatus = "0";
    private String type;
    private String country;
    private String number;
    private String gender;
    private String birthDate;

    private String issueDate;
    private String validityPeriod;

    private String infName;

    private String infMiddleName;

    private String infType;
    private String infCountry;
    private String infNumber;
    private String infGender;
    private String infBirthDate;

    private String infIssueDate;
    private String infValidityPeriod;

    private String flightNumber;
    private String flightDate;
    private String pnl;
    private String spService;
    private String infTicketNumber;

    public boolean isChild() {
        return TextUtils.equals(ageType, Constants.CHILD);
    }

    public boolean hasInf() {
        return !TextUtils.isEmpty(infTicketNumber);
    }

    public void setPassportInfo(PassportInfo passportInfo) {
        type = passportInfo.getType();
        country = passportInfo.getCountry();
        number = passportInfo.getNumber();
        gender = passportInfo.getGender();
        birthDate = passportInfo.getBirthDate();
        validityPeriod = passportInfo.getValidityPeriod();
    }

    public void setInfPassportInfo(PassportInfo passportInfo) {
        infType = passportInfo.getType();
        infCountry = passportInfo.getCountry();
        infNumber = passportInfo.getNumber();
        infGender = passportInfo.getGender();
        infBirthDate = passportInfo.getBirthDate();
        infValidityPeriod = passportInfo.getValidityPeriod();
        infName = passportInfo.getName();
    }
}