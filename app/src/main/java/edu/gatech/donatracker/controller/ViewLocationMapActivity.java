package edu.gatech.donatracker.controller;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

import edu.gatech.donatracker.R;

/**
 * Created by Qiusen Huang on 2018/11/02
 * <p>
 * Displays on Google Map the Location
 */
public class ViewLocationMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private double Lat;
    private double Lng;
    private String Name;
    private String Phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            Lat = extra.getDouble("Lat");
            Lng = extra.getDouble("Lng");
            Name = extra.getString("Name");
            Phone = extra.getString("TEL");

        }
        setContentView(R.layout.activity_view_location_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);
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
    public void onMapReady(GoogleMap googleMap) {

        // Add a marker in Sydney and move the camera
        LatLng mLocationCord = new LatLng(Lat, Lng);
        googleMap.addMarker(new MarkerOptions().position(mLocationCord).title(Phone).snippet(Name));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(mLocationCord));
    }
}
