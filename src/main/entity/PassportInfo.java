package main.entity;

import lombok.Data;

@Data
public class PassportInfo {
    // 护照类型/国籍/护照号码/性别/出生日期/护照有效期/姓名
    private String type;
    private String country;
    private String number;
    private String gender;
    private String birthDate;
    private String validityPeriod;
    private String name;
}
