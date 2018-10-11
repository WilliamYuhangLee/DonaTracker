package edu.gatech.donatracker.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.gatech.donatracker.model.user.User;

/**
 * Created by Yuhang Li on 2018/09/28
 *
 * This is our facade to the Model.
 * We use a singleton design pattern to provide access to the model from each
 * controller.
 *
 */

public class Model {

    // Singleton instance
    private static final Model instance = new Model();
    public static Model getModel() {return instance;}

    // Hold a Map of UIDs to users
    private Map<String, User> users;

    // Hold the list of all Locations
    private List<Location> locations;

    // Constructor
    private Model() {
        users = new HashMap<>();
        locations = new ArrayList<>();

        // for debugging, delete when project is finished
        loadDummyUsers();
        loadDummyLocations();
    }

    // Populate the model with dummy users. Delete when app finished
    private void loadDummyUsers() {

    }

    // Populate the model with dummy locations. Delete when app finished
    private void loadDummyLocations() {

    }

    /**
     * Add a new user to the users list.
     *
     * @param user to be added
     * @return the User replaced or null if there exists no such user
     */
    public User addUser(User user, String UID) {
        return users.put(UID, user);
    }

    /**
     * Remove a user from the users list.
     * @param UID of the user to be removed
     * @return the User removed or null if there exists no such user
     */
    public User removeUser(String UID) {
        return users.remove(UID);
    }

    /**
     * Remove a user from the users list.
     * @param user to be removed
     * @return the User removed or null if there exists no such user
     */
    public User removeUser(User user) {
        return users.remove(user.getUID());
    }

    /**
     * Get the user with the given UID.
     * @param UID of the user to be retrieved
     * @return the User or null if the User does not exist
     */
    public User getUser(String UID) {
        return users.get(UID) == null ? new User() : users.get(UID); //temporary way to fix retrieving non-existent user
    }

    /**
     * Add a new location to the users list.
     *
     * @param location to be added
     * @return true if added, false if it is already there
     */
    public boolean addLocation(Location location) {
        return !locations.contains(location) && locations.add(location);
    }

    /**
     * Take a List of Locations and check if any of them are in the locations already.
     * If there are any duplicates, nothing changes and returns false;
     * Else all the locations are added and true is returned.
     *
     * @param locations to be added
     * @return true if addition is successful, else false
     */
    public boolean addLocations(List<Location> locations) {
        boolean success = true;
        List<Location> temp = getLocations();
        for (Location location : locations) {
            success = success && (!temp.contains(location) && temp.add(location));
        }
        if (success) {
            this.locations = temp;
        }
        return success;
    }

    /**
     * Remove a location from the users list.
     * @param location to be removed
     * @return true if removed, false if there is no such location
     */
    public boolean removeLocation(Location location) {
        return locations.contains(location) && locations.remove(location);
    }

    /**
     * Get a COPY of the List locations.
     *
     * @return a COPY of the List locations
     */
    public List<Location> getLocations() {
        return new ArrayList<>(locations);
    }
}
