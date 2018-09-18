package com.giz.notes3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import BmobUtils.SharedBmobObject;
import BmobUtils.ToDo;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import toDoDatebase.ToDoDatabase;
import toDoDatebase.ToDoItem;
import utils.ArcMenu;
import utils.InstructionDialog;
import utils.ToDoAdapter;

public class ToDoFragment extends Fragment {

    //public static final String BUNDLE_USER_NAME = "USERNAME";

    private ListView mListView;
    private ToDoAdapter mAdapter;
    private Context mContext;
    private ArcMenu mArcMenu;

    private void log(String s){
        Log.d("DEBUG", s);
    }

    public static ToDoFragment newInstance(Context context){
        ToDoFragment fragment = new ToDoFragment();
//        Bundle b = new Bundle();
//        b.putString(BUNDLE_USER_NAME, username);
//        fragment.setArguments(b);
        fragment.mContext = context;
        fragment.getObjectId();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        log("ToDoFragment onCreate");
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        log("ToDoFragment onCreateView");
        View view = inflater.inflate(R.layout.tab_todo, container, false);

        mListView = (ListView)view.findViewById(R.id.tab_todo_lv);
        mArcMenu = (ArcMenu)view.findViewById(R.id.tab_todo_arcMenu);

        updateUI();

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(mArcMenu.isOpen())
                    mArcMenu.toggleMenu(300);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {

            }
        });

        mArcMenu.setMenuItemClickListener(new ArcMenu.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int pos) {
                if(view.getTag().equals("New")){
                    View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_new_thing,
                            null);
                    ((TextView)dialogView.findViewById(R.id.dialog_title)).setText(getResources().
                            getString(R.string.newToDoTitle));
                    final EditText et = dialogView.findViewById(R.id.dialog_editText);
                    //final EditText et = new EditText(mContext);
                    new AlertDialog.Builder(mContext)
                            .setView(dialogView)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ToDoItem toDoItem = new ToDoItem();
                                    toDoItem.setThing(et.getText().toString());
                                    mAdapter.setDeleteIconGone();
                                    mAdapter.addItem(toDoItem);
                                }
                            }).show();
                }else if(view.getTag().equals("Delete")){
                    if(mAdapter.isIconVisible())
                        mAdapter.setDeleteIconGone();
                    else 
                        mAdapter.setDeleteIconVisible();
                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.todo_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }

    public void foldArcMenu(){
        if(mArcMenu != null && mArcMenu.isOpen())
            mArcMenu.fold();
    }

    private void updateUI() {
        if(mAdapter == null){
            mAdapter = new ToDoAdapter(getActivity());
            mListView.setAdapter(mAdapter);
        }else{
            mListView.setAdapter(mAdapter);
        }
    }

    public void downloadToDoList(){
        if(SharedBmobObject.sToDoObjectId.equals("")){
            return;
        }
        BmobQuery<ToDo> query = new BmobQuery<ToDo>();
        query.getObject(SharedBmobObject.sToDoObjectId, new QueryListener<ToDo>() {
            @Override
            public void done(ToDo toDo, BmobException e) {
                if(e == null){
                    List<String> list = toDo.getTodolist();
                    List<ToDoItem> toDoItemList = new ArrayList<>();
                    ToDoDatabase database = ToDoDatabase.get(getActivity(),
                            SharedBmobObject.sUserName);
                    database.deleteAll();
                    for(int i = 0; i < list.size(); i++){
                        ToDoItem item = new ToDoItem();
                        item.setThing(list.get(i));
                        toDoItemList.add(item);
                        database.addItem(item);
                    }
                    mAdapter = new ToDoAdapter(mContext, toDoItemList);
                    mListView.setAdapter(mAdapter);
                }else{
                    SharedBmobObject.downloadDone = false;
                }
            }
        });
    }

    public void uploadToDoList(){
        List<ToDoItem> list = ToDoDatabase.get(mContext, SharedBmobObject.sUserName).getToDoItems();
        List<String> toDoList = new ArrayList<>();
        for(ToDoItem item : list){
            toDoList.add(item.getThing());
        }
        if(!SharedBmobObject.sToDoObjectId.equals("")){
            ToDo toDo = new ToDo();
            toDo.setTodolist(toDoList);
            toDo.update(SharedBmobObject.sToDoObjectId, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    SharedBmobObject.uploadDone = (e == null && SharedBmobObject.uploadDone);
                }
            });
        }else{
            ToDo toDo = new ToDo();
            toDo.setUsername(SharedBmobObject.sUserName);
            toDo.setTodolist(toDoList);
            toDo.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    SharedBmobObject.uploadDone = (e == null && SharedBmobObject.uploadDone);
                }
            });
        }
    }

    private void getObjectId(){
        SharedBmobObject.sToDoObjectId = "";
        BmobQuery query = new BmobQuery("ToDo");
        query.addWhereEqualTo("username", SharedBmobObject.sUserName);
        query.findObjectsByTable(new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray jsonArray, BmobException e) {
                if(e == null){
                    try{
                        JSONObject object = jsonArray.getJSONObject(0);
                        SharedBmobObject.sToDoObjectId = object.getString("objectId");
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
}
