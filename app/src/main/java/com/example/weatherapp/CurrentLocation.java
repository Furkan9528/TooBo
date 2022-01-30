package com.example.weatherapp;
import static android.webkit.ConsoleMessage.MessageLevel.LOG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.loader.content.AsyncTaskLoader;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Path;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weatherapp.Common.Common;
import com.example.weatherapp.Common.Common2;
import com.example.weatherapp.Helper.Helper;
import com.example.weatherapp.Model.Coord;
import com.example.weatherapp.Model.Main;
import com.example.weatherapp.Model.OpenWeatherMap;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoProvider;

import java.lang.reflect.Type;

public class CurrentLocation extends AppCompatActivity implements LocationListener {

    TextView txtCity, txtLastUpdate, txtHumidity, txtDeg, txtDescription, txtTemp_min, txtTemp_max, txtSunrise, txtSunset, txtWind, txtPressure;
    ImageView imageView;
    TextView usernameHeader;
    TextView emailHeader;
    int MY_PERMISSION = 0;
    LocationManager locationManager;
    String provider;



    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    OpenWeatherMap openWeatherMap = new OpenWeatherMap();


    public static double lat,lon;
    Coord latt, lonn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_location);
        drawerLayout = findViewById(R.id.current_location);

        navigationView = findViewById(R.id.navigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.menu_Open,R.string.close_menu);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtCity = (TextView) findViewById(R.id.text_city);
        txtLastUpdate = (TextView) findViewById(R.id.text_lastUpdate);
        txtHumidity = (TextView) findViewById(R.id.humidity);
        txtDeg = (TextView) findViewById(R.id.text_deg);
        txtDescription = (TextView) findViewById(R.id.text_description);
        imageView = (ImageView) findViewById(R.id.idImage);

        txtTemp_min = (TextView) findViewById(R.id.temp_min);
        txtTemp_max = (TextView) findViewById(R.id.temp_max);
        txtSunrise = (TextView) findViewById(R.id.sunrise);
        txtSunset = (TextView) findViewById(R.id.sunset);
        txtPressure = (TextView) findViewById(R.id.pressure);
        txtWind = (TextView) findViewById(R.id.wind);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(CurrentLocation.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSION);
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location == null)
            Log.e("TAG", "No location");

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CurrentLocation.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSION);
        }
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CurrentLocation.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSION);
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        lat = latt.getLat();
        lon = lonn.getLon();

        new GetWeather().execute(Common2.apiRequest(String.valueOf(lat), String.valueOf(lon)));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    private class GetWeather extends AsyncTask<String, Void, String> {

        ProgressDialog pd = new ProgressDialog(CurrentLocation.this);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setTitle("Please wait ...");
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String stream = null;
            String urlString = params[0];
            Helper http = new Helper();
            stream = http.getHTTPData(urlString);
            return stream;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Gson gson=new Gson();
            Type mType= new TypeToken<OpenWeatherMap>(){}.getType();
            openWeatherMap=gson.fromJson(s, mType);
            pd.dismiss();


            if(Common2.temp == Main.Temp.METRIC) {
                txtCity.setText(String.format("%s,%s", openWeatherMap.getName(), openWeatherMap.getSys().getCountry()));
                txtLastUpdate.setText(String.format("%s", Common2.getDateNow()));
                txtDescription.setText(String.format("%s", openWeatherMap.getWeather().get(0).getDescription()));
                txtHumidity.setText(String.format("%d %%", openWeatherMap.getMain().getHumidity()));
                txtSunrise.setText(String.format("%.7s AM", Common2.unixTimeStampToDateTime(openWeatherMap.getSys().getSunrise())));
                txtSunset.setText(String.format("%.7s PM", Common2.unixTimeStampToDateTime(openWeatherMap.getSys().getSunset())));
                txtDeg.setText(String.format("%.0f °C", openWeatherMap.getMain().getTemp()));
                txtTemp_min.setText(String.format("Min temp: %.2f °C", openWeatherMap.getMain().getTemp_min()));
                txtTemp_max.setText(String.format("Max temp: %.2f °C", openWeatherMap.getMain().getTemp_max()));
                txtWind.setText(String.format("%.2f km/h", openWeatherMap.getWind().getSpeed()));
                txtPressure.setText(String.format("%.1f hPa", openWeatherMap.getMain().getPressure()));
            }
            else{
                txtCity.setText(String.format("%s,%s",openWeatherMap.getName(),openWeatherMap.getSys().getCountry()));
                txtLastUpdate.setText(String.format("%s", Common2.getDateNow()));
                txtDescription.setText(String.format("%s",openWeatherMap.getWeather().get(0).getDescription()));
                txtHumidity.setText(String.format("%d %%",openWeatherMap.getMain().getHumidity()));
                txtSunrise.setText(String.format("%.7s AM",Common2.unixTimeStampToDateTime(openWeatherMap.getSys().getSunrise())));
                txtSunset.setText(String.format("%.7s PM",Common2.unixTimeStampToDateTime(openWeatherMap.getSys().getSunset())));
                txtDeg.setText(String.format("%.0f °F",openWeatherMap.getMain().getTemp()));
                txtTemp_min.setText(String.format("Min temp: %.2f °F",openWeatherMap.getMain().getTemp_min()));
                txtTemp_max.setText(String.format("Max temp: %.2f °F",openWeatherMap.getMain().getTemp_max()));
                txtWind.setText(String.format("%.2f m/h",openWeatherMap.getWind().getSpeed()));
                txtPressure.setText(String.format("%.1f hPa",openWeatherMap.getMain().getPressure()));
            }

            Picasso.get()
                    .load(Common.getImage(openWeatherMap.getWeather().get(0).getIcon()))
                    .into(imageView);
        }
    }
}