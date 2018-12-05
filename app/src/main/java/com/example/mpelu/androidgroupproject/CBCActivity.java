package com.example.mpelu.androidgroupproject;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.content.Context;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * The main activity of CBC News Reader. The array list view is prominent, where articles are shown in row format.
 */
public class CBCActivity extends AppCompatActivity  {

    private static final String ACTIVITY_NAME = "ChatWindow";
    /**
     * ArrayList<String> variables for use when parsing the CBC RSS feed
     */
    private ArrayList<String> articles = new ArrayList<String>();
    private ArrayList<String> newsTitles = new ArrayList<String>();
    private ArrayList<String> newsLinks = new ArrayList<String>();
    private ArrayList<String> newsDescription = new ArrayList<String>();
    private ArrayList<String> newsIcon = new ArrayList<String>();
    private int wordCount = 0;
    private SQLiteDatabase db;
    private static final int VERSION_NUM = 32;
    private static final String DATABASE_NAME = "Articles.db";
    private static final String TABLE_NAME = "articles";
    private NewsAdapter newsAdapter = null;
    private Cursor c;
    private ContentValues cv;
    private HttpURLConnection conn = null;
    private HttpURLConnection conn2 = null;
    private String urlString = "https://www.cbc.ca/cmlink/rss-world";
    private URL url = null;
    private static final String ns = null;
    private Boolean inItem = false;
    private Bitmap bm = null;
    private boolean backTwice = false;

    /**
     * Runtime function. Sets toolbar, views, and components.
     * @param savedInstanceState: default bundle from last state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbc);

        //set toolbar
        Toolbar cbcToolbar = findViewById(R.id.cbcToolbar);
        setSupportActionBar(cbcToolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        //set progressbar
        ProgressBar pgb = findViewById(R.id.cbcProgressBar);
        pgb.setVisibility(View.INVISIBLE);
        pgb.setScaleX(2f);
        pgb.setScaleY(2f);

        //database setup
        NewsDatabaseHelper dbHelper = new NewsDatabaseHelper(CBCActivity.this);
        db = dbHelper.getWritableDatabase();
        cv = new ContentValues();

        //2 query cursors. One to fetch all, and one to fetch saved articles
        c = db.rawQuery("SELECT id,title,link,description,icon,saved,wordcount from " + TABLE_NAME, null);
        final Cursor cSaved = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE saved=1", null);

        final ListView lv = findViewById(R.id.cbcFeedView);
        Button b = findViewById(R.id.cbcRefreshButton);

        newsAdapter = new NewsAdapter(CBCActivity.this);
        lv.setAdapter(newsAdapter);

        //when a LV row is clicked, send it to CBCDetails.java
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                Intent detailsIntent = new Intent(CBCActivity.this, CBCDetails.class);
                detailsIntent.putExtra("newsID", newsAdapter.getItemId(position));
                detailsIntent.putExtra("newsTitle", newsAdapter.getItemTitle(position));
                detailsIntent.putExtra("newsLink", newsAdapter.getItemLink(position));
                detailsIntent.putExtra("newsDescription", newsAdapter.getItemDescription(position));
                detailsIntent.putExtra("newsIcon", newsAdapter.getItemIcon(position));
                detailsIntent.putExtra("newsSaved", newsAdapter.getItemSaved(position));
                detailsIntent.putExtra("newsWordcount", newsAdapter.getItemWordcount(position));
                detailsIntent.putExtra("savedCount", getSavedCount());
                detailsIntent.putExtra("lvPosition", position);
                startActivityForResult(detailsIntent, 1);
            }
        });

        //refreshes listview for variables to re-initialize (like saved articles)
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();

                newsQuery nq = new newsQuery();
                nq.execute();
            }
        });

        //repopulate articles if there is any in the database
        if (articles.isEmpty()) {
            int colIndex = c.getColumnIndex("title");
            c.moveToFirst();
            while(!c.isAfterLast()) {
                String fromDB = c.getString(colIndex);
                articles.add(fromDB);

                c.moveToNext();
            }
        }

        //populate listview if there is nothing in database
        if (getAllCount() == 0) {
            newsQuery nq = new newsQuery();
            nq.execute();
        }

    }

    /**
     * close database on app close
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (db != null ) {
            db.close();
        }
    }

    /**
     * Custom array adapter. Handles most functions interacting with the list view
     */
    private class NewsAdapter extends ArrayAdapter<String> {
        /**
         * Default constructor
         * @param ctx: this
         */
        public NewsAdapter(Context ctx) {
            super(ctx, 0);
        }

        /**
         * gets arrayList size
         * @return size of arrayList
         */
        public int getCount() {
            return articles.size();
        }

        /**
         * gets an item in position of array list
         * @param position: position in array list
         * @return value of item
         */
        public String getItem(int position) {
            return articles.get(position);
        }

        /**
         * Article row inflater. If an item is saved, the image will have a red 'SAVED' ontop of it.
         * @param position: position of item
         * @param convertView: default
         * @param parent: default view
         * @return article rows
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = CBCActivity.this.getLayoutInflater();
            View result = null;

            //Returns different layouts depending if the user has saved an article or not
            if (getItemSaved(position) == 0) {
                result = inflater.inflate(R.layout.cbc_feed,null);
            }
            else {
                result = inflater.inflate(R.layout.cbc_feed_saved,null);
            }

            String iconURL = getItemIcon(position);
            String[] imageSrcParts;
            String iconPath = null;

            //Check if icons loaded incorrectly, refresh to load the
            if (iconURL.equals("Icon loaded incorrectly")) {
                refresh();
            }
            else {
                imageSrcParts = iconURL.split("/");
                iconPath = imageSrcParts[9];
            }


            //HTTPURL connection alterative to ASYNC Task
            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);

                try {
                    URL bmURL = new URL(iconURL);
                    conn2 = (HttpURLConnection) bmURL.openConnection();
                    int resp = conn2.getResponseCode();
                    if (resp == 200) {
                        bm = BitmapFactory.decodeStream(conn2.getInputStream());
                        FileOutputStream outputStream = openFileOutput(iconPath, Context.MODE_PRIVATE);
                        bm.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                        outputStream.flush();
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (conn2 != null) {
                        conn2.disconnect();
                    }
                }
            }
            ImageView img = result.findViewById(R.id.cbcFeedThumb);
            img.setImageBitmap(bm);


            TextView title = result.findViewById(R.id.cbcFeedText);
            title.setText(getItem(position));

            return result;
        }

        /**
         * gets item id at position
         * @param position: position of item
         * @return item id
         */
        public long getItemId(int position) {
            if (c != null && c.moveToFirst()) {
                c.moveToPosition(position);
                Long val1 = c.getLong(c.getColumnIndex("id"));

                return val1;
            }
            return 000;
        }

        /**
         * gets item title at position
         * @param position: position of item
         * @return item title
         */
        public String getItemTitle(int position) {
            if (c != null && c.moveToFirst()) {
                c.moveToPosition(position);
                String val2 = c.getString(c.getColumnIndex("title"));

                return val2;
            }
            return "Title loaded incorrectly";
        }

        /**
         * gets link at position
         * @param position: position of item
         * @return item link
         */
        public String getItemLink(int position) {
            if (c != null && c.moveToFirst()) {
                c.moveToPosition(position);
                String val3 = c.getString(c.getColumnIndex("link"));

                return val3;
            }
            return "Link loaded incorrectly";
        }

        /**
         * gets description at position
         * @param position: position of item
         * @return item description
         */
        public String getItemDescription(int position) {
            if (c != null && c.moveToFirst()) {
                c.moveToPosition(position);
                String val4 = c.getString(c.getColumnIndex("description"));

                return val4;
            }
            return "Description loaded incorrectly";
        }

        /**
         * gets icon at position
         * @param position: position of item
         * @return item icon
         */
        public String getItemIcon(int position) {
            if (c != null && c.moveToFirst()) {
                c.moveToPosition(position);
                String val5 = c.getString(c.getColumnIndex("icon"));

                return val5;
            }
            return "Icon loaded incorrectly";
        }

        /**
         * gets saved value at position
         * @param position: position of item
         * @return item saved value (0 or 1)
         */
        public Integer getItemSaved(int position) {
            if (c != null && c.moveToFirst()) {
                c.moveToPosition(position);
                Integer val6 = c.getInt(c.getColumnIndex("saved"));

                return val6;
            }
            return 0;
        }

        /**
         * gets wordcount at position
         * @param position: position of item
         * @return item wordcount
         */
        public int getItemWordcount(int position) {
            if (c != null && c.moveToFirst()) {
                c.moveToPosition(position);
                Integer val7 = c.getInt(c.getColumnIndex("wordcount"));

                return val7;
            }
            return 0;
        }

    }

    /**
     * Custom database helper
     */
    private class NewsDatabaseHelper extends SQLiteOpenHelper {

        /**
         * Default constructor
         * @param ctx: this
         */
        public NewsDatabaseHelper(Context ctx) {
            super(ctx, DATABASE_NAME, null, VERSION_NUM);
        }

        /**
         * Creates table for this activity (articles)
         * @param db: database object
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, title String UNIQUE, link String, description String, icon String, saved INTEGER DEFAULT 0, wordcount INTEGER);");

            Log.i("NewsDatabaseHelper", "Calling onCreate");
        }

        /**
         * Clears table on upgrade
         * @param db: database object
         * @param oldVer: old version before upgrade
         * @param newVer: new version after upgrade
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);

            Log.i(ACTIVITY_NAME, "Calling onUpgrade, oldVersion = " + oldVer + " newVersion = = " +newVer);
        }
    }

    /**
     * marks an article as saved (1 in the database)
     * @param id: id of article
     * @param pos: position in arraylist
     */
    public void saveArticle(long id, int pos) {
        ContentValues cv2 = new ContentValues();
        cv2.put("saved",1);
        db.update(TABLE_NAME, cv2, "id=" + id, null);
    }

    /**
     * unmarks a saved article (0 in the database)
     * @param id: id of article
     * @param pos: position in arrayList
     */
    public void unsaveArticle(long id, int pos) {
        ContentValues cv2 = new ContentValues();
        cv2.put("saved",0);
        db.update(TABLE_NAME, cv2, "id=" + id, null);
    }

    /**
     * callback function from CBCDetails
     * @param requestCode: requestCode
     * @param resultCode: resultCode
     * @param data: return data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //save article
        if (requestCode == 1 && resultCode == 888) {
            Long msgID = data.getLongExtra("retID", 0);
            Integer lvPOS = data.getIntExtra("lvPOS", 0);
            saveArticle(msgID, lvPOS);
            refresh();
        }
        //unsave article
        else if (requestCode == 1 && resultCode == 999) {
            Long msgID = data.getLongExtra("retID", 0);
            Integer lvPOS = data.getIntExtra("lvPOS", 0);
            unsaveArticle(msgID, lvPOS);
            refresh();
        }
    }

    /**
     * Custom query using AsyncTask HTTP url connections
     */
    class newsQuery extends AsyncTask<String, Integer, String> {

        /**
         * Setup. Lets user know something is happening. Sets progress bar.
         */
        @Override
        protected void onPreExecute() {
            ProgressBar pgb = findViewById(R.id.cbcProgressBar);
            pgb.setVisibility(ProgressBar.VISIBLE);
        }

        /**
         * Fetches data to show in the arraylist. Parses CBC website getting data.
         * @param params: params
         * @return: null
         */
        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,false);
                parser.setInput(conn.getInputStream(), ns);

                int eventType = parser.getEventType();
                int count = 0;
                while(eventType != XmlPullParser.END_DOCUMENT) {
                    String name = parser.getName();

                    //Make sure start parse at start_tag <START>
                    //Adds text of tags into corresponding array list
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            if (name.equalsIgnoreCase("item")) {
                                //Make sure in item tag
                                inItem = true;
                            }
                            else if (name.equalsIgnoreCase("title")) {
                                if (inItem) {
                                    newsTitles.add(parser.nextText());
                                    publishProgress(25);
                                }
                            }
                            else if (name.equalsIgnoreCase("link")) {
                                if (inItem) {
                                    newsLinks.add(parser.nextText());
                                    publishProgress(50);
                                }
                            }
                            else if (name.equalsIgnoreCase("description")) {
                                if (count > 0) {
                                    String msgDesc = parser.nextText();
                                    String[] strParts = msgDesc.split("'");

                                    newsIcon.add(strParts[1]);
                                    newsDescription.add(msgDesc);
                                    publishProgress(75);
                                }
                                count++;
                            }

                            break;
                        case XmlPullParser.TEXT:
                            break;
                        case XmlPullParser.END_TAG:
                            inItem = false;
                            break;
                    }
                    eventType = parser.next();
                }
            }catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            publishProgress(100);
            return null;
        }

        /**
         * Updates the progress bar when publishProgress(int) is called
         * @param values: value of progress
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            ProgressBar pgb = findViewById(R.id.cbcProgressBar);
            pgb.setProgress(values[0]);
        }

        /**
         * After parsing data, insert data into the database.
         * @param s
         */
        @Override
        protected void onPostExecute(String s) {
            ProgressBar pgb = findViewById(R.id.cbcProgressBar);
            for (int i=0; i<newsTitles.size(); i++) {
                if (!articles.contains(newsTitles.get(i))) {
                    String[] strParts = newsDescription.get(i).split("'");
                    String[] strParts2 = newsDescription.get(i).split("<p>");
                    String content = strParts2[1];
                    Integer contentLen = content.length() - 20;

                    String imageDesc = strParts[7];
                    String allText = imageDesc + "\n\n" + content.substring(0, contentLen);
                    wordCount = allText.split("\\w+").length ;

                    articles.add(newsTitles.get(i));
                    cv.put("title", newsTitles.get(i));
                    cv.put("link", newsLinks.get(i));
                    cv.put("description", newsDescription.get(i));
                    cv.put("icon", newsIcon.get(i));
                    cv.put("wordcount", wordCount);
                    db.insert(TABLE_NAME, null, cv);

                    newsAdapter.notifyDataSetChanged();
                }

            }
            pgb.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Inflate toolbar menu
     * @param m: Toolbar
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.cbc_toolbarmenu, m);
        return true;
    }

    /**
     * Sets up items inside the toolbar
     * @param mi: menu item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        switch (mi.getItemId()) {

            //This is the stats custom dialog
            case R.id.action_save:
                final AlertDialog.Builder builder = new AlertDialog.Builder(CBCActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.cbc_customstats, null);

                builder.setView(dialogView);

                final TextView statsSavedText = dialogView.findViewById(R.id.cbc_customStats1);
                final TextView statsText = dialogView.findViewById(R.id.cbc_customStats2);

                Long statsSavedCount = getSavedCount();

                statsSavedText.setText("Number of articles saved = " + statsSavedCount);

                builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                break;

                //About custom dialog
            case R.id.action_about:
                final AlertDialog.Builder builder2 = new AlertDialog.Builder(CBCActivity.this);
                LayoutInflater inflater2 = getLayoutInflater();
                final View dialogView2 = inflater2.inflate(R.layout.cbc_customdialog, null);

                builder2.setView(dialogView2);
                builder2.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //
                    }
                });

                AlertDialog dialog2 = builder2.create();
                dialog2.show();
                break;
        }
        return true;
    }

    /**
     * Tell the user they have to press back twice to exit
     */
    @Override
    public void onBackPressed() {
        if (backTwice) {
            super.onBackPressed();
            return;
        }

        backTwice = true;
        Toast.makeText(this, "Click BACK again to confirm exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backTwice=false;
            }
        }, 2000); //They have 2 seconds to press back twice
    }

    /**
     * Get how many articles that are marked as saved
     * @return: amount of articles saved
     */
    public long getSavedCount() {
        Cursor statsSaved = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE saved = 1", null);
        long savedCount = statsSaved.getCount();
        statsSaved.close();

        return savedCount;
    }

    /**
     * Get all articles in the database (saved or not)
     * @return amount of all articles
     */
    public long getAllCount() {
        Cursor allDB = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        long allCount = allDB.getCount();
        allDB.close();

        return allCount;
    }

    /**
     * re-initialize variables, eg to show the 'SAVED' icon or hide buttons
     */
    public void refresh() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

}
