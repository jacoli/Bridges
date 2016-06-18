package com.lichuange.bridges.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.lichuange.bridges.R;
import com.lichuange.bridges.models.MainService;
import com.lichuange.bridges.models.ProjectsModel;
import com.lichuange.bridges.views.MyToast;

import java.util.List;

public class PlanListActivity extends MyBaseActivity {

    private ProjectsModel model;
    private int selectedIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_list);

        createTitleBar();
        titleBar.setLeftText("方案");
        titleBar.setTitle("方案列表");

        selectedIndex = -1;

        final ListView listView = (ListView)findViewById(R.id.listView);

        BaseAdapter baseAdapter = new BaseAdapter() {

            @Override
            public int getCount() {
                if (model != null) {
                    List<ProjectsModel.ProjectInfo> items =  model.getItems();
                    if (items != null) {
                        return items.size();
                    }
                }

                return 0;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = getLayoutInflater().inflate(R.layout.list_item_base_action, null);
                TextView textView = (TextView)v.findViewById(R.id.textView);
                if (model != null) {
                    List<ProjectsModel.ProjectInfo> items =  model.getItems();
                    if (items.size() > position) {
                        ProjectsModel.ProjectInfo projectInfo = items.get(position);
                        textView.setText(projectInfo.getProjectName());
                        if (selectedIndex == position) {
                            textView.setTextColor(Color.parseColor("#4287e1"));
                        }
                        else {
                            textView.setTextColor(Color.parseColor("#5b5b58"));
                        }
                    }
                }

                return v;
            }
        };

        listView.setAdapter(baseAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (model != null) {
                    List<ProjectsModel.ProjectInfo> items = model.getItems();
                    if (items.size() > position) {
                        ProjectsModel.ProjectInfo projectInfo = items.get(position);
                        Intent intent = new Intent(PlanListActivity.this ,PlanDetailActivity.class);
                        intent.putExtra("ProjectID", projectInfo.getID());
                        startActivity(intent);

                        selectedIndex = position;
                        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
                    }
                }
            }
        });

        MainService.getInstance().sendPlanProjectsQuery(handler);
    }

    @Override
    public void onResponse(int msgCode, Object obj) {
        ListView listView = (ListView)findViewById(R.id.listView);
        model = (ProjectsModel)obj;
        switch (msgCode) {
            case MainService.MSG_QUERY_PROJECTS_SUCCESS:
                MyToast.showMessage(getBaseContext(), "获取方案列表成功");
                ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
                break;
            case MainService.MSG_QUERY_PROJECTS_FAILED:
                MyToast.showMessage(getBaseContext(), "获取方案列表失败");
                ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
                break;
            default:
                break;
        }
    }
}
