package group8.tcss450.uw.edu.consearch.fragments.homeFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.io.Serializable;

import group8.tcss450.uw.edu.consearch.R;


/**
 * @author Team 8
 * @version 1.0
 * Fragment containing the Search function
 */
public class SearchFragment extends Fragment implements Serializable{


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context)   {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_search, container, false);
        RadioGroup group = (RadioGroup) view.findViewById(R.id.search_radio_group);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                EditText editText = (EditText) view.findViewById(R.id.home_search_editText);
                InputFilter[] filter = new InputFilter[1];
                switch (checkedId) {
                    case R.id.artist_search_radio_button:
                        filter[0] = new InputFilter.LengthFilter(30);
                        editText.setText("");
                        editText.setFilters(filter);
                        editText.setHint(getString(R.string.search_artist_hint));
                        editText.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case R.id.venue_search_radio_button:
                        filter[0] = new InputFilter.LengthFilter(30);
                        editText.setText("");
                        editText.setFilters(filter);
                        editText.setHint(getString(R.string.search_venue_hint));
                        editText.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case R.id.location_search_radio_button:
                        filter[0] = new InputFilter.LengthFilter(5);
                        editText.setText("");
                        editText.setFilters(filter);
                        editText.setHint(getString(R.string.search_location_hint));
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
            }
        });
        return view;
    }

}
