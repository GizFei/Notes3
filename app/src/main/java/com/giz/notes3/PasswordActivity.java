package com.giz.notes3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class PasswordActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mNewPsd;
    private EditText mNewPsdAgain;
    private Button mFindBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpsd);

        initViews();
        initEvents();
    }

    private void initViews(){
        mEmail = findViewById(R.id.findpsd_email);
        mNewPsd = findViewById(R.id.findpsd_psd);
        mNewPsdAgain = findViewById(R.id.findpsd_again);
        mFindBtn = findViewById(R.id.findpsd_btn);
    }

    private void initEvents(){
        mFindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String psd = mNewPsd.getText().toString();
                String psdAgain = mNewPsdAgain.getText().toString();
                if(!psd.equals(psdAgain)){
                    mNewPsd.setText("");
                    mNewPsdAgain.setText("");
                    Toast.makeText(PasswordActivity.this,
                            "两次密码输入不同",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(psd.length() < 8){
                    mNewPsd.setText("");
                    mNewPsdAgain.setText("");
                    Toast.makeText(PasswordActivity.this,
                            "密码至少需要8位",Toast.LENGTH_SHORT).show();
                    return;
                }
                BmobQuery query = new BmobQuery("_User");
                query.addWhereEqualTo("email", mEmail.getText().toString());
                query.findObjectsByTable(new QueryListener<JSONArray>() {
                    @Override
                    public void done(JSONArray jsonArray, BmobException e){
                        if(e == null){
                           resetPsd(jsonArray);
                        }
                    }
                });
            }
        });
    }

    private void resetPsd(JSONArray array){
        try{
            JSONObject object = array.getJSONObject(0);
            String objectId = object.getString("objectId");
            final String username = object.getString("username");
            final String psd = mNewPsd.getText().toString();
            boolean verified = object.getBoolean("emailVerified");
            if(!verified){
                clearEditTexts();
                Toast.makeText(PasswordActivity.this,
                        "该邮箱未验证",Toast.LENGTH_SHORT).show();
                return;
            }
            BmobUser user = new BmobUser();
            user.setUsername(username);
            user.setPassword(psd);
            user.update(objectId, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e == null){
                        clearEditTexts();
                        Toast.makeText(PasswordActivity.this,
                                "重置成功",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("username", username);
                        intent.putExtra("password", psd);
                        setResult(LoginActivity.PSD_REQUEST_CODE, intent);
                        finish();
                    }else{
                        clearEditTexts();
                        Toast.makeText(PasswordActivity.this,
                                "重置失败",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (Exception e){
            clearEditTexts();
            Toast.makeText(PasswordActivity.this,
                    "重置密码失败",Toast.LENGTH_SHORT).show();
        }
    }

    private void clearEditTexts() {
        mEmail.setText("");
        mNewPsdAgain.setText("");
        mNewPsd.setText("");
    }
}
