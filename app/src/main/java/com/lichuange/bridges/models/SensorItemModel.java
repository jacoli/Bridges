package com.lichuange.bridges.models;

import android.util.Log;

/**
 * Created by lichuange on 16/4/3.
 */
public class SensorItemModel {
    private String ProjectSensorID; // 传感器现场检查ID
    private String SensorNumber; // 传感器编号
    private String StackNumber; // 传感器桩号
    private String Angle; //角度
    private String Electricity; // 电量
    private String AddDate; // 更新时间
    private String Status; // 状态

    public String getProjectSensorID() {
        return ProjectSensorID;
    }

    public void setProjectSensorID(String projectSensorID) {
        ProjectSensorID = projectSensorID;
    }

    public String getSensorNumber() {
        return SensorNumber;
    }

    public void setSensorNumber(String sensorNumber) {
        SensorNumber = sensorNumber;
    }

    public String getStackNumber() {
        return StackNumber;
    }

    public void setStackNumber(String stackNumber) {
        StackNumber = stackNumber;
    }

    public String getAngle() {
        return Angle;
    }

    public void setAngle(String angle) {
        Angle = angle;
    }

    public String getElectricity() {
        return Electricity;
    }

    public void setElectricity(String electricity) {
        Electricity = electricity;
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

    public boolean isSensorFall() {
        boolean isSensorFall = false;
        if (getAngle() != null && getAngle().length() > 0) {
            try {
                Double angle = Double.valueOf(getAngle());
                if (angle < 25) {
                    isSensorFall = true;
                }
            }
            catch (Exception e) {
                Log.e("", e.toString());
            }
        }

        return isSensorFall;
    }
}
