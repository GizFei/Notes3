package com.giz.notes3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import BmobUtils.Read;
import BmobUtils.SharedBmobObject;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import readDatabase.ReadDatabase;
import readDatabase.ReadDbSchema;
import readDatabase.ReadItem;
import utils.ReadAdapter;

public class ReadFragment extends Fragment {

    private ListView mMovieLv;
    private ReadAdapter mAdapter;
    private Context mContext;

    public static ReadFragment newInstance(Context context){
        ReadFragment readFragment = new ReadFragment();
        readFragment.getObjectId();
        readFragment.mContext = context;
        return readFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_read, container, false);

        mMovieLv = (ListView)view.findViewById(R.id.tab_movie_lv);
        ImageButton mButton = (ImageButton) view.findViewById(R.id.tab_movie_btn);

        updateUI();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_new_read,
                        null);
                ((TextView)dialogView.findViewById(R.id.dialog_read_title)).setText("新看点");
                final EditText et = dialogView.findViewById(R.id.dialog_read_editText);
                final Spinner spinner = dialogView.findViewById(R.id.dialog_read_spinner);
                new AlertDialog.Builder(mContext)
                        .setView(dialogView)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String type = spinner.getSelectedItemPosition() == 0 ?
                                        ReadDbSchema.TYPE_VIDEO : ReadDbSchema.TYPE_BOOK;
                                ReadItem readItem = new ReadItem();
                                readItem.setName(et.getText().toString());
                                readItem.setType(type);
                                mAdapter.addItem(readItem);
                            }
                        }).show();
            }
        });

        return view;
    }

    private void updateUI() {
        List<ReadItem> itemList = ReadDatabase.get(mContext, SharedBmobObject.sUserName).getReadItems();

        if(mAdapter == null){
            mAdapter = new ReadAdapter(mContext, itemList);
            mMovieLv.setAdapter(mAdapter);
        }else{
            mAdapter.setReadItemList(itemList);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void uploadReadList(){
        List<ReadItem> itemList = ReadDatabase.get(mContext, SharedBmobObject.sUserName).getReadItems();
        List<JSONObject> list = new ArrayList<>();
        Read read = new Read();
        try{
            for(ReadItem item : itemList){
                JSONObject object = new JSONObject();
                object.put("name", item.getName());
                object.put("seen", item.isHasSeen());
                object.put("comment", item.getComment());
                object.put("type", item.getType());
                list.add(object);
            }
            read.setReadlist(list);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(SharedBmobObject.sReadObjectId.equals("")){
            read.setUsername(SharedBmobObject.sUserName);
            read.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    SharedBmobObject.uploadDone = (e == null && SharedBmobObject.uploadDone);
                }
            });
        }else{
            read.update(SharedBmobObject.sReadObjectId, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    SharedBmobObject.uploadDone = (e == null && SharedBmobObject.uploadDone);
                }
            });
        }
    }

    public void downloadReadList(){
        if(SharedBmobObject.sReadObjectId.equals("")){
            return;
        }

        BmobQuery<Read> query = new BmobQuery<>();
        query.getObject(SharedBmobObject.sReadObjectId, new QueryListener<Read>() {
            @Override
            public void done(Read read, BmobException e) {
                if(e == null){
                    try{
                        ReadDatabase database = ReadDatabase.get(mContext, SharedBmobObject.sUserName);
                        database.deleteAll();
                        List<JSONObject> list = read.getReadlist();
                        for(int i = 0; i < list.size(); i++){
                            JSONObject object = list.get(i);
                            ReadItem item = new ReadItem();
                            item.setName(object.getString("name"));
                            item.setHasSeen(object.getBoolean("seen"));
                            item.setComment(object.getString("comment"));
                            item.setType(object.getString("type"));
                            database.addReadItem(item);
                        }
                        updateUI();
                    }catch (Exception ex){
                        SharedBmobObject.downloadDone = false;
                        ex.printStackTrace();
                    }
                }else{
                    SharedBmobObject.downloadDone = false;
                }
            }
        });
    }

    private void getObjectId(){
        SharedBmobObject.sReadObjectId = "";
        BmobQuery query = new BmobQuery("Read");
        query.addWhereEqualTo("username", SharedBmobObject.sUserName);
        query.findObjectsByTable(new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray jsonArray, BmobException e) {
                if(e == null){
                    try{
                        JSONObject object = jsonArray.getJSONObject(0);
                        SharedBmobObject.sReadObjectId = object.getString("objectId");
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
}
