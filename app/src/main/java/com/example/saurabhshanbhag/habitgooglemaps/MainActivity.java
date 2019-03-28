package com.example.saurabhshanbhag.habitgooglemaps;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    public static Button getPlacebtn;
    public static TextView place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isServicesOK()) {
            init();
            //getPlace();
        }

    }


    public void getPlace() {
        getPlacebtn = findViewById(R.id.btnMap);
        place = findViewById(R.id.text);

        getPlacebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });
    }


    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation : getting location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {

            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {

                            Log.d(TAG, "OnComplete : found location");
                            Location currentLocation = (Location) task.getResult();
                            JSONParserData dataParser = new JSONParserData(currentLocation.getLatitude(), currentLocation.getLongitude());
                            dataParser.execute();
                            Log.d (TAG,"Current Location : ");

                            JSONParserData dataParser1 = new JSONParserData(currentLocation.getLatitude(), currentLocation.getLongitude());
                            dataParser1.execute();
                            //Log.d (TAG,"Current Location : Types = "+types);
                            //Log.d(TAG,"HUNT LIBRARY : "+types);
                            //Log.d(TAG,"ACE CLASSES : "+getPlaceInfo(18.4998,73.8090).toString());
                            //Log.d(TAG,"JW MARRIOTT : "+getPlaceInfo(18.5319,73.8297).toString());
                            //getPlaceName();
                        } else {
                            Log.d(TAG, "OnComplete : null location");
                            Toast.makeText(MainActivity.this, "Unable to get location.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation : " + e.getMessage());
        }

    }


    public void init() {
        Button btnMap = findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MapActivity.class);
                startActivity(intent);
            }
        });
    }

    public boolean isServicesOK() {
        Log.d(TAG,"isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: Google Play Services running");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesOK: Error occured");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else {
            Toast.makeText(this,"You can't use Maps",Toast.LENGTH_SHORT).show();
        }

        return false;
    }
}
