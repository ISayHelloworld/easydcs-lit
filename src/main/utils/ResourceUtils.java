package main.utils;

import javafx.scene.image.Image;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceUtils {
    private static ResourceBundle langBundle;

    private static String currentLang;

    public static Image getImg(String name) {
        String path = "img/" + name;
        Image image = new Image(ResourceUtils.class.getClassLoader().getResource(path).toString());
        return image;
    }

    public static URL getXmlUrl(String name) {
        String path = "xml/" + name + ".fxml";
        return ResourceUtils.class.getClassLoader().getResource(path);
    }

    public static String getString(String key, String... replace) {
        if (langBundle == null || !TextUtils.equals(langBundle.getLocale().getLanguage(), currentLang)) {
            langBundle = ResourceBundle.getBundle("language", Locale.getDefault());
        }
        if (key == null) {
            return "";
        }
        String res = langBundle.getString(key);
        if (res.contains("%") && replace != null && replace.length > 0) {
            res = res.replace("%", replace[0]);
        }
        return res;
    }



    public static void readLang() {
        String lang = PropertyUtils.readProperty("lang", "lang");
        if (TextUtils.isEmpty(lang)) {
            lang = "English";
        }
        currentLang = lang;
        if (TextUtils.equals(lang, "English")) {
            Locale.setDefault(Locale.ENGLISH);
        } else if (TextUtils.equals(lang, "Chinese")) {
            Locale.setDefault(Locale.CHINA);
        } else {
            Locale.setDefault(Locale.ENGLISH);
        }
    }

    public static List<String> getLangArray(List<String> keyList) {
        List<String> res = new ArrayList<>();
        for (String key : keyList) {
            res.add(getString(key));
        }
        return res;
    }
}
