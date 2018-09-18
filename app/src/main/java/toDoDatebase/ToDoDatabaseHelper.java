package toDoDatebase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import toDoDatebase.ToDoDbSchema.ToDoTable;
import toDoDatebase.ToDoDbSchema.ToDoTable.Cols;

public class ToDoDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tododatabase";

    private String mTableName;

    public ToDoDatabaseHelper(Context context, String username, int version) {
        super(context, DATABASE_NAME, null, version);
        mTableName = ToDoTable.TABLE_NAME + username;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = String.format("create table if not exists %s(%s, %s)",
                mTableName, Cols.UUID, Cols.THING);
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql = String.format("create table if not exists %s(%s, %s)",
                mTableName, Cols.UUID, Cols.THING);
        sqLiteDatabase.execSQL(sql);
    }
}
