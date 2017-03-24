package group8.tcss450.uw.edu.consearch.database;

import java.util.HashMap;

import group8.tcss450.uw.edu.consearch.model.Artist;
import group8.tcss450.uw.edu.consearch.model.Event;
import group8.tcss450.uw.edu.consearch.model.Venue;

/**
 * @author Team 8
 * @version 1.0
 * Maps to store Artist, Event, and Venue objects using a String key.
 */
public class Maps {
    private HashMap<String, Artist> artistDatabase;
    private HashMap<String, Event> eventDatabase;
    private HashMap<String, Venue> venueDatabase;

    public Maps() {
        artistDatabase = new HashMap<>();
        eventDatabase = new HashMap<>();
        venueDatabase = new HashMap<>();
    }

    /**
     * Checks to see if Artist is in database.
     * @param name String key for hashmap
     * @return True if Artist is in database, false if not
     */
    boolean checkArtist(String name)  {
        return artistDatabase.containsKey(name);
    }

    /**
     * Checks to see if Event is in database.
     * @param name String key for hashmap
     * @return True if Event is in database, false if not
     */
    boolean checkEvent(String name)  {
        return eventDatabase.containsKey(name);
    }

    /**
     * Gets an Artist from the database.
     * @param name The key for the hashmap
     * @return Returns the Artist
     */
    public Artist getArtist(String name)    {
        return artistDatabase.get(name);
    }

    /**
     * Gets an Event from the database.
     * @param name The key for the hashmap
     * @return Returns the Event
     */
    public Event getEvent(String name)    {
        return eventDatabase.get(name);
    }

    /**
     * Gets a Venue from the database.
     * @param name The key for the hashmap
     * @return Returns the Venue
     */
    public Venue getVenue(String name)    {
        return venueDatabase.get(name);
    }

    /**
     * Adds an Artist to the database.
     * @param name The key
     * @param object The Artist
     */
    void addArtist(String name, Artist object)    {
        artistDatabase.put(name, object);
    }

    /**
     * Adds an Event to the database.
     * @param name The key
     * @param object The Event
     */
    void addEvent(String name, Event object)    {
        eventDatabase.put(name, object);
    }

    /**
     * Adds a Venue to the database.
     * @param name the key
     * @param object The Venue
     */
    void addVenue(String name, Venue object)    {
        venueDatabase.put(name, object);
    }
}
