package edu.gatech.donatracker.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

import edu.gatech.donatracker.R;
import edu.gatech.donatracker.database.FirebaseManager;
import edu.gatech.donatracker.model.Donation;
import edu.gatech.donatracker.model.Model;

public class ViewDonationDetailsActivity extends AppCompatActivity {
    private static final String TAG = "ViewDonationDetailsActivity.class";
    // Models
    private Model model;
    private Donation currentDonation;
    private DocumentReference donationDocRef;


    // UI References
    private TextView textViewDonationTime;
    private TextView textViewDonationLocation;
    private TextView textViewFullDescription;
    private TextView textViewValueInUSD;
    private TextView textViewComments;
    private TextView textViewCategory;
//    private TextView textViewPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_donation_details);

        // Initiate Models
        currentDonation = getIntent().getParcelableExtra("Donation");
        donationDocRef = FirebaseFirestore.getInstance().collection("donations").document(currentDonation.getUuid());
        currentDonation = null;
        // Initiate UI References
        textViewDonationTime = findViewById(R.id.donation_time);
        textViewDonationLocation = findViewById(R.id.donation_location);
        textViewFullDescription = findViewById(R.id.full_description);
        textViewValueInUSD = findViewById(R.id.value_in_USD);
        textViewComments = findViewById(R.id.comments);
        textViewCategory = findViewById(R.id.category);
        //TODO not a text view, find how to inset image
        //textViewPicture = findViewById(R.id.picture);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseManager.updateObject(this, Donation.class, result -> {
            currentDonation = result;

            // Fill text fields with current Location info
            if (currentDonation != null) {
                Locale locale = getResources().getConfiguration().getLocales().get(0);
                textViewDonationTime.setText(String.format(locale, "Time: %tc", currentDonation.getDonationTime()));
                //TODO use the name instead -> might have to query for the location
                textViewDonationLocation.setText(String.format("Location: %s", Integer.toString(currentDonation.getDonationLocation())));
                textViewFullDescription.setText(String.format("Description: %s", currentDonation.getFullDescription()));
                textViewValueInUSD.setText(String.format(locale, "USD: %,.2f", currentDonation.getValueInUSD()));
                textViewComments.setText(String.format("Comments: %s", currentDonation.getComments()));
                textViewCategory.setText(String.format("Category: %s", currentDonation.getCategory()));
                //TODO implement picture later on
//            textViewPicture.append(currentDonation.get());
            } else {
                Log.d(TAG, "Cannot get current donation, current donation is null");
            }
            //TODO: handle situation where loading location info fails
        }, donationDocRef);
    }

    public void onClickEditDonation(View view) {
        Intent intent = new Intent(ViewDonationDetailsActivity.this, EditDonationDetailActivity.class);
        intent.putExtra("Donation", currentDonation);
        startActivity(intent);
    }
}
