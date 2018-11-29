package com.example.mpelu.androidgroupproject;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import org.w3c.dom.Text;
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

public class Movies extends AppCompatActivity {
    public static final String ACTIVITY_NAME = "Movies";
    Context ctx = this;
    Toolbar movieBar = null;
    MovieQuery mQuery;
    Button addFave = null;

    TextView searchResult;
    TextView mYear;
    TextView mRated;
    TextView mRuntime;
    TextView mActors;
    TextView mPlot;
    ImageView mPoster;

    ProgressBar movieProgress = null;

    public SQLiteDatabase db;
    static final int VERSION_NUM = 2;
    static final String DATABASE_NAME = "FavoriteMovies";
    static final String TABLE_NAME = "Movies";
    static final String KEY_TITLE = "Title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        movieBar = findViewById(R.id.toolbar);
        setSupportActionBar(movieBar);

        MovieDatabaseHelper dbHelp = new MovieDatabaseHelper(this);
        db = dbHelp.getReadableDatabase();

//        movieProgress.setVisibility(View.INVISIBLE);

        searchResult = findViewById(R.id.searchResult);

//        searchResult.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                if(searchResult.getText() != null){
//                    Bundle infoToPass = new Bundle();
//
//                    Intent next = new Intent(Movies.this, MovieDetails.class);
//                    next.putExtras(infoToPass);
//                    startActivityForResult(next, 49);
//
//                    //TODO display on main
//
//                }
//            }
//        });

        addFave = findViewById(R.id.movieAdd);

        addFave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //do you really want to save?  AlertDialog
                //open database
                ContentValues newRow = new ContentValues();
                newRow.put(KEY_TITLE, mQuery.title);
               // newRow.put()add all movie info

                long id = db.insert(TABLE_NAME, "", newRow);

                String newFilename = "picture"+ id + ".png";
                FileOutputStream outputStream = null;
                try {
                    outputStream = openFileOutput(newFilename, Context.MODE_PRIVATE);
                    mQuery.picture.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                    outputStream.flush();
                    outputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Snackbar.make(addFave, "Added to Favourites", Snackbar.LENGTH_LONG).show();
            }
        });

//        (new MovieQuery()).execute();

        movieProgress = findViewById(R.id.movieProgress);

        movieProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.movie_nav, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView sView = (SearchView)searchItem.getActionView();

        sView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query){
                mQuery = new MovieQuery(query);
                mQuery.execute();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText){
                mQuery = new MovieQuery(newText);
                mQuery.execute();
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.faves:

                //TODO switch activity

                break;
            case R.id.movieStats:
                AlertDialog.Builder statsBuilder = new AlertDialog.Builder(ctx);
                statsBuilder.setMessage(R.string.movie_stats_dialog)
                        .setTitle(R.string.movie_stats_dialog_title)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO  SQL stats

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                //TODO, need?

                            }
                        })
                        .show();
                break;
            case R.id.movieAbout:
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setMessage(R.string.movie_dialog)
                        .setTitle(R.string.movie_dialog_title)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO, about

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                //TODO, need?

                            }
                        })
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

    // private class MovieAdapter extends ArrayAdapter<String> arguable don't have to extend class, just declare a variable of that type - this is for faves

    class MovieDatabaseHelper extends SQLiteOpenHelper{
        public MovieDatabaseHelper(Context ctx){
            super(ctx, DATABASE_NAME, null, VERSION_NUM);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, title text, year INTEGER, rated text, runtime INTEGER, actors text, plot text, filename text)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer){
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    public class MovieQuery extends AsyncTask<String, Integer, String> {
        String queryString;

        String title;
        int year;
        String rated;
        int runtime;
        String actors;
        String plot;
        String poster;
        Bitmap picture;

        public MovieQuery(String query){
            this.queryString = query;
        }

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
                                publishProgress(20);
                                rated = xpp.getAttributeValue(null, "rated");
                                publishProgress(35);
                                String run = xpp.getAttributeValue(null, "runtime");
                                String runTrim = run.substring(0, run.indexOf(" "));
                                runtime = Integer.parseInt(runTrim);
                                publishProgress(50);
                                actors = xpp.getAttributeValue(null, "actors");
                                publishProgress(65);
                                plot = xpp.getAttributeValue(null, "plot");
                                publishProgress(80);
                                poster = xpp.getAttributeValue(null, "poster");
                                publishProgress(95);
                            } else if (name.equals("error")) {
                                Toast.makeText(ctx, "No results found", Toast.LENGTH_LONG).show();
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
            //if (fileExistence(poster) == false) {
              //  try {
                    picture = HttpUtils.getImage(poster);
                    publishProgress(100);
                  //  FileOutputStream outputStream = openFileOutput(poster, Context.MODE_PRIVATE);
                 //   picture.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                  //  outputStream.flush();
                  //  outputStream.close();
               // } catch (Exception e) {
                 //   Log.i("Exception", e.getMessage());
                //}
           // } else {
             //   FileInputStream fis = null;
              //  try {
               //     fis = openFileInput(poster);
                //} catch (FileNotFoundException e) {
                 //   e.printStackTrace();
                //}
                //picture = BitmapFactory.decodeStream(fis);
          //  }
            return "finished";
        }

//        public boolean fileExistence(String fname) {
//            File file = getBaseContext().getFileStreamPath(fname);
//            return file.exists();
//        }

        public void onProgressUpdate(Integer... args) {
            movieProgress.setVisibility(View.VISIBLE);
            movieProgress.setProgress(args[0]);
        }

        public void onPostExecute(String result) {
            searchResult = findViewById(R.id.searchResult);
            TextView yearV = findViewById(R.id.searchYear);
            TextView ratedV = findViewById(R.id.searchRated);
            TextView runtimeV = findViewById(R.id.searchRuntime);
            TextView actorsV = findViewById(R.id.searchActors);
            TextView plotV = findViewById(R.id.searchPlot);
            ImageView posterV = findViewById(R.id.searchPoster);

            searchResult.setText(title + " (" + year + ")");

            yearV.setText(year + "");
            ratedV.setText("Rated " + rated);
            runtimeV.setText(runtime + " min");
            actorsV.setText(actors +"");
            plotV.setText(plot +"");
            posterV.setImageBitmap(picture);

            Log.i(ACTIVITY_NAME, runtime + actors + plot);

            movieProgress.setVisibility(View.INVISIBLE);
            addFave.setVisibility(View.VISIBLE);
        }
    }

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
