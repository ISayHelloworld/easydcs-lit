package main.manager;

import main.callback.ValueCallback;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadManager {
    public static void download(ValueCallback callback, String url) {
        try {
            downLoadFromUrl(callback, url, "downloadTmp.zip", "tmp");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void downLoadFromUrl(ValueCallback callback, String urlStr, String fileName, String savePath) {
        URL url;
        try {
            url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            callback.ok(0);
            int length = conn.getContentLength();
            // 防止屏蔽程序抓取而返回403错误
//            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            // 得到输入流
            InputStream inputStream = conn.getInputStream();
            // 获取自己数组
            byte[] getData = readInputStream(callback, length, inputStream);

            // 文件保存位置
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdir();
            }
            File file = new File(saveDir + File.separator + fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(getData);
            fos.close();
            inputStream.close();
            conn.disconnect();
            callback.ok(100);
            System.out.println("下载完成，启动解压");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static byte[] readInputStream(ValueCallback callback, int length, InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        float lenAgree = 0F;
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
            lenAgree += len;
            float progress = lenAgree / length * 100;
            callback.ok((int) progress);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static void restart() {
        try {
            Runtime.getRuntime().exec("cmd /k start .\\update.vbs");
//            Main.close(); //关闭程序以便重启
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
