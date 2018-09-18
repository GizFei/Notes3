package thoughtDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ThoughtDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "thoughtDb";

    private String mTableName;

    public ThoughtDatabaseHelper(Context context, String username, int version){
        super(context, DATABASE_NAME, null, version );
        mTableName = ThoughtDbSchema.TABLE_NAME + username;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("onUpgrade","YES");
        String sql = String.format("create table if not exists %s(%s, %s, %s)", mTableName,
                ThoughtDbSchema.Cols.UUID, ThoughtDbSchema.Cols.TITLE, ThoughtDbSchema.Cols.CONTENT);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("onUpgrade","YES");
        String sql = String.format("create table if not exists %s(%s, %s, %s)", mTableName,
                ThoughtDbSchema.Cols.UUID, ThoughtDbSchema.Cols.TITLE, ThoughtDbSchema.Cols.CONTENT);
        db.execSQL(sql);
    }
}
