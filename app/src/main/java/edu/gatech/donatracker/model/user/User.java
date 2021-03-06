package edu.gatech.donatracker.model.user;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Parcelable, Serializable {

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // CLASS
    private static final String TAG = User.class.getSimpleName();

    // INSTANCE
    // Instance variables
    private String UID;
    private UserType userType;
    private String email;
    private String username;
    private boolean isLocked;

    // Constructors
    public User() {
    }

    private User(String UID) {
        this(UID, UserType.REGULAR_USER);
    }

    public User(String UID, UserType userType) {
        this.UID = UID;
        this.userType = userType;
    }

    private User(Parcel in) {
        UID = in.readString();
        userType = (UserType) in.readSerializable();
    }

    // Database handlers

    /**
     * Manufacture a User instance from a HashMap passed from a database
     *
     * @param data a HashMap of <field, value> pairs
     * @return a User instance generated from the data
     */
    public static User unwrapData(Map<String, Object> data) {
        String UID = (String) data.get("UID");
        UserType userType = UserType.fromString((String) data.get("userType"));
        switch (userType) {
            case DEVELOPER:
                return new Developer(UID);
            case ADMINISTRATOR:
                return new Administrator(UID);
            case MANAGER:
                return new Manager(UID);
            case LOCATION_EMPLOYEE:
                return new LocationEmployee(UID);
            case REGULAR_USER:
                return new User(UID);
            default:
                Log.e(TAG, "Illegal User type instantiation!");
                return new User(UID);
        }
    }

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

    // Implements Parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(UID);
        dest.writeSerializable(userType);
    }

    // String Representation
    public String toString() {
        return userType.toString(); // more detail to be added
    }

    // Getters and Setters
    public String getUID() {
        return UID;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public enum UserType implements Serializable {
        DEVELOPER("Developer"),
        ADMINISTRATOR("Administrator"),
        MANAGER("Manager"),
        LOCATION_EMPLOYEE("Location Employee"),
        REGULAR_USER("Regular User");

        private final String representation;

        UserType(String representation) {
            this.representation = representation;
        }

        static UserType fromString(String string) {
            for (UserType userType : UserType.values()) {
                if (userType.representation.equalsIgnoreCase(string)) {
                    return userType;
                }
            }
            throw new IllegalArgumentException("No matching UserType!");
        }

        public static UserType[] legalValues() {
            return new UserType[]{ADMINISTRATOR, MANAGER, LOCATION_EMPLOYEE, REGULAR_USER};
        }

        @Override
        public String toString() {
            return representation;
        }
    }
}
