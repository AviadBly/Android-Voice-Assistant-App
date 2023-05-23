package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherActivity extends AppCompatActivity {

    private TextView temp;
    private TextView descript;
    private TextView loc;
    private TextView humid;
    private TextView pressure;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        temp=findViewById(R.id.temperature_text_view);
        descript=findViewById(R.id.description_text_view);
        loc=findViewById(R.id.location_text_view);
        humid=findViewById(R.id.humidity_text_view);
        pressure=findViewById(R.id.pressure_text_view);
        getWeatherData();
    }
    public  void BackClick(View v){
        Intent intent= new Intent(this, MainActivity.class);

        startActivity(intent);
        finish();
    }

    private void getWeatherData() {
        Intent intent = getIntent();
        String cityName = intent.getStringExtra("cityName");
        String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + ",IL&appid=fe4e8a149e81511f74773c4bf7edd41b&units=metric";

        // Create a new thread to make the network request
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();

                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        // Read the response data
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder result = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        reader.close();
                        inputStream.close();

                        // Parse the JSON data
                        JSONObject json = new JSONObject(result.toString());

                        // Update the UI with the data on the main thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateUI(json);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateUI(JSONObject weatherData) {

        Log.i("","enter to update");
        WeatherData data = new WeatherData();

        try {
            if (weatherData.has("name")) {
                data.setLocation(weatherData.getString("name"));

            }
            // Check for the presence of the keys
            if (weatherData.has("main")) {
                JSONObject main= weatherData.getJSONObject("main");
                data.setTemperature(main.getDouble("temp"));
                data.setHumidity(main.getDouble("humidity"));
                data.setPressure(main.getDouble("pressure"));
            }
            if (weatherData.has("weather")) {
                String weatherInfo = weatherData.getString("weather");
                Log.d("weatherInfo: ",weatherInfo);
                //data.setDescription(weather.getString("description"));
                JSONArray arr = new JSONArray(weatherInfo);

                for (int i = 0; i < arr.length(); i++) {

                    JSONObject jsonPart = arr.getJSONObject(i);
                    data.setDescription(jsonPart.getString("description"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        String tempy= "Temperature: "+String.format("%.1f°", data.getTemperature());
        String descy= "Description: "+data.getDescription();
        String locy= "Now showing data of city: "+data.getLocation();
        String humidityy= "Humidity: "+data.getHumidity();
        String pressury= "Pressure: "+String.valueOf(data.getPressure());
        // Set the values in the TextViews
        temp.setText(tempy);
        descript.setText(descy);
        loc.setText(locy);
        humid.setText(humidityy);
        pressure.setText(pressury);


        // Set the weather icon
    }

}
