package edu.gatech.donatracker.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.gatech.donatracker.R;
import edu.gatech.donatracker.model.Donation;
import edu.gatech.donatracker.model.Location;
import edu.gatech.donatracker.model.Model;

public class EditDonationDetailActivity extends AppCompatActivity {
    /* ************************
                Widgets we will need for binding and getting information
             */
    public static final String TAG = "EditDonationDetailActivity.class";

    private EditText editTextShortDescription;
    private EditText editTextFullDescription;
    private EditText editTextValueInUSD;
    private EditText editTextComment;

    private Donation currentDonation;
    private Location currentLocation;

    private boolean editing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_donation_detail);

        /**
         * Grab the dialog widgets so we can get info for later
         */
        editTextShortDescription = findViewById(R.id.edit_short_description);
        editTextFullDescription = findViewById(R.id.edit_full_description);
        editTextValueInUSD = findViewById(R.id.edit_value_in_usd);
        editTextComment = findViewById(R.id.edit_comment);

        /*
           If a student has been passed in, this was an edit, if not, this is a new add
         */
        currentDonation = getIntent().getParcelableExtra("Donation");
        currentLocation = getIntent().getParcelableExtra("Location");

        if (currentDonation != null) {
            editTextShortDescription.setText(currentDonation.getShortDescription());
            editTextFullDescription.setText(currentDonation.getFullDescription());
            editTextValueInUSD.setText(String.format(getResources().getConfiguration().getLocales().get(0), "%,.2f",
                    currentDonation.getValueInUSD()));
            editTextComment.setText(currentDonation.getComments());
            editing = true;
        } else {
            currentDonation = new Donation(currentLocation.getKey());
            editing = false;
        }
    }

    /**
     * Button handler for the add/edit donation button
     * @param view the button
     */
    public void onClickSaveDonation(View view) {
        Log.d("Edit", "Add/edit donation");

        currentDonation.setShortDescription(editTextShortDescription.getText().toString());
        currentDonation.setFullDescription(editTextFullDescription.getText().toString());
        currentDonation.setValueInUSD(Double.parseDouble(editTextValueInUSD.getText().toString()));
        currentDonation.setComments(editTextComment.getText().toString());

        Log.d("Edit", (editing ? "Edit": "Add") + " donation data: " + currentDonation.toString());

        DocumentReference donationRef = FirebaseFirestore.getInstance().collection("donations").document(currentDonation
                .getUuid());

        if (!editing) {
            donationRef.set(currentDonation).addOnSuccessListener((v) -> {
                Log.d(TAG, "Donation creation and upload successful!");
                currentLocation.addDonation(currentDonation.getUuid());
                DocumentReference locationRef = FirebaseFirestore.getInstance().collection("locations").document(String.valueOf(currentLocation.getKey()));
                locationRef.set(currentLocation).addOnSuccessListener(v2 -> {
                    Log.d(TAG, "Donation inventory update successful!");
                }).addOnFailureListener(e -> {
                    Log.w(TAG, "Donation inventory update failed!", e);
                });
            }).addOnFailureListener(e -> {
                Log.d(TAG, "Donation creation and upload failed!", e);
            });
        } else {
            donationRef.set(currentDonation).addOnSuccessListener((v) -> {
                Log.d(TAG, "Donation edit and upload successful!");
            }).addOnFailureListener((e) -> {
                Log.d(TAG, "Donation edit and upload failed!", e);
            });
        }
        finish();
    }

    /**
     * Button handler for cancel
     *
     * @param view the button pressed
     */
    public void onCancelPressed(View view) {
        Log.d("Edit", "Cancel Donation");
        finish();
    }
}
