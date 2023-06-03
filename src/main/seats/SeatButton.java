package main.seats;

import javafx.scene.control.Button;

public class SeatButton extends Button {
    private boolean isAvailable = true;

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    private String mLocation;

    private String mType;

    public void setType(String type) {
        mType = type;
    }

    public SeatButton(String tag) {
        this.setText(tag);
    }
}
