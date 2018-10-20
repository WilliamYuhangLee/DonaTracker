package edu.gatech.donatracker.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.gatech.donatracker.R;
import edu.gatech.donatracker.model.Location;
import edu.gatech.donatracker.model.user.User;
import edu.gatech.donatracker.util.CSVFile;
import edu.gatech.donatracker.util.LocationFactory;

/**
 * Created by Qiusen Huang on 2018/10/11
 */
public class ViewLocationsActivity extends AppCompatActivity {

    public static final String TAG = "ViewLocationsActivity.class";

    // Models
    private User user;
    private List<Location> locationList;
    private FirebaseFirestore database;
    private CollectionReference locationsRef;

    // UI References
    private RecyclerView recyclerView;
    private Button import_locations_button;
    private Button add_location_button;
    SimpleLocationRecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_locations);

        // Initiate Models
        user = getIntent().getParcelableExtra("User Model");
        database = FirebaseFirestore.getInstance();
        locationsRef = database.collection("locations");
        locationList = new ArrayList<>();

        // Initiate UI References
        recyclerView = findViewById(R.id.recycler_view_view_locations_locations);
        import_locations_button = findViewById(R.id.button_view_locations_import_locations);
        add_location_button = findViewById(R.id.button_view_locations_add_location);

        // Set up adapters

        import_locations_button.setOnClickListener(view -> {
            // Read in Location info from CSV file
            InputStream inputStream = getResources().openRawResource(R.raw.location_data);
            CSVFile csvFile = new CSVFile(inputStream);
            List<Location> parsedLocations = LocationFactory.parseLocations(csvFile.read());
            uploadLocations(parsedLocations);
        });

        add_location_button.setOnClickListener(view -> {
            //TODO: complete this method
//            Location location;
//            uploadLocations(new ArrayList<>(location));
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new SimpleLocationRecyclerViewAdapter(locationList);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationsRef.addSnapshotListener(this, ((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "Location update failed.", e);
                return;
            }
            List<String> cities = new ArrayList<>();
            for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()) {
                switch (change.getType()) {
                    case ADDED:
                        Log.d(TAG, "Location added!");
                        locationList.add(change.getDocument().toObject(Location.class));
                        recyclerViewAdapter.notifyDataSetChanged();
                        break;
                    case MODIFIED:
                        Log.d(TAG, "Location modified!");
                        int key = ((Long) change.getDocument().getData().get("key")).intValue();
                        Location updated = locationList.stream().filter(location -> location.getKey() == key).findFirst
                                ().get();
                        locationList.remove(updated);
                        locationList.add(change.getDocument().toObject(Location.class));
                        recyclerViewAdapter.notifyDataSetChanged();
                        break;
                    case REMOVED:
                        Log.d(TAG, "Location removed!");
                        key = ((Long) change.getDocument().getData().get("key")).intValue();
                        Location removed = locationList.stream().filter(location -> location.getKey() == key).findFirst
                                ().get();
                        locationList.remove(removed);
                        recyclerViewAdapter.notifyDataSetChanged();
                        break;
                }
            }
            Log.d(TAG, "");
        }));
    }

    private void uploadLocations(List<Location> locations) {
        WriteBatch batch = database.batch();
        for (Location location : locations) {
            DocumentReference docRef = database.collection("locations").document(Integer.toString(location.getKey()));
            batch.set(docRef, location.wrapData());
        }
        batch.commit().addOnSuccessListener(v -> {
            Log.d(TAG, "Locations upload successful!");
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Locations upload failed!", e);
        });
        recyclerViewAdapter.notifyDataSetChanged();
    }

    // RecyclerViewAdapter inner class
    private class SimpleLocationRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleLocationRecyclerViewAdapter.ViewHolder> {

        /**
         * Collection of the items to be shown in this list.
         */
        private final List<Location> myLocations;

        /**
         * set the items to be used by the adapter
         *
         * @param items the list of items to be displayed in the recycler view
         */
        SimpleLocationRecyclerViewAdapter(List<Location> items) {
            myLocations = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            /*

              This sets up the view for each individual item in the recycler display
              To edit the actual layout, we would look at: res/layout/course_list_content.xml
              If you look at the example file, you will see it currently just 2 TextView elements
             */
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.location_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            /*
            This is where we have to bind each data element in the list (given by position parameter)
            to an element in the view (which is one of our two TextView widgets
             */
            //start by getting the element at the correct position
            holder.myLocation = myLocations.get(position);
            /*
              Now we bind the data to the widgets.  In this case, pretty simple, put the id in one
              textview and the string rep of a course in the other.
             */
            holder.mContentView.setText(myLocations.get(position).toString());

            /*
             * set up a listener to handle if the user clicks on this list item, what should happen?
             */
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //on a phone, we need to change windows to the detail view
                    Context context = v.getContext();
                    //create our new intent with the new screen (activity)
                    Intent intent = new Intent(context, ViewLocationDetailsActivity.class);
                    //pass along the id of the course so we can retrieve the correct data in the next window
                    intent.putExtra("Location Passed", holder.myLocation);
                    intent.putExtra("User Model", (Parcelable) user);
                    //now just display the new window
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return myLocations.size();
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
            Location myLocation;

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
