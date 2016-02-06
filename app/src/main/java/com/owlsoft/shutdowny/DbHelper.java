package com.owlsoft.shutdowny;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.owlsoft.shutdowny.models.Checkpoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by mac on 06.02.16.
 */
public class DbHelper extends SQLiteOpenHelper {

    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");

    // database version
    private static final int database_VERSION = 1;
    // database name
    private static final String database_NAME = "checkpointsDB";
    private static final String table_CHECKPOINTS = "checkpoints";
    private static final String checkpoint_id = "id";
    private static final String checkpoint_date = "date";

    private static final String[] COLUMNS = { checkpoint_id, checkpoint_date};

    public DbHelper(Context context) {
        super(context, database_NAME, null, database_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_CHECKPOINT_TABLE = "CREATE TABLE checkpoints ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT )";
        db.execSQL(CREATE_CHECKPOINT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS checkpoints");
        this.onCreate(db);
    }

    public boolean createCheckPoint(Checkpoint checkpoint){
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            // make values to be inserted
            ContentValues values = new ContentValues();

            values.put(checkpoint_date, dateFormat.format(checkpoint.when.getTime()));

            // insert book
            checkpoint.id = db.insert(table_CHECKPOINTS, null, values);

            return true;
        }
        catch (Exception exception){
            return false;
        }
        finally {
            if(db!=null)
                db.close();
        }
        // close database transaction

    }

    public List getAllCheckPoints(){
        List checkpoints = new LinkedList();

        // select book query
        String query = "SELECT  * FROM " + table_CHECKPOINTS;

        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // parse all results
        Checkpoint checkpoint = null;
        if (cursor.moveToFirst()) {
            do {
                checkpoint = new Checkpoint();
                checkpoint.id = Integer.parseInt(cursor.getString(0));
                try {
                    Calendar calendarInstance = Calendar.getInstance(Locale.getDefault());
                    calendarInstance.setTime(dateFormat.parse(cursor.getString(1)));
                    checkpoint.when = calendarInstance;
                } catch (ParseException e) {
                    checkpoint.when = Calendar.getInstance();
                }

                checkpoints.add(checkpoint);
            } while (cursor.moveToNext());
        }
        return checkpoints;
    }

    public boolean removeCheckpoint(Checkpoint checkpoint) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            // make values to be inserted
            // insert book
            db.delete(table_CHECKPOINTS,"id = ?",new String[]{String.valueOf(checkpoint.id)});
            return true;
        }
        catch (Exception exception){
            return false;
        }
        finally {
            if(db!=null)
                db.close();
        }
        // close database transaction
    }
}
