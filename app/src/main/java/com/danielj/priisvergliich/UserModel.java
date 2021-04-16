package com.danielj.priisvergliich;

public class UserModel {
    private int id;
    private String name;
    private double latitude;
    private double longitude;

    public UserModel(int id, String name, double latitude, double longitude) {
        this.id = 0;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    // argless constructor
    public UserModel() {
    }
    @Override
    public String toString() {
        return "UserModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
    /*
     * Default get/set method implementations
     * */
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
