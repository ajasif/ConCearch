package group8.tcss450.uw.edu.consearch.fragments.artistFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import group8.tcss450.uw.edu.consearch.fragments.eventFragments.EventListFragment;
import group8.tcss450.uw.edu.consearch.model.Artist;
import group8.tcss450.uw.edu.consearch.R;

/**
 * @author Team 8
 * @version 1.0
 * Fragment to show the artist image and buttons to show different fragments in the lower frame.
 */
public class ArtistResultsUpperFragment extends Fragment {

    private static final String OBJECT_KEY = "object_key";
    private static final String TYPE_KEY = "type_key";

    ImageView artistImage;
    Artist mArtist;
    TextView textView;
    ArtistBioFragment mBioFragment;
    SimilarArtistsFragment mSimilarFragment;
    EventListFragment mEventFragment;

    public ArtistResultsUpperFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_results_upper, container, false);
        artistImage = (ImageView) view.findViewById(R.id.artist_imageView);
        Bundle bundle = getArguments();
        mArtist = (Artist) bundle.getSerializable(OBJECT_KEY);
        if (mArtist != null)    {
            try{
                if (!mArtist.getImage().equals("")) {
                    Picasso.with(getActivity())
                            .load(mArtist.getImage())
                            .into(artistImage);
                } else {
                    Picasso.with(getActivity())
                            .load(R.drawable.iconimage).resize(100, 100).into(artistImage);
                }
            } catch (NullPointerException e)    {
                Picasso.with(getActivity())
                        .load(R.drawable.iconimage).resize(100, 100).into(artistImage);
            }
        }
        developFragments();
        textView = (TextView) view.findViewById(R.id.artist_name_textView);
        textView.setText(mArtist.getName());
        Button simButton = (Button) view.findViewById(R.id.similar_artists_button);
        simButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .hide(mBioFragment)
                        .hide(mEventFragment)
                        .show(mSimilarFragment);
                transaction.commit();
            }
        });
        final Button eventButton = (Button) view.findViewById(R.id.artist_events_button);
        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .hide(mBioFragment)
                        .hide(mSimilarFragment)
                        .show(mEventFragment);
                transaction.commit();
            }
        });
        Button bioButton = (Button) view.findViewById(R.id.artist_bio_button);
        bioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .hide(mSimilarFragment)
                        .hide(mEventFragment)
                        .show(mBioFragment);
                transaction.commit();
            }
        });
        return view;
    }

    /**
     * Develops the lower fragments to show artist biography, similar artists, and events.
     */
    private void developFragments() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(OBJECT_KEY, mArtist);
        bundle.putString(TYPE_KEY, "artist");
        mBioFragment = new ArtistBioFragment();
        mSimilarFragment = new SimilarArtistsFragment();
        mEventFragment = new EventListFragment();
        mBioFragment.setArguments(bundle);
        mSimilarFragment.setArguments(bundle);
        mEventFragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.home_lower_frame, mBioFragment)
                .add(R.id.home_lower_frame, mSimilarFragment)
                .hide(mSimilarFragment)
                .add(R.id.home_lower_frame, mEventFragment)
                .hide(mEventFragment)
                .addToBackStack(null);
        transaction.commit();
    }
}
