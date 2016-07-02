package com.lichuange.bridges.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.lichuange.bridges.R;
import com.lichuange.bridges.models.BGConfigsModel;
import com.lichuange.bridges.models.ExploreModel;
import com.lichuange.bridges.models.LoginModel;
import com.lichuange.bridges.models.MainService;
import com.lichuange.bridges.models.Utils;
import com.lichuange.bridges.views.MyToast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;


public class LoginActivity extends MyBaseActivity {

    private boolean isCreating = true;

    private BGConfigsModel configsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        final Button loginBtn = (Button) findViewById(R.id.loginbtn);
        final EditText usernameEdit = (EditText) findViewById(R.id.usernameEdit);
        final EditText passwordEdit = (EditText) findViewById(R.id.passwordEdit);
        final CheckBox rememberUserName = (CheckBox) findViewById(R.id.rememberUserName);
        final CheckBox rememberPassword = (CheckBox) findViewById(R.id.rememberPassword);
        final Button serverIPBtn = (Button) findViewById(R.id.serveIPBtn);

        configsModel = BGConfigsModel.fetch(this);
        if (configsModel.getServerPort().length() == 0) {
            configsModel.setServerPort("8888");
        }

        MainService.getInstance().setServerAddress(configsModel.getServerAddress(), configsModel.getServerPort());

        rememberUserName.setChecked(configsModel.isRememberUserName());
        rememberPassword.setChecked(configsModel.isRememberPassword());

        final LoginActivity activity = this;

        rememberUserName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                configsModel.setRememberUserName(arg1);

                if (!arg1) {
                    configsModel.setUserName(null);
                    if (configsModel.isRememberPassword()) {
                        rememberPassword.setChecked(false);
                    }
                }

                configsModel.persist(activity);
            }
        });

        rememberPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                configsModel.setRememberPassword(arg1);

                if (arg1) {
                    if (!configsModel.isRememberUserName()) {
                        rememberUserName.setChecked(true);
                    }
                } else {
                    configsModel.setPassword(null);
                }

                configsModel.persist(activity);
            }
        });

        serverIPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCreating = true;
                Intent intent = new Intent(LoginActivity.this, ServerAddressActivity.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validate username or password
                String username = usernameEdit.getText().toString();
                if (username.length() == 0) {
                    Toast.makeText(getApplicationContext(), "帐号为空，请输入", Toast.LENGTH_SHORT).show();
                    return;
                }

                String password = passwordEdit.getText().toString();
                if (password.length() == 0) {
                    Toast.makeText(getApplicationContext(), "密码为空，请输入", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (MainService.getInstance().login(username, password, handler)) {
                    MyToast.showMessage(getApplicationContext(), "登录中，请稍后");
                } else {
                    Toast.makeText(getApplicationContext(), "登录失败，异常", Toast.LENGTH_SHORT).show();
                }
            }
        });

        try {
            FileInputStream stream = this.openFileInput("login.s");
            ObjectInputStream ois = new ObjectInputStream(stream);
            LoginModel model = (LoginModel) ois.readObject();

            if (model != null && model.getExpirDate() != null && model.isLoginSuccess()) {
                if (!Utils.isCurrentTimeExpired(model.getExpirDate())) {
                    // 自动登录
                    MainService.getInstance().setLoginModel(model);
                    MyToast.showMessage(getApplicationContext(), "Token仍有效，自动登录");
                    Intent intent = new Intent(LoginActivity.this, MainTabActivity.class);
                    startActivity(intent);
                }
            }
        } catch (Exception e) {
        } finally {
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (isCreating) {
            isCreating = false;
        }
        else {
            try {
                LoginModel model = MainService.getInstance().getLoginModel();
                model.setToken("");
                FileOutputStream stream = this.openFileOutput("login.s", MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(stream);
                oos.writeObject(model);//td is an Instance of TableData;
            }
            catch (Exception e) {
            }
            finally {
            }

            MainService.getInstance().logout();

            persistAllExplores();
        }

        EditText usernameEdit = (EditText)findViewById(R.id.usernameEdit);
        EditText passwordEdit = (EditText)findViewById(R.id.passwordEdit);
        usernameEdit.setText(configsModel.getUserName());
        passwordEdit.setText(configsModel.getPassword());
    }

    private boolean persistAllExplores() {
        boolean ret = false;
        // 数据模型持久化
        try {
            List<ExploreModel> list = MainService.getInstance().getExploreList();
            FileOutputStream stream = this.openFileOutput("explore_list.s", MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(stream);
            oos.writeObject(list);
            oos.close();
            stream.close();
            ret = true;
        }
        catch (Exception e) {
            Log.e("", e.toString());
        }

        return ret;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResponse(int msgCode) {
        switch (msgCode) {
            case MainService.MSG_LOGIN_SUCCESS: {

                // 登录数据写到文件
                try {
                    LoginModel model = MainService.getInstance().getLoginModel();
                    FileOutputStream stream = this.openFileOutput("login.s", MODE_PRIVATE);
                    ObjectOutputStream oos = new ObjectOutputStream(stream);
                    oos.writeObject(model);//td is an Instance of TableData;
                }
                catch (Exception e) {
                }
                finally {
                }


                EditText usernameEdit = (EditText)findViewById(R.id.usernameEdit);
                EditText passwordEdit = (EditText)findViewById(R.id.passwordEdit);

                if (configsModel.isRememberUserName()) {
                    configsModel.setUserName(usernameEdit.getText().toString());
                }
                else {
                    configsModel.setUserName(null);
                }

                if (configsModel.isRememberPassword()) {
                    configsModel.setPassword(passwordEdit.getText().toString());
                }
                else {
                    configsModel.setPassword(null);
                }

                configsModel.persist(this);


                MyToast.showMessage(getApplicationContext(), "登录成功");

                Intent intent = new Intent(LoginActivity.this, MainTabActivity.class);
                startActivity(intent);
                break;
            }
            case MainService.MSG_LOGIN_FAILED: {
                Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
                break;
            }
            default:
                break;
        }
    }
}