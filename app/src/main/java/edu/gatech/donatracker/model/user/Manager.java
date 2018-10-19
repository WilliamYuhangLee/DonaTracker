package edu.gatech.donatracker.model.user;

public class Manager extends User {

    // Constructors
    public Manager(String UID) {
        super(UID, UserType.MANAGER);
    }
}
