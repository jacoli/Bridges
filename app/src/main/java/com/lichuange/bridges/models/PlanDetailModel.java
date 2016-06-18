package com.lichuange.bridges.models;

import java.util.List;

/**
 * Created by lichuange on 16/6/18.
 */
public class PlanDetailModel extends MsgResponseBase {
    private String ProjectName;
    private String ExplorDate;
    private String LineName;
    private String ControlStartStack;
    private String ControlEndStack;
    private String GZ1;
    private String GZ1Lon;
    private String GZ1Lat;
    private String SchemeImage;
    private String WarningArea;
    private String UpTransitionArea;
    private String PortraitBuffer;
    private String LateralBufferOutput;
    private String WorkspaceGOutput;
    private String DownTransitionArea;
    private String TerminatorZ;
    private String SpeedLimitVal;
    private String Table2Count;
    private List<Sign> table2;
    private String Table3Count;
    private List<Sign> table3;

    public String getProjectName() {
        return ProjectName;
    }

    public void setProjectName(String projectName) {
        ProjectName = projectName;
    }

    public String getExplorDate() {
        return ExplorDate;
    }

    public void setExplorDate(String explorDate) {
        ExplorDate = explorDate;
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

    public String getWarningArea() {
        return WarningArea;
    }

    public void setWarningArea(String warningArea) {
        WarningArea = warningArea;
    }

    public String getUpTransitionArea() {
        return UpTransitionArea;
    }

    public void setUpTransitionArea(String upTransitionArea) {
        UpTransitionArea = upTransitionArea;
    }

    public String getPortraitBuffer() {
        return PortraitBuffer;
    }

    public void setPortraitBuffer(String portraitBuffer) {
        PortraitBuffer = portraitBuffer;
    }

    public String getLateralBufferOutput() {
        return LateralBufferOutput;
    }

    public void setLateralBufferOutput(String lateralBufferOutput) {
        LateralBufferOutput = lateralBufferOutput;
    }

    public String getWorkspaceGOutput() {
        return WorkspaceGOutput;
    }

    public void setWorkspaceGOutput(String workspaceGOutput) {
        WorkspaceGOutput = workspaceGOutput;
    }

    public String getDownTransitionArea() {
        return DownTransitionArea;
    }

    public void setDownTransitionArea(String downTransitionArea) {
        DownTransitionArea = downTransitionArea;
    }

    public String getTerminatorZ() {
        return TerminatorZ;
    }

    public void setTerminatorZ(String terminatorZ) {
        TerminatorZ = terminatorZ;
    }

    public String getSpeedLimitVal() {
        return SpeedLimitVal;
    }

    public void setSpeedLimitVal(String speedLimitVal) {
        SpeedLimitVal = speedLimitVal;
    }

    public String getTable2Count() {
        return Table2Count;
    }

    public void setTable2Count(String table2Count) {
        Table2Count = table2Count;
    }

    public List<Sign> getTable2() {
        return table2;
    }

    public void setTable2(List<Sign> table2) {
        this.table2 = table2;
    }

    public String getTable3Count() {
        return Table3Count;
    }

    public void setTable3Count(String table3Count) {
        Table3Count = table3Count;
    }

    public List<Sign> getTable3() {
        return table3;
    }

    public void setTable3(List<Sign> table3) {
        this.table3 = table3;
    }

    public class Sign {
        private String SignNumber;
        private String SignCode;
        private String SignName;
        private String SignImageURL;
        private String SignCount;
        private String SignRemark;


        public String getSignNumber() {
            return SignNumber;
        }

        public void setSignNumber(String signNumber) {
            SignNumber = signNumber;
        }

        public String getSignCode() {
            return SignCode;
        }

        public void setSignCode(String signCode) {
            SignCode = signCode;
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

        public String getSignCount() {
            return SignCount;
        }

        public void setSignCount(String signCount) {
            SignCount = signCount;
        }

        public String getSignRemark() {
            return SignRemark;
        }

        public void setSignRemark(String signRemark) {
            SignRemark = signRemark;
        }
    }
}
