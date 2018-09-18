package utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.giz.notes3.R;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class ChangePsdDialog extends AlertDialog {

    private EditText mNewPsd;
    private EditText mNewPsdAgain;
    private Button mChangeBtn;
    private Button mCancelBtn;

    public ChangePsdDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_change_psd);
        setCancelable(false);
        setTitle("修改密码");

        initViews();
        initEvents();
    }

    private void initViews(){
        mNewPsd = findViewById(R.id.new_psd);
        mNewPsdAgain = findViewById(R.id.new_psd_again);
        mChangeBtn = findViewById(R.id.change_btn);
        mCancelBtn = findViewById(R.id.cancel_btn);
    }

    private void initEvents(){
        mChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPsd = mNewPsd.getText().toString();
                String newPsdAgain = mNewPsdAgain.getText().toString();
                if(!newPsd.equals(newPsdAgain)){
                    mNewPsd.setText("");
                    mNewPsdAgain.setText("");
                    Toast.makeText(getContext(), "两次输入密码不同", Toast.LENGTH_SHORT).show();
                    return;
                }
                BmobUser user = BmobUser.getCurrentUser();
                user.setPassword(newPsd);
                BmobUser.resetPasswordByEmail(user.getEmail(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e == null){
                            Toast.makeText(getContext(), "修改成功", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
