package edu.gatech.donatracker.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import edu.gatech.donatracker.R;
import edu.gatech.donatracker.model.Donation;
import edu.gatech.donatracker.model.Location;
import edu.gatech.donatracker.model.Model;

public class ViewDonationDetailsActivity extends AppCompatActivity {
    // Models
    private Model model;
    private Donation currentDonation;

    // UI References
    private TextView textViewDonationTime;
    private TextView textViewDonationLocation;
    private TextView textViewFullDescription;
    private TextView textViewValueInUSD;
    private TextView textViewComments;
//    private TextView textViewPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_donation_details);

        // Initiate Models
        model = Model.getModel();
        currentDonation = model.getCurrentDonation();

        // Initiate UI References
        textViewDonationTime = findViewById(R.id.donation_time);
        textViewDonationLocation = findViewById(R.id.donation_location);
        textViewFullDescription = findViewById(R.id.full_description);
        textViewValueInUSD = findViewById(R.id.value_in_USD);
        textViewComments = findViewById(R.id.comments);
        //TODO not a text view, find how to inset image
        //textViewPicture = findViewById(R.id.picture);

//        // Fill text fields with current Location info
//        if (currentDonation != null) {
//            Log.d("Debug", "The current donation being showed: " + currentDonation.toString());
//            textViewDonationTime.append(currentDonation.getDonationTime().toString());
//            textViewDonationLocation.append(currentDonation.getDonationLocation().toString());
//            textViewFullDescription.append(currentDonation.getFullDescription());
//            textViewValueInUSD.append(Double.toString(currentDonation.getValueInUSD()));
//            textViewComments.append(currentDonation.getComments());
//            //TODO implement picture later on
////            textViewPicture.append(currentDonation.get());
//        }
        //TODO: handle situation where loading location info fails
    }

    @Override
    public void onStart() {
        super.onStart();
        // Fill text fields with current Location info
        if (currentDonation != null) {
            Log.d("Debug", "The current donation being showed: " + currentDonation.toString());
            textViewDonationTime.append(currentDonation.getDonationTime().toString());
            textViewDonationLocation.append(currentDonation.getDonationLocation().toString());
            textViewFullDescription.append(currentDonation.getFullDescription());
            textViewValueInUSD.append(Double.toString(currentDonation.getValueInUSD()));
            textViewComments.append(currentDonation.getComments());
            //TODO implement picture later on
//            textViewPicture.append(currentDonation.get());
        }
    }
    public void onClickEditDonation(View view) {
        Context context = view.getContext();
        Intent intent = new Intent(context, EditDonationDetailActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
