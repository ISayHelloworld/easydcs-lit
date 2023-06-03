package main.utils;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import main.seats.Cockpit;
import main.seats.GridSeats;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {
    public static boolean isEmpty(String data) {
        if (data == null || data.length() == 0) {
            return true;
        }
        return false;
    }

    public static boolean equals(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equals(str2);
    }

    // W3,3;1-3/ABCDEF;Y3,3;4-12/ABCDEF;14-15/A;18-21/EFDB;19/1X;
    public static String[] getSplitCockpit(String input) {
        ArrayList<String> cockpits = new ArrayList<>();
        String[] words = input.split(";");
        String REG_COCKPIT_SPLIT = "[a-zA-Z]\\d+,\\d+";
        Pattern pattern1 = Pattern.compile(REG_COCKPIT_SPLIT);
        String REG_RANGES = "(\\d+-\\d+/[a-zA-Z]+)|(\\d+/[a-zA-Z]+)";
        Pattern pattern2 = Pattern.compile(REG_RANGES);
        String REG_COL_SLIT = "\\d+/1X";
        Pattern pattern3 = Pattern.compile(REG_COL_SLIT);
        String REG_RANGE = "\\d+-\\d+";
        Pattern patternRange = Pattern.compile(REG_RANGE);
        for (String word : words) {
            Matcher matcher1 = pattern1.matcher(word);
            Matcher matcher2 = pattern2.matcher(word);
            Matcher matcher3 = pattern3.matcher(word);
            if (matcher1.find()) {
                // Y3,3
                cockpits.add(word + ";");
            } else if (matcher2.find()) {
                // 1-3/ABC
                int index = cockpits.size() - 1;
                String old = cockpits.get(index);
                cockpits.set(cockpits.size() - 1, old + word + ";");
            } else if (matcher3.find()) {
                // 5/1X
                String number = word.replace("/1X", "").trim();
                int splitNum = Integer.parseInt(number);
                for (int i = 0; i < cockpits.size(); i++) {
                    String str = cockpits.get(i);
                    Matcher rangeMatcher = patternRange.matcher(str);
                    while (rangeMatcher.find()) {
                        String range = rangeMatcher.group();
                        String[] ranges = range.split("-");
                        int startNumber = Integer.parseInt(ranges[0]);
                        int endNumber = Integer.parseInt(ranges[1]);
                        if (splitNum >= startNumber && splitNum < endNumber) {
                            cockpits.set(i, str + word + ";");
                        }
                    }
                }
            }
        }
        return cockpits.toArray(new String[cockpits.size()]);
    }

    public static boolean isValidColArr(String str) {
        Pattern pattern = Pattern.compile("\\d$");
        return (pattern.matcher(str).find() || str.endsWith(";"));
    }

    public static boolean isValidSeatArr(String str) {
        // 1A,2C;
        Pattern pattern = Pattern.compile("^\\d.*[a-zA-Z]$");
        return pattern.matcher(str).find() || str.endsWith(";");
    }

    // @param 1,2,3,1A,2B,1-3
    // @return [1A,1B,1C,1D,2A...]
    public static String[] splitSeatByExpression(String expression, ArrayList<GridSeats> gridSeats) {
        ArrayList<String> tmp = new ArrayList<>();
        if (TextUtils.isEmpty(expression) || gridSeats == null || gridSeats.isEmpty()) {
            return tmp.toArray(new String[0]);
        }
        Pattern colMode = Pattern.compile("[0-9]+");
        Pattern colRangeMode = Pattern.compile("([0-9]+)-([0-9]+)");
        Pattern exactMode = Pattern.compile("^\\d.*[a-zA-Z]$");
        String[] seats = expression.trim().replaceAll(";", "").split(",");
        for (String seat : seats) {
            seat = seat.trim();
            if (TextUtils.isEmpty(seat)) {
                continue;
            }
            if (exactMode.matcher(seat).matches()) {
                // 1A,2B,4C
                seat = seat.toUpperCase(Locale.ROOT);
                if (!tmp.contains(seat)) {
                    tmp.add(seat);
                }
            } else if (colMode.matcher(seat).matches()) {
                // 1,2,3
                addSeatToArrByCol(Integer.parseInt(seat), gridSeats, tmp);
            } else if (colRangeMode.matcher(seat).matches()) {
                // 1-3
                String[] range = seat.split("-");
                int start = Integer.parseInt(range[0].trim());
                int end = Integer.parseInt(range[1].trim());
                for (int colNum = start; colNum <= end; colNum++) {
                    addSeatToArrByCol(colNum, gridSeats, tmp);
                }
            } else {
                continue;
            }
        }
        return tmp.toArray(new String[0]);
    }

    private static void addSeatToArrByCol(int colNum, ArrayList<GridSeats> gridSeats, ArrayList<String> tmp) {
        // 找到colNum对应的gridSeat
        for (GridSeats grid : gridSeats) {
            Cockpit cockpit = grid.getCockpit();

            // 剪枝错误的仓位
            if (colNum > cockpit.getEndCols().get(cockpit.getEndCols().size() - 1) || colNum < cockpit.getStartCols().get(0)) {
                continue;
            }
            String[] rowTitles = cockpit.getCompleteRowTitles();
            for (String row : rowTitles) {
                if (TextUtils.isEmpty(row.trim())) {
                    continue;
                }
                if (!tmp.contains(colNum + row)) {
                    tmp.add(colNum + row);
                }
            }
        }
    }

    public static void safeSetStr(Label text, String res) {
        if (!isEmpty(res) && text != null) {
            text.setText(res);
        }
    }

    public static void safeSetStr(TextField textField, String res) {
        if (!isEmpty(res) && textField != null) {
            textField.setText(res);
        }
    }
}
