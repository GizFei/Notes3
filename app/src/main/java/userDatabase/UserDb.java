package userDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class UserDb {

    private static UserDb sUserDb;
    private SQLiteDatabase mDatabase;
    private List<String> mUserList;

    public static UserDb get(Context context){
        if(sUserDb == null){
            sUserDb = new UserDb(context);
        }
        return sUserDb;
    }

    private UserDb(Context context){
        mDatabase = new UserDbHelper(context).getWritableDatabase();
        getUserList();
    }

    public void addUser(String username){
        mUserList.add(username);
        ContentValues values = new ContentValues();
        values.put(UserDbHelper.COL_USERNAME, username);
        mDatabase.insert(UserDbHelper.TABLE_NAME, null, values);
    }

    public int getUserCount(){
        return mUserList.size();
    }

    public boolean existsUser(String username){
        return mUserList.contains(username);
    }

    private void getUserList(){
        mUserList = new ArrayList<>();
        Cursor cursor = mDatabase.rawQuery("select * from " + UserDbHelper.TABLE_NAME,
                null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(UserDbHelper.COL_USERNAME);
        while (!cursor.isAfterLast()){
            mUserList.add(cursor.getString(columnIndex));
            cursor.moveToNext();
        }
        cursor.close();
    }
}
