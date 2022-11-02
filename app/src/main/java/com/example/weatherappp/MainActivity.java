package com.example.weatherappp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText user_field;
    private Button main_button;
    private TextView result_info;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_field = findViewById(R.id.user_field);
        main_button = findViewById(R.id.main_button);
        result_info = findViewById(R.id.result_info);

        main_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user_field.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, R.string.no_user_input, Toast.LENGTH_LONG).show();
                //кожен раз, коли користувач буде нажимати на кнопку, буде перевірятись чи ввів користувач данні в поле
                //якщо данні не введені, то буде спрацьовувати умова і буде показана підсказка "введіть текст"
                // якщо данні введені,буде спрацьовувати оператор else і буде запит на сервис, через який ми будемо дізнаватись погоду
                else {
                    String city = user_field.getText().toString();
                    String key ="edf4485f7af5947bf27ab297b8dbc9ba";
                    String url ="https://api.openweathermap.org/data/2.5/weather?q=" +city + "&appid=" + key + "&units=metric&lang=ua";
                    //повноцінна силлка, в яку в динамічному форматі буде підставлятись ключ API і запитуванне місто

                    new GetURLDate().execute(url);

                }
            }
        });

    }
    //створюємо клас, який буде відправляти запрос по URL адресу

    private class GetURLDate extends AsyncTask<String, String, String> {


        //метод, спарцюває коли ми відправляємо данні по URL адерсу
        protected void onPreExecute() {
            super.onPreExecute();
            result_info.setText("Очікуйте..");

        }
        @Override
        //тут ми будемо зсилатись на юрл адрес і будемо отримувати всю інформацію по ньому
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                //цей клас дозволяє нам зчитувати данні з url
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line ="";

                while((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null)
                    connection.disconnect();
                //цей блок спрацьовує в любому випадку і тут закриваються різні звьязки, для того щоб не перенагружати програмку

                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                result_info.setText("Температура:" + jsonObject.getJSONObject("main").getDouble("temp"));
                result_info.setText(result_info.getText() + "\nВідчувається як: "  + jsonObject.getJSONObject("main").getDouble("feels_like"));
                result_info.setText(result_info.getText() + "\nМінімальна температура:" + jsonObject.getJSONObject("main").getDouble("temp_min"));
                result_info.setText(result_info.getText() + "\nМаксимальна температура:" + jsonObject.getJSONObject("main").getDouble("temp_max"));
                result_info.setText(result_info.getText() + "\nНебо:" + jsonObject.getJSONObject("weather").getDouble("description"));



            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

}
