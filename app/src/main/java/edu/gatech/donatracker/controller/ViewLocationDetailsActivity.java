package edu.gatech.donatracker.controller;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.gatech.donatracker.R;
import edu.gatech.donatracker.model.Location;
import edu.gatech.donatracker.model.user.User;

/**
 * Created by Qiusen Huang on 2018/10/11
 */
public class ViewLocationDetailsActivity extends AppCompatActivity {

    public static final String TAG = "ViewLocationDetailsActivity.class";
    // Models
    private Location currentLocation;
    private FirebaseUser firebaseUser;
    private User user;
    private FirebaseFirestore database;


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
    private Button buttonMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location_details);

        currentLocation = getIntent().getParcelableExtra("Location Passed");
        user = getIntent().getParcelableExtra("User Model");

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
        buttonMap = findViewById(R.id.view_location_map);

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

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    public void onClickViewDonation(View view) {
        if (user.getUserType() == User.UserType.DEVELOPER || user.getUserType() == User.UserType.LOCATION_EMPLOYEE
                || user.getUserType() == User.UserType.MANAGER) {
            Intent intent = new Intent(ViewLocationDetailsActivity.this, ViewDonationsActivity.class);
            intent.putExtra("User Model", (Parcelable) user);
            intent.putExtra("Current Location", currentLocation);
            startActivity(intent);
        } else {
            Log.d(TAG, "View inventory access denied");
            Toast.makeText(getApplicationContext(), "You do not have permission to view the inventory.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickViewMap(View view) {
        Intent intent = new Intent(ViewLocationDetailsActivity.this, ViewLocationMapActivity.class);
        intent.putExtra("Lng", currentLocation.getLongitude());
        intent.putExtra("Lat", currentLocation.getLongitude());
        intent.putExtra("Name", currentLocation.getName());
        intent.putExtra("TEL", currentLocation.getPhone());
        startActivity(intent);
    }

}
