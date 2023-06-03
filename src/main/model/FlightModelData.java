package main.model;

import lombok.Getter;
import lombok.Setter;
import main.entity.FlightInfo;

@Getter
@Setter
public class FlightModelData {
    private static final Object LOCK = new Object();
    private static FlightModelData sInstance;

    private FlightInfo flightInfo;

    public static FlightModelData getInstance() {
        synchronized (LOCK) {
            if (sInstance == null) {
                synchronized (LOCK) {
                    sInstance = new FlightModelData();
                }
            }
        }
        return sInstance;
    }

    public void clear() {
        flightInfo = null;
    }
}