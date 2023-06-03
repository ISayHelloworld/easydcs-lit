package main.entity;

import com.alibaba.fastjson.JSON;
import main.utils.TextUtils;

import java.sql.Date;

public class FlightModel implements Cloneable {
    private String company;
    private String modelNumber;
    private String layout;

    private ModelExtend modelExtendObj = new ModelExtend();

    private String modelExtend;

    private Date recordTime;
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getLayout() {
        if (!TextUtils.isEmpty(layout) && !layout.endsWith(";")) {
            layout += ";";
        }
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    public ModelExtend getModelExtendObj() {
        if (this.modelExtendObj == null) {
            this.modelExtendObj = new ModelExtend();
        }
        return this.modelExtendObj;
    }

    public void setModelExtendObj(ModelExtend modelExtendObj) {
        this.modelExtendObj = modelExtendObj;
        this.modelExtend = JSON.toJSONString(modelExtendObj);
    }

    public void setModelExtend(String modelExtendObj) {
        this.modelExtend = modelExtendObj;
        this.modelExtendObj = JSON.parseObject(modelExtendObj, ModelExtend.class);
    }

    public String getModelExtend() {
        return this.modelExtend;
    }

    @Override
    public FlightModel clone() throws CloneNotSupportedException {
        FlightModel obj = (FlightModel) super.clone();
        if (this.modelExtendObj != null) {
            obj.setModelExtendObj(this.modelExtendObj.clone());
        }
        obj.setLayout(this.layout);
        obj.setModelNumber(this.modelNumber);
        obj.setCompany(this.company);
        obj.setRecordTime(this.recordTime);
        return obj;
    }
}