package com.example.mobCW;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;



/**
 * This abstract class reduces redundancy by providing functions that are used by both of the fragments.
 * @author Merin Haslacher, S1624420
 */
public abstract class AbstractFragment extends Fragment implements Serializable {
    private String currentRoadworksURL = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
    private String plannedRoadworksURL = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
    private String currentIncidentsURL = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";

    public TextView loadingInfo;
    public TextView searchError;
    public TextView searchInfo;
    private static final String TAG = "AbstractFragment";
    protected Activity activity = getActivity();



    /**
     * This method is used to assign the context the context whenever the fragment is added.
     * @param context The context of the activity.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            activity=(Activity) context;
        }
    }

    /**
     * This method turns xml data into roadworks and adds them to the repository.
     * @param xmlData The xml that is retrieved from the website.
     * @param url  The url from which the data was retrieved, to identify whether the source was current roadworks or planned roadworks.
     */
    public void parseRoadworks(String xmlData, String url){
        Roadwork roadwork = null;
        LinkedList<Roadwork> list = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput( new StringReader( xmlData ) );
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    switch (xpp.getName().toUpperCase()) {
                        case "CHANNEL":
                            list = new LinkedList<Roadwork>();
                            break;
                        case "ITEM":
                            roadwork = new Roadwork();
                            break;
                        case "TITLE":
                            if(roadwork != null)
                                roadwork.setTitle(xpp.nextText());
                            break;
                        case "DESCRIPTION":
                            if(roadwork != null){
                                String description = xpp.nextText();
                                roadwork.setDescription(description);
                                Calendar[] dates = parseDate(description);
                                roadwork.setStartDate(dates[0]);
                                roadwork.setEndDate(dates[1]);
                            }
                            break;
                        case "LINK":
                            if(roadwork != null)
                                roadwork.setLink(xpp.nextText());
                            break;
                        case "POINT":
                            String[] nextLine = xpp.nextText().split(" ");
                            LatLng coords = new LatLng(Double.parseDouble(nextLine[0]),Double.parseDouble(nextLine[1]));
                            roadwork.setCoords(coords);
                            break;
                        case "AUTHOR":
                            roadwork.setAuthor(xpp.nextText());
                            break;
                        case "COMMENTS":
                            roadwork.setComments(xpp.nextText());
                            break;
                        case "PUBDATE":
                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
                            cal.setTime(sdf.parse(xpp.nextText()));
                            roadwork.setPubDate(cal);
                            break;

                    }
                } else if (eventType == XmlPullParser.END_TAG) {

                    switch (xpp.getName().toUpperCase()) {
                        case "ITEM":
                            list.add(roadwork);
                            break;
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            Log.e(TAG,"Parsing error" + e.toString());
        }
        catch (IOException e) {
            Log.e(TAG,"IO parsing error" + e.toString());
        } catch (ParseException e) {
            Log.e(TAG,"Date parsing error" + e.toString());
        }
        Log.e(TAG,"End of document");

        if(url.equals(currentRoadworksURL)){
            Repository.setCurrentRoadworks(list);
        }
        else if(url.equals(plannedRoadworksURL)){
            Repository.setPlannedRoadworks(list);
        }

    }


    /**
     * This parses incidents and adds them to the repository.
     * @param xmlData The xml that is retrieved from the website.
     */
    public void parseIncidents(String xmlData){
        Incident incident = null;
        LinkedList<Incident> list = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput( new StringReader( xmlData ) );
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    switch (xpp.getName().toUpperCase()) {
                        case "CHANNEL":
                            list = new LinkedList<Incident>();
                            break;
                        case "ITEM":
                            incident = new Incident();
                            break;
                        case "TITLE":
                            if(incident != null)
                                incident.setTitle(xpp.nextText());
                            break;
                        case "DESCRIPTION":
                            if(incident != null){
                                String description = xpp.nextText();
                                incident.setDescription(description);
                            }
                            break;
                        case "LINK":
                            if(incident != null)
                                incident.setLink(xpp.nextText());
                            break;
                        case "POINT":
                            String[] nextLine = xpp.nextText().split(" ");
                            LatLng coords = new LatLng(Double.parseDouble(nextLine[0]),Double.parseDouble(nextLine[1]));
                            incident.setCoords(coords);
                            break;
                        case "AUTHOR":
                            incident.setAuthor(xpp.nextText());
                            break;
                        case "COMMENTS":
                            incident.setComments(xpp.nextText());
                            break;
                        case "PUBDATE":
                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
                            cal.setTime(sdf.parse(xpp.nextText()));
                            incident.setPubDate(cal);
                            break;

                    }
                } else if (eventType == XmlPullParser.END_TAG) {

                    switch (xpp.getName().toUpperCase()) {
                        case "ITEM":
                            list.add(incident);
                            break;
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            Log.e(TAG,"Parsing error" + e.toString());
        }
        catch (IOException e) {
            Log.e(TAG,"IO parsing error" + e.toString());
        } catch (ParseException e) {
            Log.e(TAG,"Date parsing error" + e.toString());
        }

        Log.e(TAG,"End of document");
        Repository.setIncidents(list);
    }


    /**
     * This method gets the date of the incident/roadwork by parsing the description.
     * @param description Description of the item, which includes the date.
     * @return Returns calendar object that is obtained from the description.
     */
    public Calendar[] parseDate(String description) throws ParseException {
        String split[] = description.split(" ");

        Date date = new SimpleDateFormat("MMMM").parse(split[4]);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        String[] hMin = split[7].split(":");

        Calendar startDate = Calendar.getInstance();
        startDate.set(Integer.parseInt(split[5]), cal.get(Calendar.MONTH), Integer.parseInt(split[3]),Integer.parseInt(hMin[0]), Integer.parseInt(hMin[1].substring(0,1)),0);

        date = new SimpleDateFormat("MMMM").parse(split[12]);
        cal.setTime(date);

        hMin = split[15].split(":");

        Calendar endDate = Calendar.getInstance();
        endDate.set(Integer.parseInt(split[13]), cal.get(Calendar.MONTH), Integer.parseInt(split[11]),Integer.parseInt(hMin[0]), Integer.parseInt(hMin[1].substring(0,1)),0);

        Calendar[] dates = {startDate,endDate};
        return dates;
    }


    /**
     * This clears loading/error text and calls finishLoad, which displays data.
     */
    public void uiFinishLoading(){
        finishLoad();
        searchError.setText("");
        loadingInfo.setText("");
    }

    /**
     * This notifies the user that data is currently being loaded.
     */
    public void uiLoading(){
        loadingInfo.setText("Loading data...");
        searchError.setText("");
        searchInfo.setText("");
    }

    /**
     * This notifies the user that data couldn't be loaded because a connection wasn't established.
     */
    public void uiErrorLoading(){
        searchError.setText("Couldn't connect to internet.");
        finishLoad();
        searchInfo.setText("");
        loadingInfo.setText("");
    }

    /**
     * This forces fragments to implement finishLoad, which displays data.
     */
    public abstract void finishLoad();

}
