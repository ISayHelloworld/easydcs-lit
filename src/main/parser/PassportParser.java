package main.parser;

import main.entity.PassportInfo;
import main.utils.TextUtils;

public class PassportParser implements Parser {
    // 护照信息
    private static final String mTag = ".R/DOCS";

    public PassportParser() {
    }

    @Override
    public PassportInfo parse(String data) {
        // 将行数据做分割处理并输出
        // HK1/P/CN/G51158218/CN/17FEB86/M/18MAY21/XU/JIA MR
        // HK1/P/MN/E1558049/MN/14MAY92/F/13SEP20/RAVDANSAMBUU
        // 护照类型/国籍/护照号码/出生日期/性别/护照有效期/姓名
        PassportInfo passportInfo = new PassportInfo();
        if (TextUtils.isEmpty(data)) {
            return passportInfo;
        }
        try {
            String[] words = data.split("/");
            passportInfo.setType(words[1]);
            passportInfo.setCountry(words[2]);
            passportInfo.setNumber(words[3]);
            passportInfo.setBirthDate(words[5]);
            passportInfo.setGender(words[6]);
            passportInfo.setValidityPeriod(words[7]);
            StringBuilder name = new StringBuilder(words[8]);
            for (int i = 9; i < words.length; i++) {
                name.append("/");
                name.append(words[i]);
            }
            passportInfo.setName(name.toString());
        } catch (IndexOutOfBoundsException ex) {
            System.out.println("passport error: " + data);
        }
        return passportInfo;
    }

    @Override
    public String getTag() {
        return mTag;
    }
}
