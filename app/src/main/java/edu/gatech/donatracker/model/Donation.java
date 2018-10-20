package edu.gatech.donatracker.model;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Yuhang Li on 2018/10/18
 *
 * Represents a donated item
 */
public class Donation {

//    // Category inner class
//    public static class Category {
//
//        // Store all possible categories
//        static List<Category> categories = new ArrayList<>();
//        static HashMap<Category, HashMap<Category, Integer>> adjacencyMatrix= new HashMap<>();
//
//        // Fields
//        String name;
//
//        // Constructors
//        public Category(String name, Category parent) {
//            this.name = name;
//            if (adjacencyMatrix.containsKey(this)) {
//
//            } else {
//                HashMap<Category, Integer> upMap = new HashMap<>();
//                upMap.put(parent, -1)
//                adjacencyMatrix.put(this,);
//            }
//        }
//    }

    // Instance variables
    private String uuid;
    private Date donationTime;
    private String donationLocation;
    private String shortDescription;
    private String fullDescription;
    private double valueInUSD;
    private List<String> category;
    private String comments;
//    private Bitmap picture;

    // Constructors
    public Donation() {
    }

    public Donation(String location) {
        uuid = UUID.randomUUID().toString();
        donationTime = Calendar.getInstance().getTime();
        donationLocation = location;
    }

    // Getters and Setters

    public Date getDonationTime() {
        return donationTime;
    }

    public String getDonationLocation() {
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

//    public Bitmap getPicture() {
//        return picture;
//    }
//
//    public void setPicture(Bitmap picture) {
//        this.picture = picture;
//    }
}
