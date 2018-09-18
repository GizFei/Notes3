package thoughtDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import userDatabase.UserDb;

public class ThoughtDatabase {

    private static ThoughtDatabase sThoughtDatabase;
    private static String sUsername;
    private String mTableName;

    private SQLiteDatabase mDatabase;

    public static ThoughtDatabase get(Context context, String username){
        if(sThoughtDatabase == null || !username.equals(sUsername)){
            sThoughtDatabase = new ThoughtDatabase(context, username);
        }
        return  sThoughtDatabase;
    }

    private ThoughtDatabase(Context context, String username){
        sUsername = username;
        mTableName = ThoughtDbSchema.TABLE_NAME + username;
        Context mContext = context.getApplicationContext();
        UserDb udb = UserDb.get(mContext);
        int version = 1;
        if(udb.existsUser(username)){
            version = udb.getUserCount();
        }else{
            version = udb.getUserCount() + 1;
        }
        mDatabase = new ThoughtDatabaseHelper(mContext, username, version).getWritableDatabase();
    }

    public List<Thought> getThoughts(){
        List<Thought> thoughts = new ArrayList<>();
        Cursor cursor = mDatabase.query(mTableName,
                null,
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            thoughts.add(getThought(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return thoughts;
    }

    public void addThought(Thought thought){
        ContentValues values = getContentValues(thought);
        mDatabase.insert(mTableName, null, values);
    }

    public void updateThought(Thought thought){
        mDatabase.update(mTableName, getContentValues(thought),
                ThoughtDbSchema.Cols.UUID + "=?",
                new String[]{thought.getUUID().toString()});
    }

    public void deleteThought(Thought thought){
        mDatabase.delete(mTableName, ThoughtDbSchema.Cols.UUID + "=?",
                new String[]{thought.getUUID().toString()});
    }

    public void deleteAll(){
        mDatabase.delete(mTableName, null, null);
    }

    public Thought queryThought(String uuid){
        Cursor cursor = mDatabase.query(mTableName,
                null,
                ThoughtDbSchema.Cols.UUID + "=?",
                new String[]{uuid},
                null,
                null,
                null);
        cursor.moveToFirst();
        if(cursor.getCount() != 0){
            Thought thought = getThought(cursor);
            cursor.close();
            return thought;
        }else{
            cursor.close();
            return null;
        }
    }

    private Thought getThought(Cursor cursor){
        String uuid = cursor.getString(cursor.getColumnIndex(ThoughtDbSchema.Cols.UUID));
        String title = cursor.getString(cursor.getColumnIndex(ThoughtDbSchema.Cols.TITLE));
        String content = cursor.getString(cursor.getColumnIndex(ThoughtDbSchema.Cols.CONTENT));

        Thought thought = new Thought(uuid);
        thought.setTitle(title);
        thought.setContent(content);

        return thought;
    }

    private ContentValues getContentValues(Thought thought){
        ContentValues values = new ContentValues();

        values.put(ThoughtDbSchema.Cols.UUID, thought.getUUID().toString());
        values.put(ThoughtDbSchema.Cols.TITLE, thought.getTitle());
        values.put(ThoughtDbSchema.Cols.CONTENT, thought.getContent());

        return values;
    }
}
