package com.example.mobCW;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

/**
 * Model class that represents roadworks
 * @author Merin Haslacher, S1624420
 */
public class Roadwork implements Item{
    private String title;
    private String description;
    private Calendar startDate;
    private Calendar endDate;
    private String link;
    private LatLng coords;
    private String author;
    private String comments;
    private Calendar pubDate;

    /**
     * The constructor for when no parameters are provided. Assigns values to the uninitialized variables.
     */
    public Roadwork(){
        this.title = "";
        this.description = "";
        this.startDate = Calendar.getInstance();
        this.endDate = Calendar.getInstance();
        this.link = "";
        this.coords = new LatLng(55.860916,-4.251433);
        this.author = "";
        this.comments = "";
        this.pubDate = Calendar.getInstance();
    }

    /**
     * Constructor for when values are specified.
     */
    public Roadwork(String title, String description, Calendar startDate, Calendar endDate, String link, LatLng coords, String author, String comments, Calendar pubDate){
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
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
        return "\nTitle: " + title + "\n\nDescription: " + description + "\n\nStart date: " + startDate.getTime() + "\n\nEnd date: " + endDate.getTime() + "\n\nLink: " + link + "\n\nCoordinates: " + coords.latitude + " " + coords.longitude + "\n\nAuthor: " + author + "\n\nComments: " + comments + "\n\nPublication date: " + pubDate.getTime();
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

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
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
