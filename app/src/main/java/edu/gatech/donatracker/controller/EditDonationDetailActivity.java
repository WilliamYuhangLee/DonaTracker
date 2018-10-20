package edu.gatech.donatracker.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import edu.gatech.donatracker.R;
import edu.gatech.donatracker.model.Donation;
import edu.gatech.donatracker.model.Model;

public class EditDonationDetailActivity extends AppCompatActivity {
    /* ************************
        Widgets we will need for binding and getting information
     */
    private EditText editTextShortDescription;
    private EditText editTextFullDescription;
    private EditText editTextValueInUSD;
    private EditText editTextComment;

    private Model model;

    /* ************************
       Keeping track of spinner changes.  Not really used right now, probably don't need this.
     */
    private String _major = "NA";

    /* ***********************
       Data for student being edited.
     */
    private Donation currentDonation;

    /* ***********************
       flag for whether this is a new student being added or an existing student being edited;
     */
    private boolean editing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_donation_detail);

        /**
         * Grab the dialog widgets so we can get info for later
         */
        editTextShortDescription = (EditText) findViewById(R.id.edit_short_description);
        editTextFullDescription = (EditText) findViewById(R.id.edit_full_description);
        editTextValueInUSD = (EditText) findViewById(R.id.edit_value_in_usd);
        editTextComment = (EditText) findViewById(R.id.edit_comment);

        /*
           If a student has been passed in, this was an edit, if not, this is a new add
         */
        model = Model.getModel();
        currentDonation = model.getCurrentDonation();

        if (currentDonation != null) {
            editTextShortDescription.setText(currentDonation.getShortDescription());
            editTextFullDescription.setText(currentDonation.getFullDescription());
            editTextValueInUSD.setText(Double.toString(currentDonation.getValueInUSD())); //TODO
            editTextComment.setText(currentDonation.getComments());
            editing = true;
        } else {
            currentDonation = new Donation(model.getCurrentLocation().getKey());
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
        if (!editing) {
//            model.getCurrentLocation().addDonation(currentDonation);//todo
            Log.d("Save", "Save new donation");
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
