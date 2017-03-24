package group8.tcss450.uw.edu.consearch.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Team 8
 * @version 1.0
 * Artist Object Class
 */

public class Artist implements Serializable, ConSearchObject {

    private String mName;
    private String mID;
    private String mWebsite;
    private String mImage;
    private ArrayList<String> mSimilarArtists;
    private String mBio;
    private Boolean mOnTour;
    private ArrayList<Event> mArtistEvents;

    /**
     * Constructor for the artist object
     * @param name The artist's name
     * @param id The SongKick API numerical ID
     * @param website The URL for the artist's page hosted on SongKick.com
     * @param onTour If the artist is currently on tour
     */
    public Artist(String name, Integer id, String website, Boolean onTour) {
        mName = name;
        mID = String.valueOf(id);
        mWebsite = website;
        mOnTour = onTour;
        mArtistEvents = new ArrayList<>();
    }

    public Artist(String name, Integer id, Boolean onTour) {
        mName = name;
        mID = String.valueOf(id);
        mOnTour = onTour;
        mArtistEvents = new ArrayList<>();
    }

    /**
     * Gets the artist's name
     * @return The artist's name
     */
    public String getName() {
        return mName;
    }

    /**
     * Gets the SongKick numerical ID for the artist
     * @return The ID
     */
    public String getID() {
        return mID;
    }

    /**
     * Gets the URL for the artist page hosted on SongKick.com
     * @return The String of the URL
     */
    public String getWebsite() {
        return mWebsite;
    }

    /**
     * Gets the URL for the artist's image.
     * @return The string of the URL
     */
    public String getImage() {  return mImage;  }

    /**
     * Sets the URL to reach the image.
     * @param image The string of the URL
     */
    public void setImage(String image) {   mImage = image;    }

    /**
     * Gets a list of similar artists.
     * @return An ArrayList of similar artists.
     */
    public ArrayList<String> getSimilarArtists() {
        return mSimilarArtists;
    }

    /**
     * Sets the list of similar artists.
     * @param similarArtists An ArrayList of similar artists
     */
    public void setSimilarArtists(ArrayList<String> similarArtists) {
        mSimilarArtists = similarArtists;
    }

    /**
     * Gets the artist biography.
     * @return A string of the biography.
     */
    public String getBio() {
        return mBio;
    }

    /**
     * Sets the artist's biography.
     * @param bio The artist's biography
     */
    public void setBio(String bio) {
        mBio = bio;
    }

    /**
     * Gets the events the artist will be performing at.
     * @return The list of events.
     */
    public ArrayList<Event> getArtistEvents() {
        return mArtistEvents;
    }

    /**
     * Sets the list of events the artist will be performing at.
     * @param event The list of events
     */
    public void setArtistEvent(Event event)    {
        mArtistEvents.add(event);
    }
}