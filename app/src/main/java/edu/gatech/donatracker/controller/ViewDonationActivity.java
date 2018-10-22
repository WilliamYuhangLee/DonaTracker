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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import edu.gatech.donatracker.R;
import edu.gatech.donatracker.model.Donation;
import edu.gatech.donatracker.model.Location;
import edu.gatech.donatracker.model.Model;
import edu.gatech.donatracker.model.user.User;

public class ViewDonationActivity extends AppCompatActivity{

    public static final String TAG = "ViewDonationActivity.class";

    // Models
    private Location currentLocation;
    private List<String> donationIDList;
    private List<Donation> donationList;
    private FirebaseUser firebaseUser;
    private User user;

    // UI References
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_donation);

        // Initiate Models
        currentLocation = getIntent().getParcelableExtra("Current Location");
        donationIDList = currentLocation.viewInventory();
        user = getIntent().getParcelableExtra("User Model");
        donationList = new ArrayList<>();
        CollectionReference donationsRef = FirebaseFirestore.getInstance().collection("locations")
                .document(Integer.toString(currentLocation.getKey())).collection("inventory");
        donationsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d(TAG, "Donation " + document.get("shortDescription") + " fetch successful!");
                    donationList.add(document.toObject(Donation.class));
                }
            } else {
                Log.e(TAG, "Inventory fetch failed!", task.getException());
            }
        });
        // Initiate UI References
        recyclerView = findViewById(R.id.recycler_view_view_donations_donations);

        // Set up adapters
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ViewDonationActivity.SimpleDonationRecyclerViewAdapter adapter =
                new ViewDonationActivity.SimpleDonationRecyclerViewAdapter(donationList);
        recyclerView.setAdapter(adapter);
    }

    public void onClickAddDonation(View view) {
        Intent intent = new Intent(ViewDonationActivity.this, EditDonationDetailActivity.class);
        intent.putExtra("Location", currentLocation);
        startActivity(intent);
    }

    // RecyclerViewAdapter inner class
    private class SimpleDonationRecyclerViewAdapter
            extends RecyclerView.Adapter<ViewDonationActivity.SimpleDonationRecyclerViewAdapter.ViewHolder> {

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
        public ViewDonationActivity.SimpleDonationRecyclerViewAdapter
                .ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            /*

              This sets up the view for each individual item in the recycler display
              To edit the actual layout, we would look at: res/layout/course_list_content.xml
              If you look at the example file, you will see it currently just 2 TextView elements
             */
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.donation_list_content, parent, false);
            return new ViewDonationActivity.SimpleDonationRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewDonationActivity.SimpleDonationRecyclerViewAdapter
                .ViewHolder holder, int position) {

            final Model model = Model.getModel();
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
            holder.mContentView.setText(myDonations.get(position).toString());

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
            //            public final TextView mIdView;
            final TextView mContentView;
            Donation myDonation;

            ViewHolder(View view) {
                super(view);
                mView = view;
//                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
