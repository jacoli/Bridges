package com.lichuange.bridges.models;

/**
 * Created by lichuange on 16/4/2.
 */
public class SignItemModel {
    private String SignName; // 标志名
    private String SignCode; // 标志编码
    private String SignImageFileName; // 标志图案URL地址
    private String DesignStackNumber; // 设计桩号
    private String ActualStackNumber; // 实际桩号
    private String AddDate; // 更新日期
    private String Status; // 状态，0正常，1偏离


    public String getSignName() {
        return SignName;
    }

    public void setSignName(String signName) {
        SignName = signName;
    }

    public String getSignCode() {
        return SignCode;
    }

    public void setSignCode(String signCode) {
        SignCode = signCode;
    }

    public String getSignImageFileName() {
        return SignImageFileName;
    }

    public void setSignImageFileName(String signImageFileName) {
        SignImageFileName = signImageFileName;
    }

    public String getDesignStackNumber() {
        return DesignStackNumber;
    }

    public void setDesignStackNumber(String designStackNumber) {
        DesignStackNumber = designStackNumber;
    }

    public String getActualStackNumber() {
        return ActualStackNumber;
    }

    public void setActualStackNumber(String actualStackNumber) {
        ActualStackNumber = actualStackNumber;
    }

    public String getAddDate() {
        return AddDate;
    }

    public void setAddDate(String addDate) {
        AddDate = addDate;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
