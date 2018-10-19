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
    }

    @Override
    public void onStart() {
        super.onStart();
        // Fill text fields with current Location info
        if (currentDonation != null) {
            Log.d("Debug", "The current donation being showed: " + currentDonation.toString());
            //TODO don't do string append
            textViewDonationTime.setText("Donation Time: " + currentDonation.getDonationTime().toString());
            textViewDonationLocation.setText("Donation Location: " + currentDonation.getDonationLocation().toString());
            textViewFullDescription.setText("Full Description: " + currentDonation.getFullDescription());
            textViewValueInUSD.setText("Value in USD: " + Double.toString(currentDonation.getValueInUSD()));
            textViewComments.setText("Comments: "+ currentDonation.getComments());
            //TODO implement picture later on
//            textViewPicture.append(currentDonation.get());
        }
        //TODO: handle situation where loading location info fails
    }
    public void onClickEditDonation(View view) {
        Context context = view.getContext();
        Intent intent = new Intent(context, EditDonationDetailActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
