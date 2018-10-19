package edu.gatech.donatracker.model.user;

public class Administrator extends User {

    // Constructors
    public Administrator(String UID) {
        super(UID, UserType.ADMINISTRATOR);
    }
}
