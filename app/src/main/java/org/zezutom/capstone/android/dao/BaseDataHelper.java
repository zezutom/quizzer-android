package org.zezutom.capstone.android.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public abstract class BaseDataHelper extends SQLiteOpenHelper {

    public static final String TAG = BaseDataHelper.class.getSimpleName();

    protected static final int DB_VERSION = 10;

    protected static final String DB_SCHEMA = "mutibo";

    protected static final String COLUMN_ID = "id";

    protected static final String COLUMN_CREATED_AT = "created_at";

    protected static final String TABLE_DROP = "drop table if exists ";

    public BaseDataHelper(Context context, String dbName) {
        super(context, dbName, null, DB_VERSION);
    }

    protected abstract String dbCreate();

    protected abstract String getTableName();

    @Override
    public void onCreate(SQLiteDatabase db) {
        dbDrop(db);
        db.execSQL(dbCreate());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading db from version " + oldVersion +
                " to " + newVersion + ". Saved data will be lost.");
        onCreate(db);

    }

    private void dbDrop(SQLiteDatabase db) {
        db.execSQL(TABLE_DROP + getTableName());
    }
}
