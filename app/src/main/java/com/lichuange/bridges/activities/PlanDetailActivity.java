package com.lichuange.bridges.activities;

import android.os.Bundle;

import com.lichuange.bridges.R;

public class PlanDetailActivity extends MyBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_detail);

        createTitleBar();
        titleBar.setLeftText("方案列表");
        titleBar.setTitle("方案详情");
    }
}
