package com.lichuange.bridges.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.lichuange.bridges.R;
import com.lichuange.bridges.activities.ExploreDetailActivity;
import com.lichuange.bridges.models.ExploreModel;
import com.lichuange.bridges.models.LoginModel;
import com.lichuange.bridges.models.MainService;
import com.lichuange.bridges.views.MyToast;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;


public class ExploreListFragment extends Fragment {

    private List<ExploreModel> modelList;
    private ListView listView;

    static boolean isModelListLoaded = false;
    public void checkAndLoadExploreModelList() {
        if (!isModelListLoaded) {
            isModelListLoaded = true;

            try {
                FileInputStream stream = getActivity().openFileInput("explore_list.s");
                ObjectInputStream ois = new ObjectInputStream(stream);
                List<ExploreModel> list = (List<ExploreModel>)ois.readObject();
                ois.close();
                stream.close();
                MainService.getInstance().setExploreList(list);
            }
            catch (Exception e) {
                Log.e("", e.toString());
            }
            finally {
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View selfView = getActivity().getLayoutInflater().inflate(R.layout.fragment_plan, container, false);
        listView = (ListView)selfView.findViewById(R.id.listView);

        checkAndLoadExploreModelList();

        modelList = MainService.getInstance().getExploreList();

        BaseAdapter baseAdapter = new BaseAdapter() {

            @Override
            public int getCount() {
                return modelList.size() + 1;
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
                if (position == getCount() - 1) {
                    View v = getActivity().getLayoutInflater().inflate(R.layout.list_item_base_action_button, null);
                    TextView textView = (TextView)v.findViewById(R.id.textView);
                    textView.setText("创建新的踏勘任务");
                    return v;
                }
                else {
                    ExploreModel model = modelList.get(position);
                    View v = getActivity().getLayoutInflater().inflate(R.layout.list_item_base_action_with_subtitle, null);
                    TextView textView = (TextView)v.findViewById(R.id.textView);
                    TextView textView2 = (TextView)v.findViewById(R.id.textView2);
                    textView.setText(model.getProjectName());
                    textView2.setText(model.getExplorDate());
                    return v;
                }
            }
        };

        listView.setAdapter(baseAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                LoginModel loginModel = MainService.getInstance().getLoginModel();
                boolean permission = loginModel.getManage() || loginModel.getExplor();
                if (!permission) {
                    MyToast.showMessage(getActivity(), "没有权限");
                    return;
                }


                Intent intent = new Intent(getActivity(), ExploreDetailActivity.class);
                if (position < modelList.size()) {
                    intent.putExtra(ExploreDetailActivity.EXPLORE_EXTRA_MODEL_ID, modelList.get(position).getModelId());
                }
                startActivity(intent);
            }
        });

        return selfView;
    }

    @Override
    public void onStart() {
        super.onStart();

        // 视图显示时重新刷新listview
        BaseAdapter adapter = (BaseAdapter)listView.getAdapter();
        adapter.notifyDataSetChanged();
    }
}

