/*
public class CBCActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbc);

        String[] testData = new String[]{"Post 1", "Post 2", "Post 3", "Post 4", "Post 5"};
        ListView cbcFeed = (ListView)findViewById(R.id.cbcFeedView);
        ArrayAdapter<String> feedAdapter = new ArrayAdapter<String>(this, R.layout.cbc_feed, R.id.cbcFeedText, testData);

        cbcFeed.setAdapter(feedAdapter);
    }
}
*/

package com.example.mpelu.androidgroupproject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.content.Context;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class CBCActivity extends FragmentActivity {

    private static final String ACTIVITY_NAME = "ChatWindow";
    private ArrayList<String> articles = new ArrayList<String>();
    private ArrayList<String> newsTitles = new ArrayList<String>();
    private ArrayList<String> newsLinks = new ArrayList<String>();
    private ArrayList<String> newsDescription = new ArrayList<String>();
    private ArrayList<String> newsIcon = new ArrayList<String>();
    private SQLiteDatabase db;
    private static final int VERSION_NUM = 6;
    private static final String DATABASE_NAME = "Articles.db";
    private static final String TABLE_NAME = "articles";
    private ChatAdapter newsAdapter = null;
    private Cursor c;
    private ContentValues cv;
    private HttpURLConnection conn = null;
    private HttpURLConnection conn2 = null;
    private String urlString = "https://www.cbc.ca/cmlink/rss-world";
    private URL url = null;
    private static final String ns = null;
    private Boolean inItem = false;
    private Bitmap bm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbc);

        ProgressBar pgb = findViewById(R.id.cbcProgressBar);
        pgb.setVisibility(View.VISIBLE);
        pgb.setScaleX(2f);
        pgb.setScaleY(2f);

        ChatDatabaseHelper dbHelper = new ChatDatabaseHelper(CBCActivity.this);
        db = dbHelper.getWritableDatabase();
        cv = new ContentValues();

        c = db.rawQuery("SELECT id,message,link,description,icon from " + TABLE_NAME, null);

        /*
        int colIndex = c.getColumnIndex("message");
        c.moveToFirst();
        while(!c.isAfterLast()) {
            String msgExisting = c.getString(colIndex);
            articles.add(msgExisting);

            Log.i(ACTIVITY_NAME, "SQL MESSAGE: " + c.getString(c.getColumnIndex(KEY_MESSAGE)));
            Log.i(ACTIVITY_NAME, "Cursor's column count = " + c.getColumnCount());
            Log.i(ACTIVITY_NAME, "Column name = " + c.getColumnName(colIndex));

            c.moveToNext();
        }
        */

        final ListView lv = findViewById(R.id.cbcFeedView);
        //final EditText et = (EditText)findViewById(R.id.editChatText);
        Button b = findViewById(R.id.cbcRefreshButton);


        newsAdapter = new ChatAdapter(CBCActivity.this);
        lv.setAdapter(newsAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                //String value = (String) adapterView.getItemAtPosition(position)
                Intent detailsIntent = new Intent(CBCActivity.this, CBCDetails.class);
                detailsIntent.putExtra("newsID", newsAdapter.getItemId(position));
                detailsIntent.putExtra("newsTitle", newsAdapter.getItemText(position));
                detailsIntent.putExtra("newsLink", newsAdapter.getItemLink(position));
                detailsIntent.putExtra("newsDescription", newsAdapter.getItemDescription(position));
                detailsIntent.putExtra("newsIcon", newsAdapter.getItemIcon(position));
                detailsIntent.putExtra("lvPosition", position);
                startActivityForResult(detailsIntent, 1);
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                String input = et.getText().toString();
                articles.add(input);

                cv.put("message", input);
                db.insert(TABLE_NAME, null, cv);

                newsAdapter.notifyDataSetChanged();
                et.setText("");
                */
                articles.clear();
                db.delete(TABLE_NAME, null, null);
                newsAdapter.notifyDataSetChanged();
                newsQuery fq = new newsQuery();
                fq.execute();
            }
        });

        newsQuery fq = new newsQuery();
        fq.execute();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (db != null ) {
            db.close();
        }
    }

    private class ChatAdapter extends ArrayAdapter<String> {
        public ChatAdapter(Context ctx) {
            super(ctx, 0);
        }

        public int getCount() {
            return articles.size();
        }

        public String getItem(int position) {
            return articles.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = CBCActivity.this.getLayoutInflater();
            View result = null;

            //if (position % 2 == 0) {
            result = inflater.inflate(R.layout.cbc_feed,null);
            //}

            String iconURL = getItemIcon(position);
            String[] imageSrcParts = iconURL.split("/");
            String iconPath = imageSrcParts[9];

            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8)
            {
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
            TextView message = result.findViewById(R.id.cbcFeedText);
            message.setText(getItem(position));
            img.setImageBitmap(bm);

            return result;
        }

        public long getItemId(int position) {
            if (c != null && c.moveToFirst()) {
                c.moveToPosition(position);
                Long val1 = c.getLong(c.getColumnIndex("id"));

                return val1;
            }
            return 000;
        }

        public String getItemText(int position) {
            if (c != null && c.moveToFirst()) {
                c.moveToPosition(position);
                String val2 = c.getString(c.getColumnIndex("message"));

                return val2;
            }
            return "Title loaded incorrectly";
        }

        public String getItemLink(int position) {
            if (c != null && c.moveToFirst()) {
                c.moveToPosition(position);
                String val3 = c.getString(c.getColumnIndex("link"));

                return val3;
            }
            return "Link loaded incorrectly";
        }

        public String getItemDescription(int position) {
            if (c != null && c.moveToFirst()) {
                c.moveToPosition(position);
                String val4 = c.getString(c.getColumnIndex("description"));

                return val4;
            }
            return "Description loaded incorrectly";
        }

        public String getItemIcon(int position) {
            if (c != null && c.moveToFirst()) {
                Log.i("DATABASE", DatabaseUtils.dumpCursorToString(c));
                c.moveToPosition(position);
                String val5 = c.getString(c.getColumnIndex("icon"));

                return val5;
            }
            return "Icon loaded incorrectly";
        }

    }

    private class ChatDatabaseHelper extends SQLiteOpenHelper {

        public ChatDatabaseHelper(Context ctx) {
            super(ctx, DATABASE_NAME, null, VERSION_NUM);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, message String, link String, description String, icon String);");

            Log.i("ChatDatabaseHelper", "Calling onCreate");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);

            Log.i(ACTIVITY_NAME, "Calling onUpgrade, oldVersion = " + oldVer + " newVersion = = " +newVer);
        }
    }

    public void deleteMessage(long id, int pos) {
        db.delete(TABLE_NAME, "id=" + id, null);

        articles.remove(pos);
        //newsAdapter.remove(newsAdapter.getItem((int) pos));
        newsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Long msgID = data.getLongExtra("retID", 0);
            Integer lvPOS = data.getIntExtra("lvPOS", 0);
            deleteMessage(msgID, lvPOS);
        }
    }

    class newsQuery extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            ProgressBar pgb = findViewById(R.id.cbcProgressBar);
            pgb.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            Log.i("WeatherForecast","In doInBackground");

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
                    //Log.i("CBCActivity", "Name = " + name);

                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            if (name.equalsIgnoreCase("item")) {
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

        @Override
        protected void onProgressUpdate(Integer... values) {
            ProgressBar pgb = findViewById(R.id.cbcProgressBar);
            pgb.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            ProgressBar pgb = findViewById(R.id.cbcProgressBar);
            for (int i=0; i<newsTitles.size(); i++) {
                articles.add(newsTitles.get(i));
                cv.put("message", newsTitles.get(i));
                cv.put("link", newsLinks.get(i));
                cv.put("description", newsDescription.get(i));
                cv.put("icon", newsIcon.get(i));
                db.insert(TABLE_NAME, null, cv);

                newsAdapter.notifyDataSetChanged();
            }

            //Log.i("AAAAAAAAAAA", newsLinks.get(1));
            pgb.setVisibility(View.INVISIBLE);
        }
    }
}
