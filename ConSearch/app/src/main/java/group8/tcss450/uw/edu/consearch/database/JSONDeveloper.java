package group8.tcss450.uw.edu.consearch.database;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import group8.tcss450.uw.edu.consearch.activities.MainActivity;
import group8.tcss450.uw.edu.consearch.model.Artist;
import group8.tcss450.uw.edu.consearch.model.ConSearchObject;
import group8.tcss450.uw.edu.consearch.model.Event;
import group8.tcss450.uw.edu.consearch.model.Venue;

/**
 * @author Team 8
 * @version 1.0
 * The JSON developer takes in JSON objects and pulls out information to create Artist, Venue, Event
 * objects.
 */

public final class JSONDeveloper {

    private static Maps db;
    private Context context;
    public JSONDeveloper(Context context) { this.context = context; }

    /**
     * Called when a user searches for an artist.  It retrieves the JSON Array from the
     * search results and sends the array to the helper method developBasicArtist.
     * @param object The JSON object of SongKick API search results
     * @return An ArrayList of strings of artist names
     */
    public static ArrayList<ConSearchObject> developArtistSearch(JSONObject object)   {
        ArrayList<ConSearchObject> names = new ArrayList<>();
        db = MainActivity.DataBase;
        try {
            JSONObject resultsPage = object.getJSONObject("resultsPage");
            JSONObject results = resultsPage.getJSONObject("results");
            JSONArray artists = results.getJSONArray("artist");
            names = developBasicArtist(names, artists);
        } catch (JSONException e)   {
            e.printStackTrace();
        }
        return names;
    }

    /**
     * Helper method for developArtistSearch to create Artist objects based off the JSON Array.
     * @param names The ArrayList to populate with artist names.
     * @param artists The JSON Array of artists from the SongKick API search results
     * @return ArrayList of strings of artist names
     */
    private static ArrayList<ConSearchObject> developBasicArtist(ArrayList<ConSearchObject> names,
                                                        JSONArray artists)  {
            Artist object;
            String name;
            int id;
            Boolean onTour = true;
            JSONObject artist;
            try {
                for (int i = 0; i < artists.length(); i++)  {
                    artist = artists.getJSONObject(i);
                    name = artist.getString("displayName");

                    id = artist.getInt("id");
                    if (artist.getString("onTourUntil") == null) {
                        onTour = false;
                    }
                    object = new Artist(name, id, onTour);
                    names.add(object);
                    if (!db.checkArtist(name))    {
                        db.addArtist(name, object);
                    }
                }
            } catch (JSONException e)   {
                e.printStackTrace();
            }
            return names;
    }

    /**
     * Used to get detailed information on an artist using two different APIs.  The LastFM API will
     * get the artist's image, biography, and similar artists.  The SongKick API will get the
     * artist's event list.
     * @param songKick The JSONObject from SongKick
     * @param lastFM The JSONObject from LastFM
     * @param theArtist The Artist that will be developed
     */
    public static void developArtist(JSONObject songKick, JSONObject lastFM, Artist theArtist)  {
        db = MainActivity.DataBase;
        Event eventObject;
        Artist artist = theArtist;
        String eventName;
        String eventId;
        String city;
        String time;
        String venueName;
        String artistName;
        int artistID;
        String website;
        ArrayList<String> artistNames;
        String image;
        ArrayList<String> similarArtists = new ArrayList<>();
        String summary;
        try {
            JSONObject resultsPage = songKick.getJSONObject("resultsPage");
            JSONObject results = resultsPage.getJSONObject("results");
            if (results.has("event")) {
                JSONArray events = results.getJSONArray("event");
                for (int i = 0; i < events.length(); i++)   {
                    JSONObject event = events.getJSONObject(i);
                    JSONObject start = event.getJSONObject("start");
                    JSONObject location = event.getJSONObject("location");
                    JSONObject venue = event.getJSONObject("venue");
                    JSONArray performance = event.getJSONArray("performance");
                    eventName = event.getString("displayName");
                    eventId = event.getString("id");
                    time = start.getString("time");
                    city = location.getString("city");
                    venueName = venue.getString("displayName");
                    website = event.getString("uri");
                    artistNames = new ArrayList<>();
                    for (int j = 0; j < performance.length(); j++)  {
                        JSONObject performerToplevel = performance.getJSONObject(j);
                        JSONObject performer = performerToplevel.getJSONObject("artist");
                        artistName = performer.getString("displayName");
                        artistID = performer.getInt("id");

                        db.addArtist(artistName,
                                new Artist(artistName, artistID, true));

                        artistNames.add(artistName);
                    }
                    eventObject = new Event(eventName, eventId, city, artistNames, venueName, time,
                            website);
                    if (!db.checkEvent(eventName)) {
                        db.addEvent(eventName, eventObject);
                    }
                    artist.setArtistEvent(eventObject);
                }
            }
            if (lastFM.has("artist"))   {
                JSONObject last = lastFM.getJSONObject("artist");
                JSONArray images = last.getJSONArray("image");
                JSONObject imageArray = images.getJSONObject(3);
                image = imageArray.getString("#text");
                JSONObject similar = last.getJSONObject("similar");
                JSONArray artists = similar.getJSONArray("artist");
                for (int i = 0; i < artists.length(); i++)  {
                    JSONObject simName = artists.getJSONObject(i);
                    similarArtists.add(simName.getString("name"));
                }
                JSONObject biography = last.getJSONObject("bio");
                summary = biography.getString("summary");
                artist.setImage(image);
                artist.setSimilarArtists(similarArtists);
                artist.setBio(summary);
            } else {
                artist.setBio("No Bio Available For This Artist");
                artist.setImage("");
            }
        } catch (JSONException e)   {
            e.printStackTrace();
        }
    }

    /**
     * Called when a user searches for a venue.
     * @param object The JSON object from the SongKick API search results
     * @return An ArrayList of strings of venue names
     */
    public static ArrayList<ConSearchObject> developVenues(JSONObject object)   {
        ArrayList<ConSearchObject> names = new ArrayList<>();
        String name;
        String street;
        String city;
        String state = "";
        String country = "";
        String zipcode = "";
        String address;
        String website = "";
        String phone = "";
        String id;
        try {
            JSONObject resultsPage = object.getJSONObject("resultsPage");
            if (resultsPage.getInt("totalEntries") > 0) {
                JSONObject results = resultsPage.getJSONObject("results");
                JSONArray venues = results.getJSONArray("venue");
                for (int i = 0; i < venues.length(); i++)   {
                    JSONObject venue = venues.getJSONObject(i);
                    JSONObject cityObject = venue.optJSONObject("city");
                    if (cityObject.has("country"))  {
                        JSONObject countryObject = cityObject.getJSONObject("country");
                        country = countryObject.getString("displayName");
                    }
                    if (cityObject.has("state"))    {
                        JSONObject stateObject = cityObject.getJSONObject("state");
                        state = stateObject.getString("displayName");
                    }
                    name = venue.getString("displayName");
                    if (venue.getString("zip") != null) {
                        zipcode = venue.getString("zip");
                    }
                    street = venue.getString("street");
                    city = cityObject.getString("displayName");
                    if (venue.getString("phone") != null)   {
                        phone = venue.getString("phone");
                    }
                    id = String.valueOf(venue.getInt("id"));
                    if (venue.getString("website") != null) {
                        website = venue.getString("website");
                    }
                    address = street + ", " + city;
                    if (!state.equals(""))  {
                        address = address + ", " + state;
                    }
                    if (!zipcode.equals(""))    {
                        address = address + ", " + zipcode;
                    }
                    if (!country.equals(""))    {
                        address = address + ", " + country;
                    }
                    Venue venueObject = new Venue(name, id, address, phone, website);
                    names.add(venueObject);
                    db.addVenue(name, venueObject);
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return names;
    }

    /**
     * Called when a user selects a venue from the search results.  This gets the event information
     * of the venue.
     * @param object The JSON object from the SongKick API
     * @return An ArrayList of Event objects
     */
    public static ArrayList<Event> addVenueEvents(JSONObject object)    {
        String eventName;
        String eventId;
        String location;
        String eventTime;
        String website;
        String venueName;
        ArrayList<String> artists;
        ArrayList<Event> eventList = new ArrayList<>();
        try {
            JSONObject resultsPage = object.getJSONObject("resultsPage");
            JSONObject results = resultsPage.getJSONObject("results");
            if (results.has("event"))   {
                JSONArray events = results.getJSONArray("event");
                for (int i = 0; i < events.length(); i++)   {
                    JSONObject event = events.getJSONObject(i);
                    JSONObject time = event.getJSONObject("start");
                    JSONObject venue = event.getJSONObject("venue");
                    JSONArray performers = event.getJSONArray("performance");
                    eventName = event.getString("displayName");
                    eventId = String.valueOf(event.getInt("id"));
                    eventTime = time.getString("time");
                    venueName = venue.getString("displayName");
                    location = venue.getString("lat") + "," + venue.getString("lng");
                    website = venue.getString("uri");
                    artists = new ArrayList<>();
                    for (int j = 0; j < performers.length(); j++)   {
                        JSONObject artist = performers.getJSONObject(j);
                        String artistName = artist.getString("displayName");
                        int artistId = artist.getInt("id");
                        db.addArtist(artistName, new Artist(artistName,
                                artistId, true));
                        artists.add(artistName);
                    }
                    Event eventObject = new Event(eventName, eventId, location, artists, venueName,
                            eventTime, website);
                    eventList.add(eventObject);
                    db.addEvent(eventName, eventObject);
                }
            }
        } catch (JSONException e)   {
            e.printStackTrace();
        }
        return eventList;
    }

    /**
     * Called when a user searches with a zip code.
     * @param object The JSON object from the SongKick API search results
     * @return An ArrayList of strings of Event names
     * @throws JSONException
     */
    public static ArrayList<String> developEvents(JSONObject object) throws JSONException  {
        ArrayList<String> eventList = new ArrayList<>();
        String eventName;
        String eventID;
        String eventLocation;
        ArrayList<String> artistList;
        String venueName;
        String eventTime;
        String website;

        JSONObject resultsPage = object.getJSONObject("resultsPage");
        JSONObject results = resultsPage.getJSONObject("results");
        JSONArray events = results.getJSONArray("event");
        for (int i = 0; i < events.length(); i++)   {
            JSONObject event = events.getJSONObject(i);
            JSONObject start = event.getJSONObject("start");
            eventTime = start.getString("time");
            JSONArray performers = event.getJSONArray("performance");
            artistList = new ArrayList<>();
            for (int j = 0; j < performers.length(); j++)   {
                JSONObject performer = performers.getJSONObject(j);
                JSONObject artist = performer.getJSONObject("artist");
                String artistName = artist.getString("displayName");
                int artistId = artist.getInt("id");
                Artist theArtist = new Artist(artistName, artistId, true);
                MainActivity.DataBase.addArtist(artistName, theArtist);
                artistList.add(artistName);
            }
            website = event.getString("uri");
            eventID = event.getString("id");
            JSONObject venue = event.getJSONObject("venue");
            venueName = venue.getString("displayName");
            JSONObject metroArea = venue.getJSONObject("metroArea");
            JSONObject state = metroArea.getJSONObject("state");
            eventLocation = metroArea.getString("displayName") + state.getString("displayName");
            eventName = event.getString("displayName");
            eventList.add(eventName);
            Event theEvent = new Event(eventName, eventID, eventLocation, artistList, venueName
                    ,eventTime, website);
            db.addEvent(eventName, theEvent);
        }
        return eventList;
    }
}
