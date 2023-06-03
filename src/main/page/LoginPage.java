package main.page;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import main.callback.CommonCallback;
import main.entity.FlightManagerAccount;
import main.manager.AccountManager;
import main.utils.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class LoginPage extends BasePageNode {
    @FXML
    private HBox loginArea;

    @FXML
    private Label company;

    @FXML
    private Button saveCompany;

    @FXML
    private ComboBox<String> companyList;

    private Button mLoginBtn;
    private TextField mInputAccount;
    private PasswordField mInputPassword;
    private CheckBox mRemember;
    private Button mLogOut;
    private VBox mLoginPane;
    private VBox mAccountPane;
    private Label mAccount;
    private Label mAuthority;

    @Override
    String getFxmLPath() {
        setId("tab_account");
        return "loginPage";
    }

    @Override
    void initView() {
        ViewUtils.setOutShadow(loginArea);
        mLoginBtn = (Button) mRoot.lookup("#loginBtn");
        mInputAccount = (TextField) mRoot.lookup("#account");
        mInputPassword = (PasswordField) mRoot.lookup("#pwd");
        mRemember = (CheckBox) mRoot.lookup("#remember");
        mLoginPane = (VBox) mRoot.lookup("#LoginPane");
        mAccountPane = (VBox) mRoot.lookup("#accountInfo");
        mLogOut = (Button) mRoot.lookup("#logout");
        mAccount = (Label) mRoot.lookup("#myAccount");
        mAuthority = (Label) mRoot.lookup("#authority");
        mLogOut.setOnAction(action -> {
            AccountManager.getInstance().setFlightManagerAccount(null);
            showAccount(false);
        });
        mLoginBtn.setOnAction(actionEvent -> {
            String password = mInputPassword.getText();
            String account = mInputAccount.getText();
            CloudApi.getInstance().login(account, password, new CommonCallback() {
                @Override
                public void success(String success) {
                    List<FlightManagerAccount> list = JSONArray.parseArray(success, FlightManagerAccount.class);
                    Platform.runLater(() -> {
                        if (list.size() > 0) {
                            FlightManagerAccount currentUser = list.get(0);
                            AccountManager.getInstance().setFlightManagerAccount(currentUser);
                            mAccount.setText(account);
                            if (TextUtils.equals(currentUser.getAuthority(), Constants.MANAGER)) {
                                mAuthority.setText(ResourceUtils.getString("managerUser"));
                            } else {
                                mAuthority.setText(ResourceUtils.getString("normalUser"));
                            }
                            showAccount(true);
                            if (mRemember.isSelected()) {
                                PropertyUtils.modifyProperty("user", account, password);
                            } else {
                                PropertyUtils.modifyProperty("user", account, "");
                            }
                        } else {
                            ToastUtils.toast(ResourceUtils.getString("loginFail"));
                        }
                    });
                }

                @Override
                public void fail(String fail) {
                    Platform.runLater(() -> {
                        ToastUtils.toast(ResourceUtils.getString("loginFail2") + fail);
                    });
                }
            });
        });
        saveCompany.setOnAction(btn -> {
            FlightManagerAccount currentAccount = AccountManager.getInstance().getFlightManagerAccount();
            currentAccount.setCompany(companyList.getSelectionModel().getSelectedItem());
            CloudApi.getInstance().updateAccountInfo(currentAccount, new CommonCallback() {
                @Override
                public void success(String success) {
                    Platform.runLater(() -> {
                        ToastUtils.toast(ResourceUtils.getString("saveSuccess"));
                    });
                }

                @Override
                public void fail(String fail) {
                    Platform.runLater(() -> {
                        ToastUtils.toast(ResourceUtils.getString("saveFailed"));
                    });
                }
            });
        });
    }

    private void showAccount(boolean isLogin) {
        if (isLogin) {
            loginArea.setVisible(false);
            mAccountPane.setVisible(true);
            mAccountPane.managedProperty().bind(mAccountPane.visibleProperty());
            CloudApi.getInstance().queryAllCompanies(new CommonCallback() {
                @Override
                public void success(String success) {
                    List<String> list = JSONObject.parseArray(success, String.class);
                    Platform.runLater(() -> {
                        companyList.getItems().addAll(list);
                    });
                }

                @Override
                public void fail(String fail) {
                    Platform.runLater(() -> {
                        System.out.println("queryAllCompanies:" + fail);
                    });
                }
            });
            companyList.setPromptText(AccountManager.getInstance().getCompany());
        } else {
            loginArea.setVisible(true);
            mAccountPane.setVisible(false);
            companyList.getItems().clear();
            loginArea.managedProperty().bind(loginArea.visibleProperty());
        }
    }

    @Override
    void initData() {
        companyList.getItems().clear();
        Properties prop = new Properties();
        try {
            if (new File("user.properties").exists()) {
                InputStream in = new BufferedInputStream(new FileInputStream("user.properties"));
                prop.load(in);
                Iterator<String> it = prop.stringPropertyNames().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    mInputAccount.setText(key);
                    mInputPassword.setText(prop.getProperty(key));
                }
                if (!TextUtils.isEmpty(mInputPassword.getText())) {
                    mRemember.setSelected(true);
                }
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        showAccount(AccountManager.getInstance().isLogin());
    }

    @Override
    List<Pane> getRasterizeWidthPanes() {
        return new ArrayList<>(Arrays.asList(mRoot));
    }

    @Override
    List<Pane> getRasterizeHeightPanes() {
        return new ArrayList<>(Arrays.asList(mRoot));
    }

    @Override
    public void refresh() {
        initData();
    }
}
