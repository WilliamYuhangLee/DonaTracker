package edu.gatech.donatracker.model.user;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class User {

    // CLASS

    public enum UserType {
        ADMINISTRATOR("Administrator"),
        MANAGER("Manager"),
        LOCATION_EMPLOYEE("Location Employee"),
        REGULAR_USER("Regular User");

        private String representation;

        UserType(String representation) {
            this.representation = representation;
        }

        @Override
        public String toString() {
            return representation;
        }

        @Nullable
        public static UserType fromString(String string) {
            for (UserType userType : UserType.values()) {
                if (userType.representation.equalsIgnoreCase(string)) {
                    return userType;
                }
            }
            return null;
        }
    }

    // INSTANCE

    // Instance variables
    protected String UID;
    protected UserType userType;

    // Constructors
    public User() {
    }

    public User(String UID) {
        this(UID, UserType.REGULAR_USER);
    }

    public User(String UID, UserType userType) {
        this.UID = UID;
        this.userType = userType;
    }

    // Database handlers

    /**
     * Wrap this User instance into a JSON-like HashMap for the use of databases
     *
     * @return a HashMap with field variables as String keys and their values as Object values
     */
    public HashMap<String, Object> wrapData() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("UID", UID);
        data.put("userType", userType.toString());
        return data;
    }

    public static User unwrapData(Map<String, Object> data) {
        String UID = (String) data.get("UID");
        UserType userType = UserType.fromString((String) data.get("userType"));
        switch (userType) {
            case ADMINISTRATOR: return new Administrator(UID);
            case MANAGER: return new Manager(UID);
            case LOCATION_EMPLOYEE: return new LocationEmployee(UID);
            case REGULAR_USER: return new User(UID);
        }
        Log.e("User.java", "Illegal User type instantiation!");
        return new User();
    }

    // String Representation
    public String toString() {
        return userType.toString(); // more detail to be added
    }

    // Getters and Setters
    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getUID() {
        return UID;
    }
}
