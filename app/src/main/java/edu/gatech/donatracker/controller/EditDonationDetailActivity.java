package edu.gatech.donatracker.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.gatech.donatracker.R;
import edu.gatech.donatracker.model.Donation;
import edu.gatech.donatracker.model.Location;

public class EditDonationDetailActivity extends AppCompatActivity {
    /* ************************
                Widgets we will need for binding and getting information
             */
    private static final String TAG = "EditDonationDetailActivity.class";
    private FirebaseFirestore firebaseFirestore;

    private EditText editTextShortDescription;
    private EditText editTextFullDescription;
    private EditText editTextValueInUSD;
    private EditText editTextComment;
    private EditText editTextCategory;

    private Donation currentDonation;
    private Location currentLocation;

    private boolean editing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_donation_detail);
        firebaseFirestore = FirebaseFirestore.getInstance();

        /*
          Grab the dialog widgets so we can get info for later
         */
        editTextShortDescription = findViewById(R.id.edit_short_description);
        editTextFullDescription = findViewById(R.id.edit_full_description);
        editTextValueInUSD = findViewById(R.id.edit_value_in_usd);
        editTextComment = findViewById(R.id.edit_comment);
        editTextCategory = findViewById(R.id.edit_category);

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
            editTextCategory.setText(currentDonation.getCategory() == null ? "" : currentDonation.getCategory());
            editing = true;
        } else {
            currentDonation = new Donation(currentLocation.getKey());
            editing = false;
        }
    }

    /**
     * Button handler for the add/edit donation button
     *
     * @param view the button
     */
    public void onClickSaveDonation(View view) {
        Log.d("Edit", "Add/edit donation");

        currentDonation.setShortDescription(editTextShortDescription.getText().toString());
        currentDonation.setFullDescription(editTextFullDescription.getText().toString());
        try {
            currentDonation.setValueInUSD(Double.parseDouble(editTextValueInUSD.getText().toString()));
        } catch (NumberFormatException e) {
            Log.d(TAG, "Invalid USD input, please enter a valid input");
            currentDonation.setValueInUSD(0);
        }
        currentDonation.setComments(editTextComment.getText().toString());
        currentDonation.setCategory(editTextCategory.getText().toString());

        Log.d("Edit", (editing ? "Edit" : "Add") + " donation data: " + currentDonation.toString());

        DocumentReference donationRef = firebaseFirestore.collection("donations").document(currentDonation
                .getUuid());

        if (!editing) {
            donationRef.set(currentDonation).addOnSuccessListener((v) -> {
                Log.d(TAG, "Donation creation and upload successful!");
                boolean addedSuccessfully = currentLocation.addDonation(currentDonation.getUuid());
                DocumentReference locationRef = firebaseFirestore.collection("locations").document(String.valueOf(currentLocation.getKey()));
                locationRef.set(currentLocation).addOnSuccessListener(v2 -> Log.d(TAG, "Donation inventory update successful!")).addOnFailureListener(e -> Log.w(TAG, "Donation inventory update failed!", e));
            }).addOnFailureListener(e -> Log.d(TAG, "Donation creation and upload failed!", e));
        } else {
            donationRef.set(currentDonation).addOnSuccessListener((v) -> Log.d(TAG, "Donation edit and upload successful!")).addOnFailureListener((e) -> Log.d(TAG, "Donation edit and upload failed!", e));
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
