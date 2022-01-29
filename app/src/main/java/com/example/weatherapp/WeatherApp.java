package com.example.weatherapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.weatherapp.Common.Common;
import com.example.weatherapp.Helper.Helper;
import com.example.weatherapp.Model.Main;
import com.example.weatherapp.Model.OpenWeatherMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.WeakHashMap;

public class WeatherApp extends AppCompatActivity implements LocationListener {

    TextView txtCity, txtLastUpdate, txtHumidity, txtDeg, txtDescription, txtTemp_min, txtTemp_max, txtSunrise, txtSunset, txtWind, txtPressure;
    ImageView imageView;
    TextView usernameHeader;
    TextView emailHeader;
    int MY_PERMISSION = 0;
    LocationManager locationManager;
    String provider;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private String userID;


    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    OpenWeatherMap openWeatherMap = new OpenWeatherMap();

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.activity_main);
        navigationView = findViewById(R.id.navigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.menu_Open,R.string.close_menu);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.nav_home:
                        Log.i("MENU_DRAWER_TAG", "Home item is clicked");
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.current_location:
                        Log.i("MENU_DRAWER_TAG", "Home item is clicked");
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.info:
                        startActivity(new Intent(WeatherApp.this, ProfileActivity.class));
                        break;

                    case R.id.settings:
                        startActivity(new Intent(WeatherApp.this, InfoActivity.class ));
                        break;

                    case R.id.notifications:
                        AlertDialog alertDialog=new AlertDialog.Builder(WeatherApp.this).create();
                        alertDialog.setTitle("Notifications");
                        alertDialog.setMessage("Enabled");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                        break;

                    case R.id.signOut:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(WeatherApp.this, MainActivity.class));
                        break;
                }
                return true;
            }


        });


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


        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(WeatherApp.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSION);
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if(location == null)
            Log.e("TAG","No location");


        updateNavHeader();

    }



   public void updateNavHeader() {

       navigationView = findViewById(R.id.navigationView);
       View headerView = navigationView.getHeaderView(0);

       usernameHeader = headerView.findViewById(R.id.header_title);
       emailHeader = headerView.findViewById(R.id.email_title);

       usernameHeader.setText(user.getDisplayName());
       emailHeader.setText(user.getEmail());

   }


    @Override
    protected void onPause() {
        super.onPause();
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(WeatherApp.this,new String[]{
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
            ActivityCompat.requestPermissions(WeatherApp.this, new String[]{
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

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null) {
                    String city = userProfile.city;
                    new GetWeather().execute(Common.apiRequest(String.valueOf(city)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeatherApp.this, "Something wrong happened!", Toast.LENGTH_LONG ).show();
            }
        });
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

    private class GetWeather extends AsyncTask<String,Void,String> {

        ProgressDialog pd= new ProgressDialog(WeatherApp.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setTitle("Please wait ...");
            pd.show();
        }

        @Override
        protected String doInBackground(String...params){
            String stream=null;
            String urlString=params[0];
            Helper http=new Helper();
            stream=http.getHTTPData(urlString);
            return stream;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Gson gson=new Gson();
            Type mType= new TypeToken<OpenWeatherMap>(){}.getType();
            openWeatherMap=gson.fromJson(s, mType);
            pd.dismiss();



            if(Common.temp == Main.Temp.METRIC) {
                txtCity.setText(String.format("%s,%s", openWeatherMap.getName(), openWeatherMap.getSys().getCountry()));
                txtLastUpdate.setText(String.format("%s", Common.getDateNow()));
                txtDescription.setText(String.format("%s", openWeatherMap.getWeather().get(0).getDescription()));
                txtHumidity.setText(String.format("%d %%", openWeatherMap.getMain().getHumidity()));
                txtSunrise.setText(String.format("%.7s AM", Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunrise())));
                txtSunset.setText(String.format("%.7s PM", Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunset())));
                txtDeg.setText(String.format("%.0f °C", openWeatherMap.getMain().getTemp()));
                txtTemp_min.setText(String.format("Min temp: %.2f °C", openWeatherMap.getMain().getTemp_min()));
                txtTemp_max.setText(String.format("Max temp: %.2f °C", openWeatherMap.getMain().getTemp_max()));
                txtWind.setText(String.format("%.2f km/h", openWeatherMap.getWind().getSpeed()));
                txtPressure.setText(String.format("%.1f hPa", openWeatherMap.getMain().getPressure()));
            }
            else{
                txtCity.setText(String.format("%s,%s",openWeatherMap.getName(),openWeatherMap.getSys().getCountry()));
                txtLastUpdate.setText(String.format("%s", Common.getDateNow()));
                txtDescription.setText(String.format("%s",openWeatherMap.getWeather().get(0).getDescription()));
                txtHumidity.setText(String.format("%d %%",openWeatherMap.getMain().getHumidity()));
                txtSunrise.setText(String.format("%.7s AM",Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunrise())));
                txtSunset.setText(String.format("%.7s PM",Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunset())));
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
