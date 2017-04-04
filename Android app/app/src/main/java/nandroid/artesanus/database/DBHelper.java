package nandroid.artesanus.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

/**
 * Created by Nando on 27/08/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Artesanus.db";
    public static final String CRAFTING_TABLE_NAME = "measures";
    public static final String CRAFTING_COLUMN_ID = "craftingId";
    public static final String CRAFTING_COLUMN_PROCESS_ID = "processId";
    public static final String CRAFTING_COLUMN_TEMPERATURE = "temperature";
    public static final String CRAFTING_COLUMN_DENSITY = "density";
    public static final String CRAFTIN_COLUMN_TIMESTAMP = "time";

    private HashMap hp;

    public DBHelper(Context  ctx)
    {
        super(ctx, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table " + CRAFTING_TABLE_NAME +
                " (" + CRAFTING_COLUMN_ID + " "
                + CRAFTING_COLUMN_PROCESS_ID + " "
                + CRAFTING_COLUMN_TEMPERATURE + " "
                + CRAFTING_COLUMN_DENSITY + " "
                + CRAFTIN_COLUMN_TIMESTAMP + " )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + CRAFTING_TABLE_NAME + ")");

        onCreate(db);
    }

    //Method for temperature's measure insertions
    public boolean insertMeasureTemp (long craftingId, long processId, double temperature, long timestamp )
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(CRAFTING_COLUMN_ID, craftingId);
        content.put(CRAFTING_COLUMN_PROCESS_ID, processId);
        content.put(CRAFTING_COLUMN_TEMPERATURE, temperature);
        content.put(CRAFTIN_COLUMN_TIMESTAMP, timestamp);
        return db.insert(CRAFTING_TABLE_NAME, null, content) != -1;

    }

    // Method for density's measure insertions
    public boolean insertMeasureDens (long craftingId, long processId, double density, long timestamp )
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(CRAFTING_COLUMN_ID, craftingId);
        content.put(CRAFTING_COLUMN_PROCESS_ID, processId);
        content.put(CRAFTING_COLUMN_DENSITY, density);
        content.put(CRAFTIN_COLUMN_TIMESTAMP, timestamp);
        return db.insert(CRAFTING_TABLE_NAME, null, content) != -1;
    }

    public Cursor getData(int id)
    {
        SQLiteDatabase db =  this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from measures where  craftingId = ? ", new String[]{Integer.toString(id)});
        return res;
    }

    public Cursor getTemperature(long craftingId, long processId)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from measures where craftingId = ? and processId = ? and temperature IS NOT NULL",
                new String[]{Long.toString(craftingId), Long.toString(processId)});
        return res;
    }

    public Cursor getDensity(long craftingId, long processId)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from measures where craftingId = ? and processId = ? and density IS NOT NULL",
                new String[]{Long.toString(craftingId), Long.toString(processId)});
        return res;
    }

}
