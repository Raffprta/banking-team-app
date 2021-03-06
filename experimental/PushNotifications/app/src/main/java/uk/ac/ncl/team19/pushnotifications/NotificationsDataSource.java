package uk.ac.ncl.team19.pushnotifications;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class NotificationsDataSource {
    private static final String TAG = NotificationsDataSource.class.getSimpleName();

    private Context context;

    private SQLiteDatabase database;
    private DBOpenHelper dbHelper;
    private String[] notificationsColumns = { DBOpenHelper.COLUMN_ID,
            DBOpenHelper.COLUMN_DATE,
            DBOpenHelper.COLUMN_NOTIFICATION_TYPE,
            DBOpenHelper.COLUMN_NOTIFICATION_MESSAGE };

    public NotificationsDataSource(Context context) {
        this.context = context;
        dbHelper = new DBOpenHelper(context);
    }

    public void open() throws SQLException {
        // Open the database
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long createNotification(long dateInMillis, String notificationType, String notificationMessage) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COLUMN_DATE, dateInMillis);
        values.put(DBOpenHelper.COLUMN_NOTIFICATION_TYPE, notificationType);
        values.put(DBOpenHelper.COLUMN_NOTIFICATION_MESSAGE, notificationMessage);
        return database.insert(DBOpenHelper.TABLE_NOTIFICATIONS, null, values);
    }

    public int updateNotifications(long id, long dateInMillis, String notificationTitle, String notificationMessage) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COLUMN_DATE, dateInMillis);
        values.put(DBOpenHelper.COLUMN_NOTIFICATION_TYPE, notificationTitle);
        values.put(DBOpenHelper.COLUMN_NOTIFICATION_MESSAGE, notificationMessage);
        return database.update(DBOpenHelper.TABLE_NOTIFICATIONS, values, DBOpenHelper.COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
    }

    public LloydsNotification getNotification(long notificationID) {
        Cursor cursor = database.query( DBOpenHelper.TABLE_NOTIFICATIONS,
                notificationsColumns,
                DBOpenHelper.COLUMN_ID + " = ?",
                new String[] { String.valueOf(notificationID) },
                null, null, null);

        cursor.moveToFirst();
        LloydsNotification notification = new LloydsNotification(cursor.getLong(1), cursor.getString(2), cursor.getString(3));
        cursor.close();
        return notification;
    }

    public List<LloydsNotification> getAllNotifications() {
        List<LloydsNotification> notifications = new ArrayList<LloydsNotification>();

        Cursor cursor = database.query( DBOpenHelper.TABLE_NOTIFICATIONS,
                notificationsColumns,
                null, null, null, null,
                DBOpenHelper.COLUMN_DATE + " DESC",
                null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            notifications.add(new LloydsNotification(cursor.getLong(1), cursor.getString(2), cursor.getString(3)));
            cursor.moveToNext();
        }

        cursor.close();
        return notifications;
    }

    public int deleteRow(String table, long id) {
        return database.delete(table, DBOpenHelper.COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
    }
}
