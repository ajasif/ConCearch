package group8.tcss450.uw.edu.consearch.fragments.artistFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import group8.tcss450.uw.edu.consearch.model.Artist;
import group8.tcss450.uw.edu.consearch.R;


/**
 * @author Team 8
 * @version 1.0
 * Fragment to show a list of similar artists of an artist.
 */
public class SimilarArtistsFragment extends Fragment {

    private static final String OBJECT_KEY = "object_key";

    Artist mArtist;

    public SimilarArtistsFragment() {   }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_similar_artists, container, false);
        mArtist = (Artist) getArguments().getSerializable(OBJECT_KEY);
        ListView listView = (ListView) view.findViewById(R.id.artist_similar_listView);
        if (mArtist.getSimilarArtists() != null)    {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_list_item_1, mArtist.getSimilarArtists());
            listView.setAdapter(adapter);
        }
        return view;
    }

}
