package main.parser;

import main.entity.FlightUser;
import main.entity.PassportInfo;
import main.utils.Constants;
import main.utils.TextUtils;

public class CustomerParser implements Parser {
    public CustomerParser() {
    }

    @Override
    public FlightUser parse(String data) {
        // 8L866/25JUL KOS PART1
        // AVAIL
        // KOS CTU
        // 1ZHONG/GUOMIN .L/MDTD51 .R/TKNE HK1 8592400779865/1
        //.R/FBA  20K
        //.R/DOCS HK1/P/CN/EF2187057/CN/03JUN68/M/30JAN29/ZHONG/GUOMIN
        //.R/CTCT1326825700 .R/CTCM13478871428
        //.R/CTC SHE335-22JUL19-1458-T SHE/SHE/T 024-22502222/SHE XIANG RUI AIR
        //.RN/- SERVICE CO  LTD/  .R/CTC T SHE/DU YAN
        FlightUser flightUser = new FlightUser();
        if (TextUtils.isEmpty(data)) {
            return flightUser;
        }
        String[] lines = data.split("\n");
        StringBuilder pnl = new StringBuilder();
        for (String line : lines) {
            if (line.startsWith(".") || line.startsWith("1")) {
                pnl.append(line).append("\n");
            }
            String[] splitRes = line.split(" ");
            for (int i = 0; i < splitRes.length; i++) {
                String word = splitRes[i].trim();
                if (i == 0 && word.startsWith("1")) {
                    String name = word.replaceFirst("1", "");
                    int groupIndex = name.indexOf("-");
                    if (groupIndex != -1) {
                        name = name.substring(0, groupIndex);
                    }
                    flightUser.setName(name);
                    continue;
                }
                if (TextUtils.equals(word, ".R/FBA")) {
                    flightUser.setFreeBudget(nextWords(splitRes, i));
                    continue;
                }
                if (TextUtils.equals(word, ".R/TKNE")) {
                    String nextWord = nextWords(splitRes, i);
                    if (nextWord.endsWith("INF")) {
                        flightUser.setInfTicketNumber(nextWord);
                    } else {
                        flightUser.setTicketNumber(nextWord);
                    }
                    continue;
                }
                if (TextUtils.equals(word, ".R/CHLD")) {
                    flightUser.setAgeType(Constants.CHILD);
                    continue;
                }
                if (TextUtils.equals(word, ".R/DOCS")) {
                    PassportParser passportParser = new PassportParser();
                    String nextWords = nextWords(splitRes, i);
                    if (nextWords.endsWith("INF")) {
                        int index = nextWords.lastIndexOf("INF");
                        nextWords = nextWords.substring(0, index).trim();
                        PassportInfo passportInfo = passportParser.parse(nextWords);
                        flightUser.setInfPassportInfo(passportInfo);
                    } else {
                        PassportInfo passportInfo = passportParser.parse(nextWords);
                        flightUser.setPassportInfo(passportInfo);
                    }
                    continue;
                }
                if (word.startsWith(".L/")) {
                    flightUser.setRecordNumber(word.replaceFirst(".L/", ""));
                }
            }
        }
        flightUser.setPnl(pnl.toString());
        return flightUser;
    }

    @Override
    public String getTag() {
        return "";
    }

    private String nextWords(String[] str, int index) {
        StringBuilder res = new StringBuilder();
        while (index + 1 < str.length) {
            if (TextUtils.isEmpty(str[index + 1])) {
                index++;
                continue;
            }
            if (str[index + 1].startsWith(".")) {
                break;
            }
            res.append(str[index + 1]);
            res.append(" ");
            index ++;
        }
        return res.toString().trim();
    }
}
