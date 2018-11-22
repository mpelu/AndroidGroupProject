package com.example.mpelu.androidgroupproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Movies extends AppCompatActivity {
    public static final String ACTIVITY_NAME = "Movies";
    Context ctx = this;

    Toolbar movieBar = null;
    ListView movieList = null;
    ProgressBar movieProgress = null;

    MovieQuery query;


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

        query = new MovieQuery();
        query.execute();

        MovieDatabaseHelper dbHelp = new MovieDatabaseHelper(this);
        db = dbHelp.getReadableDatabase();

        movieList = findViewById(R.id.movieList);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.movie_nav, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView sView = (SearchView)searchItem.getActionView();

        sView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query){
                //TODO, SQL

                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText){
               //TODO - what?

                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.movieStats:

                //TODO, SQL

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

    // private class MovieAdapter extends ArrayAdapter<String> arguable don't have to extend class, just declare a variable of that type

    class MovieDatabaseHelper extends SQLiteOpenHelper{
        public MovieDatabaseHelper(Context ctx){
            super(ctx, DATABASE_NAME, null, VERSION_NUM);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (title String)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer){
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    public class MovieQuery extends AsyncTask<String, Integer, String>{

          //TODO class variables (title, actors, etc)

        public String doInBackground(String...args){
            try{
                URL url = new URL("http://www.google.com/"); //TODO, encode
                HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();
                InputStream response = urlConnect.getInputStream();

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(response, "UTF-8");

                while(xpp.getEventType() != XmlPullParser.END_DOCUMENT){
                    switch(xpp.getEventType()){
                        case XmlPullParser.START_TAG:
                            String name = xpp.getName();
                            if(name.equals("movie")){

                                //TODO, Async



                            }else if(name.equals("error")){

                                //TODO toast
                            }
                            break;
                        case XmlPullParser.TEXT:

                            break;
                        default:
                    }
                    xpp.next();
                }
            }catch(Exception e){
                Log.i("Exception", e.getMessage());
            }
            //TODO ? download image?

            return "finished";
        }

        public void onProgressUpdate(Integer...args){
            movieProgress.setVisibility(View.VISIBLE);
            movieProgress.setProgress(args[0]);
        }

        public void onPostExecute(String result){
            //TODO change TextViews - but where are they?
        }
    }
}
