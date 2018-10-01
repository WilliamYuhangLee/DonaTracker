package edu.gatech.donatracker.model.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

    // CLASS

    // Initialize a map of all legal User types and their string representation
    private static Map<String, Class<? extends User>> legalUserTypes = new HashMap<>();
    static {
        legalUserTypes.put("Administrator", Administrator.class);
        legalUserTypes.put("Manager", Manager.class);
        legalUserTypes.put("Location Employee", LocationEmployee.class);
        legalUserTypes.put("Regular User", User.class);
    }

    // Return an ArrayList of String representations of all legal User types
    public static List<String> getUserTypes() {
        return new ArrayList<>(legalUserTypes.keySet());
    }

    // Return the corresponding User type from a String
    public static Class<? extends User> getClassFromName(String className) throws ClassNotFoundException {
        if (legalUserTypes.containsKey(className)) {
            return legalUserTypes.get(className);
        } else {
            throw new ClassNotFoundException(className + " is not a legal user type!");
        }
    }

    // INSTANCE

    // Instance variables
    protected String userType;
    protected String UID;

    // Constructors
    public User() {
        userType = "Regular User";
    }

    // String Representation
    public String toString() {
        return userType; // more detail to be added
    }

    // Getters and Setters
    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
