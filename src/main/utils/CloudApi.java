package main.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import main.callback.CommonCallback;
import main.entity.*;
import main.manager.AccountManager;
import okhttp3.*;
import okio.Buffer;
import okio.BufferedSource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.List;

public class CloudApi {
    private static OkHttpClient mOkHttpClient = new OkHttpClient();
    private volatile static CloudApi INSTANCE;
    private static final Object LOCK = new Object();
    private static final MediaType MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");
    private static final String API_PORT = ":8080";

    // 后端服务器地址
    public static final String SERVER_HOST = "http://47.94.2.124" + API_PORT;
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private CloudApi() {
    }

    public void uploadFlightInfo(FlightInfo flightInfo, CommonCallback callback) {
        String url = SERVER_HOST + "/uploadFlightInfo";
        HashMap map = new HashMap();
        map.put("flightInfo", flightInfo);
        map.put("account", AccountManager.getInstance().getFlightManagerAccount());
        String jsonStr = JSONObject.toJSONString(map);
        post(jsonStr, url, callback);
    }

    public void uploadFlightInfoAndUsers(FlightInfo flightInfo, CommonCallback callback) {
        String url = SERVER_HOST + "/uploadFlightInfoAndUsers";
        HashMap map = new HashMap();
        map.put("flightInfo", flightInfo);
        map.put("account", AccountManager.getInstance().getFlightManagerAccount());
        String jsonStr = JSONObject.toJSONString(map);
        post(jsonStr, url, callback);
    }

    public void uploadFlightUsers(List<FlightUser> flightUsers, CommonCallback callback) {
        String url = SERVER_HOST + "/uploadFlightUsers";
        HashMap map = new HashMap();
        map.put("flightUsers", flightUsers);
        map.put("account", AccountManager.getInstance().getFlightManagerAccount());
        String jsonStr = JSONObject.toJSONString(map);
        post(jsonStr, url, callback);
    }

    public void queryFlightInfos(String company, String flightNumber, CommonCallback callback) {
        String url = SERVER_HOST + "/queryFlightInfos" + "?company=" + company + "&flightNumber=" + flightNumber;
        get(url, callback);
    }

    public void queryFlightInfo(String company, String flightNumber, String date, CommonCallback callback) {
        String url = SERVER_HOST + "/queryFlightInfo" + "?company=" + company + "&flightNumber=" + flightNumber + "&date=" + date;
        get(url, callback);
    }

    public void queryFlightUsers(String company, String flightNumber, String flightDate, CommonCallback callback) {
        String url = SERVER_HOST + "/queryFlightUsers" + "?company=" + company + "&flightNumber=" + flightNumber + "&flightDate=" + flightDate;
        get(url, callback);
    }

    public void queryAllFlightUsers(String company, CommonCallback callback) {
        String url = SERVER_HOST + "/queryAllFlightUsers" + "?company=" + company;
        get(url, callback);
    }

    public void queryFlightBoardingUsers(String company, String flightNumber, String flightDate, CommonCallback callback) {
        String url = SERVER_HOST + "/queryFlightBoardingUsers" + "?company=" + company + "&flightNumber=" + flightNumber + "&flightDate=" + flightDate;
        get(url, callback);
    }

    public void queryUserLog(FlightUser flightUser, CommonCallback callback) {
        String url = SERVER_HOST + "/queryUserLog";
        HashMap map = new HashMap();
        map.put("flightUser", flightUser);
        map.put("account", AccountManager.getInstance().getFlightManagerAccount());
        post(JSON.toJSONString(map), url, callback);
    }

    public void queryFlightControlLog(FlightInfo flightInfo, CommonCallback callback) {
        String url = SERVER_HOST + "/queryFlightControlLog";
        HashMap map = new HashMap();
        map.put("flightInfo", flightInfo);
        map.put("account", AccountManager.getInstance().getFlightManagerAccount());
        post(JSON.toJSONString(map), url, callback);
    }

    public void queryFlightModels(String company, CommonCallback callback) {
        String url = SERVER_HOST + "/queryModels" + "?company=" + company;
        get(url, callback);
    }

    public void queryAllFlightInfo(String company, CommonCallback callback) {
        String url = SERVER_HOST + "/queryAllFlightInfo" + "?company=" + company;
        get(url, callback);
    }

    public void queryAllCompanies(CommonCallback callback) {
        String url = SERVER_HOST + "/queryAllCompanies";
        get(url, callback);
    }

    public void login(String account, String password, CommonCallback callback) {
        String url = SERVER_HOST + "/selectFlightAccount" + "?account=" + account + "&password=" + password;
        get(url, callback);
    }

    public void updateAccountInfo(FlightManagerAccount account, CommonCallback callback) {
        String url = SERVER_HOST + "/updateAccountInfo";
        HashMap map = new HashMap();
        map.put("account", account);
        post(JSON.toJSONString(map), url, new CommonCallback() {
            @Override
            public void success(String res) {
                if (Integer.parseInt(res) >= 0) {
                    callback.success(res);
                } else {
                    callback.fail(res);
                }
            }

            @Override
            public void fail(String fail) {
                callback.fail(fail);
            }
        });
    }

    public void uploadPackageInfo(AirPackage airPackage, CommonCallback callback) {
        String url = SERVER_HOST + "/uploadAirPackage";
        HashMap map = new HashMap();
        map.put("airPackage", airPackage);
        map.put("account", AccountManager.getInstance().getFlightManagerAccount());
        post(JSON.toJSONString(map), url, new CommonCallback() {
            @Override
            public void success(String res) {
                if (Integer.parseInt(res) >= 0) {
                    callback.success(res);
                } else {
                    callback.fail(res);
                }
            }

            @Override
            public void fail(String fail) {
                callback.fail(fail);
            }
        });
    }

    public void uploadFlightModel(FlightModel flightModel, CommonCallback callback) {
        String url = SERVER_HOST + "/uploadFlightModel";
        HashMap map = new HashMap();
        map.put("flightModel", flightModel);
        map.put("account", AccountManager.getInstance().getFlightManagerAccount());
        post(JSON.toJSONString(map), url, new CommonCallback() {
            @Override
            public void success(String res) {
                if (Integer.parseInt(res) >= 0) {
                    callback.success(res);
                } else {
                    callback.fail(res);
                }
            }

            @Override
            public void fail(String fail) {
                callback.fail(fail);
            }
        });
    }

    public void deletePackage(AirPackage airPackage, CommonCallback callback) {
        String url = SERVER_HOST + "/deleteAirPackage";
        HashMap map = new HashMap();
        map.put("airPackage", airPackage);
        map.put("account", AccountManager.getInstance().getFlightManagerAccount());
        post(JSON.toJSONString(map), url, new CommonCallback() {
            @Override
            public void success(String res) {
                if (Integer.parseInt(res) >= 0) {
                    callback.success(res);
                } else {
                    callback.fail(res);
                }
            }

            @Override
            public void fail(String fail) {
                callback.fail(fail);
            }
        });
    }

    public void queryPackages(String company, String ticketNumber, CommonCallback callback) {
        String url = SERVER_HOST + "/queryPackages" + "?company=" + company + "&ticketNumber=" + ticketNumber;
        get(url, callback);
    }

    public void queryAllPackages(String company, CommonCallback callback) {
        String url = SERVER_HOST + "/queryAllPackages" + "?company=" + company;
        get(url, callback);
    }

    public void queryFlightPackageSum(String company, String flightNumber, String flightDate, CommonCallback callback) {
        String url = SERVER_HOST + "/queryFlightPackageSum" + "?company=" + company+ "&flightNumber=" + flightNumber + "&flightDate=" + flightDate;
        get(url, callback);
    }

    public void queryLatestVersion(CommonCallback callback) {
        String url = SERVER_HOST + "/queryLatestVersion";
        get(url, callback);
    }

    public void deleteBoardFlightUser(FlightUser flightUser, CommonCallback callback) {
        String url = SERVER_HOST + "/deleteBoard";
        HashMap map = new HashMap();
        map.put("flightUser", flightUser);
        map.put("account", AccountManager.getInstance().getFlightManagerAccount());
        post(JSON.toJSONString(map), url, new CommonCallback() {
            @Override
            public void success(String res) {
                if (Integer.parseInt(res) >= 0) {
                    callback.success(res);
                } else {
                    callback.fail(res);
                }
            }

            @Override
            public void fail(String fail) {
                callback.fail(fail);
            }
        });
    }

    public void uploadFlightUser(FlightUser flightUser, CommonCallback callback) {
        String url = SERVER_HOST + "/updateUserInfo";
        HashMap map = new HashMap();
        map.put("flightUser", flightUser);
        map.put("account", AccountManager.getInstance().getFlightManagerAccount());
        post(JSON.toJSONString(map), url, new CommonCallback() {
            @Override
            public void success(String res) {
                try {
                    if (Integer.parseInt(res) >= 0) {
                        callback.success(res);
                    } else {
                        callback.fail(res);
                    }
                } catch (NumberFormatException exception) {
                    callback.fail(exception.getMessage());
                }
            }

            @Override
            public void fail(String fail) {
                callback.fail(fail);
            }
        });
    }

    private static void post(String jsonStr, String uri, CommonCallback callback) {
        if (TextUtils.isEmpty(uri)) {
            return;
        }
        RequestBody body = RequestBody.create(MEDIA_TYPE, jsonStr);
        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .url(uri)
                .post(body).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.fail(e.getMessage());
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                ResponseBody responseBody = response.body();
                BufferedSource source = responseBody.source();
                try {
                    source.request(Long.MAX_VALUE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Buffer buffer = source.buffer();
                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8);
                    } catch (UnsupportedCharsetException e) {
                        e.printStackTrace();
                    }
                }
                callback.success(buffer.clone().readString(charset));
                response.close();
            }
        });
    }

    private static void get(String url, CommonCallback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Request request = new Request.Builder().url(url).get().build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.fail(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.body() != null) {
                    ResponseBody responseBody = response.body();
                    BufferedSource source = responseBody.source();
                    try {
                        source.request(Long.MAX_VALUE); // Buffer the entire body.
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Buffer buffer = source.buffer();

                    Charset charset = UTF8;
                    MediaType contentType = responseBody.contentType();
                    if (contentType != null) {
                        try {
                            charset = contentType.charset(UTF8);
                        } catch (UnsupportedCharsetException e) {
                            e.printStackTrace();
                        }
                    }
                    callback.success(buffer.clone().readString(charset));
                } else {
                    callback.success("response body is null");
                }
            }
        });
    }

    public static CloudApi getInstance() {
        if (INSTANCE == null) {
            synchronized (LOCK) {
                if (INSTANCE == null) {
                    INSTANCE = new CloudApi();
                }
            }
        }
        return INSTANCE;
    }
}
