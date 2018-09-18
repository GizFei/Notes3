package readDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import readDatabase.ReadDbSchema.Cols;

public class ReadDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movieInfo";
    private String mTableName;

    public ReadDatabaseHelper(Context context, String username, int version){
        super(context, DATABASE_NAME, null, version);
        mTableName = ReadDbSchema.TABLE_NAME + username;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format("create table %s(%s, %s, %s, %s, %s)", mTableName,
                Cols.UUID, Cols.NAME, Cols.SEEN, Cols.COMMENT, Cols.TYPE);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = String.format("create table %s(%s, %s, %s, %s, %s)", mTableName,
                Cols.UUID, Cols.NAME, Cols.SEEN, Cols.COMMENT, Cols.TYPE);
        db.execSQL(sql);
    }
}
