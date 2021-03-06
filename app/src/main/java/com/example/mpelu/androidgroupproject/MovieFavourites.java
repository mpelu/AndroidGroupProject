package com.example.mpelu.androidgroupproject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @author mpelu
 * @version 1.0
 * List of MovieFavourites. Uses ArrayAdapter inner class to display abridged movie entry
 */
public class MovieFavourites extends AppCompatActivity {
    //activity name
    public static final String ACTIVITY_NAME = "MovieFavourites";

    //ArrayAdapter variables
    ListView movieList = null;
    ArrayList<String> movieArray = new ArrayList<String>();
    MovieAdapter mAdapter;

    //Database variables
    Cursor c;
    public SQLiteDatabase db;
    static final String TABLE_NAME = "Movies";

    /**
     * Creates database opener and prepares information to be passed to fragment when list item is clicked
     * @param savedInstanceState - passed information
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_favourites);

        //initialize database
        MovieDatabaseHelper dbHelp = new MovieDatabaseHelper(this);
        db = dbHelp.getWritableDatabase();
        c = db.rawQuery("SELECT _id, Title, Year, Rated, Runtime, Actors, Plot from Movies", null);

        //get column index for favourites list (and then fragment)
        int title = c.getColumnIndex("Title");
        int year = c.getColumnIndex("Year");

        //get column index for fragment list
        int rated = c.getColumnIndex("Rated");
        int runtime = c.getColumnIndex("Runtime");
        int actors = c.getColumnIndex("Actors");
        int plot = c.getColumnIndex("Plot");

        while(c.moveToNext()){
            String titleYear = c.getString(title) + " (" + c.getInt(year) + ")";
            movieArray.add(titleYear);
        }

        movieList = findViewById(R.id.movieList);
        mAdapter = new MovieAdapter(this);
        movieList.setAdapter(mAdapter);

        movieList = findViewById(R.id.movieList);
        movieList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Bundle infoToPass = new Bundle();

                infoToPass.putInt("position", position);
                infoToPass.putLong("_id", mAdapter.getItemId(position));
                infoToPass.putString("title", c.getString(title));
                infoToPass.putInt("year", c.getInt(year));
                infoToPass.putString("rated", c.getString(rated));
                infoToPass.putInt("runtime", c.getInt(runtime));
                infoToPass.putString("actors", c.getString(actors));
                infoToPass.putString("plot", c.getString(plot));

                Intent next = new Intent(MovieFavourites.this, MovieDetails.class);
                next.putExtras(infoToPass);
                startActivityForResult(next, 49);
            }
        });
    }

    /**
     * Side-effect: delete movie from favourites
     * @param request - intent request
     * @param result -intent result
     * @param data - intent received
     */
    @Override
    protected void onActivityResult(int request, int result, Intent data){
        if(result == 999){
            Bundle extras = data.getExtras();
            deleteMessage(extras.getInt("_id"), extras.getInt("position"));
        }
    }

    /**
     * Delete movie from favourites
     * @param id - id of movie to be deleted
     */
    protected void deleteMessage(int id, int pos){
        try{
            db.delete(TABLE_NAME, "_id" + " = " + id, null);
            movieArray.remove(pos);
            mAdapter.notifyDataSetChanged();
        } catch(Exception e){
            Log.i(ACTIVITY_NAME, "Exception thrown");
        }
    }

    /**
     * Inner Array Adapter class allows manipulation of listview
     */
    public class MovieAdapter extends ArrayAdapter<String>{
        public MovieAdapter(Context context) {  super(context, 0);  }

        /**
         * @return - number of rows in array
         */
        @Override
        public int getCount() { return movieArray.size(); }

        /**
         * @param position - position index
         * @return - item at position index
         */
        public String getItem(int position){ return movieArray.get(position); }

        /**
         * @param position - position of item
         * @param convertView - view to be converted
         * @param parent - parent view
         * @return - array view
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = MovieFavourites.this.getLayoutInflater();
            View result = inflater.inflate(R.layout.movie_favourites_list, null);

            TextView titleYear = result.findViewById(R.id.movieTitleYear);
            titleYear.setText(getItem(position));

            return result;
        }

        /**
         * @param position - index
         * @return - position of item
         */
        public long getItemId(int position){
            c.moveToPosition(position);
            return position;
        }
    }
}
