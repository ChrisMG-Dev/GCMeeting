package com.guide.cordobatourplus;

/**
 * Created by Chris on 29/05/2015.
 */
public class Attraction implements Comparable<Attraction>{
    private String name;
    private String image;
    private String latitude;
    private String longitude;
    private String zoom;
    private String description;

    public Attraction(String name, String image, String latitude, String longitude, String zoom, String description) {
        this.name = name;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.zoom = zoom;
        this.description = description;
    }

    public Attraction(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int compareTo(Attraction another) {
        return getName().compareTo(another.getName());
    }
}
