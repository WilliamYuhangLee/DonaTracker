package edu.gatech.donatracker.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import edu.gatech.donatracker.model.user.LocationEmployee;

/**
 * Created by Yuhang Li on 2018/09/28
 *
 * POJO for a location, holds information about this location.
 */
public class Location implements Parcelable {

    // Static fields
    private static int nextId = 0;

    // Instance variables
    private int id;
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
        id = nextId++;
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

    public int getId() {
        return this.id;
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
        dest.writeString(name);
        dest.writeString(type);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeInt(zip);
        dest.writeString(website);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private Location (Parcel in) {
         name = in.readString();
         type = in.readString();
         longitude = in.readDouble();
         latitude = in.readDouble();
         address = in.readString();
         phone = in.readString();
         city = in.readString();
         state = in.readString();
         zip = in.readInt();
         website = in.readString();
    }

}
