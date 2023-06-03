package main.seats;

import javafx.scene.image.ImageView;
import main.utils.Constants;
import main.utils.ResourceUtils;
import main.utils.TextUtils;


public class Seat extends ImageView {
    // 已选中，未选中，C，禁
    private String type;

    private boolean isAvailable = true;

    private boolean isUsed = false;

    private static final int WIDTH = 30;

    private static final int HEIGHT = 25;


    public Seat() {
        this.setFitHeight(HEIGHT);
        this.setFitWidth(WIDTH);
        setType(Constants.SEAT_PRIORITY);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        String path;
        if (TextUtils.equals(type, Constants.SEAT_X)) {
            isAvailable = false;
        }
        if (isAvailable || TextUtils.equals(type, Constants.SEAT_X)) {
            path = type + ".png";
        } else {
            path = type + "Used.png";
        }
        this.setImage(ResourceUtils.getImg(path));
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        if (available != isAvailable) {
            isAvailable = available;
            String path;
            if (available) {
                path = type + ".png";
            } else {
                path = type + "Used.png";
                isUsed = true;
            }
            this.setImage(ResourceUtils.getImg(path));
        }
    }

    public boolean isUsed() {
        return isUsed;
    }
}
