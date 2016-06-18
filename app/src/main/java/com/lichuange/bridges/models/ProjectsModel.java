package com.lichuange.bridges.models;

import java.util.List;

public class ProjectsModel extends MsgResponseBase {
    private List<ProjectInfo> items;

    public List<ProjectInfo> getItems() {
        return items;
    }

    public void setItems(List<ProjectInfo> items) {
        this.items = items;
    }

    public class ProjectInfo {
        private String ID;
        private String ProjectNumber;
        private String ProjectName;
        private String CurrentStep;


        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getProjectNumber() {
            return ProjectNumber;
        }

        public void setProjectNumber(String projectNumber) {
            ProjectNumber = projectNumber;
        }

        public String getProjectName() {
            return ProjectName;
        }

        public void setProjectName(String projectName) {
            ProjectName = projectName;
        }

        public String getCurrentStep() {
            return CurrentStep;
        }

        public void setCurrentStep(String currentStep) {
            CurrentStep = currentStep;
        }
    }
}
