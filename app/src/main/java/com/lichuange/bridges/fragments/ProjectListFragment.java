package com.lichuange.bridges.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;

import com.lichuange.bridges.R;
import com.lichuange.bridges.activities.CheckDetailActivity;
import com.lichuange.bridges.models.LoginModel;
import com.lichuange.bridges.models.MainService;
import com.lichuange.bridges.models.ProjectsModel;
import com.lichuange.bridges.views.MyToast;

import java.lang.ref.WeakReference;
import java.util.List;


public class ProjectListFragment extends Fragment {
    private View selfView;

    private ListView listView;

    private int curIndex = -1;

    private LayoutInflater minflater;

    private MyHandler handler = new MyHandler(this);

    static class MyHandler extends Handler {
        WeakReference<ProjectListFragment> weakFragment;

        public MyHandler(ProjectListFragment fragment){
            weakFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (weakFragment == null) {
                return;
            }

            switch (msg.what) {
                case MainService.MSG_QUERY_PROJECTS_SUCCESS:
                    weakFragment.get().onResponseSuccess();
                    break;
                case MainService.MSG_QUERY_PROJECTS_FAILED:
                    weakFragment.get().onResponseFailed();
                    break;
                default:
                    break;
            }
        }
    }

    public ProjectListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        selfView = inflater.inflate(R.layout.fragment_projectlist, container,
                false);
        minflater = inflater;
        listView = (ListView)selfView.findViewById(R.id.listView);

        BaseAdapter baseAdapter = new BaseAdapter() {

            @Override
            public int getCount() {
                ProjectsModel model = MainService.getInstance().getProjectsModel();
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
                View v = getActivity().getLayoutInflater().inflate(R.layout.list_item_base_action, null);
                TextView textView = (TextView)v.findViewById(R.id.textView);
                ProjectsModel model = MainService.getInstance().getProjectsModel();
                if (model != null) {
                    List<ProjectsModel.ProjectInfo> items =  model.getItems();
                    if (items.size() > position) {
                        ProjectsModel.ProjectInfo projectInfo = items.get(position);
                        textView.setText(projectInfo.getProjectName());
                        if (curIndex == position) {
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
                LoginModel loginModel = MainService.getInstance().getLoginModel();
                boolean permission = loginModel.getManage() || loginModel.getImplement();
                if (!permission) {
                    MyToast.showMessage(getActivity(), "没有权限");
                    return;
                }

                ProjectsModel model = MainService.getInstance().getProjectsModel();
                if (model != null) {
                    List<ProjectsModel.ProjectInfo> items = model.getItems();
                    if (items.size() > position) {
                        ProjectsModel.ProjectInfo projectInfo = items.get(position);
                        Intent intent = new Intent(getActivity(), CheckDetailActivity.class);
                        intent.putExtra("ProjectID", projectInfo.getID());
                        startActivity(intent);

                        curIndex = position;
                        ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
                    }
                }
            }
        });

        LoginModel loginModel = MainService.getInstance().getLoginModel();
        if (loginModel.getManage() || loginModel.getImplement()) {
            MainService.getInstance().sendProjectsQuery(handler);
        }
        else {
            MyToast.showMessage(getActivity(), "没有权限");
        }

        return selfView;
    }

    public void onResponseSuccess() {
        MyToast.showMessage(getActivity(), "获取项目列表成功");
        ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
    }

    public void onResponseFailed() {
        Toast.makeText(getActivity(), "获取项目列表失败", Toast.LENGTH_SHORT).show();
        ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
    }
}
