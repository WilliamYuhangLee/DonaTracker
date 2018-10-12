package edu.gatech.donatracker.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.widget.TextView;

import edu.gatech.donatracker.R;
import edu.gatech.donatracker.model.Location;
import edu.gatech.donatracker.model.Model;

/**
 * Created by Qiusen Huang on 2018/10/11
 */
public class ViewLocationDetailsActivity extends AppCompatActivity {

    // Models
    private Model model;
    private Location currentLocation;

    // UI References
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

        // Initiate Models
        model = Model.getModel();
        currentLocation = model.getCurrentLocation();

        // Initiate UI References
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

        // Fill text fields with current Location info
        if (currentLocation != null) {
            textViewName.append(currentLocation.getName());
            textViewType.append(currentLocation.getType());
            textViewLongitude.append(Double.toString(currentLocation.getLongitude()));
            textViewLatitude.append(Double.toString(currentLocation.getLatitude()));
            textViewAddress.append(currentLocation.getAddress());
            textViewPhone.append(currentLocation.getPhone());
            textViewCity.append(currentLocation.getCity());
            textViewState.append(currentLocation.getState());
            textViewZip.append(Integer.toString(currentLocation.getZip()));
            textViewWebsite.append(currentLocation.getWebsite());
        }
        //TODO: handle situation where loading location info fails
    }
}
