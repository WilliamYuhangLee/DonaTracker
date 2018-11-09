package edu.gatech.donatracker.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.gatech.donatracker.model.Location;

/**
 * Created by Yuhang Li on 2018/10/11
 * <p>
 * A factory class that has a static method parseLocations() that returns a List of
 * Locations when passed in a List<String[]> that is returned from the CSVFile class's
 * read() method.
 */
public class LocationFactory {

    private List<String[]> rawList;
    private HashMap<String, Integer> indices;
    private List<Location> locations;

    private LocationFactory(List<String[]> rawList) {
        if (rawList == null) {
            Log.e("Error", "Location Factory cannot take an empty list");
            throw new IllegalArgumentException("raw list cannot be null");
        } else {
            this.rawList = rawList;
            this.indices = new HashMap<>();
            locations = new ArrayList<>();
            init();
            manufactureLocations();
        }
    }

    /**
     * The only public method of this class.
     * Take a List<String[]> that is generated from the CSVFile class's read()
     * method, and return a List of Locations with the list's elements as parameters.
     *
     * @param rawList a List of String[] generated from a CSF file through the CSVFile class
     * @return a List of Locations
     */
    public static List<Location> parseLocations(List<String[]> rawList) {
        LocationFactory locationFactory = new LocationFactory(rawList);
        return locationFactory.locations;
    }

    private void init() {
        String[] titles = rawList.get(0);
        for (int i = 0; i < titles.length; i++) {
            indices.put(titles[i], i);
        }
    }

    private void manufactureLocations() {
        for (String[] args : rawList.subList(1, rawList.size())) {
            Location location = new Location();
            location.setKey(Integer.parseInt(args[indices.get("Key")]));
            location.setName(args[indices.get("Name")]);
            location.setLatitude(Double.parseDouble(args[indices.get("Latitude")]));
            location.setLongitude(Double.parseDouble(args[indices.get("Longitude")]));
            location.setAddress(args[indices.get("Street Address")]);
            location.setCity(args[indices.get("City")]);
            location.setState(args[indices.get("State")]);
            location.setZip(Integer.parseInt(args[indices.get("Zip")]));
            location.setType(args[indices.get("Type")]);
            location.setPhone(args[indices.get("Phone")]);
            location.setWebsite(args[indices.get("Website")]);
            locations.add(location);
        }
    }
}
