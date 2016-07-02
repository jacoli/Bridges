package com.lichuange.bridges.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.lichuange.bridges.R;
import com.lichuange.bridges.activities.CheckDetailActivity;
import com.lichuange.bridges.activities.PlanListActivity;
import com.lichuange.bridges.activities.WebViewActivity;
import com.lichuange.bridges.models.LoginModel;
import com.lichuange.bridges.models.MainService;
import com.lichuange.bridges.views.MyToast;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlanFragment extends Fragment {
    final String[] models = {"现场踏勘", "方案设计", "方案审批", "实施方案"};

    public PlanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View selfView = getActivity().getLayoutInflater().inflate(R.layout.fragment_plan, container, false);
        ListView listView = (ListView)selfView.findViewById(R.id.listView);

        BaseAdapter baseAdapter = new BaseAdapter() {

            @Override
            public int getCount() {
                return models.length;
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
                textView.setText(models[position]);
                return v;
            }
        };

        listView.setAdapter(baseAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //final String[] models = {"现场踏勘", "方案设计", "方案审批", "实施方案"};

                boolean permission = false;
                LoginModel loginModel = MainService.getInstance().getLoginModel();
                if (0 == position) {
                    permission = loginModel.getManage() || loginModel.getExplor();
                } else if (1 == position) {
                    permission = loginModel.getManage() || loginModel.getScheme();
                } else if (2 == position) {
                    permission = loginModel.getManage() || loginModel.getAudit1() || loginModel.getAudit2() ||
                    loginModel.getAudit3() || loginModel.getExternalAuditor();
                } else {
                    permission = loginModel.getManage() || loginModel.getImplement();
                }

                if (!permission) {
                    MyToast.showMessage(getActivity(), "没有权限");
                    return;
                }


                if (position < 3) {
                    Intent intent = new Intent(getActivity(), WebViewActivity.class);
                    String token = MainService.getInstance().getLoginModel().getToken();
                    String url = MainService.getInstance().serverBaseUrl + "/Maintain/APP.ashx?Type=LoginWeb&Token=" + token;
                    intent.putExtra(WebViewActivity.WEBVIEW_EXTRA_URL, url);
                    intent.putExtra(WebViewActivity.WEBVIEW_EXTRA_TITLE, models[position]);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), PlanListActivity.class);
                    startActivity(intent);
                }
            }
        });

        return selfView;
    }
}
