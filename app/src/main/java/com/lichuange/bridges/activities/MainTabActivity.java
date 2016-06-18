package com.lichuange.bridges.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.lichuange.bridges.fragments.PlanFragment;
import com.lichuange.bridges.fragments.ProjectListFragment;
import com.lichuange.bridges.R;
import com.lichuange.bridges.fragments.ExploreListFragment;

public class MainTabActivity extends MyBaseActivity implements View.OnClickListener {

    /**
     * 踏勘tab页
     */
    private ExploreListFragment exploreListFragment;
    /**
     * 方案tab页
     */
    private PlanFragment planFragment;
    /**
     * 检查tab页
     */
    private ProjectListFragment projectListFragment;


    private View explorItemView;
    private View planItemView;
    private View checkItemView;

    private View explorFlagView;
    private View planFlagView;
    private View checkFlagView;

    private TextView explorText;
    private TextView planText;
    private TextView checkText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        createTitleBar();
        titleBar.setLeftText("注销");

        explorItemView = findViewById(R.id.explorLayout);
        planItemView = findViewById(R.id.planLayout);
        checkItemView = findViewById(R.id.checkLayout);

        explorFlagView = (View) findViewById(R.id.explorFlag);
        planFlagView = (View) findViewById(R.id.planFlag);
        checkFlagView = (View) findViewById(R.id.checkFlag);

        explorText = (TextView) findViewById(R.id.explorText);
        planText = (TextView) findViewById(R.id.planText);
        checkText = (TextView) findViewById(R.id.checkText);

        explorItemView.setOnClickListener(this);
        planItemView.setOnClickListener(this);
        checkItemView.setOnClickListener(this);

        // 第一次启动时选中第0个tab
        setTabSelection(2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.explorLayout:
                // 当点击了消息tab时，选中第1个tab
                setTabSelection(0);
                break;
            case R.id.planLayout:
                // 当点击了联系人tab时，选中第2个tab
                setTabSelection(1);
                break;
            case R.id.checkLayout:
                // 当点击了动态tab时，选中第3个tab
                setTabSelection(2);
                break;
            default:
                break;
        }
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     * @param index
     *            每个tab页对应的下标。0表示消息，1表示联系人，2表示动态，3表示设置。
     */
    private void setTabSelection(int index) {

        // 每次选中之前先清楚掉上次的选中状态
        clearSelection();
        // 开启一个Fragment事务
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            case 0:
                titleBar.setTitle("踏勘项目列表");

                explorText.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.tab_item_selected));
                explorText.setTextSize(20);
                explorFlagView.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.tab_item_selected));

                if (exploreListFragment == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    exploreListFragment = new ExploreListFragment();
                    transaction.add(R.id.content, exploreListFragment);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(exploreListFragment);
                }
                break;
            case 1:
                titleBar.setTitle("方案");

                planText.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.tab_item_selected));
                planText.setTextSize(20);
                planFlagView.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.tab_item_selected));

                if (planFragment == null) {
                    // 如果ContactsFragment为空，则创建一个并添加到界面上
                    planFragment = new PlanFragment();
                    transaction.add(R.id.content, planFragment);
                } else {
                    // 如果ContactsFragment不为空，则直接将它显示出来
                    transaction.show(planFragment);
                }
                break;
            case 2:
                titleBar.setTitle("检查项目列表");

                checkText.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.tab_item_selected));
                checkText.setTextSize(20);
                checkFlagView.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.tab_item_selected));


                if (projectListFragment == null) {
                    // 如果NewsFragment为空，则创建一个并添加到界面上
                    projectListFragment = new ProjectListFragment();
                    transaction.add(R.id.content, projectListFragment);
                } else {
                    // 如果NewsFragment不为空，则直接将它显示出来
                    transaction.show(projectListFragment);
                }
                break;
            default:
                break;
        }
        transaction.commit();
    }
    /**
     * 清除掉所有的选中状态。
     */
    private void clearSelection() {
        explorText.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.tab_item_unselected));
        explorText.setTextSize(14);
        explorFlagView.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.tab_item_unselected));
        planText.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.tab_item_unselected));
        planText.setTextSize(14);
        planFlagView.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.tab_item_unselected));
        checkText.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.tab_item_unselected));
        checkText.setTextSize(14);
        checkFlagView.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.tab_item_unselected));
    }
    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction
     *            用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (exploreListFragment != null) {
            transaction.hide(exploreListFragment);
        }
        if (planFragment != null) {
            transaction.hide(planFragment);
        }
        if (projectListFragment != null) {
            transaction.hide(projectListFragment);
        }
    }
}
