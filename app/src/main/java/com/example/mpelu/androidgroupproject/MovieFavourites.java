package com.example.mpelu.androidgroupproject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MovieFavourites extends AppCompatActivity {
    ListView movieList = null;
    Button fragmentButton;
    ArrayList<String> movieArray = new ArrayList<String>();
    MovieAdapter mAdapter;
    Cursor c;
    public SQLiteDatabase db;
    static final int VERSION_NUM = 3;
    static final String DATABASE_NAME = "FavoriteMovies";
    static final String TABLE_NAME = "Movies";
    static final String KEY_TITLE = "Title";
    static final String KEY_YEAR = "Year";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_favourites);

        MovieDatabaseHelper dbHelp = new MovieDatabaseHelper(this);
        db = dbHelp.getReadableDatabase();

        c = db.rawQuery("SELECT Title, Year from Movies", null);
        int title = c.getColumnIndex("Title");
        int year = c.getColumnIndex("Year");
//        Log.i("Found results: " , c.getCount()+ " rows");
        while(c.moveToNext()){
            String titleYear = c.getString(title) + " (" + c.getInt(year) + ")";
            movieArray.add(titleYear);
        }
        movieList = findViewById(R.id.movieList);
        mAdapter = new MovieAdapter(this);
        movieList.setAdapter(mAdapter);

//        fragmentButton = findViewById(R.id.toMovieFrag);
//        fragmentButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent next = new Intent(MovieFavourites.this, MovieDetails.class);
//                startActivityForResult(next, 51);
//            }
//        });

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

        @Override
        public int getCount() {
            return movieArray.size();
        }

        public String getItem(int position){
            return movieArray.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = MovieFavourites.this.getLayoutInflater();
            View result = inflater.inflate(R.layout.movie_favourites_list, null);

            TextView titleYear = result.findViewById(R.id.movieTitleYear);
            titleYear.setText(getItem(position));

            return result;
        }

        public long getItemId(int position){
            c.moveToPosition(position);
            return position;

//            c.moveToPosition(position);
//            return c.getInt(c.getColumnIndex("_id")); //TODO IllegalStateException couldn't read row 0, col -1 from CursorWindow
//                                                                    //TODO make sure cursor is initialized correctly before accessing data from it
        }
    }
}
