package main.utils;

import main.Main;

import java.io.*;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

public class PropertyUtils {
    private String propertyPath;
    public static String readProperty(String type, String key) {
        Properties prop = new Properties();
        String res = "";
        try {
            if (new File(type + ".properties").exists()) {
                InputStream in = new BufferedInputStream(new FileInputStream(type + ".properties"));
                prop.load(in);
                Iterator<String> it = prop.stringPropertyNames().iterator();
                while (it.hasNext()) {
                    String keyIterator = it.next();
                    if (!TextUtils.equals(keyIterator, key)) {
                        continue;
                    }
                    res = prop.getProperty(keyIterator);
                    break;
                }
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static void modifyProperty(String type, String key, String value) {
        Properties prop = new Properties();
        try {
            FileOutputStream oFile = new FileOutputStream(type + ".properties", false);
            prop.setProperty(key, value);
            prop.store(oFile, null);
            oFile.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
