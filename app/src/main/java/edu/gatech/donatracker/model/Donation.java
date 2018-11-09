package edu.gatech.donatracker.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Yuhang Li on 2018/10/18
 * <p>
 * Represents a donated item
 */
public class Donation implements Parcelable {


    public static final Parcelable.Creator<Donation> CREATOR
            = new Parcelable.Creator<Donation>() {
        public Donation createFromParcel(Parcel in) {
            return new Donation(in);
        }

        public Donation[] newArray(int size) {
            return new Donation[size];
        }
    };
    // Instance variables
    private String uuid;
    private Date donationTime;
    private int donationLocation;
    private String shortDescription;
    private String fullDescription;
    private double valueInUSD;
    private String category;
//    private Bitmap picture;

    // String Representation (debug purpose)
    private String comments;

    // Constructors

    // no-args constructor for use of toObject()
    public Donation() {
    }

    public Donation(String uuid, Date donationTime, int donationLocation,
                    String shortDescription, String fullDescription, double valueInUSD,
                    String category, String comments) {
        this.uuid = uuid;
        this.donationTime = donationTime;
        this.donationLocation = donationLocation;
        this.shortDescription = shortDescription;
        this.fullDescription = fullDescription;
        this.valueInUSD = valueInUSD;
        this.category = category;
        this.comments = comments;
    }

    public Donation(int location) {
        uuid = UUID.randomUUID().toString();
        donationTime = Calendar.getInstance().getTime();
        donationLocation = location;
    }

    // Getters and Setters

    private Donation(Parcel in) {
        uuid = in.readString();
        donationTime = (Date) in.readSerializable();
        donationLocation = in.readInt();
        shortDescription = in.readString();
        fullDescription = in.readString();
        valueInUSD = in.readDouble();
        category = in.readString();
        comments = in.readString();
    }

    @Override
    public String toString() {
        return "Donation[" + uuid + "] " + shortDescription;
    }

    public Date getDonationTime() {
        return donationTime;
    }

    public void setDonationTime(Date donationTime) {
        this.donationTime = donationTime;
    }

    public int getDonationLocation() {
        return donationLocation;
    }

    public void setDonationLocation(int donationLocation) {
        this.donationLocation = donationLocation;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCategory() {
        return category;
    }

    // Implement Parcelable

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeSerializable(donationTime);
        dest.writeInt(donationLocation);
        dest.writeString(shortDescription);
        dest.writeString(fullDescription);
        dest.writeDouble(valueInUSD);
        dest.writeString(category);
        dest.writeString(comments);
    }

    //    public Bitmap getPicture() {
//        return picture;
//    }
//
//    public void setPicture(Bitmap picture) {
//        this.picture = picture;
//    }
}
