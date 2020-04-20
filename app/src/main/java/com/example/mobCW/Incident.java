package com.example.mobCW;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

/**
 * The model class that is used to represent incidents.
 * @author Merin Haslacher, S1624420
 */
public class Incident implements Item{
    private String title;
    private String description;
    private String link;
    private LatLng coords;
    private String author;
    private String comments;
    private Calendar pubDate;

    /**
     * The constructor for when no parameters are provided. Assigns values to the uninitialized variables.
     */
    public Incident(){
        this.title = "";
        this.title = "";
        this.link = "";
        this.coords = new LatLng(55.860916,-4.251433);
        this.author = "";
        this.comments = "";
        this.pubDate = Calendar.getInstance();
    }

    /**
     * Constructor for when values are specified.
     */
    public Incident(String title, String description, String link, LatLng coords, String author, String comments, Calendar pubDate){
        this.title = title;
        this.title = description;
        this.link = link;
        this.coords = coords;
        this.author = author;
        this.comments = comments;
        this.pubDate = pubDate;
    }

    /**
     * Provides string representation of the incident.
     * @return String version of object.
     */
    @Override
    public String toString() {
        return "\nTitle: " + title + "\n\nDescription: " + description + "\n\nLink: " + link + "\n\nCoordinates: " + coords.latitude + " " + coords.longitude + "\n\nAuthor: " + author + "\n\nComments: " + comments + "\n\nPublication date: " + pubDate.getTime();

    }


    //Getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public LatLng getCoords() {
        return coords;
    }

    public void setCoords(LatLng coords) {
        this.coords = coords;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Calendar getPubDate() {
        return pubDate;
    }

    public void setPubDate(Calendar pubDate) {
        this.pubDate = pubDate;
    }

}
