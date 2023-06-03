package main.page;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import main.Main;
import main.callback.CommonCallback;
import main.entity.AirPackage;
import main.entity.FlightInfo;
import main.entity.FlightUser;
import main.manager.AccountManager;
import main.utils.*;
import main.view.ColumnChooseWindow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataPage extends BasePageNode {
    private static final String PASSENGERS = "pax_list";
    private static final String FLIGHTS = "flight_list";
    private static final String BAGS = "bags_list";
    @FXML
    private TableView exportTable;

    @FXML
    private ComboBox<String> tableList;

    @FXML
    private Button query;

    @FXML
    Button export;

    @Override
    String getFxmLPath() {
        setId("tab_data");
        return "dataPage";
    }

    @Override
    void initView() {
        ViewUtils.setTableEmpty(exportTable);
        query.setOnAction(btn -> {
            String tableName = tableList.getSelectionModel().getSelectedItem();
            queryByTableName(tableName);
        });
        export.setOnAction(btn -> {
            if (exportTable.getColumns().size() == 0) {
                ToastUtils.toast(ResourceUtils.getString("exportTip"));
                return;
            }
            List<TableColumn> columns = exportTable.getColumns();
            ColumnChooseWindow window = new ColumnChooseWindow(Alert.AlertType.CONFIRMATION);
            window.showWindow(columns, obj -> {
                if (!(obj instanceof List)) {
                    return;
                }
                List<String> chooseColumns = (List<String>) obj;
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File fileInit = new File(System.getProperty("user.dir"));
                directoryChooser.setInitialDirectory(fileInit);
                directoryChooser.setTitle(ResourceUtils.getString("plsChooseFile"));
                File file = directoryChooser.showDialog(Main.getStage());
                String path = file.getPath();
                exportTable(chooseColumns, path);
            });
        });
    }

    private void exportTable(List<String> chooseColumns, String path) {
        Workbook workbook = new HSSFWorkbook();
        Sheet spreadsheet = workbook.createSheet("data");
        Row row = spreadsheet.createRow(0);
        int indexJ = 0;
        for (int j = 0; j < exportTable.getColumns().size(); j++) {
            TableColumn column = (TableColumn) exportTable.getColumns().get(j);
            if (!chooseColumns.contains(column.getText())) {
                continue;
            }
            row.createCell(indexJ).setCellValue(column.getText());
            indexJ++;
        }
        for (int i = 0; i < exportTable.getItems().size(); i++) {
            row = spreadsheet.createRow(i + 1);
            indexJ = 0;
            for (int j = 0; j < exportTable.getColumns().size(); j++) {
                TableColumn column = (TableColumn) exportTable.getColumns().get(j);
                if (!chooseColumns.contains(column.getText())) {
                    continue;
                }
                if (column.getCellData(i) != null) {
                    row.createCell(indexJ).setCellValue(column.getCellData(i).toString());
                } else {
                    row.createCell(indexJ).setCellValue("");
                }
                indexJ++;
            }
        }
        try {
            FileOutputStream fileOut = new FileOutputStream(path + "/workbook.xls");
            workbook.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void queryByTableName(String tableName) {
        exportTable.getColumns().clear();
        if (TextUtils.equals(tableName, PASSENGERS)) {
            CloudApi.getInstance().queryAllFlightUsers(AccountManager.getInstance().getCompany(), new CommonCallback() {
                @Override
                public void success(String success) {
                    List<FlightUser> users = JSONArray.parseArray(success, FlightUser.class);
                    Platform.runLater(() -> {
                        initFlightUserSchema();
                        exportTable.getItems().clear();
                        exportTable.getItems().addAll(users);
                    });
                }

                @Override
                public void fail(String fail) {
                    Platform.runLater(() -> {
                        ToastUtils.toast(ResourceUtils.getString("queryFailed2") + fail);
                    });
                }
            });
        } else if (TextUtils.equals(tableName, FLIGHTS)) {
            CloudApi.getInstance().queryAllFlightInfo(AccountManager.getInstance().getCompany(), new CommonCallback() {
                @Override
                public void success(String success) {
                    JSONArray jsonArray = JSONArray.parseArray(success);
                    List<FlightInfo> flightInfoList = new ArrayList<>();
                    for (Object json : jsonArray) {
                        FlightInfo flightInfo = JSONObject.parseObject(json.toString(), FlightInfo.class);
                        flightInfoList.add(flightInfo);
                    }
                    Platform.runLater(() -> {
                        initFlightInfoSchema();
                        exportTable.getItems().clear();
                        exportTable.getItems().addAll(flightInfoList);
                    });
                }

                @Override
                public void fail(String fail) {
                    Platform.runLater(() -> {
                        ToastUtils.toast(ResourceUtils.getString("queryFailed2") + fail);
                    });
                }
            });
        } else if (TextUtils.equals(tableName, BAGS)) {
            CloudApi.getInstance().queryAllPackages(AccountManager.getInstance().getCompany(), new CommonCallback() {
                @Override
                public void success(String success) {
                    JSONArray jsonArray = JSONArray.parseArray(success);
                    List<AirPackage> packageList = new ArrayList<>();
                    for (Object json : jsonArray) {
                        AirPackage airPackage = JSONObject.parseObject(json.toString(), AirPackage.class);
                        packageList.add(airPackage);
                    }
                    Platform.runLater(() -> {
                        initFlightPackageSchema();
                        exportTable.getItems().clear();
                        exportTable.getItems().addAll(packageList);
                    });
                }

                @Override
                public void fail(String fail) {
                    Platform.runLater(() -> {
                        ToastUtils.toast(ResourceUtils.getString("queryFailed2") + fail);
                    });
                }
            });
        } else {
            ToastUtils.toast(ResourceUtils.getString("exportTip"));
        }
    }

    private void initFlightUserSchema() {
        List<String> key = new ArrayList<>(Arrays.asList("name", "ticketNumber", "position", "freeBudget", "recordNumber", "flight", "weight", "luggageNumber", "seatNumber", "order", "checkInStatus", "type", "country", "number", "gender", "birthDate", "validityPeriod"));
        List<String> keyName = ResourceUtils.getLangArray(key);
        for (int i = 0; i < keyName.size(); i++) {
            TableColumn<FlightUser, String> col = new TableColumn<>(keyName.get(i));
            col.setCellValueFactory(new PropertyValueFactory<>(key.get(i)));
            exportTable.getColumns().add(col);
        }
    }

    private void initFlightInfoSchema() {
        List<String> key = new ArrayList<>(Arrays.asList("flightNumber", "fromLocation", "toLocation", "date", "planeNumber", "boardingTime", "boardingGate", "takeoffTime", "landTime", "checkInStatus", "boardingStatus", "statusChangeTime", "locationSetStr", "layout", "closeTime", "remark", "usedSeats", "customModel", "csn"));
        List<String> keyName = ResourceUtils.getLangArray(key);
        for (int i = 0; i < keyName.size(); i++) {
            TableColumn<FlightUser, String> col = new TableColumn<>(keyName.get(i));
            col.setCellValueFactory(new PropertyValueFactory<>(key.get(i)));
            exportTable.getColumns().add(col);
        }
    }

    private void initFlightPackageSchema() {
        List<String> key = new ArrayList<>(Arrays.asList("flightNumber", "flightDate", "ticketNumber", "type", "num", "number", "weight", "toLocation", "isPrint"));
        List<String> keyName = ResourceUtils.getLangArray(key);
        for (int i = 0; i < keyName.size(); i++) {
            TableColumn<FlightUser, String> col = new TableColumn<>(keyName.get(i));
            col.setCellValueFactory(new PropertyValueFactory<>(key.get(i)));
            exportTable.getColumns().add(col);
        }
    }

    @Override
    void initData() {
        tableList.getItems().clear();
        tableList.getSelectionModel().select(-1);
        exportTable.getColumns().clear();
        exportTable.getItems().clear();
        List<String> tables = new ArrayList<>();
        tables.add(PASSENGERS);
        tables.add(FLIGHTS);
        tables.add(BAGS);
        tableList.getItems().addAll(tables);
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
