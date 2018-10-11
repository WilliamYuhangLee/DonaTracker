package edu.gatech.donatracker.model;

import java.util.ArrayList;
import java.util.List;

import edu.gatech.donatracker.model.user.LocationEmployee;

public class Location {

    // Instance variables
    private List<LocationEmployee> localEmployees;
    private String name;
    private String type;
    private double longitude;
    private double latitude;
    private String address;
    private String phone;
    private String city;
    private String state;
    private int zip;
    private String website;

    // Constructors
    public Location() {
        localEmployees = new ArrayList<>();
    }

    public void addEmployee(LocationEmployee employee) {
        localEmployees.add(employee);
    }

    public void removeEmployee(LocationEmployee employee) {
        localEmployees.remove(employee);
    }

    public List<LocationEmployee> viewEmployees() {
        return new ArrayList<>(localEmployees);
    }
}
