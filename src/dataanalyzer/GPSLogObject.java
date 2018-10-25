/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

/**
 *
 * @author aribdhuka
 */
public class GPSLogObject extends LogObject {
    
    double lat;
    double longi;
    
    public GPSLogObject(String TAG, double lat, double longi, long time) {
        this.TAG = TAG;
        this.lat = lat;
        this.longi = longi;
        this.time = time;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }
    
    public double distanceTo(double lat, double longi) {
        throw new UnsupportedOperationException("Not Supported Yet."); //TODO Implement distance calculation off of two coordinates in decimal form.
    }
}