package edu.gatech.donatracker.model;

import android.graphics.Bitmap;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Yuhang Li on 2018/10/18
 *
 * Represents a donated item
 */
public class Donation {

    // Instance variables
    Date donationTime;
    Location donationLocation;
    String shortDescription;
    String fullDescription;
    double valueInUSD;
    //TODO: implement hierarchical categorization
    String comments;
    Bitmap picture;

    // Constructors
    public Donation(Location location) {
        donationTime = Calendar.getInstance().getTime();
        donationLocation = location;
    }

    // Getters and Setters


    public Date getDonationTime() {
        return donationTime;
    }

    public Location getDonationLocation() {
        return donationLocation;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public double getValueInUSD() {
        return valueInUSD;
    }

    public void setValueInUSD(double valueInUSD) {
        this.valueInUSD = valueInUSD;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }
}
