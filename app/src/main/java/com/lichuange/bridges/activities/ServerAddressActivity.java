package com.lichuange.bridges.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.lichuange.bridges.R;
import com.lichuange.bridges.models.BGConfigsModel;
import com.lichuange.bridges.models.MainService;
import com.lichuange.bridges.views.MyToast;

public class ServerAddressActivity extends MyBaseActivity {
    private BGConfigsModel configsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_address);

        createTitleBar();
        titleBar.setTitle("修改服务器地址");

        final Button modifyBtn = (Button)findViewById(R.id.modifyBtn);
        final EditText oldServerAddress = (EditText)findViewById(R.id.oldServerAddress);
        final EditText newServerAddress = (EditText)findViewById(R.id.newServerAddress);

        configsModel = BGConfigsModel.fetch(this);

        oldServerAddress.setText(configsModel.getServerAddress());

        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newAddress = newServerAddress.getText().toString();
                if (newAddress.length() == 0) {
                    MyToast.showMessage(getApplicationContext(), "服务器地址不能为空");
                    return;
                }

                if (!MainService.getInstance().setServerAddress(newAddress)) {
                    MyToast.showMessage(getApplicationContext(), "服务器地址格式不对");
                    return;
                }

                configsModel.setServerAddress(newAddress);
                configsModel.persist(ServerAddressActivity.this);
                MyToast.showMessage(getApplicationContext(), "服务器地址修改成功");
            }
        });
    }
}
