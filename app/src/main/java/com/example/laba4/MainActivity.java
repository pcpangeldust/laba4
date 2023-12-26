package com.example.laba4;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MainActivity extends AppCompatActivity {

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);

        // Добавляем TextView для отображения информации о треке
        TextView trackInfoTextView = findViewById(R.id.trackInfoTextView);

        Button viewRecordsButton = findViewById(R.id.viewRecordsButton);
        viewRecordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecordsActivity.class);
                startActivity(intent);
            }
        });

        // Запускаем асинхронный опрос сервера каждые 20 секунд
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Проверяем подключение к интернету
                if (isInternetAvailable()) {
                    // Если есть подключение, создаем экземпляр AsyncTask и запускаем выполнение
                    TrackInfoUpdater trackInfoUpdater = new TrackInfoUpdater(trackInfoTextView, dbHelper);
                    trackInfoUpdater.execute();
                } else {
                    // Если нет интернета, показываем Toast с предупреждением
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Отсутствует подключение к интернету. Режим просмотра внесенных записей.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                // Повторяем опрос через 20 секунд
                trackInfoTextView.postDelayed(this, 20000);
            }
        };
        trackInfoTextView.post(runnable);
    }


    // Метод для проверки доступности подключения к интернету
    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

    private void addSongInfo(String artist, String songName) {
        // Ваш существующий код для добавления записи в базу данных SQLite
        long currentTime = Calendar.getInstance().getTimeInMillis();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Artist", artist);
        values.put("SongName", songName);
        values.put("AddingTime", currentTime);
        long newRowId = db.insert("SongInfo", null, values);
        if (newRowId != -1) {
            Toast.makeText(this, "Данные успешно добавлены с ID: " + newRowId, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ошибка при добавлении данных", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }
}