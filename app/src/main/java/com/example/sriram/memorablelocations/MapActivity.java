package com.example.sriram.memorablelocations;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    LocationManager locationManager;

    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,10,locationListener);
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng newPosition) {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                Intent intent = getIntent();
                    try {
                        List<Address> addressList = geocoder.getFromLocation(newPosition.latitude, newPosition.longitude, 1);
                        if (addressList.get(0).getAddressLine(0) != null) {
                            googleMap.addMarker(new MarkerOptions().position(newPosition).title(addressList.get(0).getAddressLine(0)));
                            MainActivity.memorablePlaces.add(addressList.get(0).getAddressLine(0));
                            MainActivity.arrayAdapter.notifyDataSetChanged();
                            Toast.makeText(MapActivity.this, "Location Saved", Toast.LENGTH_SHORT).show();
                        } else {
                            googleMap.addMarker(new MarkerOptions().position(newPosition).title(newPosition.latitude + ", " + newPosition.longitude));
                            MainActivity.memorablePlaces.add(newPosition.latitude + ", " + newPosition.longitude);
                            MainActivity.arrayAdapter.notifyDataSetChanged();
                            Toast.makeText(MapActivity.this, "Location Saved", Toast.LENGTH_SHORT).show();
                        }
                        MainActivity.Latitude.add(newPosition.latitude);
                        MainActivity.Longitude.add(newPosition.longitude);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        });

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                Intent intent = getIntent();
                if (intent.getIntExtra("placeNumber", -1) == 0) {
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (addresses.get(0).getAddressLine(0) != null) {
                            mMap.clear();
                            LatLng userPosition = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(userPosition).title(addresses.get(0).getAddressLine(0)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 10));
                            MainActivity.memorablePlaces.add(addresses.get(0).getAddressLine(0));
                            MainActivity.arrayAdapter.notifyDataSetChanged();
                            Toast.makeText(MapActivity.this, "Location Saved", Toast.LENGTH_SHORT).show();
                        } else {
                            LatLng userPosition = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(userPosition).title(Double.toString(location.getLatitude()) + ", " + Double.toString(location.getLongitude())));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 10));
                            MainActivity.memorablePlaces.add(location.getLatitude() + ", " + location.getLongitude());
                            MainActivity.arrayAdapter.notifyDataSetChanged();
                            Toast.makeText(MapActivity.this, "Location Saved", Toast.LENGTH_SHORT).show();
                        }
                        MainActivity.Latitude.add(location.getLatitude());
                        MainActivity.Longitude.add(location.getLongitude());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                    LatLng latLng = new LatLng(MainActivity.Latitude.get(intent.getIntExtra("placeNumber", -1)), MainActivity.Longitude.get(intent.getIntExtra("placeNumber", -1)));

                    mMap.addMarker(new MarkerOptions().position(latLng).title(MainActivity.memorablePlaces.get(intent.getIntExtra("placeNumber", -1))));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,10,locationListener);
        }
    }
}
