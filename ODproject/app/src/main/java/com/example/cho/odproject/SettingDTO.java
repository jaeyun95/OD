package com.example.cho.odproject;

public class SettingDTO {
    private double distance;
    private boolean on_off;

    public SettingDTO() {}
    public SettingDTO(double distance, boolean on_off) {
        this.distance = distance;
        this.on_off = on_off;
    }

    public double getDistance(){return this.distance;}
    public boolean getOn_off(){return this.on_off;}

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setOn_off(boolean on_off) {
        this.on_off = on_off;
    }

}
