package main.manager;

import main.entity.FlightManagerAccount;
import main.utils.Log;
import main.utils.TextUtils;

public class AccountManager {
    private static final Object LOCK = new Object();
    private static AccountManager sInstance;

    private FlightManagerAccount mFlightManagerAccount;
    public static AccountManager getInstance() {
        synchronized (LOCK) {
            if (sInstance == null) {
                synchronized (LOCK) {
                    sInstance = new AccountManager();
                }
            }
        }
        return sInstance;
    }

    private AccountManager() {
    }

    public boolean isLogin() {
        return mFlightManagerAccount != null;
    }

    public boolean isManager() {
        return mFlightManagerAccount != null && TextUtils.equals(mFlightManagerAccount.getAuthority(), "1");
    }

    public void setFlightManagerAccount(FlightManagerAccount flightManagerAccount) {
        mFlightManagerAccount = flightManagerAccount;
        Log.setAccount(flightManagerAccount);
    }
    public FlightManagerAccount getFlightManagerAccount() {
        return mFlightManagerAccount;
    }

    public String getCompany() {
        return mFlightManagerAccount.getCompany();
    }
}
