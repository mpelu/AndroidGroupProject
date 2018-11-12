package com.example.mpelu.androidgroupproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Movies extends Activity {
    public static final String ACTIVITY_NAME = "Movies";
    Context ctx = this;
    public SQLiteDatabase db;
    static final int VERSION_NUM = 2;
    static final String DATABASE_NAME = "FavoriteMovies";
    static final String TABLE_NAME = "Movies";
    static final String KEY_TITLE = "Title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        MovieDatabaseHelper dbHelp = new MovieDatabaseHelper(this);
        db = dbHelp.getReadableDatabase();



        final EditText searchTerm = findViewById(R.id.movieEdit);
        final Button search = findViewById(R.id.movieButton);
        final ListView movieList = findViewById(R.id.movieList);

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage("This is the movie activity")
                .setTitle("Movie")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent result = new Intent();
                        result.putExtra("Response", "My information");
                        setResult(42, result);
                        finish();
                    }
                });


        search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Toast toast = Toast.makeText(ctx, "button clicked", Toast.LENGTH_LONG);

//                MyHttpQuery query = new MyHttpQuery();
//                query.execute("one", "two");
            }
        });

//        Snackbar.make(search, "string to show", Snackbar.LENGTH_LONG.show());
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

    class MyHttpQuery extends AsyncTask<String, Integer, String>{

        public String doInBackground(String...args){
            try{
                URL url = new URL("http://www.google.com/");
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
                            String value = xpp.getAttributeValue(null, "message");
                            break;
                        case XmlPullParser.TEXT:

                            break;
                        default:
                    }
                    xpp.next();
                }
            }catch(Exception e){
            }

            String first = args[0];

            publishProgress();
            return null;
        }

        public void onProgressUpdate(Integer...args){

        }

        public void onPostExecute(String result){

        }
    }
}
