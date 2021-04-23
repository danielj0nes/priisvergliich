package com.danielj.priisvergliich.Models;

/*
Class model for the 'User' of the app. Realistically only the lat/long values are stored, but this
model is capable of being scaled easily to fit any additional needs.
*/
public class UserModel {
    private int id;
    private String name;
    private double latitude;
    private double longitude;
    /*Variable instantiation*/
    public UserModel(int id, String name, double latitude, double longitude) {
        this.id = 0;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    /*Argless instantiation*/
    public UserModel() {
    }
    /*Conventional string representation method*/
    @Override
    public String toString() {
        return "UserModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
    /*Default get/set method implementations*/
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
