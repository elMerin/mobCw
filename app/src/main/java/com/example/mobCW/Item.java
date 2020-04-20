package com.example.mobCW;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

/**
 * Interface for model classes. Allows for efficient programming when displaying incidents and roadworks in menus.
 * @author Merin Haslacher, S1624420
 */
public interface Item {

    public String getTitle();

    public void setTitle(String title);

    public String getDescription();

    public void setDescription(String description);

    public String getLink();

    public void setLink(String link);

    public LatLng getCoords();

    public void setCoords(LatLng georss);

    public String getAuthor();

    public void setAuthor(String author);

    public String getComments();

    public void setComments(String comments);

    public Calendar getPubDate();

    public void setPubDate(Calendar pubDate);

    public String toString();
}
