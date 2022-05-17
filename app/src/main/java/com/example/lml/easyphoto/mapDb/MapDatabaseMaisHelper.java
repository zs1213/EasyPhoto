package com.example.lml.easyphoto.mapDb;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class MapDatabaseMaisHelper extends MapSDCardSQLiteOpenHelper {
    private Context context;
    private static final String DATABASE_NAME = "china.mbtiles";
    public MapDatabaseMaisHelper(Context context, CursorFactory factory) throws NameNotFoundException {
        super(context, DATABASE_NAME, factory, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {

        super.onOpen(db);
    }

}
