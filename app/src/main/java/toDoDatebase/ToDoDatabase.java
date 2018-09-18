package toDoDatebase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import userDatabase.UserDb;

public class ToDoDatabase {

    private static ToDoDatabase sToDoDatabase;
    private static String sUsername;
    private String mTableName;

    private SQLiteDatabase mDatabase;

    public static ToDoDatabase get(Context context, String username) {
        if(sToDoDatabase == null || !username.equals(sUsername)){
            sToDoDatabase = new ToDoDatabase(context, username);
        }
        return sToDoDatabase;
    }

    private ToDoDatabase(Context context, String username){
        sUsername = username;
        mTableName = ToDoDbSchema.ToDoTable.TABLE_NAME + username;
        Context mContext = context.getApplicationContext();
        UserDb udb = UserDb.get(mContext);
        int version = 1;
        if(udb.existsUser(username)){
            version = udb.getUserCount();
        }else{
            version = udb.getUserCount() + 1;
        }
        mDatabase = new ToDoDatabaseHelper(mContext, username, version).getWritableDatabase();
    }

    public List<ToDoItem> getToDoItems(){
        Cursor cursor = mDatabase.query(mTableName,
                null,
                null,
                null,
                null,
                null,
                null);
        List<ToDoItem> items = new ArrayList<>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            ToDoItem item = new ToDoItem(cursor.getString(cursor.getColumnIndex(ToDoDbSchema.ToDoTable.Cols.UUID)));
            item.setThing(cursor.getString(cursor.getColumnIndex(ToDoDbSchema.ToDoTable.Cols.THING)));
            items.add(item);
            cursor.moveToNext();
        }
        cursor.close();

        return items;
    }

    public void addItem(ToDoItem item){
        ContentValues values = getContentValues(item);

        mDatabase.insert(mTableName, null, values);
    }

    public void deleteItem(ToDoItem item){
        String uuid = item.getUUID().toString();
        mDatabase.delete(mTableName,
                ToDoDbSchema.ToDoTable.Cols.UUID + "=?", new String[]{uuid});
    }

    public void deleteAll(){
        mDatabase.delete(mTableName, null, null);
    }

    @NonNull
    private ContentValues getContentValues(ToDoItem item) {
        ContentValues values = new ContentValues();

        values.put(ToDoDbSchema.ToDoTable.Cols.UUID, item.getUUID().toString());
        values.put(ToDoDbSchema.ToDoTable.Cols.THING, item.getThing());
        return values;
    }

    public void updateItem(ToDoItem item){
        String uuid = item.getUUID().toString();

        ContentValues values = getContentValues(item);
        mDatabase.update(mTableName, values, ToDoDbSchema.ToDoTable.Cols.UUID +
                "=?", new String[]{uuid});
    }
}
