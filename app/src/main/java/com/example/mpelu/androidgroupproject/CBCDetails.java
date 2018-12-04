package com.example.mpelu.androidgroupproject;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class CBCDetails extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_message_details);

        CBCFragment messageFragment = new CBCFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.detailsFrame, messageFragment).addToBackStack(null);
        fragmentTransaction.commit();

        Long msgID = getIntent().getExtras().getLong("newsID");
        String msgTitle = getIntent().getExtras().getString("newsTitle");
        String msgLink = getIntent().getExtras().getString("newsLink");
        String msgDescription = getIntent().getExtras().getString("newsDescription");
        String msgIcon = getIntent().getExtras().getString("newsIcon");

        String[] strParts = msgDescription.split("'");
        String[] strParts2 = msgDescription.split("<p>");
        String content = strParts2[1];
        Integer contentLen = content.length() - 20;

        String imageSrc = strParts[1];
        String imageDesc = strParts[7];
        String allText = imageDesc + "\n\n" + content.substring(0, contentLen);

        Button saveButton = findViewById(R.id.cbcSaveButton);

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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Long msgID = getIntent().getExtras().getLong("newsID");
                Integer lvPOS = getIntent().getExtras().getInt("lvPosition");

                Intent parentIntent = new Intent();
                parentIntent.putExtra("retID", msgID);
                parentIntent.putExtra("lvPOS", lvPOS);
                setResult(RESULT_OK, parentIntent);
                finish();
                */
            }
        });


    }
}

