package readDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import userDatabase.UserDb;

public class ReadDatabase {

    private static ReadDatabase sReadDatabase;
    private static String sUsername;
    private String mTableName;

    private SQLiteDatabase mDatabase;

    public static ReadDatabase get(Context context, String username) {
        if(sReadDatabase == null || !username.equals(sUsername)){
            sReadDatabase = new ReadDatabase(context, username);
        }
        return sReadDatabase;
    }

    private ReadDatabase(Context context, String username){
        sUsername = username;
        mTableName = ReadDbSchema.TABLE_NAME + username;
        Context mContext = context.getApplicationContext();
        UserDb udb = UserDb.get(mContext);
        int verion;
        if(udb.existsUser(username)){
            verion = udb.getUserCount();
        }else{
            verion = udb.getUserCount() + 1;
        }
        mDatabase = new ReadDatabaseHelper(mContext, username, verion).getWritableDatabase();
    }

    public List<ReadItem> getReadItems(){
        List<ReadItem> items = new ArrayList<>();

        Cursor cursor = mDatabase.query(mTableName,
                null,
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            items.add(getItem(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return items;
    }

    public void addReadItem(ReadItem readItem){
        mDatabase.insert(mTableName, null,
                getContentValues(readItem));
    }

    public void updateReadItem(ReadItem readItem){
        mDatabase.update(mTableName, getContentValues(readItem),
                ReadDbSchema.Cols.UUID + "=?",
                new String[]{readItem.getUUID().toString()});
    }

    public void deleteReadItem(ReadItem readItem){
        mDatabase.delete(mTableName, ReadDbSchema.Cols.UUID + "=?",
                new String[]{readItem.getUUID().toString()});
    }

    public void deleteAll(){
        mDatabase.delete(mTableName, null, null);
    }

    public ReadItem queryReadItem(String uuid){
        Cursor cursor = mDatabase.query(mTableName, null,
                ReadDbSchema.Cols.UUID + "=?",
                new String[]{uuid},
                null,
                null,
                null);
        cursor.moveToFirst();
        ReadItem item = getItem(cursor);
        cursor.close();

        return item;
    }

    private ContentValues getContentValues(ReadItem readItem){
        ContentValues values = new ContentValues();

        values.put(ReadDbSchema.Cols.UUID, readItem.getUUID().toString());
        values.put(ReadDbSchema.Cols.NAME, readItem.getName());
        values.put(ReadDbSchema.Cols.SEEN, readItem.isHasSeen());
        values.put(ReadDbSchema.Cols.COMMENT, readItem.getComment());
        values.put(ReadDbSchema.Cols.TYPE, readItem.getType());

        return values;

    }

    private ReadItem getItem(Cursor cursor){
        String uuid = cursor.getString(cursor.getColumnIndex(ReadDbSchema.Cols.UUID));
        String name = cursor.getString(cursor.getColumnIndex(ReadDbSchema.Cols.NAME));
        int hasSeen = cursor.getInt(cursor.getColumnIndex(ReadDbSchema.Cols.SEEN));
        String comment = cursor.getString(cursor.getColumnIndex(ReadDbSchema.Cols.COMMENT));
        String type = cursor.getString(cursor.getColumnIndex(ReadDbSchema.Cols.TYPE));

        ReadItem readItem = new ReadItem(UUID.fromString(uuid));
        readItem.setName(name);
        readItem.setHasSeen(hasSeen == 1);
        readItem.setComment(comment);
        readItem.setType(type);

        return readItem;
    }
}
