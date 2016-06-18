package com.lichuange.bridges.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lichuange.bridges.R;
import com.lichuange.bridges.models.LoginModel;
import com.lichuange.bridges.models.MainService;
import com.lichuange.bridges.models.Utils;
import com.lichuange.bridges.views.MyToast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class LoginActivity extends MyBaseActivity {

    private boolean isCreating = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        final Button loginBtn = (Button)findViewById(R.id.loginbtn);
        final EditText usernameEdit = (EditText)findViewById(R.id.usernameEdit);
        final EditText passwordEdit = (EditText)findViewById(R.id.passwordEdit);

        // mock
//        usernameEdit.setText("heweizhi");
//        passwordEdit.setText("123456");

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
            LoginModel model = (LoginModel)ois.readObject();

            if (usernameEdit.getText() == null || usernameEdit.getText().length() == 0) {
                if (model != null) {
                    usernameEdit.setText(model.getUserName());
                }
            }


            if (model != null && model.getExpirDate() != null && model.isLoginSuccess()) {
                if (!Utils.isCurrentTimeExpired(model.getExpirDate())) {



                    // 自动登录
                    MainService.getInstance().setLoginModel(model);
                    MyToast.showMessage(getApplicationContext(), "Token仍有效，自动登录");
                    Intent intent = new Intent(LoginActivity.this, MainTabActivity.class);
                    startActivity(intent);
                }
            }
        }
        catch (Exception e) {
        }
        finally {
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (isCreating) {
            isCreating = false;
        }
        else {
            MainService.getInstance().logout();

            try {
                LoginModel model = MainService.getInstance().getLoginModel();
                model.setToken("");
                FileOutputStream stream = this.openFileOutput("login.s", MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(stream);
                oos.writeObject(model);//td is an Instance of TableData;

                EditText usernameEdit = (EditText)findViewById(R.id.usernameEdit);
                if (usernameEdit.getText() == null || usernameEdit.getText().length() == 0) {
                    usernameEdit.setText(model.getUserName());
                }
            }
            catch (Exception e) {
            }
            finally {
            }
        }
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