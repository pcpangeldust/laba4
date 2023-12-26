package com.example.laba4;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.app.AppCompatActivity;

public class RecordsActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        dbHelper = new DBHelper(this);
        displayRecords();
    }

    private void displayRecords() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT rowid _id, NameOfTheSong FROM SongInfo", null);

        String[] columns = {"NameOfTheSong"};
        int[] viewIDs = {R.id.recordTextView};

        adapter = new SimpleCursorAdapter(this, R.layout.record_item, cursor, columns, viewIDs, 0);
        ListView listView = findViewById(R.id.recordsListView);
        listView.setAdapter(adapter);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.swapCursor(null);
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}