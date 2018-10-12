package edu.gatech.donatracker.controller;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

import edu.gatech.donatracker.R;
import edu.gatech.donatracker.model.Model;
import edu.gatech.donatracker.model.Location;
import edu.gatech.donatracker.util.CSVFile;
import edu.gatech.donatracker.util.LocationFactory;

/**
 * Created by Qiusen Huang on 2018/10/11
 */
public class ViewLocationsActivity extends AppCompatActivity {

    // Models
    private Model model;
    private List<Location> locationList;

    // UI References
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_locations);

        // Initiate Models
        model = Model.getModel();

        // Initiate UI References
        recyclerView = findViewById(R.id.recycler_view_view_locations_locations);

        // Read in Location info from CSV file
        InputStream inputStream = getResources().openRawResource(R.raw.location_data);
        CSVFile csvFile = new CSVFile(inputStream);
        locationList = LocationFactory.parseLocations(csvFile.read());
        model.addLocations(locationList);

        // Set up adapters
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SimpleLocationRecyclerViewAdapter adapter = new SimpleLocationRecyclerViewAdapter(locationList);
        recyclerView.setAdapter(adapter);
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

            final Model model = Model.getModel();
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
                        /*
                            pass along the id of the course so we can retrieve the correct data in
                            the next window
                         */
                    //intent.putExtra("LocationPassed", holder.myLocation.getId());

                    model.setCurrentLocation(holder.myLocation);

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
