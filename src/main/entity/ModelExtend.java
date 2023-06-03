package main.entity;

import main.utils.Constants;
import main.utils.ResourceUtils;
import main.utils.TextUtils;
import main.utils.ToastUtils;

import java.util.HashSet;

public class ModelExtend  implements Cloneable {
    // 格式: 1A,12B,8C 无";"
    private String modelC;

    private HashSet<String> modelCArr = new HashSet<>();

    private String modelE;

    private HashSet<String> modelEArr = new HashSet<>();

    private String modelX;

    private HashSet<String> modelXArr = new HashSet<>();

    public String getModelC() {
        return modelC;
    }

    public void setModelC(String modelC) {
        this.modelC = modelC;
        modelCArr = parseModel(this.modelC);
    }

    public String getModelE() {
        return modelE;
    }

    public void setModelE(String modelE) {
        this.modelE = modelE;
        modelEArr = parseModel(this.modelE);
    }

    public String getModelX() {
        return modelX;
    }

    public String getModelByType(String type) {
        if (TextUtils.isEmpty(type)) {
            return "";
        }
        if (TextUtils.equals(type, Constants.SEAT_X)) {
            return modelX;
        } else if (TextUtils.equals(type, Constants.SEAT_SUB_PRIORITY)) {
            return modelC;
        } else if (TextUtils.equals(type, Constants.SEAT_EMERGENCY)) {
            return modelE;
        }
        return "";
    }

    public void setModelX(String modelX) {
        this.modelX = modelX;
        modelXArr = parseModel(this.modelX);
    }

    public int addModelStar(String[] modifySeats, boolean isModifyEEnable) {
        if (modifySeats ==null || modifySeats.length == 0) {
            return -2;
        }
        for (String seat : modifySeats) {
            // e不能改
            if (!isModifyEEnable && modelEArr.contains(seat)) {
                ToastUtils.toast(ResourceUtils.getString("forbiddenE"));
                return -1;
            }
            modelEArr.remove(seat);
            setModelEArr(modelEArr);
            modelXArr.remove(seat);
            setModelXArr(modelXArr);
            modelCArr.remove(seat);
            setModelCArr(modelCArr);
        }
        return 0;
    }

    public int addModelX(String[] modifySeats) {
        if (modifySeats ==null || modifySeats.length == 0) {
            return -2;
        }
        for (String seat : modifySeats) {
            modelXArr.add(seat);
            setModelXArr(modelXArr);

            modelEArr.remove(seat);
            setModelEArr(modelEArr);
            modelCArr.remove(seat);
            setModelCArr(modelCArr);
        }
        return 0;
    }

    public int addModelE(String[] modifySeats) {
        if (modifySeats ==null || modifySeats.length == 0) {
            return -2;
        }
        for (String seat : modifySeats) {
            modelEArr.add(seat);
            setModelEArr(modelEArr);
            modelXArr.remove(seat);
            setModelXArr(modelXArr);
            modelCArr.remove(seat);
            setModelCArr(modelCArr);
        }
        return 0;
    }

    public int addModelC(String[] modifySeats) {
        if (modifySeats ==null || modifySeats.length == 0) {
            return -2;
        }
        for (String seat : modifySeats) {
            // e不能改
            if (modelEArr.contains(seat)) {
                ToastUtils.toast(ResourceUtils.getString("forbiddenE"));
                return -1;
            }
            modelCArr.add(seat);
            setModelCArr(modelCArr);
            modelXArr.remove(seat);
            setModelXArr(modelXArr);
        }
        return 0;
    }

    private HashSet<String> parseModel(String model) {
        HashSet<String> arrayList = new HashSet<>();
        if (TextUtils.isEmpty(model)) {
            return arrayList;
        }
        model = model.replace(";", "");
        String[] models = model.split(",");
        for (String str: models) {
            if (TextUtils.isEmpty(str) || arrayList.contains(str)) {
                continue;
            }
            arrayList.add(str);
        }
        return arrayList;
    }

    public HashSet<String> getModelCArr() {
        return modelCArr;
    }

    public void setModelCArr(HashSet<String> modelCArr) {
        if (modelCArr == null) {
            this.modelC = null;
            return;
        }
        this.modelCArr = modelCArr;
        modelC = String.join(",", (String[])modelCArr.toArray(new String[0]));
    }

    public HashSet<String> getModelEArr() {
        return modelEArr;
    }

    public void setModelEArr(HashSet<String> modelEArr) {
        if (modelEArr == null) {
            this.modelE = null;
            return;
        }
        this.modelEArr = modelEArr;
        modelE = String.join(",", (String[])modelEArr.toArray(new String[0]));
    }

    public HashSet<String> getModelXArr() {
        return modelXArr;
    }

    public void setModelXArr(HashSet<String> modelXArr) {
        if (modelXArr == null) {
            this.modelX = null;
            return;
        }
        this.modelXArr = modelXArr;
        modelX = String.join(",", (String[])modelXArr.toArray(new String[0]));
    }

    private void removeRefresh(HashSet<String>[] sets, String seat) {
        if (sets == null) {
            return;
        }
        for (HashSet<String> set : sets) {
            if (set == null) {
                continue;
            }
            set.remove(seat);
        }
    }

    @Override
    public ModelExtend clone() throws CloneNotSupportedException {
        ModelExtend obj = (ModelExtend) super.clone();
        obj.setModelX(this.modelX);
        obj.setModelC(this.modelC);
        obj.setModelE(this.modelE);
        return obj;
    }
}