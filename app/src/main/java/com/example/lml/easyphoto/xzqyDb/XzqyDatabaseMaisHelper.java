package com.example.lml.easyphoto.xzqyDb;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class XzqyDatabaseMaisHelper extends XzqySDCardSQLiteOpenHelper {
    private Context context;
    private static final String DATABASE_NAME = "xzqy";
    public XzqyDatabaseMaisHelper(Context context, CursorFactory factory) throws NameNotFoundException {
        super(context, DATABASE_NAME, factory, 5);
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
