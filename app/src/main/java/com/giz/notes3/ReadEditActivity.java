package com.giz.notes3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import BmobUtils.SharedBmobObject;
import readDatabase.ReadDatabase;
import readDatabase.ReadItem;

public class ReadEditActivity extends AppCompatActivity {

    private ReadItem mReadItem;
    private EditText mMovieNameEt;
    private Switch mSeenSwitch;
    private EditText mCommentText;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_edit);

        String uuid = getIntent().getStringExtra("MOVIE_UUID");
        mReadItem = ReadDatabase.get(this, SharedBmobObject.sUserName).queryReadItem(uuid);

        initViews();
    }

    private void initViews() {
        mMovieNameEt = (EditText) findViewById(R.id.movie_edit_name);
        mSeenSwitch = (Switch)findViewById(R.id.movie_edit_seen);
        mCommentText = (EditText)findViewById(R.id.movie_edit_comment);
        mButton = (Button)findViewById(R.id.movie_edit_btn);

        mMovieNameEt.setText(mReadItem.getName());

        mSeenSwitch.setChecked(mReadItem.isHasSeen());
        mSeenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mReadItem.setHasSeen(isChecked);
            }
        });

        mCommentText.setText(mReadItem.getComment());

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReadItem.setComment(mCommentText.getText().toString());
                mReadItem.setName(mMovieNameEt.getText().toString());
                // update the database
                ReadDatabase.get(ReadEditActivity.this, SharedBmobObject.sUserName).updateReadItem(mReadItem);
                Intent intent = new Intent(ReadEditActivity.this,
                        MainActivity.class);
                intent.putExtra("MOVETOTHREE", "YES");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                ReadEditActivity.this.finish();
            }
        });
    }
}
