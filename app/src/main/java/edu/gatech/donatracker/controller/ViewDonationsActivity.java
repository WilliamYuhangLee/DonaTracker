package edu.gatech.donatracker.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import edu.gatech.donatracker.R;
import edu.gatech.donatracker.model.Donation;
import edu.gatech.donatracker.model.Location;
import edu.gatech.donatracker.model.user.User;

public class ViewDonationsActivity extends AppCompatActivity{

    public static final String TAG = "ViewDonationsActivity.class";

    // Models
    private Location currentLocation;
    private DocumentReference locationRef;
    private List<String> donationIDList;
    private List<Donation> donationList;
    private CollectionReference donationsRef;

    private FirebaseUser firebaseUser;
    private User user;

    // UI References
    private RecyclerView recyclerView;
    SimpleDonationRecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_donations);

        // Initiate Models
        currentLocation = getIntent().getParcelableExtra("Current Location");
        donationIDList = currentLocation.viewInventory();
        user = getIntent().getParcelableExtra("User Model");
        donationList = new ArrayList<>();
        locationRef = FirebaseFirestore.getInstance().collection("locations").document(Integer.toString
                (currentLocation.getKey()));

        // Initiate UI References
        recyclerView = findViewById(R.id.recycler_view_view_donations_donations);

        // Set up adapters
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new SimpleDonationRecyclerViewAdapter(donationList);
        recyclerView.setAdapter(recyclerViewAdapter);

        locationRef.get().addOnSuccessListener(documentSnapshot -> {
            donationIDList = (List<String>) documentSnapshot.getData().get("inventory");
            Log.d(TAG, "Donation ID list fetch successful!");
            updateDonations();
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Donation ID list fetch failed!", e);
        });
    }

    private void updateDonations() {
        donationList.clear();
        for (String donationID : donationIDList) {
            DocumentReference donationRef = FirebaseFirestore.getInstance().collection("donations").document(donationID);
            donationRef.get().addOnSuccessListener(documentSnapshot -> {
                donationList.add(documentSnapshot.toObject(Donation.class));
                Log.d(TAG, "Donation[" + donationID + "] fetch successful!");
                recyclerViewAdapter.notifyDataSetChanged();
            }).addOnFailureListener(e -> {
                Log.w(TAG, "Donation[" + donationID + "] fetch failed!", e);
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationRef.addSnapshotListener(((documentSnapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Location data update failed", e);
                return;
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                Log.d(TAG, "Location data update successful!");
                donationIDList = (List) documentSnapshot.get("inventory");
                updateDonations();
            }
        }));
    }

    public void onClickAddDonation(View view) {
        Intent intent = new Intent(ViewDonationsActivity.this, EditDonationDetailActivity.class);
        intent.putExtra("Location", currentLocation);
        startActivity(intent);
    }

    // RecyclerViewAdapter inner class
    private class SimpleDonationRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleDonationRecyclerViewAdapter.ViewHolder> {

        /**
         * Collection of the items to be shown in this list.
         */
        private final List<Donation> myDonations;

        /**
         * set the items to be used by the adapter
         *
         * @param items the list of items to be displayed in the recycler view
         */
        SimpleDonationRecyclerViewAdapter(List<Donation> items) {
            myDonations = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            /*

              This sets up the view for each individual item in the recycler display
              To edit the actual layout, we would look at: res/layout/course_list_content.xml
              If you look at the example file, you will see it currently just 2 TextView elements
             */
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.donation_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            /*
            This is where we have to bind each data element in the list (given by position parameter)
            to an element in the view (which is one of our two TextView widgets
             */
            //start by getting the element at the correct position
            holder.myDonation = myDonations.get(position);
            /*
              Now we bind the data to the widgets.  In this case, pretty simple, put the id in one
              textview and the string rep of a course in the other.
             */
            holder.mContentView.setText(myDonations.get(position).getShortDescription());

            /*
             * set up a listener to handle if the user clicks on this list item, what should happen?
             */
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //on a phone, we need to change windows to the detail view
                    Context context = v.getContext();
                    //create our new intent with the new screen (activity)
                    Intent intent = new Intent(context, ViewDonationDetailsActivity.class);
                        /*
                            pass along the id of the course so we can retrieve the correct data in
                            the next window
                         */
                    intent.putExtra("Donation", holder.myDonation);

                    //now just display the new window
                    context.startActivity(intent);

                }
            });
        }

        @Override
        public int getItemCount() {
            return myDonations.size();
        }

        /**
         * This inner class represents a ViewHolder which provides us a way to cache information
         * about the binding between the model element (in this case a Course) and the widgets in
         * the list view (in this case the two TextView)
         */

        class ViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final TextView mContentView;
            Donation myDonation;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}