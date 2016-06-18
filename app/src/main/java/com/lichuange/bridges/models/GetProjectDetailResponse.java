package com.lichuange.bridges.models;

import java.util.List;

/**
 * Created by lichuange on 16/4/2.
 */
public class GetProjectDetailResponse extends MsgResponseBase {

    private String ProjectName;
    private String LineName;
    private String ControlStartStack;
    private String ControlEndStack;
    private String GZ1;
    private String GZ1Lon;
    private String GZ1Lat;
    private String SchemeImage;
    private String SignCount;
    private List<CheckItemModel> items;
    private int checkedItemsCount;

    public String getProjectName() {
        return ProjectName;
    }

    public void setProjectName(String projectName) {
        ProjectName = projectName;
    }

    public String getLineName() {
        return LineName;
    }

    public void setLineName(String lineName) {
        LineName = lineName;
    }

    public String getControlStartStack() {
        return ControlStartStack;
    }

    public void setControlStartStack(String controlStartStack) {
        ControlStartStack = controlStartStack;
    }

    public String getControlEndStack() {
        return ControlEndStack;
    }

    public void setControlEndStack(String controlEndStack) {
        ControlEndStack = controlEndStack;
    }

    public String getGZ1() {
        return GZ1;
    }

    public void setGZ1(String GZ1) {
        this.GZ1 = GZ1;
    }

    public String getGZ1Lon() {
        return GZ1Lon;
    }

    public void setGZ1Lon(String GZ1Lon) {
        this.GZ1Lon = GZ1Lon;
    }

    public String getGZ1Lat() {
        return GZ1Lat;
    }

    public void setGZ1Lat(String GZ1Lat) {
        this.GZ1Lat = GZ1Lat;
    }

    public String getSchemeImage() {
        return SchemeImage;
    }

    public void setSchemeImage(String schemeImage) {
        SchemeImage = schemeImage;
    }

    public String getSignCount() {
        return SignCount;
    }

    public void setSignCount(String signCount) {
        SignCount = signCount;
    }

    public List<CheckItemModel> getItems() {
        return items;
    }

    public void setItems(List<CheckItemModel> items) {
        this.items = items;
    }

    public int getCheckedItemsCount() {
        return checkedItemsCount;
    }

    public void setCheckedItemsCount(int checkedItemsCount) {
        this.checkedItemsCount = checkedItemsCount;
    }

    public class CheckItemModel {
        private String SignNumber;
        private String SignName;
        private String SignImageURL;
        private String StackNumber;

        public String getSignNumber() {
            return SignNumber;
        }

        public void setSignNumber(String signNumber) {
            SignNumber = signNumber;
        }

        public String getSignName() {
            return SignName;
        }

        public void setSignName(String signName) {
            SignName = signName;
        }

        public String getSignImageURL() {
            return SignImageURL;
        }

        public void setSignImageURL(String signImageURL) {
            SignImageURL = signImageURL;
        }

        public String getStackNumber() {
            return StackNumber;
        }

        public void setStackNumber(String stackNumber) {
            StackNumber = stackNumber;
        }
    }
}
