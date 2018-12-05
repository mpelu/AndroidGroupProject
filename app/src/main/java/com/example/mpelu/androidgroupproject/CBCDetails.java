package com.example.mpelu.androidgroupproject;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This activity occurs when the user clicks a row in CBCActivity.java. Details of the news article they have clicked.
 */
public class CBCDetails extends AppCompatActivity {
    public boolean backTwice = false;
    public long msgSavedCount;
    public int wordCount;

    /**
     * Runtime function. Sets all components (TextViews, ImageViews) retrieved from CBCActivity.java
     * @param savedInstanceState: default bundle from last state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_message_details);

        //Toolbar
        Toolbar cbcToolbar = findViewById(R.id.cbcToolbar);
        setSupportActionBar(cbcToolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        CBCFragment newsFragment = new CBCFragment();

        //Add fragment to detailsFrame layout
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.detailsFrame, newsFragment).addToBackStack(null);
        fragmentTransaction.commit();

        //Bundle data retrieved from CBCActivity.java
        msgSavedCount = getIntent().getExtras().getLong("savedCount");
        Long msgID = getIntent().getExtras().getLong("newsID");
        String msgTitle = getIntent().getExtras().getString("newsTitle");
        String msgLink = getIntent().getExtras().getString("newsLink");
        String msgDescription = getIntent().getExtras().getString("newsDescription");
        String msgIcon = getIntent().getExtras().getString("newsIcon");
        Integer msgSaved = getIntent().getExtras().getInt("newsSaved");
        wordCount = getIntent().getExtras().getInt("newsWordcount");

        //Exploding parsed news description data for easier use (to get image source link and image description text)
        String[] strParts = msgDescription.split("'");
        String[] strParts2 = msgDescription.split("<p>");
        String content = strParts2[1];
        Integer contentLen = content.length() - 20;

        String imageSrc = strParts[1];
        String imageDesc = strParts[7];
        String allText = imageDesc + "\n\n" + content.substring(0, contentLen);

        final Button saveButton = findViewById(R.id.cbcSaveButton);
        final Button unsaveButton = findViewById(R.id.cbcUnsaveButton);
        if (msgSaved == 0) {
            saveButton.setVisibility(View.VISIBLE);
        }
        else {
            unsaveButton.setVisibility(View.VISIBLE);
        }

        TextView detailsTitle = findViewById(R.id.detailsTitle);
        TextView detailsLink = findViewById(R.id.detailsLink);
        TextView detailsDescription = findViewById(R.id.detailsDescription);
        ImageView detailsIcon = findViewById(R.id.detailsIcon);

        //ImageView
        Bitmap bm = null;
        HttpURLConnection conn2 = null;
        String iconURL = msgIcon;
        String[] imageSrcParts = iconURL.split("/");
        String iconPath = imageSrcParts[9];

        //ASYNC alternative
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

        detailsIcon.setImageBitmap(bm);
        detailsTitle.setText(msgTitle);
        detailsLink.setText(msgLink);
        detailsDescription.setText(allText);
        detailsDescription.setMovementMethod(new ScrollingMovementMethod());

        //Calls function to save an article. Once clicked, hides the save button and shows the undo save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveButton.setVisibility(View.GONE);
                unsaveButton.setVisibility(View.VISIBLE);
                Snackbar.make(findViewById(R.id.cbcSaveButton), "Article saved", Snackbar.LENGTH_LONG).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Long msgID = getIntent().getExtras().getLong("newsID");
                        Integer lvPOS = getIntent().getExtras().getInt("lvPosition");

                        Intent parentIntent = new Intent();
                        parentIntent.putExtra("retID", msgID);
                        parentIntent.putExtra("lvPOS", lvPOS);
                        setResult(888, parentIntent);
                        finish();
                    }
                }, 1250);

            }
        });

        unsaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveButton.setVisibility(View.VISIBLE);
                unsaveButton.setVisibility(View.GONE);
                Snackbar.make(findViewById(R.id.cbcSaveButton), "Save removed", Snackbar.LENGTH_LONG).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Long msgID = getIntent().getExtras().getLong("newsID");
                        Integer lvPOS = getIntent().getExtras().getInt("lvPosition");

                        Intent parentIntent = new Intent();
                        parentIntent.putExtra("retID", msgID);
                        parentIntent.putExtra("lvPOS", lvPOS);
                        setResult(999, parentIntent);
                        finish();
                    }
                }, 1250);

            }
        });

    }

    /**
     * inflates toolbarmenu
     * @param m: Tooblar
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.cbc_toolbarmenu, m);
        return true;
    }

    /**
     * Sets up menubar items
     * @param mi: menu item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        switch (mi.getItemId()) {

            case R.id.action_save:
                final AlertDialog.Builder builder = new AlertDialog.Builder(CBCDetails.this);
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.cbc_customstats, null);

                builder.setView(dialogView);

                final TextView statsSavedText = dialogView.findViewById(R.id.cbc_customStats1);
                final TextView statsText = dialogView.findViewById(R.id.cbc_customStats2);

                statsSavedText.setText("Number of articles saved = " + msgSavedCount);
                statsText.setText("Wordcount = " + wordCount);

                builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                break;

            case R.id.action_about:
                final AlertDialog.Builder builder2 = new AlertDialog.Builder(CBCDetails.this);
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
        }, 2500);
    }
}

