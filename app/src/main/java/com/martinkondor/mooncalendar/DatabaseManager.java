package com.martinkondor.mooncalendar;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.ArrayMap;

import java.util.ArrayList;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database (SQLite) management with the events table
 */
public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DB_NAME = "moon_calendar";
    private static final int DB_VERSION = 2;
    private static final String TABLE_NAME = "events";

    // Events table columns
    private static final String ID_COL = "id";
    private static final String TIME_COL = "time";
    private static final String TITLE_COL = "title";
    private static final String YEAR_COL = "year";
    private static final String MONTH_COL = "month";
    private static final String DAY_COL = "day";
    private static final String DESC_COL = "description";

    public DatabaseManager(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + YEAR_COL + " INTEGER,"
                + MONTH_COL + " INTEGER,"
                + DAY_COL + " INTEGER,"
                + TIME_COL + " TEXT,"
                + TITLE_COL + " TEXT,"
                + DESC_COL + " TEXT);";
        db.execSQL(query);
    }

    /**
     * Delete database on VERSION change
     * @param db d
     * @param oldVersion o
     * @param newVersion n
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Insert event in the database
     * @param e The event to be inserted
     * @param db The database instance given
     */
    public void insertEvent(Event e, SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        if (e.getId() != -1) {
            values.put(ID_COL, e.getId());
        }
        values.put(YEAR_COL, e.getYear());
        values.put(MONTH_COL, e.getMonth());
        values.put(DAY_COL, e.getDay());
        values.put(TIME_COL, e.getTime());
        values.put(TITLE_COL, e.getTitle());
        values.put(DESC_COL, e.getDesc());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    /**
     * Insert event in the database
     * @param e The event to be inserted
     */
    public void insertEvent(Event e) {
        SQLiteDatabase db = this.getWritableDatabase();
        insertEvent(e, db);
        db.close();
    }

    /**
     * Delete an event from the database
     * @param id The event's id to be deleted
     * @param db The database instance given
     */
    public void deleteEvent(int id, SQLiteDatabase db) {
        String whereClause = ID_COL + "=?";
        db.delete(TABLE_NAME, whereClause, new String[] {String.valueOf(id)});
    }

    /**
     * Delete an event from the database
     * @param id The event's id to be deleted
     */
    public void deleteEvent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        deleteEvent(id, db);
        db.close();
    }

    public void updateEvent(Event e) {
        deleteEvent(e.getId());
        insertEvent(e);
    }

    /**
     * Load and return the cursor for all the events of the asked day
     * @param db database instance given
     * @param year the needed year
     * @param month the needed month
     * @param day the needed day
     * @return Cursor with all the events for the given year,month,day
     */
    private Cursor getEventsCursor(SQLiteDatabase db, int year, int month, int day) {
        String[] tableColumns = new String[] {"*"};
        String[] selectionArgs = new String[] {String.valueOf(year), String.valueOf(month), String.valueOf(day)};
        String selectionStr = YEAR_COL + "=? AND " +
                MONTH_COL + "=? AND " +
                DAY_COL + "=?";
        return (Cursor) db.query(
                TABLE_NAME, tableColumns, selectionStr, selectionArgs, null, null, TIME_COL);
    }

    /**
     * Loads events in the given eventsPerDays param
     * @param year the year of the events
     * @param month the month of the events
     * @param numberOfDays number of day in the given month
     */
    @SuppressLint("Range")
    public void loadEvents(ArrayMap<Integer, ArrayList<Event>> dayEventMap, int year, int month, int numberOfDays) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Remove all previous events in the storage
        dayEventMap.clear();

        // To save the database cursors
        ArrayList<Cursor> cursorsForDays = new ArrayList<>();

        // Create empty lists & load cursors for each day
        for (int i = 1; i < numberOfDays + 1; i++) {
            ArrayList<Event> events = new ArrayList<>();
            dayEventMap.put(i, events);

            Cursor dc = getEventsCursor(db, year, month, i);
            cursorsForDays.add(dc);
        }

        // Consume each cursor
        for (Cursor c : cursorsForDays) {
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        // Get event's data from the database
                        int mid = c.getInt(c.getColumnIndexOrThrow(ID_COL));
                        int myear = c.getInt(c.getColumnIndexOrThrow(YEAR_COL));
                        int mmonth = c.getInt(c.getColumnIndexOrThrow(MONTH_COL));
                        int mday = c.getInt(c.getColumnIndexOrThrow(DAY_COL));
                        String mtime = c.getString(c.getColumnIndexOrThrow(TIME_COL));
                        String mtitle = c.getString(c.getColumnIndexOrThrow(TITLE_COL));
                        String mdesc = c.getString(c.getColumnIndexOrThrow(DESC_COL));

                        // Save event to the event store
                        dayEventMap.get(mday)
                                .add(new Event(mid, myear, mmonth, mday, mtime, mtitle, mdesc));

                    } while (c.moveToNext());
                }
                c.close();
            }
        }
        db.close();
    }
}
