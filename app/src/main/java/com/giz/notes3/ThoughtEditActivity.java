package com.giz.notes3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import BmobUtils.SharedBmobObject;
import thoughtDatabase.Thought;
import thoughtDatabase.ThoughtDatabase;
import thoughtDatabase.ThoughtDbSchema;

public class ThoughtEditActivity extends AppCompatActivity {

    private Thought mThought;
    private EditText mTitleText;
    private EditText mContentText;
    private Button mButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thought_edit);
        if(getIntent().hasExtra("THOUGHT_UUID")){
            String uuid = getIntent().getStringExtra("THOUGHT_UUID");
            mThought = ThoughtDatabase.get(this, SharedBmobObject.sUserName).queryThought(uuid);

        }else{
            mThought = new Thought();
        }

        initViews();
    }

    private void initViews() {
        mTitleText = (EditText)findViewById(R.id.thought_edit_title);
        mContentText = (EditText)findViewById(R.id.thought_edit_content);
        mButton = (Button)findViewById(R.id.thought_edit_btn);

        mTitleText.setText(mThought.getTitle());
        mContentText.setText(mThought.getContent());

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThought.setTitle(mTitleText.getText().toString());
                mThought.setContent(mContentText.getText().toString());
                if(ThoughtDatabase.get(ThoughtEditActivity.this, SharedBmobObject.sUserName)
                        .queryThought(mThought.getUUID().toString()) != null){
                    ThoughtDatabase.get(ThoughtEditActivity.this, SharedBmobObject.sUserName).updateThought(mThought);
                }else{
                    ThoughtDatabase.get(ThoughtEditActivity.this, SharedBmobObject.sUserName).addThought(mThought);
                }
                Intent intent = new Intent(ThoughtEditActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("MOVETOTWO", "YES");
                startActivity(intent);
                finish();
            }
        });
    }
}
