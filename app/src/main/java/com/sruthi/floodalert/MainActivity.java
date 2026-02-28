package com.sruthi.floodalert;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    // UI elements
    TextView txtLoading, txtCityName, tempText, humidityText, windSpeedText, windDirText, rainText;
    LinearLayout weatherLayout;
    
    // Location client
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        txtLoading = findViewById(R.id.txtLoading);
        txtCityName = findViewById(R.id.txtCityName);
        weatherLayout = findViewById(R.id.dataLayout);

        tempText = findViewById(R.id.txtTemp);
        humidityText = findViewById(R.id.txtHumidity);
        windSpeedText = findViewById(R.id.txtWindSpeed);
        windDirText = findViewById(R.id.txtWindDir);
        rainText = findViewById(R.id.txtRain);

        // Show loading initially
        txtLoading.setVisibility(View.VISIBLE);
        weatherLayout.setVisibility(View.GONE);

        // Initialize Location Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permissions should have been granted in LoginActivity, but double checking here.
            Toast.makeText(this, "Location permission missing", Toast.LENGTH_SHORT).show();
            return;
        }
        
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            fetchWeatherData(location.getLatitude(), location.getLongitude());
                        } else {
                            txtLoading.setText("Unable to find location. Ensure location is enabled.");
                        }
                    }
                });
    }

    private void fetchWeatherData(double lat, double lon) {

        String apiKey = "b71f1e082c91418b2f1cec26bf46a3b1";
        // Update URL to use lat and lon instead of city name
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        String cityName = response.getString("name");
                        
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

                        // Update Location text
                        txtCityName.setText(cityName != null && !cityName.isEmpty() ? cityName : "Current Location");

                        // Update UI
                        tempText.setText(String.format("%.1f °C", tempC));
                        humidityText.setText("Humidity: " + humidity + "%");
                        windSpeedText.setText("Wind: " + windSpeed + " m/s");
                        windDirText.setText("Direction: " + windDeg + "°");
                        rainText.setText("Rain: " + rainValue);

                        // Show data
                        txtLoading.setVisibility(View.GONE);
                        weatherLayout.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        txtLoading.setText("Error loading data parsing");
                    }
                },
                error -> txtLoading.setText("Network error fetching weather")
        );

        queue.add(request);
    }
}