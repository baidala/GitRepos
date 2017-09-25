package ua.itstep.android11.gitrepos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Maksim Baydala on 25/09/17.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "gitrepos";
    private static final String CREATE_TABLE_RESULTS = "create table results (_id integer primary key autoincrement, login text, git_id text, avatar_url text, html_url text);";


    public DBHelper(Context context, int version) {
        super(context, DB_NAME, null, version);
        if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "DBHelper  construct ");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_RESULTS);

        if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "DBHelper  onCreate ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, "DBHelper  onUpgrade");

        db.execSQL("drop table results");

        db.execSQL(CREATE_TABLE_RESULTS);



    }


}
