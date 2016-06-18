package com.lichuange.bridges.models;

import com.google.gson.Gson;

import java.security.PublicKey;
import java.util.List;
import java.util.Map;

/**
 * Created by lichuange on 16/4/16.
 */
public class ExploreParamsMetaInfo {

    public class ExploreParamValueModel {
        private String displayName;
        private String value;

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public class ExploreParamConditionModel {
        private String conditionKey;
        private List<String> conditionValue;
        private String itemValue;
        private List<ExploreParamConditionModel> subConditions;


        public String getConditionKey() {
            return conditionKey;
        }

        public void setConditionKey(String conditionKey) {
            this.conditionKey = conditionKey;
        }

        public List<String> getConditionValue() {
            return conditionValue;
        }

        public void setConditionValue(List<String> conditionValue) {
            this.conditionValue = conditionValue;
        }

        public String getItemValue() {
            return itemValue;
        }

        public void setItemValue(String itemValue) {
            this.itemValue = itemValue;
        }

        public List<ExploreParamConditionModel> getSubConditions() {
            return subConditions;
        }

        public void setSubConditions(List<ExploreParamConditionModel> subConditions) {
            this.subConditions = subConditions;
        }
    }

    public class ExploreParamItemModel {
        static final public String SELECTION_MODE_SELECT = "selection";
        static final public String SELECTION_MODE_EDIT_NUM = "edit_number";
        static final public String SELECTION_MODE_EDIT_TEXT = "edit_text";

        private String itemKey;
        private String displayName;
        private String unit;
        private String selectionMode;
        private String defaultValueRange;
        private String defaultValue;
        private Map<String, List<ExploreParamValueModel>> valueRanges;
        private List<ExploreParamConditionModel> conditions;
        private String minValue;
        private String maxValue;
        private String RegEx;

        public String getItemKey() {
            return itemKey;
        }

        public void setItemKey(String itemKey) {
            this.itemKey = itemKey;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public List<ExploreParamConditionModel> getConditions() {
            return conditions;
        }

        public void setConditions(List<ExploreParamConditionModel> conditions) {
            this.conditions = conditions;
        }


        public Map<String, List<ExploreParamValueModel>> getValueRanges() {
            return valueRanges;
        }

        public void setValueRanges(Map<String, List<ExploreParamValueModel>> valueRanges) {
            this.valueRanges = valueRanges;
        }

        public String getDefaultValueRange() {
            return defaultValueRange;
        }

        public void setDefaultValueRange(String defaultValueRange) {
            this.defaultValueRange = defaultValueRange;
        }

        public String getSelectionMode() {
            return selectionMode;
        }

        public void setSelectionMode(String selectionMode) {
            this.selectionMode = selectionMode;
        }

        public String getMinValue() {
            return minValue;
        }

        public void setMinValue(String minValue) {
            this.minValue = minValue;
        }

        public String getMaxValue() {
            return maxValue;
        }

        public void setMaxValue(String maxValue) {
            this.maxValue = maxValue;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public String getRegEx() {
            return RegEx;
        }

        public void setRegEx(String regEx) {
            RegEx = regEx;
        }
    }

    private List<ExploreParamItemModel> items;

    public List<ExploreParamItemModel> getItems() {
        return items;
    }

    public void setItems(List<ExploreParamItemModel> items) {
        this.items = items;
    }

    static ExploreParamsMetaInfo paramsModel;

    static public ExploreParamsMetaInfo getParamsModel() {
        return paramsModel;
    }

    static public void setNewParamsModel(String jsonData) {
        if (jsonData == null || jsonData.length() == 0) {
            return;
        }

        Gson gson = new Gson();
        paramsModel = gson.fromJson(jsonData, ExploreParamsMetaInfo.class);
    }
}
