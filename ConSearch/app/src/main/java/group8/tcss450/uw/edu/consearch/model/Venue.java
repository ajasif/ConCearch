package group8.tcss450.uw.edu.consearch.model;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * @author Team 8
 * @version 1.0
 * Creates a Venue Object
 */

public class Venue implements Serializable, ConSearchObject {

    private String mName;
    private String mId;
    private String mAddress;
    private String mPhone;
    private String mWebSite;
    private String mImage;
    private String mPlaceId;
    private ArrayList<Event> mEventList;

    /**
     * Constructor for the Venue
     * @param name The venue's name
     * @param id The venue's numerical ID from SongKick.com
     * @param address The address where the venue is located
     * @param website The URL for the venue's page hosted on SongKick.com
     */
    public Venue(String name, String id, String address, String phone, String website)   {
        mName = name;
        mId = id;
        mAddress = address;
        mPhone = phone;
        mWebSite = website;
        mEventList = new ArrayList<>();
    }

    /**
     * Gets the venue's name.
     * @return The venue's name
     */
    public String getName() { return mName; }

    /**
     * Gets the venue's ID.
     * @return The venue's ID
     */
    public String getId() {
        return mId;
    }

    /**
     * Gets the city where the venue is located.
     * @return The city
     */
    public String getAddress() {
        return mAddress;
    }

    /**
     * Gets the phone number of the venue.
     * @return The phone number
     */
    public String getPhone()    {   return mPhone;  }


    /**
     * Gets the URL for the venue's page hosted on SongKick.com
     * @return The URL
     */
    public String getWebSite() {
        return mWebSite;
    }

    /**
     * Sets an event list for the venue.
     * @param eventList The event list to add
     */
    public void setEventList(ArrayList<Event> eventList)    {
        mEventList = eventList;
    }

    /**
     * Gets the event list for the venue.
     * @return The list of events
     */
    public ArrayList<Event> getEventList() {
        return mEventList;
    }
}