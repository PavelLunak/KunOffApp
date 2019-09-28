package com.kunoff.lupal.plasickakun.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbHelper extends SQLiteOpenHelper {

    public Context context;

    public static final String TABLE_PATHS = "table_paths";
    public static final String TABLE_DEFAULT_SOUNDS = "table_default_sounds";

    //TABLE_PATHS
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_IS_ENABLED = "enabled";

    //TABLE_DEFAULT_SOUNDS
    public static final String COLUMN_DEF_ID = "id";
    public static final String COLUMN_DEF_NAME = "name";
    public static final String COLUMN_DEF_IS_ENABLED = "enabled";

    private static final String DATABASE_NAME = "kunoff.db";
    private static final int DATABASE_VERSION = 6;

    private static final String TABLE_PATHS_CREATE = "CREATE TABLE "
            + TABLE_PATHS
            + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_PATH + " VARCHAR (500), "
            + COLUMN_NAME + " VARCHAR (500), "
            + COLUMN_IS_ENABLED + " TINYINT (1) "
            + ");";

    private static final String TABLE_DEFAULT_SOUNDS_CREATE = "CREATE TABLE "
            + TABLE_DEFAULT_SOUNDS
            + " ( " + COLUMN_DEF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_DEF_NAME + " VARCHAR (500), "
            + COLUMN_DEF_IS_ENABLED + " TINYINT (1) "
            + ");";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_PATHS_CREATE);
        database.execSQL(TABLE_DEFAULT_SOUNDS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATHS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEFAULT_SOUNDS);
        onCreate(db);
    }
}
