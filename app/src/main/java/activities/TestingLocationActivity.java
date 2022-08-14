package activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.elijah.ukeme.onlinecampospos.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class TestingLocationActivity extends AppCompatActivity {

    TextView tv_lat, tv_lon, tv_accuracy, tv_speed, tv_altitude, tv_sensor, tv_update, tv_address;
    Switch sw_locationUpdate, sw_gps;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    public static final int PERMISSION_FINE_LOCATION = 99;
    LocationCallback locationCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing_location);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_address = findViewById(R.id.tv_address);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_update = findViewById(R.id.tv_updates);
        sw_gps = findViewById(R.id.sw_gps);
        sw_locationUpdate = findViewById(R.id.sw_locationsupdates);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallback = new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateUI(locationResult.getLastLocation());
            }
        };

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_gps.isChecked()) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS Sensor");
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers + WIFI");
                }
            }
        });

        sw_locationUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_locationUpdate.isChecked()) {
                    startLocationUpdate();
                } else {
                    stopLocationUpdate();
                }
            }
        });
        updateLocation();


    }

    private void stopLocationUpdate() {
        tv_altitude.setText("Location is not Tracked");
        tv_speed.setText("Location is not Tracked");
        tv_accuracy.setText("Location is not Tracked");
        tv_lat.setText("Location is not Tracked");
        tv_lon.setText("Location is not Tracked");
        tv_address.setText("Location is not Tracked");
        tv_update.setText("Location is not Tracked");
        tv_sensor.setText("Location is not Tracked");

        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null );
    }

    private void updateLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    updateUI(location);
                }
            });
        }else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }

    }

    private void updateUI(Location location) {
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));

        if (location.hasSpeed()){
            tv_speed.setText(String.valueOf(location.getSpeed()));
        }else {
            tv_speed.setText("Not Available");
        }
        if (location.hasAltitude()){
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        }else {
            tv_altitude.setText("Not Available");
        }

        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            tv_address.setText(addresses.get(0).getAddressLine(0));
        }catch (Exception ignored){
            tv_address.setText("Unable to get the address");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_FINE_LOCATION:
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    updateLocation();
                }else {
                    Toast.makeText(this, "Permission Request required", Toast.LENGTH_SHORT).show();
                }
        }
    }
}