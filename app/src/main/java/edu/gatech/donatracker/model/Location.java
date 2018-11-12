package edu.gatech.donatracker.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yuhang Li on 2018/09/28
 * <p>
 * POJO for a location, holds information about this location.
 */
public class Location implements Parcelable {

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
    // Instance variables
    private int key;
    private String name;
    private String type;
    private List<String> employees;
    private List<String> inventory;
    private double longitude;
    private double latitude;
    private String address;
    private String phone;
    private String city;
    private String state;
    private int zip;

    // Constructors
    private String website;

    public Location() {
        employees = new ArrayList<>();
        inventory = new ArrayList<>();
    }

    private Location(Parcel in) {
        key = in.readInt();
        name = in.readString();
        type = in.readString();
        employees = new ArrayList<>();
        in.readList(employees, null);
        inventory = new ArrayList<>();
        in.readList(inventory, null);
        longitude = in.readDouble();
        latitude = in.readDouble();
        address = in.readString();
        phone = in.readString();
        city = in.readString();
        state = in.readString();
        zip = in.readInt();
        website = in.readString();
    }

    // Database Handlers

    private Location(int key, String name, String type, List<String> employees, List<String> inventory,
                     double longitude, double latitude, String address, String phone, String city, String state, int zip, String website) {
        this.key = key;
        this.name = name;
        this.type = type;
        this.employees = employees;
        this.inventory = inventory;
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
        this.phone = phone;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.website = website;
    }

    @SuppressWarnings("unchecked")
    public static Location unwrapData(HashMap<String, Object> data) {
        return new Location((int) data.get("key"), (String) data.get("name"), (String) data.get("type"),
                (List<String>) data.get("employees"), (List<String>) data.get("inventory"),
                (double) data.get("longitude"), (double) data.get("latitude"), (String) data.get
                ("address"), (String) data.get("phone"), (String) data.get("city"),
                (String) data.get("state"), (int) data.get("zip"), (String) data.get
                ("website"));
    }

    // Data Management

    public HashMap<String, Object> wrapData() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("key", key);
        data.put("name", name);
        data.put("type", type);
        data.put("employees", employees);
        data.put("inventory", inventory);
        data.put("longitude", longitude);
        data.put("latitude", latitude);
        data.put("address", address);
        data.put("phone", phone);
        data.put("city", city);
        data.put("state", state);
        data.put("zip", zip);
        data.put("website", website);
        return data;
    }

    public boolean addEmployee(String employee) {
        return employees.add(employee);
    }

    public boolean addEmployees(List employees) {
        //noinspection unchecked,unchecked
        return this.employees.addAll(employees);
    }

    public boolean removeEmployee(String employee) {
        return employees.remove(employee);
    }

    public List<String> viewEmployees() {
        return new ArrayList<>(employees);
    }

    public boolean addDonation(String donation) {
        return inventory.add(donation);
    }

    @SuppressWarnings("unchecked")
    public boolean addDonations(List donations) {
        return inventory.addAll(donations);
    }

    public boolean removeDonation(String donation) {
        return inventory.remove(donation);
    }

    //Getters and Setters

    public List<String> viewInventory() {
        return new ArrayList<>(inventory);
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

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

    public List<String> getEmployees() {
        return employees;
    }

    public void setEmployees(List<String> employees) {
        this.employees = employees;
    }

    public List<String> getInventory() {
        return inventory;
    }

    public void setInventory(List<String> inventory) {
        this.inventory = inventory;
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

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(key);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeList(employees);
        dest.writeList(inventory);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeInt(zip);
        dest.writeString(website);
    }
}
