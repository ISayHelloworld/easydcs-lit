package main.manager;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import main.callback.CommonCallback;
import main.page.*;
import main.utils.ResourceUtils;
import main.utils.TextUtils;
import main.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PageRouter {
    private static Pane sContent;

    private static List<HBox> sTabs = new ArrayList<>();

    private static HashMap<Integer, BasePageNode> pageMap = new HashMap<>();

    private static int currentIndex = -1;

    private static ArrayList<Integer> checkPages = new ArrayList<>(Arrays.asList(1,2,3));

    private static ArrayList<Integer> boardingPages = new ArrayList<>(Arrays.asList(9,10));

    public static void jumpToPage(int pageIndex) {
        // 这里写法上要看下怎么优化
        if (!pageMap.containsKey(pageIndex)) {
            switch (pageIndex) {
                case 1:
                    pageMap.put(1, new CheckInPage1());
                    break;
                case 2:
                    pageMap.put(2, new CheckInPage2());
                    break;
                case 3:
                    pageMap.put(3, new CheckInPage3());
                    break;
                case 4:
                    pageMap.put(4, new LoginPage());
                    break;
                case 5:
                    pageMap.put(5, new VersionPage());
                    break;
                case 6:
                    pageMap.put(6, new ManagerPage());
                    break;
                case 7:
                    pageMap.put(7, new ControlPage());
                    break;
                case 8:
                    pageMap.put(8, new LogPage());
                    break;
                case 9:
                    pageMap.put(9, new BoardingPage1());
                    break;
                case 10:
                    pageMap.put(10, new BoardingPage2());
                    break;
                case 11:
                    pageMap.put(11, new DataPage());
                    break;
                case 12:
                    pageMap.put(12, new SettingPage());
                    break;
            }
            if (isFilter(pageIndex)) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                String title;
                if (checkPages.contains(currentIndex)) {
                    title = ResourceUtils.getString("switchToBoarding");
                } else {
                    title = ResourceUtils.getString("switchToCheckin");
                }
                alert.setTitle(title);
                alert.setHeaderText(ResourceUtils.getString("confirmLeave"));
                ok.setOnAction(view->{
                    create(pageIndex);
                });
                alert.show();
                return;
            }
            create(pageIndex);
        } else {
            if (isFilter(pageIndex)) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                Button ok = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                String title;
                if (checkPages.contains(currentIndex)) {
                    title = ResourceUtils.getString("switchToBoarding");
                } else {
                    title = ResourceUtils.getString("switchToCheckin");
                }
                alert.setTitle(title);
                alert.setHeaderText(ResourceUtils.getString("confirmLeave"));
                ok.setOnAction(view->{
                    jump(pageIndex);
                });
                alert.show();
                return;
            }
            jump(pageIndex);
        }
    }

    private static void create(int pageIndex) {
        sContent.getChildren().clear();
        BasePageNode page = pageMap.get(pageIndex);
        sContent.getChildren().add(page.getNode());
        currentIndex = pageIndex;
    }

    private static boolean isFilter(int pageIndex) {
        boolean isCheckToBoarding = checkPages.contains(currentIndex) && boardingPages.contains(pageIndex);
        boolean isBoardingToCheck = boardingPages.contains(currentIndex) && checkPages.contains(pageIndex);
        return isCheckToBoarding || isBoardingToCheck;
    }

    private static void jump(int pageIndex) {
        sContent.getChildren().clear();
        currentIndex = pageIndex;
        BasePageNode page = pageMap.get(pageIndex);
        sContent.getChildren().add(page.getNode());

        // 重新进入界面时刷新数据
        page.refresh();
    }

    public static void init(Pane content, Pane tab) {
        sContent = content;
        for (int i = 0; i < tab.getChildren().size(); i++) {
            Node node = tab.getChildren().get(i);
            if (!(node instanceof HBox)) {
                continue;
            }
            sTabs.add((HBox) node);
        }
    }

    public static void clearCache() {
        pageMap.clear();
    }
}
