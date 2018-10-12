package edu.gatech.donatracker.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.widget.TextView;

import edu.gatech.donatracker.R;
import edu.gatech.donatracker.model.Location;
import edu.gatech.donatracker.model.Model;

public class ViewLocationDetailsActivity extends AppCompatActivity {
    private Model model;
    private Location currentLocation;
    private TextView textViewName;
    private TextView textViewType;
    private TextView textViewLongitude;
    private TextView textViewLatitude;
    private TextView textViewAddress;
    private TextView textViewPhone;
    private TextView textViewCity;
    private TextView textViewState;
    private TextView textViewZip;
    private TextView textViewWebsite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location_details);

        model = Model.getModel();
        currentLocation = model.getCurrentLocation();

        textViewName = findViewById(R.id.location_name);
        textViewType = findViewById(R.id.location_type);
        textViewLongitude = findViewById(R.id.location_longitude);
        textViewLatitude = findViewById(R.id.location_latitude);
        textViewAddress = findViewById(R.id.location_address);
        textViewPhone = findViewById(R.id.location_phone);
        textViewCity = findViewById(R.id.location_city);
        textViewState = findViewById(R.id.location_state);
        textViewZip = findViewById(R.id.location_zip);
        textViewWebsite = findViewById(R.id.location_website);

        if (currentLocation != null) {
            textViewName.setText(currentLocation.getName().toString());
            textViewType.setText(currentLocation.getType().toString());
            textViewLongitude.setText(Double.toString(currentLocation.getLongitude()));
            textViewLatitude.setText(Double.toString(currentLocation.getLatitude()));
            textViewAddress.setText(currentLocation.getAddress().toString());
            textViewPhone.setText(currentLocation.getPhone().toString());
            textViewCity.setText(currentLocation.getCity().toString());
            textViewState.setText(currentLocation.getState().toString());
            textViewZip.setText(Integer.toString(currentLocation.getZip()));
            textViewWebsite.setText(currentLocation.getWebsite().toString());

        }


    }
}
