package main.utils;

import main.entity.FlightManagerAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Calendar;

public class Log {
    private static Logger logger = LoggerFactory.getLogger(Log.class);
    private static FlightManagerAccount mCurrentUser;

    private static int mCount = 0;

    private static int mFileCount = 0;

    private static StringBuffer mBuffer = new StringBuffer();

    private static final int MAX_LOG_LINE = 10;

    public static void setAccount(FlightManagerAccount flightManagerAccount) {
        mCurrentUser = flightManagerAccount;
    }

    public static void info(String msg) {
        if (mCurrentUser == null) {
            mCurrentUser = new FlightManagerAccount();
            mCurrentUser.setAccount("unLogin");
        }
        String content = get_nowDate() + " " + mCurrentUser.getAccount() + " " + msg;
        mBuffer.append(content);
        mBuffer.append("\n");
        logger.info(content);
        mCount++;
        if (mCount == MAX_LOG_LINE) {
            try {
                writeLog("logs/log" + mFileCount + ".txt", mBuffer.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            mCount = 0;
            mFileCount++;
            mBuffer.setLength(0);
        }
    }

    public static void writeLog(String path, String content) throws IOException {
        File F = new File(path);
        //如果文件不存在,就动态创建文件
        if (!F.exists()) {
            F.createNewFile();
        }
        FileWriter fw = null;
        String writeDate = content;
        try {
            //设置为:True,表示写入的时候追加数据
            fw = new FileWriter(F, true);
            //回车并换行
            fw.write(writeDate + "\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                fw.close();
            }
        }
    }

    public static String get_nowDate() {
        Calendar D = Calendar.getInstance();
        int year;
        int moth;
        int day;
        year = D.get(Calendar.YEAR);
        moth = D.get(Calendar.MONTH) + 1;
        day = D.get(Calendar.DAY_OF_MONTH);
        String now_date = year + "-" + moth + "-" + day;
        return now_date;
    }

    public static String readGroupLog() {
        mFileCount = getNumberOfFiles();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < mFileCount; i++) {
            try {
                buffer.append(readLog("log" + i + ".txt"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return buffer.toString();
    }

    public static String readLog(String fileName) throws IOException {
        String filePath = "logs/" + fileName;
        FileInputStream fin = new FileInputStream(filePath);
        InputStreamReader reader = new InputStreamReader(fin);
        BufferedReader buffReader = new BufferedReader(reader);
        String strTmp = "";
        StringBuilder buffer = new StringBuilder();
        buffer.append("---").append(fileName).append("---");
        buffer.append("\n");
        while ((strTmp = buffReader.readLine()) != null) {
            buffer.append(strTmp);
            buffer.append("\n");
        }
        buffReader.close();
        return buffer.toString();
    }

    public static int getNumberOfFiles() {
        File folder = new File("logs");
        File[] list = folder.listFiles();
        int fileCount = 0;
        for (File file : list) {
            if (file.isFile()) {
                fileCount++;
            }
        }
        return fileCount;
    }
}