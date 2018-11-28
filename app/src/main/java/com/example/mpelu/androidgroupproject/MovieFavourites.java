package com.example.mpelu.androidgroupproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MovieFavourites extends AppCompatActivity {
    ListView movieList = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_favourites);

        movieList = findViewById(R.id.movieList);


        movieList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Bundle infoToPass = new Bundle();

                Intent next = new Intent(MovieFavourites.this, MovieDetails.class);
                next.putExtras(infoToPass);
                startActivityForResult(next, 49);

                //TODO button
            }
        });
    }
}
