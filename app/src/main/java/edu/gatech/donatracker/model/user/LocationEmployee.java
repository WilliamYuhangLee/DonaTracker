package edu.gatech.donatracker.model.user;

public class LocationEmployee extends User {

    private int locationID;

    // Constructors
    public LocationEmployee(String UID) {
        super(UID, UserType.LOCATION_EMPLOYEE);
    }

    public int getLocationID() {
        return locationID;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }
}
