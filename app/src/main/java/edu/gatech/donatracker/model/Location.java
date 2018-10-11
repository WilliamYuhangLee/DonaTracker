package edu.gatech.donatracker.model;

import java.util.ArrayList;
import java.util.List;

import edu.gatech.donatracker.model.user.LocationEmployee;

/**
 * Created by Yuhang Li on 2018/09/28
 *
 * POJO for a location, holds information about this location.
 */
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

    //Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
