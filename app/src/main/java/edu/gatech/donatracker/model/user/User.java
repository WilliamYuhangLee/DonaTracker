package edu.gatech.donatracker.model.user;

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

        public String toString() {
            return representation;
        }
    }

    // INSTANCE

    // Instance variables
    protected UserType userType;
    protected String UID;

    // Constructors
    public User() {
        this(UserType.REGULAR_USER);
    }

    public User(UserType userType) {
        this.userType = userType;
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

    public void setUID(String UID) {
        this.UID = UID;
    }
}
