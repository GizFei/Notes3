package com.giz.notes3;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity {

    public static final int PSD_REQUEST_CODE = 1;
    public static final int SIGN_REQUEST_CODE = 2;

    public static final String INTENT_USERNAME = "Username";
    public static final String PREFERENCE_USERNAME = "p_username";
    public static final String PREFERENCE_PASSWORD = "p_password";

    private EditText mUserName;
    private EditText mPassword;
    private TextView mForgetPsd;
    private TextView mSignUp;
    private TextView mLoginBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bmob.initialize(this, "5c7b8e1cfc804f9ef3efdab5723b2f5e");

        setContentView(R.layout.activity_login);

        if(!getIntent().hasExtra("CHANGE_ACCOUNT")){
            directLogin();
        }else{
            findViewById(R.id.start_bg).setVisibility(View.GONE);
        }

        initViews();
        initEvents();
    }

    private void initViews(){
        Log.d("INITVIEW", "YES");
        mUserName = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);
        mForgetPsd = findViewById(R.id.forget_psd);
        mSignUp = findViewById(R.id.sign_up);
        mLoginBtn = findViewById(R.id.login_btn);
    }

    private void initEvents(){
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = mUserName.getText().toString();
                final String psd = mPassword.getText().toString();
                BmobUser user = new BmobUser();
                user.setUsername(username);
                user.setPassword(psd);
                user.login(new SaveListener<BmobUser>() {
                    @Override
                    public void done(BmobUser bmobUser, BmobException e) {
                        if(e == null){
                            SharedPreferences preferences = LoginActivity.this.getPreferences(MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(PREFERENCE_USERNAME, username);
                            editor.putString(PREFERENCE_PASSWORD, psd);
                            editor.apply();
                            Intent intent = new Intent(LoginActivity.this,
                                    MainActivity.class);
                            intent.putExtra(INTENT_USERNAME, username);
                            startActivity(intent);
                            finish();
                        }else{
                            mUserName.setText("");
                            mPassword.setText("");
                            Toast.makeText(LoginActivity.this, "用户名或密码错误",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,
                        SignUpActivity.class);
                startActivityForResult(intent,SIGN_REQUEST_CODE);
            }
        });

        mForgetPsd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,
                        PasswordActivity.class);
                startActivityForResult(intent, PSD_REQUEST_CODE);
            }
        });
    }

    private void directLogin() {
        Log.d("DL", "YES");
        SharedPreferences preferences = this.getPreferences(MODE_PRIVATE);
        String username = preferences.getString(PREFERENCE_USERNAME, null);
        String password = preferences.getString(PREFERENCE_PASSWORD, null);
        if(username != null && password != null){
            BmobUser user = new BmobUser();
            user.setUsername(username);
            user.setPassword(password);
            user.login(new SaveListener<BmobUser>() {
                @Override
                public void done(BmobUser bmobUser, BmobException e) {
                    if(e == null){
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        intent.putExtra(INTENT_USERNAME, bmobUser.getUsername());
                        startActivity(intent);
                        finish();
                    }else{
                        findViewById(R.id.start_bg).setVisibility(View.GONE);
                    }
                }
            });
        }else {
            findViewById(R.id.start_bg).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("On", String.valueOf(resultCode));
        if(requestCode == SIGN_REQUEST_CODE){
            if(data != null){
                Log.d("Intent", "SIGN");
                mUserName.setText(data.getStringExtra("username"));
                mPassword.setText(data.getStringExtra("password"));
            }
        }else if(requestCode == PSD_REQUEST_CODE){
            if(data != null){
                Log.d("Intent", "PSD");
                mUserName.setText(data.getStringExtra("username"));
                mPassword.setText(data.getStringExtra("password"));
            }
        }
    }
}
