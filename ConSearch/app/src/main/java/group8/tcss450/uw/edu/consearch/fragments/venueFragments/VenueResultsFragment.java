package group8.tcss450.uw.edu.consearch.fragments.venueFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;

import group8.tcss450.uw.edu.consearch.R;
import group8.tcss450.uw.edu.consearch.model.Venue;


/**
 * @author Team 8
 * @version 1.0
 * Fragment to show information about the venue a user selected.
 */
public class VenueResultsFragment extends Fragment implements Serializable  {

    private static final String OBJECT_KEY = "object_key";
    private static final String LOCATED_IN = "Venue located in: ";
    private static final String PHONE_NUMBER = "Venue Phone Number: ";
    private static final String WEBSITE = "Venue Website: ";

    public VenueResultsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_venue_results, container, false);
        Venue venue = (Venue) getArguments().getSerializable(OBJECT_KEY);
        TextView nameTextView = (TextView) view.findViewById(R.id.venue_name_textView);
        TextView addTextView = (TextView) view.findViewById(R.id.venue_address_textView);
        TextView phoneTextView = (TextView) view.findViewById(R.id.venue_phone_textView);
        TextView webTextView = (TextView) view.findViewById(R.id.venue_website_textView);
        if (venue != null)  {
            String address = LOCATED_IN + venue.getAddress();
            String website = "No Website Available";
            String phoneNull = "No Phone Number Available";
            String phoneGood = PHONE_NUMBER + venue.getPhone();
            nameTextView.setText(venue.getName());
            addTextView.setText(address);
            if (!venue.getPhone().equals("null"))   {
                phoneTextView.setText(phoneGood);
            } else {
                phoneTextView.setText(phoneNull);
            }
            if (!venue.getWebSite().equals("null")) {
                website = WEBSITE + venue.getWebSite();
                webTextView.setText(website);
            } else {
                webTextView.setText(website);
            }
        }
        return view;
    }
}
