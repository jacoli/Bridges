package com.lichuange.bridges.models;

import android.util.Log;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lichuange on 16/4/3.
 */
public class ExploreModel implements Serializable {
    private String modelId;             // 模型ID
    private Map<String, String> params; // 参数列表

    public ExploreModel() {
        params = new HashMap<>();
    }

    public String getProjectName() {
        return params.get("ProjectName");
    }

    public void setProjectName(String projectName) {
        try {
            params.put("ProjectName", projectName);
        }
        catch (Exception e) {
            Log.e("MainService", e.toString());
        }
    }

    public String getExplorDate() {
        return params.get("ExplorDate");
    }

    public void setExplorDate(String explorDate) {
        try {
            params.put("ExplorDate", explorDate);
        }
        catch (Exception e) {
            Log.e("MainService", e.toString());
        }
    }

    public String getLineName() {
        return params.get("LineName");
    }

    public void setLineName(String lineName) {
        try {
            params.put("LineName", lineName);
        }
        catch (Exception e) {
            Log.e("MainService", e.toString());
        }
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
