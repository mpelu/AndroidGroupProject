package com.example.mpelu.androidgroupproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    final Context ctx = this;
    final int movieCode = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button toMovies = findViewById(R.id.goToMovie);

        toMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextScreen = new Intent(ctx, Movies.class);
                startActivityForResult(nextScreen, movieCode);
            }
        });
    }





    //set on activity result button

}
