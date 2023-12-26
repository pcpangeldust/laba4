package com.example.laba4;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Songs";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_SONG_INFO = "SongInfo";

    // Конструктор
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // создаем таблицу с полями ID, NameOfTheSong, AddingTime
        db.execSQL("create table SongInfo ("
                + "ID integer primary key autoincrement,"
                + "NameOfTheSong text,"
                + "AddingTime integer" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Обновление базы данных при изменении версии
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONG_INFO);
        onCreate(db);
    }
}