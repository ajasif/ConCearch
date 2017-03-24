package group8.tcss450.uw.edu.consearch.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Team 8
 * @version 1.0
 * Event Object Class.
 */

public class Event implements Serializable, ConSearchObject {


    private String mName;
    private String mID;
    private String mLocation;
    private String mTime;
    private ArrayList<String> mArtists;
    private String mVenueName;
    private String mWebsite;
    private ArrayList<Event> mArtistEvents;

    /**
     * Constructor for the Event Object.
     * @param name The event's name
     * @param location The event's location
     * @param artists The artists performing at the event
     * @param venueName The venue's name for the event
     * @param website The URL for the event hosted on SongKick.com
     */
    public Event(String name, String id, String location, ArrayList<String> artists,
                 String venueName, String time, String website)  {
        mName = name;
        mID = id;
        mLocation = location;
        mArtists = artists;
        mVenueName = venueName;
        mTime = time;
        mWebsite = website;
    }

    /**
     * Gets the name of the Event.
     * @return The event's name
     */
    public String getName() {
        return mName;
    }

    /**
     * Gets the lat,lon of the event.
     * @return The event's location
     */
    public String getLocation() {
        return mLocation;
    }

    /**
     * Gets the list of artists performing at the event.
     * @return The list of artists
     */
    public ArrayList<String> getArtists() {
        return mArtists;
    }

    /**
     * Gets the name of the venue the event is occurring.
     * @return The venue's name
     */
    public String getVenueName() {
        return mVenueName;
    }

    /**
     * Gets the URL for the event's page hosted on Songkick.com
     * @return The event's URL
     */
    public String getWebsite() {
        return mWebsite;
    }

    /**
     * Gets the event id.
     * @return The event id.
     */
    public String getId()   { return mID; }

    /**
     * Gets the date of the event.
     * @return The date of the event
     */
    public String getTime() {
        return mTime;
    }
}