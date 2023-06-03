package main.parser;

import main.seats.Cockpit;
import main.utils.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CockpitParser {
    // 输入区 Y168 W3,3;1-3/ABCDEF;
    // 正则1:字母+(，数字)n个（纵向分仓）

    // 正则3:字母/（数字/数字+字母）,.* (仓位确定）
    private static final String REG_Y_SPLIT = "[a-zA-Z]\\d+(,\\d)+";
    private static final String REG_X_SPLIT = "(\\d)+/1X";

    // 正则2:数字-数字/字母（横向分仓）
    private static final String REG_COL = "(\\d)*-(\\d)*/([a-zA-Z])+";
    private static final String REG_COL_SINGLE = "(\\d)+/([a-zA-Z])+";

    // 正则3:数字/1X
    private static final String REG_SPLIT_COCKPIT = "\\d+/1X";
    private static final String REG_OPERATION = "";
    private static final String REG_SPACE = "[\\s\\p{Zs}]|;";
    private Cockpit mCockpit;
    private String mInput;
    private String[][] seatStr;
    private int mXPicNum;

    private int mYPicNum;

    private boolean mIsFirst = false;

    private boolean mIsLast = false;

    public CockpitParser(String input, Cockpit cockpit, boolean isFirst, boolean isLast) {
        mInput = input;
        mCockpit = cockpit;
        mIsFirst = isFirst;
        mIsLast = isLast;
        parse();
    }

    public void parse() {
        String[] words = mInput.split(REG_SPACE);
        for (String word : words) {
            if (TextUtils.isEmpty(word)) {
                continue;
            }
            if (dealWithYSplit(word)) {
            } else if (dealWithXSplit(word)) {
            } else if (dealWithCol(word)) {
            } else {
            }
        }
        seatStr = parseSeatStr();
        getTitles();
    }

    public Cockpit getCockpit() {
        return mCockpit;
    }

    // W3,3
    private boolean dealWithXSplit(String word) {
        Pattern pattern = Pattern.compile(REG_X_SPLIT);
        Matcher matcher = pattern.matcher(word);
        if (matcher.find()) {
            getXSplit(matcher.group());
            return true;
        }
        return false;
    }

    private boolean dealWithCol(String word) {
        Pattern pattern = Pattern.compile(REG_COL);
        Matcher matcher = pattern.matcher(word);
        Pattern pattern2 = Pattern.compile(REG_COL_SINGLE);
        Matcher matcher2 = pattern2.matcher(word);
        if (matcher.find() || matcher2.find()) {
            // 1-3/ABCDE || 3/ABC
            getCol(word);

            // 获取左右的ABCDEF
            getLineTitle(word);
            return true;
        }
        return false;
    }

    // W3,3 -> [3,3]
    private void getYSplit(String word) {
        Pattern pattern = Pattern.compile("[\\d+|,]+");
        Matcher matcher = pattern.matcher(word);
        if (!matcher.find()) {
            return;
        }
        mCockpit.setCockpitName(word.substring(0, 1));
        String res = matcher.group();
        String[] split = res.split(",");
        ArrayList<Integer> splitNum = new ArrayList<>();
        for (String num : split) {
            try {
                splitNum.add(Integer.parseInt(num.trim()));
            } catch (NumberFormatException ex) {
                System.out.println(ex.getMessage());
            }
        }
        mCockpit.setYSplit(splitNum);
        mCockpit.setTransYSplit(getTransYSplits(splitNum));
    }

    private ArrayList<Integer> getTransYSplits(ArrayList<Integer> inputSplit) {
        ArrayList<Integer> splits = new ArrayList<>();
        if (inputSplit == null || inputSplit.size() == 0) {
            return splits;
        }
        splits.add(inputSplit.get(0) + 1);
        splits.add(inputSplit.get(0) + 2);
        int numLast = inputSplit.get(0) + 2;
        for (int i = 1; i < inputSplit.size() - 1; i++) {
            splits.add(numLast + inputSplit.get(i) + 1);
            splits.add(numLast + inputSplit.get(i) + 2);
            numLast = numLast + inputSplit.get(i) + 2;
        }
        return splits;
    }

    private boolean dealWithYSplit(String word) {
        Pattern pattern = Pattern.compile(REG_Y_SPLIT);
        Matcher matcher = pattern.matcher(word);
        if (matcher.find()) {
            getYSplit(matcher.group());
            return true;
        }
        return false;
    }

    // 5/1X, 2-7  实际就是从
    private void getXSplit(String word) {
        String[] splits = word.split("/1X|,");
        for (String str : splits) {
            mCockpit.addXSplit(Integer.parseInt(str.trim()));
        }
    }

    // 1-3/ABCDEF -> [1,3]  3/ABC -> [3]
    private void getCol(String word) {
        // W3,3;1-3/ABCDEF;Y3,3;5-12/ABCDEF;13/A;
        Pattern pattern = Pattern.compile("[\\d|-]+");
        Matcher matcher = pattern.matcher(word);
        if (!matcher.find()) {
            return;
        }
        String res = matcher.group().trim();
        if (res.contains("-")) {
            String[] split = res.split("-");
            if (split.length != 2) {
                return;
            }
            int start = Integer.parseInt(split[0]);
            mCockpit.addStartCol(start);
            int end = Integer.parseInt(split[1]);
            mCockpit.addEndCol(end);
            int[] title2 = new int[end - start + 1];
            for (int i = 0; i < end - start + 1; i++) {
                title2[i] = start + i;
            }
            mCockpit.addTitle2(title2);
        } else {
            int number = Integer.parseInt(res);
            int[] title2 = new int[]{number};
            mCockpit.addStartCol(number);
            mCockpit.addEndCol(number);
            mCockpit.addTitle2(title2);
        }
    }

    // 1-3/ABCDEF -> [A,B,C,R,L,D,E,F]
    private void getLineTitle(String word) {
        Pattern pattern = Pattern.compile("([a-zA-Z])+");
        Matcher matcher = pattern.matcher(word);
        if (!matcher.find()) {
            return;
        }
        String res = matcher.group();
        ArrayList<String> output = new ArrayList<>();
        for (char chr : res.toCharArray()) {
            output.add(0, String.valueOf(chr));
        }
        ArrayList<Integer> arrayList = mCockpit.getTransYSplit();
        int mid = (res.length() + arrayList.size()) / 2;
        for (Integer num : arrayList) {
            if (num - 1 < 0 || num - 1 >= output.size()) {
                continue;
            }
            if (num <= mid) {
                output.add(num - 1, "");
            } else {
                output.add(num - 1, "");
            }
        }
        String[] leftStrs = new String[output.size()];
        String[] rightStrs = new String[output.size()];
        for (int i = 0; i < output.size(); i++) {
            if (mIsFirst) {
                if (i < mid) {
                    leftStrs[i] = "R" + output.get(i);
                } else {
                    leftStrs[i] = "L" + output.get(i);
                }
            } else {
                leftStrs[i] = output.get(i);
            }
        }
        for (int i = 0; i < output.size(); i++) {
            if (mIsLast) {
                if (i < mid) {
                    rightStrs[i] = "R" + output.get(i);
                } else {
                    rightStrs[i] = "L" + output.get(i);
                }
            } else {
                rightStrs[i] = output.get(i);
            }
        }
        String[] rowTitles = output.toArray(new String[0]);
        if (mCockpit.getCompleteRowTitles() == null || rowTitles.length > mCockpit.getCompleteRowTitles().length) {
            // colName只获取最完整的
            mCockpit.setCompleteRowTitles(rowTitles);
            mCockpit.setCompleteLeftColTitles(leftStrs);
            mCockpit.setCompleteRightColTitles(rightStrs);
        }
        mCockpit.addRowTitles(rowTitles);
    }

    public String[][] getSeatStr() {
        return seatStr;
    }

    public String getCockpitName() {
        // W
        return mCockpit.getSubCockpitName();
    }

    public int[] getCombineTitle2Str() {
        // 1-7 -> [1,2,3,4,5,6,-1,7]
        // -1表示分仓空行
        // 1-3/ABC; 4-5/AB -> {[1,2,3],[4,5]}
        ArrayList<int[]> titles = getTitles();
        mCockpit.setTitle(titles);
        int size = 0;
        for (int[] arr : titles) {
            size += arr.length;
        }
        int[] res = new int[size];
        int i = 0;
        for (int[] arr : titles) {
            for (int num : arr) {
                res[i] = num;
                i++;
            }
        }
        return res;
    }

    public ArrayList<int[]> getTitles() {
        ArrayList<Integer> starts = mCockpit.getStartCols();
        ArrayList<Integer> ends = mCockpit.getEndCols();
        ArrayList<int[]> titles = new ArrayList<>();
        for (int index = 0; index < starts.size(); index++) {
            int start = starts.get(index);
            int end = ends.get(index);
            int[] res = new int[end - start + 1];
            int j = 0;
            for (int i = start; i <= end; i++) {
                res[j] = i;
                j++;
            }
            ArrayList<Integer> xSplit = mCockpit.getXSplit();
            ArrayList<Integer> filterXSplit = filterXSplit(xSplit, start, end);
            List<Integer> resArr = Arrays.stream(res).boxed().collect(Collectors.toList());
            for (int num : filterXSplit) {
                int addIndex = resArr.indexOf(num) + 1;
                resArr.add(addIndex, -1);
            }
            titles.add(resArr.stream().mapToInt(Integer::intValue).toArray());
        }
        return titles;
    }

    private String[][] parseSeatStr() {
        ArrayList<Integer> startCols = mCockpit.getStartCols();
        ArrayList<Integer> endCols = mCockpit.getEndCols();
        mXPicNum = 0;
        mYPicNum = 0;
        for (int i = 0; i < endCols.size(); i++) {
            int startCol = startCols.get(i);
            int endCol = endCols.get(i);

            // {3,3}
            ArrayList<Integer> ySplitTmp = mCockpit.getYSplit();

            // {1,3}
            ArrayList<Integer> xSplitTmp = mCockpit.getXSplit();

            // 计算区
            int yLineNum = 0;
            for (int num : ySplitTmp) {
                yLineNum += num;
            }
            ArrayList<Integer> mYSplit = mCockpit.getTransYSplit();
            ArrayList<Integer> mXSplit = getXSplits(xSplitTmp);
            int xLineNum = endCol - startCol + 1;

            // 横向行数等于实际数量+隔离空行
            mXPicNum += xLineNum + mXSplit.size();

            // 纵向行数等于实际数量+隔离空行
            mYPicNum = Math.max(mYPicNum, yLineNum + mYSplit.size());
        }
        return fillInitValues();
    }

    private String[][] fillInitValues() {
        // [1,2][a,b,c,d,e,f]   [3,4,5][a,b,c] 按组来处理
        String[][] initValues = new String[mYPicNum][mXPicNum];

        // 入参 title2 yColumns yCompletes
        int size = mCockpit.getRowTitles().size();
        int colNum = 0;
        for (int index = 0; index < size; index++) {
            // colNum不对
            colNum = fillInitValues2(mCockpit.getStartCols().get(index), colNum, initValues, mCockpit.getTitle2().get(index), mCockpit.getRowTitles().get(index), mCockpit.getXSplit());
        }
//
//
//        ArrayList<Integer> mYSplit = mCockpit.getTransYSplit();
//        ArrayList<String[]> rows = mCockpit.getRowTitles();
//        int startCol = mCockpit.getStartCols().get(0);
//        for (int x = 0; x < mXPicNum; x++) {
//            for (int y = 0; y < mYPicNum; y++) {
//                if (mXSplit.contains(x + startCol)) {
//                    // 隔离空行
//                    initValues[y][x] = "&&";
//                    continue;
//                }
//                if (mYSplit.contains(y + 1)) {
//                    initValues[y][x] = " = ";
//                } else {
//                    initValues[y][x] = " * ";
//                }
//            }
//        }
        return initValues;
    }

    private ArrayList<Integer> filterXSplit(ArrayList<Integer> allXsplit, int colStart, int colEnd) {
        // 2, 3, 9 -> 过滤成当前组的 1-5/ABC -> 2, 3
        ArrayList<Integer> filterXSplit = new ArrayList<>();
        for (Integer num : allXsplit) {
            if (num >= colStart && num < colEnd) {
                filterXSplit.add(num);
            }
        }
        return filterXSplit;
    }

    private int fillInitValues2(int colStart, int colStartIndex, String[][] seatStr, int[] title2, String[] rows, ArrayList<Integer> xSplits) {
        int colNum = colStartIndex;
        ArrayList<Integer> mYSplit = mCockpit.getTransYSplit();
        int colEnd = colStart + title2.length - 1;
        ArrayList<Integer> filterXSplit = filterXSplit(xSplits, colStart, colEnd);
        // 按列循環
        for (int i = 0; i < title2.length + filterXSplit.size(); i++) {
            // 填充座位与空行
            for (int y = 0; y < rows.length; y++) {
                // 填充每行
                int column = findColumnByName(rows[y]);
                if (filterXSplit.contains(i + colStart - 1)) {
                    // 隔离空行
                    seatStr[y][i + colStartIndex] = "&&";
                    continue;
                }
                seatStr[column][i + colStartIndex] = " * ";
            }
            // 填充空行
            for (int z = 0; z < mYSplit.size(); z++) {
                int column = mYSplit.get(z) - 1;
                seatStr[column][i + colStartIndex] = " = ";
            }
            colNum++;
        }
        return colNum;
    }

    private int findColumnByName(String name) {
        String[] completeColumns = mCockpit.getCompleteRowTitles();
        int column = 0;
        for (String str : completeColumns) {
            if (TextUtils.equals(name, str)) {
                return column;
            }
            column++;
        }
        return -1;
    }

    // 获取实际的X分离横坐标
    private ArrayList<Integer> getXSplits(ArrayList<Integer> inputSplit) {
        ArrayList<Integer> splits = new ArrayList<>();
        if (inputSplit == null || inputSplit.isEmpty()) {
            return splits;
        }
        int inc = 0;
        for (int i = 0; i < inputSplit.size(); i++) {
            splits.add(inputSplit.get(i) + inc);
            inc++;
        }
        return splits;
    }
}
