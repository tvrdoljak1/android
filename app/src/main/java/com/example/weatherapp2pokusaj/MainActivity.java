package com.example.weatherapp2pokusaj;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import static java.lang.Math.round;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    EditText editText;
    Button button;
    ImageView imageView;
    TextView country_yt, city_yt, temp_yt, longitude, latitude, humidity, sunrise, sunset, pressure, wind, country, city_nam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editTextTextPersonName);
        button = findViewById(R.id.button);
        country_yt = findViewById(R.id.country);
        city_yt = findViewById(R.id.city);
        temp_yt = findViewById(R.id.textView3);
        imageView = findViewById(R.id.imageView);

        longitude = findViewById(R.id.Longitude);
        latitude = findViewById(R.id.Latitude);
        humidity = findViewById(R.id.Humidity);
        sunrise = findViewById(R.id.Sunrise);
        sunset = findViewById(R.id.Sunset);
        pressure = findViewById(R.id.Pressure);
        wind = findViewById(R.id.WindSpeed);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findWeather(null);
            }
        });
    }

    public void findWeather(String locality) {
        String city;
        if (locality == null) {
            city = editText.getText().toString();
        } else {
            city = locality;
        }
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=3065cdda9f2fc274807469282f32fd0a";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);


                    JSONObject object1 = jsonObject.getJSONObject("sys");
                    String country_find = object1.getString("country");
                    country_yt.setText(country_find);

                    String city_find = jsonObject.getString("name");
                    city_yt.setText(city_find);

                    JSONObject object2 = jsonObject.getJSONObject("main");
                    double temp_find = object2.getDouble("temp");
                    temp_find = round(temp_find - 273.15);
                    temp_yt.setText(temp_find + " \u2103");

                    JSONArray jsonArray = jsonObject.getJSONArray("weather");
                    JSONObject obj = jsonArray.getJSONObject(0);
                    String icon = obj.getString("icon");

                    Picasso.get().load("http://openweathermap.org/img/wn/" + icon + "@2x.png").into(imageView);

                    JSONObject object3 = jsonObject.getJSONObject("coord");
                    double lat_find = object3.getDouble("lat");
                    latitude.setText(lat_find + "");

                    JSONObject object4 = jsonObject.getJSONObject("coord");
                    double long_find = object4.getDouble("lon");
                    longitude.setText(long_find + "");

                    JSONObject object5 = jsonObject.getJSONObject("main");
                    int humidity_find = object5.getInt("humidity");
                    humidity.setText(humidity_find + "");

                    JSONObject object6 = jsonObject.getJSONObject("sys");
                    String sunrise_find = object6.getString("sunrise");
                    sunrise.setText(sunrise_find);

                    JSONObject object7 = jsonObject.getJSONObject("sys");
                    String sunset_find = object7.getString("sunset");
                    sunset.setText(sunset_find);

                    JSONObject object8 = jsonObject.getJSONObject("main");
                    String pressure_find = object8.getString("pressure");
                    pressure.setText(pressure_find + " hPa");


                    JSONObject object9 = jsonObject.getJSONObject("wind");
                    String wind_find = object9.getString("speed");
                    wind.setText(wind_find + " hm/h");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }

    //      LOCATION
    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        Location location = getLastKnownLocation();
        if (location != null) {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (listAddresses != null && listAddresses.size() > 0) {
                    String locality = listAddresses.get(0).getLocality();
                    Log.i("INFO", locality);

                    findWeather(locality);
                    editText.setText(locality);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(MainActivity.this, "Can't get location", Toast.LENGTH_LONG).show();
        }
    }

    private Location getLastKnownLocation() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    public void clickLocation(View v) {
        getLocation();
    }
}