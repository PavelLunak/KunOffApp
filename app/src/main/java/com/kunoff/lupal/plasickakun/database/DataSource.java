package com.kunoff.lupal.plasickakun.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.kunoff.lupal.plasickakun.utils.AppConstants;
import com.kunoff.lupal.plasickakun.MainActivity;
import com.kunoff.lupal.plasickakun.R;
import com.kunoff.lupal.plasickakun.listeners.OnAllItemsDeletedListener;
import com.kunoff.lupal.plasickakun.listeners.OnAllPathItemsLoadedListener;
import com.kunoff.lupal.plasickakun.listeners.OnDatabaseChangedListener;
import com.kunoff.lupal.plasickakun.listeners.OnDatabaseItemsCountCheckedListener;
import com.kunoff.lupal.plasickakun.listeners.OnDefaultItemAddedListener;
import com.kunoff.lupal.plasickakun.listeners.OnItemDeletedListener;
import com.kunoff.lupal.plasickakun.listeners.OnItemUpdatedListener;
import com.kunoff.lupal.plasickakun.objects.ItemMedia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DataSource implements AppConstants {

    private SQLiteDatabase database;

    Context context;
    MainActivity activity;

    private DbHelper dbHelper;

    public DataSource(Context context) {
        dbHelper = new DbHelper(context);
        this.context = context;
        this.activity = (MainActivity) context;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void deleteAll(OnAllItemsDeletedListener listener) {
        if (database == null) open();
        int deletedItem = database.delete(DbHelper.TABLE_PATHS, DbHelper.COLUMN_ID + ">= '0'", null);
        if (listener != null) listener.onAllItemsDeleted("Odstraněno " + deletedItem + " přerušení\nOdstraněno " + deletedItem + " period");
        close();
    }

    public void addItem(boolean closeDb, ItemMedia itemMedia, final OnDatabaseChangedListener onDatabaseChangedListener) {
        if (database == null) open();

        if (closeDb) {
            close();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_PATH, itemMedia.getPath());
        values.put(DbHelper.COLUMN_NAME, itemMedia.getFileName());
        values.put(DbHelper.COLUMN_IS_ENABLED, itemMedia.isEnabled() ? 1 : 0);

        long insertId = database.insert(DbHelper.TABLE_PATHS, null, values);
        close();

        if (onDatabaseChangedListener != null) onDatabaseChangedListener.onItemAdded(insertId);
    }

    public void addDefaultItem(boolean closeDb, String name, OnDefaultItemAddedListener listener) {
        if (closeDb) {
            close();
            return;
        }

        open();
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_NAME, name);
        values.put(DbHelper.COLUMN_IS_ENABLED, 1);

        long insertId = database.insert(DbHelper.TABLE_DEFAULT_SOUNDS, null, values);
        close();

        if (listener != null) listener.onDefaultItemAdded();
    }

    public boolean checkForTableExistsAndHasData(String table){
        open();
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + table + "'";
        final Cursor cursor = database.rawQuery(sql, null);

        if (cursor.getCount() > 0) {
            if (getItemsCountOfDefaultItems(null) > 0) {
                cursor.close();
                return true;
            }
        }

        cursor.close();
        close();
        return false;
    }

    public void addDefaultData() {
        if (checkForTableExistsAndHasData(dbHelper.TABLE_DEFAULT_SOUNDS)) return;

        ArrayList<CharSequence> namesList = new ArrayList<>(Arrays.asList(((MainActivity) context).getResources().getTextArray(R.array.default_sounds_names)));

        for (int i = 0, count = namesList.size(); i <= count; i ++ ) {
            if (i == count) addDefaultItem(true, "", null);
            else addDefaultItem(false, namesList.get(i).toString(), null);
        }
    }

    public void updateCustomItemEnabled(long itemId, boolean enabled, OnItemUpdatedListener listener) {
        open();
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_IS_ENABLED, enabled ? 1 : 0);
        int rowsUpdated = database.update(DbHelper.TABLE_PATHS, values, DbHelper.COLUMN_ID + " = ?", new String[] {String.valueOf(itemId)});
        if (listener != null) listener.onItemUpdated();
        close();
    }

    public void updateDefaultItemEnabled(long itemId, boolean enabled, OnItemUpdatedListener listener) {
        open();
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_DEF_IS_ENABLED, enabled ? 1 : 0);
        int rowsUpdated = database.update(DbHelper.TABLE_DEFAULT_SOUNDS, values, DbHelper.COLUMN_ID + " = ?", new String[] {String.valueOf(itemId)});
        if (listener != null) listener.onItemUpdated();
        close();
    }

    public void removeitem(long id, OnItemDeletedListener listener) {
        open();
        int rowsRemoved = database.delete(DbHelper.TABLE_PATHS, DbHelper.COLUMN_ID + " = ?", new String[] {String.valueOf(id)});
        listener.onItemDeleted();
        close();
    }

    public void removaAll(String table, OnAllItemsDeletedListener listener) {
        open();
        int rowsRemoved = database.delete(table, null, null);
        if (listener != null) {
            listener.onAllItemsDeleted("Odstraněno " + rowsRemoved + " položek.");
        }
        close();
    }

    public List<ItemMedia> getAllCustomItems(OnAllPathItemsLoadedListener listenerLoaded) {
        open();
        ArrayList<ItemMedia> toReturn = new ArrayList<>();
        ItemMedia itemMedia;

        Cursor cursor;

        try {
            cursor = database.query(
                    DbHelper.TABLE_PATHS,    //TABLE NAME
                    null,
                    null,
                    null,
                    null,
                    null,
                    null/*"id DESC"*/);
        } catch (Exception e) {
            if(listenerLoaded != null) listenerLoaded.onAllPathItemsLoaded(toReturn);
            return toReturn;
        }

        cursor.moveToFirst();

        for(int i = 0, count = cursor.getCount(); i < count; i ++) {
            itemMedia = cursorToCustomItem(cursor);
            toReturn.add(itemMedia);
            if(cursor.isLast()) break;
            cursor.moveToNext();
        }

        cursor.close();
        close();

        activity.setItemsMedia(new ArrayList<>(toReturn));
        if(listenerLoaded != null) listenerLoaded.onAllPathItemsLoaded(activity.getItemsMedia());
        return toReturn;
    }

    public List<ItemMedia> getAllDefaultItems(OnAllPathItemsLoadedListener listenerLoaded) {
        open();
        ArrayList<ItemMedia> toReturn = new ArrayList<>();
        ItemMedia itemMedia;

        Cursor cursor;

        try {
            cursor = database.query(
                    DbHelper.TABLE_DEFAULT_SOUNDS,    //TABLE NAME
                    null,
                    null,
                    null,
                    null,
                    null,
                    null/*"id DESC"*/);
        } catch (Exception e) {
            if(listenerLoaded != null) listenerLoaded.onAllPathItemsLoaded(toReturn);
            return toReturn;
        }

        cursor.moveToFirst();

        for(int i = 0, count = cursor.getCount(); i < count; i ++) {
            itemMedia = cursorToDefaultItem(cursor, i);
            toReturn.add(itemMedia);
            if(cursor.isLast()) break;
            cursor.moveToNext();
        }

        cursor.close();
        close();

        if(listenerLoaded != null) {
            activity.setItemsMedia(new ArrayList<>(toReturn));
            listenerLoaded.onAllPathItemsLoaded(activity.getItemsMedia());
        }

        return toReturn;
    }

    private ItemMedia cursorToDefaultItem(Cursor cursor, int id) {
        ItemMedia itemMedia = new ItemMedia();

        itemMedia.setId(id);
        itemMedia.setPath(null);
        itemMedia.setEnabled(cursor.getInt(2) == 1 ? true : false);

        return itemMedia;
    }

    private ItemMedia cursorToCustomItem(Cursor cursor) {
        ItemMedia itemMedia = new ItemMedia();

        itemMedia.setId(cursor.getInt(0));
        itemMedia.setPath(cursor.getString(1));
        itemMedia.setFileName(cursor.getString(2));
        itemMedia.setEnabled(cursor.getInt(3) == 1 ? true : false);

        return itemMedia;
    }

    public int getItemsCountOfDefaultItems(OnDatabaseItemsCountCheckedListener listener) {
        open();
        int toReturn = 0;
        String selectQuery = "SELECT COUNT(*) AS pocet FROM " + DbHelper.TABLE_DEFAULT_SOUNDS;

        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            toReturn = cursor.getInt(0);
        }

        cursor.close();
        close();

        if (listener != null) listener.OnDatabaseItemsCountCheckedListener(toReturn);
        return toReturn;
    }
}
