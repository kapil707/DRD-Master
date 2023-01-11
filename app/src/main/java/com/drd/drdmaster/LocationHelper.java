package com.drd.drdmaster;

public class LocationHelper {

    private double Longitude;
    private double Latitude;
    private String Gettime;
    private String Getdate;

    public LocationHelper(double longitude, double latitude, String gettime, String getdate) {
        Longitude = longitude;
        Latitude = latitude;
        Gettime = gettime;
        Getdate = getdate;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public String getGettime() {
        return Gettime;
    }

    public void setGettime(String gettime) {
        Gettime = gettime;
    }

    public String getGetdate() {
        return Getdate;
    }

    public void setGetdate(String getdate) {
        Getdate = getdate;
    }
}
