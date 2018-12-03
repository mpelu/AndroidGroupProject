package com.example.mpelu.androidgroupproject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MovieFavourites extends AppCompatActivity {
    ListView movieList = null;
    Button fragmentButton;
    ArrayList<String> movieArray = new ArrayList<String>();
    MovieAdapter mAdapter;
    Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_favourites);

        mAdapter = new MovieAdapter(this);

        fragmentButton = findViewById(R.id.toMovieFrag);
        fragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(MovieFavourites.this, MovieDetails.class);
                startActivityForResult(next, 51);
            }
        });

        movieList = findViewById(R.id.movieList);

        movieList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Bundle infoToPass = new Bundle();

                infoToPass.putLong("_id", mAdapter.getItemId(position));
                infoToPass.putString("title", movieArray.get(position));
                //etc

                Intent next = new Intent(MovieFavourites.this, MovieDetails.class);
                next.putExtras(infoToPass);
                startActivityForResult(next, 49);
            }
        });
    }

    public class MovieAdapter extends ArrayAdapter<String>{
        public MovieAdapter(Context context) {
            super(context, 0);
        }

        public long getItemId(int position){
            c.moveToPosition(position);
            return c.getInt(c.getColumnIndex("_id"));
        }
    }
}
