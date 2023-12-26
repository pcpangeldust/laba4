package com.example.laba4;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import java.util.Calendar;

public class TrackInfoUpdater extends AsyncTask<Void, Void, String> {

    private TextView trackInfoTextView;
    private DBHelper dbHelper;

    public TrackInfoUpdater(TextView trackInfoTextView, DBHelper dbHelper) {
        this.trackInfoTextView = trackInfoTextView;
        this.dbHelper = dbHelper;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String result = "";
        try {
            // Формируем URL для запроса
            URL url = new URL("https://media.itmo.ru/api_get_current_song.php");

            // Создаем HttpsURLConnection для отправки запроса
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            // Устанавливаем параметры логина и пароля
            String login = "4707login";
            String password = "4707pass";
            String postData = "login=" + login + "&password=" + password;
            urlConnection.getOutputStream().write(postData.getBytes());

            // Получаем ответ от сервера
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }

            // Закрываем соединение и потоки
            inputStream.close();
            urlConnection.disconnect();

        } catch (IOException e) {
        e.printStackTrace();
    } catch (Exception e) {
        Log.e("DO_IN_BACKGROUND_ERROR", "Error in doInBackground: " + e.getMessage());
        e.printStackTrace();
    }
    return result;
}

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            String info = jsonObject.getString("info");

            if (!isSongAlreadyAdded(info)) {
                addSongInfo(info);
            }

            trackInfoTextView.setText(info);
        } catch (JSONException e) {
            Log.e("JSON_ERROR", "Error with JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isSongAlreadyAdded(String songName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query("SongInfo",
                    null,
                    "NameOfTheSong = ?",
                    new String[]{songName},
                    null,
                    null,
                    null);
            return cursor != null && cursor.getCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return false;
    }

    private void addSongInfo(String songName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("NameOfTheSong", songName);
        values.put("AddingTime", Calendar.getInstance().getTimeInMillis());
        long newRowId = db.insert("SongInfo", null, values);
        if (newRowId != -1) {
            Log.d("DATABASE", "Data added successfully with ID: " + newRowId);
        } else {
            Log.e("DATABASE", "Error adding data");
        }
        db.close();
    }
}
