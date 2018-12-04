package com.example.mpelu.androidgroupproject;

import android.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class MovieDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Bundle infoToPass = getIntent().getExtras();

        MovieFragment mFrag = new MovieFragment();

        mFrag.setArguments(infoToPass);
        FragmentManager fm = getFragmentManager();
        android.app.FragmentTransaction ftrans = fm.beginTransaction();
//        ftrans.replace(R.id.movie_frag_location, mFrag);
        ftrans.commit();
        //this holds info
    }
}
