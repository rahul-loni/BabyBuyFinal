package com.example.babybuy1;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.NavigationRes;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME="ITEMS_INFO_DB";
    public static final String TABLE_NAME="ITEMS_INFO_TABLE";
    public static final int DB_VERSION = 1;

    // table columns
    public static final String COLUMN_ID = "id";
    public static final String NAME = "name";
    public static final String PRICE = "price";
    public static final String DESCRIPTION = "description";
    public static final String IMAGE = "image";
    public static final String PURCHASED = "purchased";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME + " TEXT NOT NULL, " +
                PRICE + " TEXT NOT NULL, " +
                DESCRIPTION + " TEXT, " +
                IMAGE + " TEXT, " +
                PURCHASED + " INTEGER)";
        db.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sqlQuery = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sqlQuery);
        onCreate(db);
    }

    public Cursor queryData(String sqlQuery){
        SQLiteDatabase database = getWritableDatabase();
        return database.rawQuery(sqlQuery, null);
    }

    public Boolean insert(
            String name,
            double price,
            String description,
            String image,
            boolean purchased
    ) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO " + TABLE_NAME + " VALUES (NULL, ?, ?, ?, ?, ?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1, name);
        statement.bindDouble(2, price);
        statement.bindString(3, description);
        statement.bindString(4, image);
        statement.bindLong(5, purchased ? 1 : 0);
        long result = statement.executeInsert();
        database.close();
        return result != -1;
    }

    public Cursor getElementById(int id) {
        SQLiteDatabase database = getWritableDatabase();
        String sqlQuery = "SELECT * FROM " + TABLE_NAME + " WHERE id=?";
        return database.rawQuery(
                sqlQuery,
                new String[]{String.valueOf(id)}
        );
    }

    public Cursor getAll() {
        SQLiteDatabase database = getReadableDatabase();
        String sqlQuery = "SELECT * FROM " + TABLE_NAME;
        return database.rawQuery(sqlQuery, null);
    }

    public Boolean update(
            int id,
            String name,
            double price,
            String description,
            String image,
            boolean purchased
    ) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE " + TABLE_NAME + " SET " + NAME + "=?, " +
                PRICE + "=?, " + DESCRIPTION + "=?, " + IMAGE + "=?, " + PURCHASED + "=? WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1, name);
        statement.bindDouble(2, price);
        statement.bindString(3, description);
        statement.bindString(4, image);
        statement.bindLong(4, purchased ? 1 : 0);
        statement.bindLong(5, id);
        int result = statement.executeUpdateDelete();
        database.close();
        return result != -1;
    }

    public void delete(long id) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(
                TABLE_NAME,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)});
    }
}
