package group8.tcss450.uw.edu.consearch.fragments.artistFragments;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;

import group8.tcss450.uw.edu.consearch.model.Artist;
import group8.tcss450.uw.edu.consearch.R;


/**
 * @author Team 8
 * @version 1.0
 * Fragment to show the biography of the artist.
 */
public class ArtistBioFragment extends Fragment implements Serializable {

    private static final String OBJECT_KEY = "object_key";

    TextView textView;
    Artist mArtist;


    public ArtistBioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mArtist = (Artist) getArguments().getSerializable(OBJECT_KEY);
        View view = inflater.inflate(R.layout.fragment_artist_bio, container, false);
        textView = (TextView) view.findViewById(R.id.artist_bio_textView);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        //Depreciated .fromHtml to be used for API versions lower than Nougat to match minimum API
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)  {
            textView.setText(Html.fromHtml(mArtist.getBio()));
        } else {
            textView.setText(Html.fromHtml(mArtist.getBio(), Html.FROM_HTML_OPTION_USE_CSS_COLORS));
        }

        return view;
    }
}
