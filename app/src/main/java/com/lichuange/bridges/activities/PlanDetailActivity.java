package com.lichuange.bridges.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.ListView;
import com.lichuange.bridges.R;
import com.lichuange.bridges.models.MainService;
import com.lichuange.bridges.models.PlanDetailModel;
import com.lichuange.bridges.views.MyToast;

public class PlanDetailActivity extends MyBaseActivity {
    private PlanDetailModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_detail);

        createTitleBar();
        titleBar.setLeftText("方案列表");
        titleBar.setTitle("方案详情");

        Intent intent = getIntent();
        String modelId = intent.getStringExtra("ProjectID");
        if (modelId != null && modelId.length() > 0) {
            MainService.getInstance().sendPlanProjectDetailQuery(modelId, handler);

        }
    }

    @Override
    public void onResponse(int msgCode, Object obj) {
        ListView listView = (ListView)findViewById(R.id.listView);
        model = (PlanDetailModel)obj;
        switch (msgCode) {
            case MainService.MSG_QUERY_PROJECT_DETAIL_SUCCESS:
                MyToast.showMessage(getBaseContext(), "获取方案详情成功");
                ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
                break;
            case MainService.MSG_QUERY_PROJECT_DETAIL_FAILED:
                MyToast.showMessage(getBaseContext(), "获取方案详情失败");
                ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
                break;
            default:
                break;
        }
    }
}
