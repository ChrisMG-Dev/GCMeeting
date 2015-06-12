package com.guide.cordobatourplus;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Chris on 28/05/2015.
 */
public class Evento implements Comparable<Evento> {
    private String[] activities;
    private String altitude = "";
    private String longitude = "";
    private String zoom = "";
    private String place = "";
    private String address = "";
    private String date = "";
    private String time = "";
    private String resName = "";
    private String resPhone = "";
    private String streamingUrl = "";
    private boolean hasVoteButton = false;

    public Evento(String[] activities, String altitude, String longitude,
                  String zoom, String place, String address, String date,
                  String time, String resName, String resPhone,
                  String streamingUrl, boolean hasVoteButton) {
        this.activities = activities;
        this.altitude = altitude;
        this.longitude = longitude;
        this.zoom = zoom;
        this.place = place;
        this.address = address;
        this.date = date;
        this.time = time;
        this.resName = resName;
        this.resPhone = resPhone;
        this.streamingUrl = streamingUrl;
        this.hasVoteButton = hasVoteButton;
    }

    public Evento(){

    }

    public String[] getActivities() {
        return activities;
    }

    public void setActivities(String[] activities) {
        this.activities = activities;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getZoom() {
        return zoom;
    }

    public void setZoom(String zoom) {
        this.zoom = zoom;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getResPhone() {
        return resPhone;
    }

    public void setResPhone(String resPhone) {
        this.resPhone = resPhone;
    }

    public String getStreamingUrl() {
        return streamingUrl;
    }

    public void setStreamingUrl(String streamingUrl) {
        this.streamingUrl = streamingUrl;
    }

    @Override
    public int compareTo(Evento another) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date fechaEvento = null;
        Date fechaOtro = null;
        try {
            fechaEvento = df.parse(getDate() + " " + getTime());
            fechaOtro = df.parse(another.getDate() + " " + another.getTime());
        } catch (Exception e) {
            Log.e("Error fecha", e.getMessage());
        }
        if (fechaOtro.after(fechaEvento)) {
            return -1;
        } else if (fechaOtro.before(fechaEvento)) {
            return 1;
        } else {
            return 0;
        }
    }

    public boolean isHasVoteButton() {
        return hasVoteButton;
    }

    public void setHasVoteButton(boolean hasVoteButton) {
        this.hasVoteButton = hasVoteButton;
    }
}
