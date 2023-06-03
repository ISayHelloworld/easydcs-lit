package main.parser;

import javafx.stage.FileChooser;
import main.utils.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DataInput {
    public static File openSingleFileChooser() {
        FileChooser fileChooser = new FileChooser();
        return fileChooser.showOpenDialog(null);
    }

    public static String readFileTxt(File file) {
        if (file == null) {
            return null;
        }
        StringBuilder res = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (TextUtils.isEmpty(line)) {
                    continue;
                }
                res.append(line);
                res.append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res.toString();
    }
}
