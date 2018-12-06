package com.example.mpelu.androidgroupproject;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

/**
 * @author mpelu
 * @version 1.0
 * Holds information in preparation for fragment class, and loads fragment layout
 */
public class MovieDetails extends Activity {
    /**
     * Instantiate fragment and implement fragment methods
     * @param savedInstanceState - information passed
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Bundle infoToPass = getIntent().getExtras();

        MovieFragment mFrag = new MovieFragment();
        mFrag.setArguments(infoToPass);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ftrans = fm.beginTransaction();
        ftrans.replace(R.id.movie_frag_location, mFrag);
        ftrans.commit();
    }
}
