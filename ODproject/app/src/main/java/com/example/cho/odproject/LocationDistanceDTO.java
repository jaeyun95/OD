package com.example.cho.odproject;

public class LocationDistanceDTO {
    private String category;
    private String title;
    private String date;
    private String address;
    private String memo;
    private double latitude;
    private double longitude;
    private double distance;

    public LocationDistanceDTO() {}
    public LocationDistanceDTO(String title, String category, String date, String address, String memo, double latitude, double longitude, double distance) {
        this.title = title;
        this.category = category;
        this.date = date;
        this.address = address;
        this.memo = memo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }

    public String getCategory(){return this.category;}
    public String getTitle(){return this.title;}
    public String getDate(){return this.date;}
    public String getAddress(){return this.address;}
    public String getMemo(){return this.memo;}
    public double getLatitude(){return this.latitude;}
    public double getLongitude(){return this.longitude;}
    public double getDistance(){return this.distance;}

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAddress(String location) {
        this.address = location;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
