package uk.ac.ncl.team19.lloydsapp.utils.push;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_FILENAME = "data.db";

    public static final String TABLE_NOTIFICATIONS = "notifications";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_NOTIFICATION_TYPE = "notification_type";
    public static final String COLUMN_NOTIFICATION_MESSAGE = "notification_message";

    private static final int DATABASE_VERSION = 1;

    private static final String TAG = DBOpenHelper.class.getSimpleName();

    // Executed when creating database for the first time
    private static final String DATABASE_CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE " + TABLE_NOTIFICATIONS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_DATE + " INTEGER, "
            + COLUMN_NOTIFICATION_TYPE + " TEXT, "
            + COLUMN_NOTIFICATION_MESSAGE + " TEXT"
            + ");";


    public DBOpenHelper(Context context) {
        super(context, DATABASE_FILENAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.w(TAG, "Didn't find database file - creating.");

        // Data tables
        sqLiteDatabase.execSQL(DATABASE_CREATE_NOTIFICATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Later versions of the database schema do upgrading work here.
        Log.i(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys = ON;");
        }
    }
}