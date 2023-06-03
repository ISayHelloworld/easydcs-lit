package main.utils;


import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {
    public static void unzip(String zipPath, String descDir) {
        try {
            File zipFile = new File(zipPath);
            if (!zipFile.exists()) {
                throw new IOException("unzip file not exist");
            }
            File pathFile = new File(descDir);
            if (!pathFile.exists()) {
                System.out.println("pathFile not exist");
                pathFile.mkdirs();
            }
            InputStream input = new FileInputStream(zipPath);
            unzipWithStream(input, descDir);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void unzipWithStream(InputStream inputStream, String descDir) {
        if (!descDir.endsWith(File.separator)) {
            descDir = descDir + File.separator;
        }
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream, Charset.forName("GBK"))) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String zipEntryNameStr = zipEntry.getName();
                String zipEntryName = zipEntryNameStr;
                if (zipEntryNameStr.contains("/")) {
                    String str1 = zipEntryNameStr.substring(0, zipEntryNameStr.indexOf("/"));
                    zipEntryName = zipEntryNameStr.substring(str1.length() + 1);
                }
                String outPath = (descDir + zipEntryName).replace("\\\\", "/");
                File outFile = new File(outPath.substring(0, outPath.lastIndexOf('/')));
                if (!outFile.exists()) {
                    outFile.mkdirs();
                }
                if (new File(outPath).isDirectory()) {
                    continue;
                }
                writeFile(outPath, zipInputStream);
                zipInputStream.closeEntry();
            }
            System.out.println(ResourceUtils.getString("unZipSuccess"));
        } catch (IOException e) {
            System.out.println(ResourceUtils.getString("unZipFail2") + e.getMessage());
        }
    }

    public static void writeFile(String filePath, ZipInputStream zipInputStream) {
        try (OutputStream outputStream = new FileOutputStream(filePath)) {
            byte[] bytes = new byte[4096];
            int len;
            while ((len = zipInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
        } catch (IOException ex) {
            System.out.println((ResourceUtils.getString("unZipFail2") + ex.getMessage()));
        }
    }

    public static Boolean deleteFile(File file) {
        //判断文件不为null或文件目录存在
        if (file == null || !file.exists()) {
            return false;
        }
        //获取目录下子文件
        File[] files = file.listFiles();
        if (files == null) {
            return false;
        }
        //遍历该目录下的文件对象
        for (File f : files) {
            //判断子目录是否存在子目录,如果是文件则删除
            if (f.isDirectory()) {
                //递归删除目录下的文件
                deleteFile(f);
            } else {
                //文件删除
                f.delete();
            }
        }
        //文件夹删除
        file.delete();
        return true;
    }
}