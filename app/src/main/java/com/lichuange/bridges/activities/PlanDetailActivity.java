package com.lichuange.bridges.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lichuange.bridges.R;
import com.lichuange.bridges.models.GetProjectDetailResponse;
import com.lichuange.bridges.models.MainService;
import com.lichuange.bridges.models.PlanDetailModel;
import com.lichuange.bridges.models.ProjectModel;
import com.lichuange.bridges.models.SignItemModel;
import com.lichuange.bridges.models.Utils;
import com.lichuange.bridges.views.MyToast;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        updateMainInfoViews();
    }

    public void updateMainInfoViews() {
        // 项目基本信息
        TextView projectNameText = (TextView)findViewById(R.id.projectNameText);
        projectNameText.setText(model != null ? model.getProjectName() : "");

        TextView timeText = (TextView)findViewById(R.id.TimeText);
        timeText.setText(model != null ? model.getImplementStartDate() : "");

        TextView lineNameText = (TextView)findViewById(R.id.LineNameText);
        lineNameText.setText(model != null ? model.getLineName() : "");

        TextView controlStartStackText = (TextView)findViewById(R.id.ControlStartStackText);
        controlStartStackText.setText(model != null ? model.getControlStartStack() : "");

        TextView controlEndStackText = (TextView)findViewById(R.id.ControlEndStackText);
        controlEndStackText.setText(model != null ? model.getControlEndStack() : "");

//        TextView workStartStackText = (TextView)findViewById(R.id.WorkStartStackText);
//        workStartStackText.setText(model != null ? model.getGZ1() : "");
//
//        String locationDesc = "东经：" + (model != null ? model.getGZ1Lon() : "") + "，北纬：" + (model != null ? model.getGZ1Lat() : "");
//        TextView workStackLocationText = (TextView)findViewById(R.id.WorkStackLocationText);
//        workStackLocationText.setText(locationDesc);

        // 更新图片
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseContext()));
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        imageLoader.displayImage(model != null ? model.getSchemeImage() : "", imageView);
    }

    public void updateTable1() {
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseContext()));
        LinearLayout boardsCheckLayout = (LinearLayout)findViewById(R.id.content1);
        boardsCheckLayout.removeAllViews();

        if (model != null) {

            final List<Map<String, String>> dataSource = new ArrayList<>();
            Map<String, String> item1 = new HashMap<>();
            item1.put("v1", "警告区");
            item1.put("v2", "S");
            item1.put("v3", "m");
            item1.put("v4", model != null ? model.getWarningArea() : "");
            dataSource.add(item1);

            Map<String, String> item2 = new HashMap<>();
            item2.put("v1", "上游过渡区");
            item2.put("v2", "Ls或Lj");
            item2.put("v3", "m");
            item2.put("v4", model != null ? model.getUpTransitionArea() : "");
            dataSource.add(item2);

            Map<String, String> item3 = new HashMap<>();
            item3.put("v1", "纵向缓冲区");
            item3.put("v2", "H");
            item3.put("v3", "m");
            item3.put("v4", model != null ? model.getPortraitBuffer() : "");
            dataSource.add(item3);

            Map<String, String> item6 = new HashMap<>();
            item6.put("v1", "横向缓冲区");
            item6.put("v2", "Hh");
            item6.put("v3", "m");
            item6.put("v4", model != null ? model.getLateralBufferOutput() : "");
            dataSource.add(item6);

            Map<String, String> item7 = new HashMap<>();
            item7.put("v1", "工作区");
            item7.put("v2", "G");
            item7.put("v3", "m");
            item7.put("v4", model != null ? model.getWorkspaceGOutput() : "");
            dataSource.add(item7);




            Map<String, String> item5 = new HashMap<>();
            item5.put("v1", "下游过渡区");
            item5.put("v2", "Lx");
            item5.put("v3", "m");
            item5.put("v4", model != null ? model.getDownTransitionArea() : "");
            dataSource.add(item5);





            Map<String, String> item8 = new HashMap<>();
            item8.put("v1", "终止区");
            item8.put("v2", "Z");
            item8.put("v3", "m");
            item8.put("v4", model != null ? model.getTerminatorZ() : "");
            dataSource.add(item8);


            Map<String, String> item4 = new HashMap<>();
            item4.put("v1", "限速值");
            item4.put("v2", "V");
            item4.put("v3", "km/h");
            item4.put("v4", model != null ? model.getSpeedLimitVal() : "");
            dataSource.add(item4);



            for (int i = 0; i < dataSource.size(); ++i) {
                Map<String, String> item = dataSource.get(i);

                LinearLayout boardLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.plan_detail_table_item1, null);

                TextView indexText = (TextView)boardLayout.findViewById(R.id.IndexText);
                TextView nameText = (TextView)boardLayout.findViewById(R.id.NameText);
                TextView designedStackText = (TextView)boardLayout.findViewById(R.id.DesignedStackText);
                final TextView realStackText = (TextView)boardLayout.findViewById(R.id.RealStackText);

                indexText.setText(item.get("v1"));
                nameText.setText(item.get("v2"));
                designedStackText.setText(item.get("v3"));
                realStackText.setText(item.get("v4"));

                boardsCheckLayout.addView(boardLayout);
            }

        }

        ScrollView scrollView = (ScrollView)findViewById(R.id.myScrollView);
        scrollView.invalidate();
    }

    public void updateTable2() {
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseContext()));
        LinearLayout boardsCheckLayout = (LinearLayout)findViewById(R.id.content2);
        boardsCheckLayout.removeAllViews();

        if (model != null && model.getTable2() != null) {
            for (int i = 0; i < model.getTable2().size(); ++i) {
                final PlanDetailModel.Sign item = model.getTable2().get(i);

                LinearLayout boardLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.plan_detail_table_item2, null);

                TextView indexText = (TextView)boardLayout.findViewById(R.id.IndexText);
                TextView nameText = (TextView)boardLayout.findViewById(R.id.NameText);
                TextView designedStackText = (TextView)boardLayout.findViewById(R.id.DesignedStackText);
                final TextView realStackText = (TextView)boardLayout.findViewById(R.id.RealStackText);
                ImageView stackImageView = (ImageView)boardLayout.findViewById(R.id.ImageView);

                String indexStr = Integer.toString(i + 1);
                indexText.setText(indexStr);
                nameText.setText(item.getSignName());
                designedStackText.setText(item.getSignCount());
                realStackText.setText(item.getSignRemark());
                imageLoader.displayImage(item.getSignImageURL(), stackImageView);

                boardsCheckLayout.addView(boardLayout);
            }
        }

        ScrollView scrollView = (ScrollView)findViewById(R.id.myScrollView);
        scrollView.invalidate();
    }

    public void updateTable3() {
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseContext()));
        LinearLayout boardsCheckLayout = (LinearLayout)findViewById(R.id.content3);
        boardsCheckLayout.removeAllViews();

        if (model != null && model.getTable3() != null) {
            for (int i = 0; i < model.getTable3().size(); ++i) {
                final PlanDetailModel.Sign2 item = model.getTable3().get(i);

                LinearLayout boardLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.plan_detail_table_item3, null);

                TextView indexText = (TextView)boardLayout.findViewById(R.id.IndexText);
                TextView nameText = (TextView)boardLayout.findViewById(R.id.NameText);
                TextView designedStackText = (TextView)boardLayout.findViewById(R.id.DesignedStackText);
                ImageView stackImageView = (ImageView)boardLayout.findViewById(R.id.ImageView);

                String indexStr = Integer.toString(i + 1);
                indexText.setText(indexStr);
                nameText.setText(item.getSignName());
                designedStackText.setText(item.getStackNumber());
                imageLoader.displayImage(item.getSignImageURL(), stackImageView);

                boardsCheckLayout.addView(boardLayout);
            }

        }

        ScrollView scrollView = (ScrollView)findViewById(R.id.myScrollView);
        scrollView.invalidate();
    }

    @Override
    public void onResponse(int msgCode, Object obj) {
        model = (PlanDetailModel)obj;
        switch (msgCode) {
            case MainService.MSG_QUERY_PROJECT_DETAIL_SUCCESS:
                MyToast.showMessage(getBaseContext(), "获取方案详情成功");
                updateMainInfoViews();
                updateTable1();
                updateTable2();
                updateTable3();
                break;
            case MainService.MSG_QUERY_PROJECT_DETAIL_FAILED:
                MyToast.showMessage(getBaseContext(), "获取方案详情失败");
                updateMainInfoViews();
                updateTable1();
                updateTable2();
                updateTable3();
                break;
            default:
                break;
        }
    }
}
