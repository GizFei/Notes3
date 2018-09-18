package com.giz.notes3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import BmobUtils.SharedBmobObject;
import BmobUtils.Thoughts;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import thoughtDatabase.Thought;
import thoughtDatabase.ThoughtDatabase;

public class ThoughtFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private Context mContext;

    private ThoughtAdapter mAdapter;

    public static ThoughtFragment newInstance(Context context){
        ThoughtFragment fragment = new ThoughtFragment();
        fragment.mContext = context;
        fragment.getObjectId();
        return fragment;
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
        View view = inflater.inflate(R.layout.tab_thought, container, false);

        mRecyclerView = view.findViewById(R.id.tab_thought_rv);
        ImageButton mButton = view.findViewById(R.id.tab_thought_btn);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        updateUI();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Thought thought = new Thought();
                //ThoughtDatabase.get(mContext, SharedBmobObject.sUserName).addThought(thought);
                Intent intent = new Intent(mContext, ThoughtEditActivity.class);
                //intent.putExtra("THOUGHT_UUID", thought.getUUID().toString());
                startActivity(intent);
            }
        });

        return view;
    }

    private void updateUI(){
        List<Thought> list = ThoughtDatabase.get(mContext, SharedBmobObject.sUserName).getThoughts();

        if(mAdapter == null){
            mAdapter = new ThoughtAdapter(list);
            mRecyclerView.setAdapter(mAdapter);
        }else{
            mAdapter.setThoughtList(list);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class ThoughtHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener{

        private Thought mThought;
        private TextView mTitleView;
        private TextView mContentView;

        private ThoughtHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.tab_thought_item, parent, false));

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            mTitleView = itemView.findViewById(R.id.thought_item_title);
            mContentView = itemView.findViewById(R.id.thought_item_content);
        }

        public void bind(Thought thought){
            mThought = thought;

            mTitleView.setText(mThought.getTitle());
            mContentView.setText(mThought.getContent());
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, ThoughtEditActivity.class);
            intent.putExtra("THOUGHT_UUID", mThought.getUUID().toString());
            mContext.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            new AlertDialog.Builder(mContext)
                    .setTitle("确定删除该想法吗？")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ThoughtDatabase.get(mContext, SharedBmobObject.sUserName).deleteThought(mThought);
                            mAdapter.deleteItem(mThought);
                        }
                    }).show();
            return true;
        }
    }

    private class ThoughtAdapter extends RecyclerView.Adapter<ThoughtHolder>{

        private List<Thought> mThoughtList;

        private ThoughtAdapter(List<Thought> list) {
            mThoughtList = list;
        }

        @NonNull
        @Override
        public ThoughtHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new ThoughtHolder(inflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull ThoughtHolder thoughtHolder, int i) {
            Thought thought = mThoughtList.get(i);
            thoughtHolder.bind(thought);
        }

        @Override
        public int getItemCount() {
            return mThoughtList.size();
        }

        private void setThoughtList(List<Thought> list){
            mThoughtList = list;
        }

        public void deleteItem(Thought thought){
            mThoughtList.remove(thought);
            notifyDataSetChanged();
        }
    }

    private void getObjectId(){
        SharedBmobObject.sThoughtObjectId = "";
        BmobQuery query = new BmobQuery("Thoughts");
        query.addWhereEqualTo("username", SharedBmobObject.sUserName);
        query.findObjectsByTable(new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray jsonArray, BmobException e) {
                if(e == null){
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        SharedBmobObject.sThoughtObjectId = jsonObject.getString("objectId");
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public void uploadThoughts() {
        List<Thought> list = ThoughtDatabase.get(mContext, SharedBmobObject.sUserName).getThoughts();;
        List<JSONObject> array = new ArrayList<>();
        try{
            for(Thought thought : list){
                JSONObject object = new JSONObject();
                object.put("title", thought.getTitle());
                object.put("content", thought.getContent());
                array.add(object);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        // 创建新的
        if(SharedBmobObject.sThoughtObjectId.equals("")){
            Thoughts thoughts = new Thoughts();
            thoughts.setThoughts(array);
            thoughts.setUsername(SharedBmobObject.sUserName);
            thoughts.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    SharedBmobObject.uploadDone = (e == null && SharedBmobObject.uploadDone);
                }
            });
        }else{
            Thoughts thoughts = new Thoughts();
            thoughts.setThoughts(array);
            thoughts.update(SharedBmobObject.sThoughtObjectId, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    SharedBmobObject.uploadDone = (e == null && SharedBmobObject.uploadDone);
                }
            });
        }
    }

    public void downloadThoughts(){
        if(SharedBmobObject.sThoughtObjectId.equals("")){
            return;
        }

        BmobQuery<Thoughts> query = new BmobQuery<>();
        query.getObject(SharedBmobObject.sThoughtObjectId, new QueryListener<Thoughts>() {
            @Override
            public void done(Thoughts thoughts, BmobException e) {
                if(e == null){
                    try{
                        List<JSONObject> array = thoughts.getThoughts();
                        ThoughtDatabase database = ThoughtDatabase.get(mContext, SharedBmobObject.sUserName);
                        database.deleteAll();
                        //List<Thought> thoughtList = new ArrayList<>();
                        for(int i = 0; i < array.size(); i++){
                            JSONObject object = array.get(i);
                            Thought thought = new Thought();
                            thought.setTitle(object.getString("title"));
                            thought.setContent(object.getString("content"));
                            database.addThought(thought);
                            //thoughtList.add(thought);
                        }
//                        if(mAdapter == null){
//                            mAdapter = new ThoughtAdapter(thoughtList);
//                            mAdapter.notifyDataSetChanged();
//                        }else{
//                            mAdapter.setThoughtList(thoughtList);
//                            mAdapter.notifyDataSetChanged();
//                        }
                        updateUI();
                    }catch (Exception ex){
                        ex.printStackTrace();
                        SharedBmobObject.downloadDone = false;
                    }
                }else{
                    SharedBmobObject.downloadDone = false;
                }
            }
        });
    }
}
