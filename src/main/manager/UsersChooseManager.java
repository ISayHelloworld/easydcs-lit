package main.manager;

import main.callback.CommonCallback;
import main.entity.FlightUser;
import main.utils.CloudApi;

import java.util.ArrayList;
import java.util.List;

public class UsersChooseManager {
    private static final Object LOCK = new Object();
    private static UsersChooseManager sInstance;
    private String mCurrentLocation;
    private List<FlightUser> mCurrentUsers = new ArrayList<>();
    public static UsersChooseManager getInstance() {
        synchronized (LOCK) {
            if (sInstance == null) {
                synchronized (LOCK) {
                    sInstance = new UsersChooseManager();
                }
            }
        }
        return sInstance;
    }

    private UsersChooseManager() {
    }

    public String getCurrentLocation() {
        return mCurrentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        mCurrentLocation = currentLocation;
    }

    public List<FlightUser> getCurrentUsers() {
        return mCurrentUsers;
    }

    public void addCurrentUser(FlightUser currentUser) {
        if (!mCurrentUsers.contains(currentUser)) {
            mCurrentUsers.add(currentUser);
        }
    }

    public void clear() {
        mCurrentLocation = null;
        mCurrentUsers.clear();
    }

    public void removeByIndex(int index) {
        if (index >= mCurrentUsers.size()) {
            return;
        }
        mCurrentUsers.remove(index);
    }
}