package com.giz.notes3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class SignUpActivity extends AppCompatActivity {

    private EditText mUsername;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mPsdAgain;
    private Button mSignUpBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bmob.initialize(this, "5c7b8e1cfc804f9ef3efdab5723b2f5e");

        setContentView(R.layout.activity_signup);
        initViews();
        initEvents();
    }

    private void initViews(){
        mUsername = findViewById(R.id.signup_username);
        mEmail = findViewById(R.id.signup_email);
        mPassword = findViewById(R.id.signup_password);
        mPsdAgain = findViewById(R.id.password_again);
        mSignUpBtn = findViewById(R.id.sign_up_btn);
        Toolbar toolbar = findViewById(R.id.signup_toolbar);
        setSupportActionBar(toolbar);
    }

    private void initEvents(){
        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = mUsername.getText().toString();
                String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                String psdAgain = mPsdAgain.getText().toString();
                if(username.length() == 0){
                    mUsername.setText("");
                    Toast.makeText(SignUpActivity.this, "用户名不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(email.length() == 0 || !email.matches("[a-zA-Z0-9._-]+@[a-z.]+")){
                    mEmail.setText("");
                    Toast.makeText(SignUpActivity.this, "邮箱地址有误",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.length() < 8){
                    mPsdAgain.setText("");
                    mPassword.setText("");
                    Toast.makeText(SignUpActivity.this, "密码至少需要8位",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.equals(psdAgain)){
                    BmobUser user = new BmobUser();
                    user.setEmail(email);
                    user.setUsername(username);
                    user.setPassword(password);
                    user.signUp(new SaveListener<BmobUser>() {
                        @Override
                        public void done(BmobUser bmobUser, BmobException e) {
                            if(e == null){
                                Toast.makeText(SignUpActivity.this, "注册成功",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.putExtra("username", username);
                                intent.putExtra("password", password);
                                setResult(LoginActivity.SIGN_REQUEST_CODE, intent);
                                finish();
                            }else{
                                mUsername.setText("");
                                mPassword.setText("");
                                mPsdAgain.setText("");
                                mEmail.setText("");
                                Toast.makeText(SignUpActivity.this, "未知错误",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(SignUpActivity.this, "两次输入密码不同",
                            Toast.LENGTH_SHORT).show();
                    mPassword.setText("");
                    mPsdAgain.setText("");
                }
            }
        });
    }
}
