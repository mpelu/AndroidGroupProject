package com.example.mpelu.androidgroupproject;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author mpelu
 * @version 1.0
 * Splash page for Movie Activity. Displays results form user query using Async Task inner class
 */
public class Movies extends AppCompatActivity {
    public static final String ACTIVITY_NAME = "Movies";
    Context ctx = this;
    Toolbar movieBar = null;
    ProgressBar movieProgress = null;
    Button addFave = null;

//    TextView searchResult;
//    TextView mYear;
//    TextView mRated;
//    TextView mRuntime;
//    TextView mActors;
//    TextView mPlot;
//    ImageView mPoster;

    //Async Task object
    MovieQuery mQuery;

    //Database variables
    public SQLiteDatabase db;
    Cursor c;
    static final int VERSION_NUM = 2;
    static final String DATABASE_NAME = "FavoriteMovies";
    static final String TABLE_NAME = "Movies";
    static final String KEY_TITLE = "Title";
    static final String KEY_YEAR = "Year";
    static final String KEY_RATED = "Rated";
    static final String KEY_RUNTIME = "Runtime";
    static final String KEY_ACTORS = "Actors";
    static final String KEY_PLOT = "Plot";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        movieBar = findViewById(R.id.toolbar);
        setSupportActionBar(movieBar);

        MovieDatabaseHelper dbHelp = new MovieDatabaseHelper(this);
        db = dbHelp.getWritableDatabase();

        addFave = findViewById(R.id.movieAdd);
        addFave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if user query retrieved a result, insert movie details as a row into database
                if(mQuery.year != 0){
                    ContentValues newRow = new ContentValues();
                    newRow.put(KEY_TITLE, mQuery.title);
                    newRow.put(KEY_YEAR, mQuery.year);
                    newRow.put(KEY_RATED, mQuery.rated);
                    newRow.put(KEY_RUNTIME, mQuery.runtime);
                    newRow.put(KEY_ACTORS, mQuery.actors);
                    newRow.put(KEY_PLOT, mQuery.plot);

                    long id = db.insert(TABLE_NAME, "", newRow);

                    //arrayadapter notify dataset changed

//                String newFilename = "picture"+ id + ".png";
//                FileOutputStream outputStream = null;
//                try {
//                    outputStream = openFileOutput(newFilename, Context.MODE_PRIVATE);
//                    mQuery.picture.compress(Bitmap.CompressFormat.PNG, 80, outputStream); //TODO compress on null object (npe)
//                    outputStream.flush();
//                    outputStream.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                }
                Snackbar.make(addFave, R.string.movieSnack, Snackbar.LENGTH_LONG).show();
            }
        });

        movieProgress = findViewById(R.id.movieProgress);
        movieProgress.setVisibility(View.VISIBLE);
    }

    /**
     * Inflate options menu and handle query text submission
     * @param menu - Toolbar menu
     * @return if Menu handled query using built-in methods
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.movie_nav, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView sView = (SearchView)searchItem.getActionView();

        sView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query){
                addFave.setVisibility(View.INVISIBLE);
                mQuery = new MovieQuery(query);
                mQuery.execute();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText){ return false; }
        });
        return true;
    }

    /**
     * Handle menu option selected
     * @param item - MenuItem selected
     * @return false
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.faves:
                Intent next = new Intent(Movies.this, MovieFavourites.class);
                startActivityForResult(next, 89);
                break;
            case R.id.movieStats:
                c = db.rawQuery("select AVG(Runtime), MAX(Runtime), MIN(Runtime), AVG(Year), MAX(Year), MIN(Year) from Movies", null);


                AlertDialog.Builder statsBuilder = new AlertDialog.Builder(ctx);
                statsBuilder.setMessage("")
                        .setTitle(R.string.movie_stats_dialog_title)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO  SQL stats



                            }
                        })
//                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
//                            @Override
//                            public void onClick(DialogInterface dialog, int which){ }
//                        })
                        .show();
                break;
            case R.id.movieAbout:
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setMessage(R.string.movie_dialog)
                        .setTitle(R.string.movie_dialog_title)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { }
                        })
//                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
//                            @Override
//                            public void onClick(DialogInterface dialog, int which){ }
//                        })
                        .show();
                break;
            default:
        }
        return false;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        db.close();
    }


    public class MovieQuery extends AsyncTask<String, Integer, String> {
        //User input query
        String queryString;

        //Movie details
        String title="";
        int year;
        String rated;
        int runtime;
        String actors;
        String plot;
        String poster;
        Bitmap picture;

        //Constructor to pass user query
        public MovieQuery(String query){ this.queryString = query; }

        //Query http server with user query
        public String doInBackground(String... args) {
            try {
                String queryUrl = "http://www.omdbapi.com/?t=" + URLEncoder.encode(queryString, "UTF-8") + "&r=xml&apikey=b32928c2";
                URL url = new URL(queryUrl);
                HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
                InputStream response = urlConnect.getInputStream();

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(response, "UTF-8");

                while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                    switch (xpp.getEventType()) {
                        case XmlPullParser.START_TAG:
                            String name = xpp.getName();
                            if (name.equals("movie")) {
                                title = xpp.getAttributeValue(null, "title");
                                publishProgress(5);
                                year = Integer.parseInt(xpp.getAttributeValue(null, "year"));
                                publishProgress(15);
                                rated = xpp.getAttributeValue(null, "rated");
                                publishProgress(25);
                                String run = xpp.getAttributeValue(null, "runtime");
                                String runTrim = run.substring(0, run.indexOf(" "));
                                runtime = Integer.parseInt(runTrim);
                                publishProgress(35);
                                actors = xpp.getAttributeValue(null, "actors");
                                publishProgress(45);
                                plot = xpp.getAttributeValue(null, "plot");
                                publishProgress(55);
                                poster = xpp.getAttributeValue(null, "poster");
                                publishProgress(75);
                                //track if query returned result
                            } else if (name.equals("error")) {
                                return "error";
                            }
                            break;
                        case XmlPullParser.TEXT:
                            break;
                        default:
                    }
                    xpp.next();
                }
            } catch (Exception e) {
                Log.i("Exception", e.getMessage());
            }

            picture = HttpUtils.getImage(poster);
            publishProgress(100);

            return "finished";
        }

        public void onProgressUpdate(Integer... args) {
            movieProgress.setVisibility(View.VISIBLE);
            movieProgress.setProgress(args[0]);
        }

        /**
         * Sets XML views to queried data
         * @param result - string passed from doInBackground
         */
        public void onPostExecute(String result) {
            TextView titleV = findViewById(R.id.searchTitle);
            TextView yearV = findViewById(R.id.searchYear);
            TextView ratedV = findViewById(R.id.searchRated);
            TextView runtimeV = findViewById(R.id.searchRuntime);
            TextView actorsV = findViewById(R.id.searchActors);
            TextView plotV = findViewById(R.id.searchPlot);
            ImageView posterV = findViewById(R.id.searchPoster);

            if(result.equals("error")) {
                Toast.makeText(ctx, R.string.movieToast, Toast.LENGTH_LONG).show();
                return;
            }

            titleV.setText(title + "");
            yearV.setText(year + "");
            ratedV.setText("Rated " + rated);
            runtimeV.setText(runtime + " min");
            actorsV.setText(actors + "");
            plotV.setText(plot + "");
            posterV.setImageBitmap(picture);

            movieProgress.setVisibility(View.INVISIBLE);
            addFave.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Helper class to retrieve picture for Async task
     */
    static class HttpUtils {
        public static Bitmap getImage(URL url) {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    return BitmapFactory.decodeStream(connection.getInputStream());
                } else
                    return null;
            } catch (Exception e) {
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        public static Bitmap getImage(String urlString) {
            try {
                URL url = new URL(urlString);
                return getImage(url);
            } catch (MalformedURLException e) {
                return null;
            }
        }
    }
}