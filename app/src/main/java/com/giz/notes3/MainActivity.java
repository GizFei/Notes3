package com.giz.notes3;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import BmobUtils.SharedBmobObject;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import userDatabase.UserDb;
import utils.ColorPopupWindow;
import utils.InstructionDialog;
import utils.Theme;

public class MainActivity extends AppCompatActivity {

    private static final String THEME_COLOR_KEY = "theme_color_key";
    private static final String TO_DO_FRAGMENT = "toDoFragment";
    private static final String THOUGHT_FRAGMENT = "thoughtFragment";
    private static final String MOVIE_FRAGMENT = "movieFragment";
    private static final String PREFERENCE_KEY = "key_name";
    private static final String CURRENT_FRAGMENT = "currentFragment";

    private ToDoFragment mToDoFragment;
    private ThoughtFragment mThoughtFragment;
    private ReadFragment mReadFragment;
    private ConstraintLayout mContainerLayout;
    private View mMaskView;
    private DrawerLayout mDrawerLayout;
    private TextView mUsernameTv;
    private BottomNavigationView mBottomNavigationView;

    private int currentFragment = 1;
    private int mThemeColor = Theme.THEME_COLOR_DEFAULT;
    private boolean hasChangedPsd = false;

    private BmobUser mUser;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_todo:
                    log("nav_todo");
                    setCurrentFragment(1);
                    return true;
                case R.id.navigation_thought:
                    log("nav_thought");
                    setCurrentFragment(2);
                    if(mToDoFragment != null)
                        mToDoFragment.foldArcMenu();
                    return true;
                case R.id.navigation_movie:
                    setCurrentFragment(3);
                    if(mToDoFragment != null)
                        mToDoFragment.foldArcMenu();
                    return true;
            }
            return false;
        }
    };

    private NavigationView.OnNavigationItemSelectedListener mNavigationItemSelectedListener =
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.nav_account_change:
                            Intent intent = new Intent(MainActivity.this,
                                    LoginActivity.class);
                            intent.putExtra("CHANGE_ACCOUNT", 1);
                            startActivity(intent);
                            finish();
                            break;
                        case R.id.nav_change_psd:
                            if(!mUser.getEmailVerified()){
                                Toast.makeText(MainActivity.this, "该邮箱还未验证，" +
                                                "无法修改密码。", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            if(!hasChangedPsd){
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("是否要修改密码？" +
                                                "若确认修改，则修改密码的链接已经发到您的邮箱上。")
                                        .setCancelable(false)
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                hasChangedPsd = true;
                                                BmobUser.resetPasswordByEmail(mUser.getEmail(), new UpdateListener() {
                                                    @Override
                                                    public void done(BmobException e) {
                                                        if(e == null){
                                                            Toast.makeText(MainActivity.this, "邮件已发送",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            Toast.makeText(MainActivity.this, e.getMessage(),
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        })
                                        .setNegativeButton(android.R.string.cancel, null)
                                        .show();
                            }else{
                                Toast.makeText(MainActivity.this, "请勿频繁修改。",
                                        Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case R.id.nav_palette:
                            final ColorPopupWindow cpw = new ColorPopupWindow(MainActivity.this, mThemeColor);
                            PopupWindow pw = cpw.getPopupWindow();
                            mMaskView.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.mask_in));
                            mMaskView.setVisibility(View.VISIBLE);
                            pw.showAtLocation(mContainerLayout, Gravity.BOTTOM, 0, 0);
                            pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
                                @Override
                                public void onDismiss() {
                                    int selectedColor = cpw.getSelectedColorId();
                                    mMaskView.setAnimation(AnimationUtils.loadAnimation(MainActivity.this,
                                            R.anim.mask_out));
                                    mMaskView.setVisibility(View.GONE);
                                    if(mThemeColor != selectedColor){
                                        mThemeColor = selectedColor;
                                        SharedPreferences preferences = MainActivity.this
                                                .getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putInt(PREFERENCE_KEY, mThemeColor);
                                        editor.apply();
                                        recreate();
                                    }
                                }
                            });
                            break;
                        case R.id.nav_upload:
                            uploadAllData();
                            break;
                        case R.id.nav_download:
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("云端数据是您上次同步的数据，确定恢复吗？")
                                    .setPositiveButton(android.R.string.ok,
                                            new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            downloadCloudData();
                                        }
                                    })
                                    .setNegativeButton(android.R.string.cancel, null)
                                    .setCancelable(false)
                                    .show();
                            break;
                        case R.id.nav_instruction:
                            InstructionDialog id = new InstructionDialog(MainActivity.this,
                                    R.layout.app_instruction);
                            ((TextView)id.findViewById(R.id.instruction_tv)).
                                    setMovementMethod(ScrollingMovementMethod.getInstance());
                            id.show();
                            break;
                        case R.id.nav_log:
                            InstructionDialog id1 = new InstructionDialog(MainActivity.this,
                                    R.layout.update_log);
                            ((TextView)id1.findViewById(R.id.update_log_tv)).
                                    setMovementMethod(ScrollingMovementMethod.getInstance());
                            id1.show();
                            break;
                    }
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
            };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_FRAGMENT, currentFragment);
         //保存Fragment
//        if(mToDoFragment != null){
//            getSupportFragmentManager().putFragment(outState, TO_DO_FRAGMENT, mToDoFragment);
//        }
//        if(mThoughtFragment != null){
//            getSupportFragmentManager().putFragment(outState, THOUGHT_FRAGMENT, mThoughtFragment);
//        }
//        if(mReadFragment != null){
//            getSupportFragmentManager().putFragment(outState, MOVIE_FRAGMENT, mReadFragment);
//        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            int resumeFragment = savedInstanceState.getInt(CURRENT_FRAGMENT);
            log("on restore" + String.valueOf(resumeFragment));
            setCurrentFragment(resumeFragment);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void log(String s){
        Log.d("DEBUG", s);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("MainActivity onCreate");
        SharedPreferences preferences = this.getSharedPreferences(PREFERENCE_KEY,
                Context.MODE_PRIVATE);
        mThemeColor = preferences.getInt(PREFERENCE_KEY, Theme.THEME_COLOR_DEFAULT);
        setNotesTheme(mThemeColor);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_main);

        Bmob.initialize(this, "5c7b8e1cfc804f9ef3efdab5723b2f5e");
        mUser = BmobUser.getCurrentUser();

        initView();
        setUsername();

        // !要在super.OnCreate(savedInstanceState)之后调用。
        if(savedInstanceState == null){
            initFragments();
        }else {
            log("not null saveInstanceState");
            List<Fragment> list = getSupportFragmentManager().getFragments();
            mToDoFragment = (ToDoFragment)list.get(0);
            mThoughtFragment = (ThoughtFragment) list.get(1);
            mReadFragment = (ReadFragment) list.get(2);
        }

        if(getIntent().hasExtra("MOVETOTWO")){
            log("move to 2");
            mBottomNavigationView.setSelectedItemId(R.id.navigation_thought);
        }else if(getIntent().hasExtra("MOVETOTHREE")){
            log("move to 3");
            mBottomNavigationView.setSelectedItemId(R.id.navigation_movie);
        }else{
            mBottomNavigationView.setSelectedItemId(R.id.navigation_todo);
        }
        //setCurrentFragment(resumeFragment);
    }

    private void initView() {
        mContainerLayout = findViewById(R.id.container);
        mMaskView = findViewById(R.id.mask);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout= findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(mNavigationItemSelectedListener);

        View view = navigationView.getHeaderView(0);
        mUsernameTv = view.findViewById(R.id.username_tv);

        mBottomNavigationView = findViewById(R.id.navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void setUsername() {
        if(mUser != null){
            mUsernameTv.setText(mUser.getUsername());
            if(!UserDb.get(this).existsUser(mUser.getUsername())){
                UserDb.get(this).addUser(mUser.getUsername());
            }
            setSharedBmobObject();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setNotesTheme(int themeId){
        switch (themeId){
            case Theme.THEME_COLOR_DEFAULT:
                setTheme(R.style.AppTheme);
                break;
            case Theme.THEME_COLOR_BLUE:
                setTheme(R.style.AppBlueTheme);
                break;
            case Theme.THEME_COLOR_PINK:
                setTheme(R.style.AppPinkTheme);
                break;
            case Theme.THEME_COLOR_RED:
                setTheme(R.style.AppRedTheme);
                break;
            case Theme.THEME_COLOR_GRAY:
                setTheme(R.style.AppGrayTheme);
                break;
            case Theme.THEME_COLOR_YELLOW:
                setTheme(R.style.AppYellowTheme);
                break;
            case Theme.THEME_COLOR_ORANGE:
                setTheme(R.style.AppOrangeTheme);
                break;
            case Theme.THEME_COLOR_GREEN:
                setTheme(R.style.AppGreenTheme);
                break;
            case Theme.THEME_COLOR_PURPLE:
                setTheme(R.style.AppPurpleTheme);
                break;
            default:
                setTheme(R.style.AppTheme);
                break;
        }
    }

    private void initFragments(){
        log("initFragments");
        mToDoFragment = ToDoFragment.newInstance(this);
        mThoughtFragment = ThoughtFragment.newInstance(this);
        mReadFragment = ReadFragment.newInstance(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, mToDoFragment)
                .add(R.id.fragment_container, mThoughtFragment)
                .add(R.id.fragment_container, mReadFragment)
                .commit();
    }

    private void setCurrentFragment(int index){
        log("setcurrentfragment"+String.valueOf(index));
        currentFragment = index;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideFragment(transaction);
        switch (index){
            case 1:
                transaction.show(mToDoFragment);
                break;
            case 2:
                transaction.show(mThoughtFragment);
                break;
            case 3:
                transaction.show(mReadFragment);
                break;
        }
        transaction.commit();
    }

//    private void setCurrentFragment(int index){
//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction transaction = fm.beginTransaction();
//        hideFragment(transaction);
//        switch (index){
//            case 1:
//                currentFragment = 1;
//               if(mToDoFragment == null){
//                    mToDoFragment = ToDoFragment.newInstance(this);
//                    transaction.add(R.id.fragment_container, mToDoFragment);
//                }else{
//                    transaction.show(mToDoFragment);
//                }
//                break;
//            case 2:
//                currentFragment = 2;
//                if(mThoughtFragment == null){
//                    mThoughtFragment = ThoughtFragment.newInstance(this);
//                    transaction.add(R.id.fragment_container, mThoughtFragment);
//                }else{
//                    transaction.show(mThoughtFragment);
//                }
//                break;
//            case 3:
//                currentFragment = 3;
//                if(mReadFragment == null){
//                    mReadFragment = new ReadFragment();
//                    transaction.add(R.id.fragment_container, mReadFragment);
//                }else{
//                    transaction.show(mReadFragment);
//                }
//                break;
//        }
//
//        transaction.commit();
//    }

    private void hideFragment(FragmentTransaction transaction) {
        if(mToDoFragment != null){
            transaction.hide(mToDoFragment);
        }
        if(mThoughtFragment != null){
            transaction.hide(mThoughtFragment);
        }
        if(mReadFragment != null){
            transaction.hide(mReadFragment);
        }
    }

    private void setSharedBmobObject(){
        SharedBmobObject.sUserName = mUser.getUsername();
    }

    private void uploadAllData(){
//        if(mToDoFragment == null){
////            mToDoFragment = ToDoFragment.newInstance(this);
////            getSupportFragmentManager().beginTransaction()
////                    .add(R.id.fragment_container, mToDoFragment)
////                    .commit();
////        }
////        if(mThoughtFragment == null){
////            mThoughtFragment = ThoughtFragment.newInstance(this);
////            getSupportFragmentManager().beginTransaction()
////                    .add(R.id.fragment_container, mThoughtFragment)
////                    .commit();
////        }
        mToDoFragment.uploadToDoList();
        mThoughtFragment.uploadThoughts();
        mReadFragment.uploadReadList();
        if(SharedBmobObject.uploadDone){
            Toast.makeText(this, "上传成功！", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "上传失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadCloudData(){
//        if(mToDoFragment == null){
//            mToDoFragment = ToDoFragment.newInstance(this);
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.fragment_container, mToDoFragment)
//                    .commit();
//        }
//        if(mThoughtFragment == null){
//            mThoughtFragment = ThoughtFragment.newInstance(this);
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.fragment_container, mThoughtFragment)
//                    .commit();
//        }
        mToDoFragment.downloadToDoList();
        mThoughtFragment.downloadThoughts();
        mReadFragment.downloadReadList();
        if(SharedBmobObject.downloadDone){
            Toast.makeText(this, "同步成功！", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "同步失败", Toast.LENGTH_SHORT).show();
        }
    }
}
