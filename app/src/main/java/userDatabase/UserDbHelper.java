package userDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase";
    private static final int VERSION = 1;
    public static final String TABLE_NAME = "Users";
    public static final String COL_USERNAME = "username";

    public UserDbHelper(Context context){
        super(context.getApplicationContext(), DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format("create table %s(_id integer primary key autoincrement" +
                ", %s)", TABLE_NAME, COL_USERNAME);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
