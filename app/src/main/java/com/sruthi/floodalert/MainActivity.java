package com.sruthi.floodalert;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    // UI elements
    TextView txtLoading, tempText, humidityText, windSpeedText, windDirText, rainText;
    LinearLayout weatherLayout;
    EditText edtCity;
    Button btnSearch;

    String currentCity = "Chennai"; // default city

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        txtLoading = findViewById(R.id.txtLoading);
        weatherLayout = findViewById(R.id.dataLayout);

        tempText = findViewById(R.id.txtTemp);
        humidityText = findViewById(R.id.txtHumidity);
        windSpeedText = findViewById(R.id.txtWindSpeed);
        windDirText = findViewById(R.id.txtWindDir);
        rainText = findViewById(R.id.txtRain);

        edtCity = findViewById(R.id.edtCity);
        btnSearch = findViewById(R.id.btnSearch);

        // Show loading initially
        txtLoading.setVisibility(View.VISIBLE);
        weatherLayout.setVisibility(View.GONE);

        // Load default city weather
        fetchWeatherData(currentCity);

        // Search button click
        btnSearch.setOnClickListener(v -> {
            String cityInput = edtCity.getText().toString().trim();

            if (!cityInput.isEmpty()) {
                currentCity = cityInput;
                txtLoading.setVisibility(View.VISIBLE);
                weatherLayout.setVisibility(View.GONE);
                fetchWeatherData(currentCity);
            } else {
                edtCity.setError("Enter city name");
            }
        });
    }

    private void fetchWeatherData(String city) {

        String apiKey = "b71f1e082c91418b2f1cec26bf46a3b1";
        String url = "https://api.openweathermap.org/data/2.5/weather?q="
                + city + "&appid=" + apiKey;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONObject main = response.getJSONObject("main");
                        double tempC = main.getDouble("temp") - 273.15;
                        int humidity = main.getInt("humidity");

                        JSONObject wind = response.getJSONObject("wind");
                        double windSpeed = wind.getDouble("speed");
                        double windDeg = wind.getDouble("deg");

                        String rainValue = "No Rain";
                        if (response.has("rain")) {
                            JSONObject rain = response.getJSONObject("rain");
                            if (rain.has("1h")) {
                                rainValue = rain.getString("1h") + " mm";
                            }
                        }

                        // Update UI
                        tempText.setText(String.format("Temperature: %.1f °C", tempC));
                        humidityText.setText("Humidity: " + humidity + " %");
                        windSpeedText.setText("Wind Speed: " + windSpeed + " m/s");
                        windDirText.setText("Wind Direction: " + windDeg + "°");
                        rainText.setText("Rain: " + rainValue);

                        // Show data
                        txtLoading.setVisibility(View.GONE);
                        weatherLayout.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        txtLoading.setText("Error loading data");
                    }
                },
                error -> txtLoading.setText("City not found / Network error")
        );

        queue.add(request);
    }
}