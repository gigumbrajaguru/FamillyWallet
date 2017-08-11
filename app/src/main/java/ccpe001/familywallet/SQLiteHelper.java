package ccpe001.familywallet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by harithaperera on 7/10/17.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_NOTIFICATION = "notification";
    public static final String COLUMN_NOTIID = "id";
    public static final String COLUMN_NOTIDATE = "date";
    public static final String COLUMN_NOTIDESC = "desc";
    public static final String COLUMN_NOTITITLE = "title";
    private SQLiteDatabase database;
    private String[] allColumns = { SQLiteHelper.COLUMN_NOTIID,SQLiteHelper.COLUMN_NOTITITLE, SQLiteHelper.COLUMN_NOTIDESC,
            SQLiteHelper.COLUMN_NOTIDATE};

    private static final String DATABASE_NAME = "notification.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE = "CREATE TABLE "+ TABLE_NOTIFICATION + " ("+COLUMN_NOTIID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+COLUMN_NOTITITLE+" TEXT, "+COLUMN_NOTIDESC+" TEXT, "+COLUMN_NOTIDATE+" TEXT)";


    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
        onCreate(db);
    }


    public void deleteNoti(int notiId){
        database = getWritableDatabase();
        database.delete(SQLiteHelper.TABLE_NOTIFICATION, SQLiteHelper.COLUMN_NOTIID
                + " = " + notiId, null);
        database.close();
    }

    public long addNoti(String title,String date,String desc){
        database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_NOTITITLE, title);
        values.put(SQLiteHelper.COLUMN_NOTIDESC, desc);
        values.put(SQLiteHelper.COLUMN_NOTIDATE, date);
        long temo = database.insert(SQLiteHelper.TABLE_NOTIFICATION, null,
                values);
        database.close();
        return temo;
    }

    public List<DAO> viewNoti(){
        database = getWritableDatabase();
        List<DAO> dao = new ArrayList<>();
        Cursor cursor = database.query(SQLiteHelper.TABLE_NOTIFICATION,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            DAO dao1 = cursorToComment(cursor);
            dao.add(dao1);
            cursor.moveToNext();
        }
        cursor.close();
        database.close();
        return dao;
    }

    private DAO cursorToComment(Cursor cursor) {
        DAO dao = new DAO();
        dao.setId(cursor.getInt(0));
        dao.setTitle(cursor.getString(1));
        dao.setDesc(cursor.getString(2));
        dao.setDate(cursor.getString(3));
        return dao;
    }

    static class DAO{
        String title;
        String desc;
        String date;
        int id;

        public DAO() {}

        public void setTitle(String title) {
            this.title = title;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public void setId(int id) {
            this.id = id;
        }


    }

}
