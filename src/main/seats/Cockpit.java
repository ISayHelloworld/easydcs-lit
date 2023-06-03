package main.seats;

import main.parser.CockpitParser;
import main.utils.TextUtils;

import java.util.ArrayList;

// 代表一个仓位的数据，比如
// W仓数据或者Y仓数据

public class Cockpit {
    private boolean isFirstPit = false;

    // W/Y
    private String mCockpitName;

    private String mSubCockpitName;

    // 仓位座位数
    private int seatNum;

    private int seatXNum;

    public boolean isFirstPit() {
        return isFirstPit;
    }

    public void setFirstPit(boolean firstPit) {
        isFirstPit = firstPit;
    }

    public boolean isLastPit() {
        return isLastPit;
    }

    public void setLastPit(boolean lastPit) {
        isLastPit = lastPit;
    }

    private boolean isLastPit = false;

    // 3-10/ABCDEF中的3
    private ArrayList<Integer> mStartCols = new ArrayList<>();

    // 3-10/ABCDEF中的10
    private ArrayList<Integer> mEndCols = new ArrayList<>();


    // 顶部的1，2，3列数标题
    private ArrayList<int[]> mColTitles;

    private String[] mRightColTitles;

    private String[] mLeftColTitles;

    private String[] mCompleteRowTitles;

    private ArrayList<String[]> mRowTitles = new ArrayList<>();

    private String mRes;

    public String[][] getSeatStr() {
        return mSeatStr;
    }

    public void setSeatStr(String[][] seatStr) {
        this.mSeatStr = seatStr;
    }

    public String getLeftRightStr() {
        return mLeftRightStr;
    }

    public void setLeftRightStr(String leftRightStr) {
        this.mLeftRightStr = leftRightStr;
    }

    public String getTitle1Str() {
        return mCockpitName;
    }

    public int[] getCombineTitle2Str() {
        return mCombineTitle2Str;
    }


    public ArrayList<int[]> getTitle2() {
        return mTitle2Str;
    }


    public String[] getCompleteRowTitles() {
        return mCompleteRowTitles;
    }

    // 二进制数组字符串表示座位 * & =
    private String[][] mSeatStr;

    private String mLeftRightStr;

    private ArrayList<int[]> mTitle2Str = new ArrayList<>();

    private int[] mCombineTitle2Str;

    public void parseInput(String input, boolean isFirstPit, boolean isLastPit) {
        if (TextUtils.isEmpty(input)) {
            return;
        }

        // 解析
        CockpitParser parser = new CockpitParser(input, this, isFirstPit, isLastPit);

        // 获取座位为string[][]数组
        mSeatStr = parser.getSeatStr();

        // 获取分仓名
        mCockpitName = parser.getCockpitName();

        // 获取顶部标题数组
        mTitle2Str = parser.getTitles();
        mCombineTitle2Str = parser.getCombineTitle2Str();
    }

    public ArrayList<Integer> getStartCols() {
        return mStartCols;
    }

    public void setTitle(ArrayList<int[]> titles) {
        mColTitles = titles;
    }

    public void addStartCol(int startCol) {
        mStartCols.add(startCol);
    }

    public void setCompleteRowTitles(String[] rowTitles) {
        mCompleteRowTitles = rowTitles;
    }

    public void setCompleteLeftColTitles(String[] colTitles) {
        mLeftColTitles = colTitles;
    }

    public void setCompleteRightColTitles(String[] colTitles) {
        mRightColTitles = colTitles;
    }

    public String[] getRightColTitles() {
        return mRightColTitles;
    }

    public String[] getLeftColTitles() {
        return mLeftColTitles;
    }

    public ArrayList<Integer> getEndCols() {
        return mEndCols;
    }

    public void addEndCol(int endCol) {
        mEndCols.add(endCol);
    }

    public void addTitle2(int[] title2) {
        mTitle2Str.add(title2);
    }

    public ArrayList<Integer> getYSplit() {
        return mYSplit;
    }

    public void setYSplit(ArrayList<Integer> ySplit) {
        mYSplit = ySplit;
    }

    public ArrayList<Integer> getXSplit() {
        return mXSplit;
    }

    public void addXSplit(int xSplittNum) {
        if (mXSplit == null) {
            mXSplit = new ArrayList<>();
        }
        mXSplit.add(xSplittNum);
    }

    // 1,2,1
    ArrayList<Integer> mYSplit;

    public ArrayList<Integer> getTransYSplit() {
        return mTransYSplit;
    }

    public void setTransYSplit(ArrayList<Integer> transYSplit) {
        mTransYSplit = transYSplit;
    }

    ArrayList<Integer> mTransYSplit;

    ArrayList<Integer> mXSplit = new ArrayList<>();

    public String getSubCockpitName() {
        return mSubCockpitName;
    }


    public void setCockpitName(String cockpitName) {
        mSubCockpitName = cockpitName;
    }

    public ArrayList<String[]> getRowTitles() {
        return mRowTitles;
    }

    public void addRowTitles(String[] rowTitle) {
        // [a,b,c, , d,e,f]
        ArrayList<String> rows = new ArrayList<>();
        // 刪除掉空格
        for (String str : rowTitle) {
            if (TextUtils.isEmpty(str)) {
                continue;
            }
            rows.add(str);
        }
        mRowTitles.add(rows.toArray(new String[0]));
    }
}
