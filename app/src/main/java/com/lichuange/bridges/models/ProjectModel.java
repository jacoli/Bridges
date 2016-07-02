package com.lichuange.bridges.models;

import java.util.List;

/**
 * Created by lichuange on 16/4/2.
 */
public class ProjectModel {
    private GetProjectDetailResponse detail;
    private List<SignItemModel> SignItems;
    private List<SensorItemModel> SensorItems;

    public List<SignItemModel> getSignItems() {
        return SignItems;
    }

    public void setSignItems(List<SignItemModel> signItems) {
        SignItems = signItems;
    }

    public GetProjectDetailResponse getDetail() {
        return detail;
    }

    public void setDetail(GetProjectDetailResponse detail) {
        this.detail = detail;
    }

    public List<SensorItemModel> getSensorItems() {
        return SensorItems;
    }

    public void setSensorItems(List<SensorItemModel> sensorItems) {
        SensorItems = sensorItems;
    }

    public String findSensorIdForSensorNumber(String sensorNumber) {
        if (SensorItems == null) {
            return null;
        }

        for (SensorItemModel item : SensorItems) {
            if (item.getSensorNumber().equals(sensorNumber)) {
                return item.getProjectSensorID();
            }
        }

        return null;
    }

    public void deleteSignWithID(String signId) {
        if (signId == null || signId.length() == 0) {
            return;
        }

        for (SignItemModel item : SignItems) {
            if (item.getID().equals(signId)) {
                item.setActualStackNumber("");
            }
        }
    }

    public void deleteSensorForSensorNumber(String sensorNumber) {
        if (SensorItems == null) {
            return;
        }

        for (SensorItemModel item : SensorItems) {
            if (item.getSensorNumber().equals(sensorNumber)) {
                SensorItems.remove(item);
                return;
            }
        }
    }
}
