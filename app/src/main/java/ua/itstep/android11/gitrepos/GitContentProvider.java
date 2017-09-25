package ua.itstep.android11.gitrepos;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class GitContentProvider extends ContentProvider {
    private SQLiteDatabase database;
    private DBHelper dbHelper;


    public GitContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (Prefs.DEBUG) Log.d(Prefs.LOG_TAG, getClass().getSimpleName() + " insert");
        long id = 0L;
        Uri insertUri = null;
        database = dbHelper.getWritableDatabase();
        id = database.insert(Prefs.TABLE_RESULTS, null, values);
        if (id != 0) {
            insertUri = ContentUris.withAppendedId(Prefs.URI_RESULTS, id);
        }

        return insertUri;
    }

    @Override
    public boolean onCreate() {
        if(Prefs.DEBUG) Log.d(Prefs.LOG_TAG, getClass().getSimpleName() +" onCreate");
        dbHelper = new DBHelper(getContext(), Prefs.DB_CURRENT_VERSION);
        database = dbHelper.getWritableDatabase();
        if ( database == null ) {
            Log.d(Prefs.LOG_TAG, getClass().getSimpleName() +" onCreate  database == null");
        }
        return (database != null);

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
