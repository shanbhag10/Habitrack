package com.example.saurabhshanbhag.habitgooglemaps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapActivity extends AppCompatActivity {



    private static final String fine = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String course = Manifest.permission.ACCESS_COARSE_LOCATION;

    private static final String TAG = "MapActivity";
    private static final float DEFAULT_ZOOM = 16.5f;

    public static String types = "";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getLocationPermission();

        back();
    }

    public void back() {
        Button btnMap = findViewById(R.id.backbtn);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapActivity.this,MainActivity.class);
                startActivity(intent);
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
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                            JSONParserData dataParser = new JSONParserData(currentLocation.getLatitude(), currentLocation.getLongitude());
                            dataParser.execute();
                            Log.d (TAG,"Current Location : "+types);
                            Toast.makeText(MapActivity.this,types,Toast.LENGTH_SHORT).show();

                            //JSONParserData dataParser1 = new JSONParserData(currentLocation.getLatitude(), currentLocation.getLongitude());
                            //dataParser1.execute();
                            //Log.d (TAG,"Current Location : Types = "+types);
                            //Log.d(TAG,"HUNT LIBRARY : "+types);
                            //Log.d(TAG,"ACE CLASSES : "+getPlaceInfo(18.4998,73.8090).toString());
                            //Log.d(TAG,"JW MARRIOTT : "+getPlaceInfo(18.5319,73.8297).toString());
                            //getPlaceName();
                        } else {
                            Log.d(TAG, "OnComplete : null location");
                            Toast.makeText(MapActivity.this, "Unable to get location.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation : " + e.getMessage());
        }

    }




/*    private Address getAddress (double latitude, double longitude) {

        Address place = null;

        Log.d(TAG, "getPlaceInfo : Current Location = "+latitude+" , "+longitude);

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocation(latitude,longitude,1);
            Log.d (TAG, "getPlaceInfo : getting place");
        }catch (Exception e) {
            Log.e(TAG, "getPlaceInfo : "+e.getMessage());
        }

        if (list.size() > 0) {
            place = list.get(0);
        }

        return place;
    }*/


    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera : moving camera to latitude : " + latLng.latitude + " and longitude : " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                //Toast.makeText(MapActivity.this, "Map is Ready", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "initMap : Map is Ready");
                mMap = googleMap;

                if (mLocationPermissionGranted) {
                    getDeviceLocation();

                    if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                }
            }
        });
    }

    protected void getLocationPermission() {

        Log.d(TAG,"getLocationPermission : getting permissions");
        String[] permissions = {fine,course};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),fine) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),course) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            }
            else {
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else {
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG,"onRequestPermissionsResult : called");
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {

                    for (int i =0;i<grantResults.length;i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d(TAG,"onRequestPermissionsResult : permission failed");
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    Log.d(TAG,"onRequestPermissionsResult : permission granted");
                    initMap();
                }
            }
        }
    }

    public static void setText(String text) {
        types = text;
    }
}
