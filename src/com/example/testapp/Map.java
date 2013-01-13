package com.example.testapp;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Map extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        GoogleMap map = ((SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);
        map.addMarker(new MarkerOptions().position(new LatLng(55.711011, 13.210523)).title("E-Huset"));
        map.addMarker(new MarkerOptions().position(new LatLng(55.709597, 13.210566)).title("M-Huset"));
        map.addMarker(new MarkerOptions().position(new LatLng(55.712402, 13.209150)).title("Kårhuset"));
        
        LocationManager locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
              //makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
          };

        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        
        Location myLoc = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); 
        
        LatLng latLng = new LatLng(myLoc.getLatitude(), myLoc.getLongitude());
        CameraPosition camPosition = new CameraPosition.Builder().target(latLng).zoom(13).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));
    }
}
