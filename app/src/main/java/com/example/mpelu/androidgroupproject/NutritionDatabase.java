package com.example.mpelu.androidgroupproject;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NutritionDatabase extends ListActivity {
    private ArrayList<String> nutritionFaves;
    private ListView nutritionFavesListView;
    private Button searchBtn;
    private EditText searchEditTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nutritionFaves = new ArrayList<String>();
        nutritionFavesListView = findViewById(R.id.nutritionListView);
        searchBtn = findViewById(R.id.searchBtn);
        searchEditTxt = findViewById(R.id.searchEditTxt);

        JSONQuery query = new JSONQuery();
        query.execute();

      nutritionFavesListView.setAdapter(new ArrayAdapter<String>(
              (this), R.layout.activity_nutrition_database, R.id.nutritionListView, nutritionFaves));
        Toast toastOn = Toast.makeText(this, "Toast's up", Toast.LENGTH_LONG);
//      searchBtn.setOnClickListener(e -> toastOn.show());

    }

    class JSONQuery extends AsyncTask<String, Integer, String> {
        // see:  https://developer.edamam.com/food-database-api-docs
        private static final String PARSE_REQ = "https://api.edamam.com/api/food-database/parser";
        private static final String API_ID = "41f81ac2";
        private static final String API_KEY = "0a48b3a022772a8cee148f55f166f9a3";
        private static final String HINTS = "hints";    // calories and fat data are stored in an array called "hints"
        private static final String CAL_KEY = "ENERC_KCAL";
        private static final String FAT_KEY = "FAT";


        public String doInBackground(String... args) {
            try {
                //connect to Server:
                URL url = new URL("https://api.edamam.com/api/food-database/parser?ingr=red%20apple&app_id=41f81ac2&app_key=0a48b3a022772a8cee148f55f166f9a3");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream response = urlConnection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();
                JSONObject root = new JSONObject(result); //jObject is the root object

                JSONArray nutritionArray = root.getJSONArray("text"); //
                JSONObject item = nutritionArray.getJSONObject(0); //get first element of array


            } catch (Exception e) {
                Log.i("Exception", e.getMessage());
            }

            return "";
        }

        public void onProgressUpdate(Integer... args) //update your GUI
        {
            //setText()
            //setImage()
        }


        public void onPostExecute(String result)  // doInBackground has finished
        {

        }

        protected class NutritionDatabaseHelper extends SQLiteOpenHelper {


            public static final String DATABASE_NAME = "Nutrition.db";
            public static final String TABLE_NAME = "NUTRITION";
            public static final String KEY_ID = "_id";
            public static final String KEY_NAME = "NAME";
            public static final String KEY_CAL = "CALORIES";
            public static final String KEY_FAT = "FAT";
            public static final int VERSION_NUM = 1;

            public NutritionDatabaseHelper(Context ctx) {
                super(ctx, DATABASE_NAME, null, VERSION_NUM);
            }

            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE " + TABLE_NAME + "( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        KEY_NAME + "," + KEY_CAL + "," + KEY_FAT + " text )");
                Log.i("NutritionDatabaseHelper", "Calling 'onCreate'");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
                Log.i("NutritionDatabaseHelper", "Calling 'onUpgrade'");

                onCreate(db);
            }
        }
    }

    private class NutritionFavesAdapter extends ArrayAdapter<String>{
        NutritionFavesAdapter(Context ctx) {
            super(ctx, 0);
        }

        public int getCount() {
            return nutritionFaves.size();
        }

        public String getItem(int pos) {
            return nutritionFaves.get(pos);
        }


        public long getItemId(int position) {
            return position;
        }
    }
}