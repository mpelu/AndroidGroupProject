package com.example.mpelu.androidgroupproject;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    final Context ctx = this;
    final int movieCode = 42;
    final int cbcCode = 77;
 Button nutritionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nutritionBtn = findViewById(R.id.nutritionInfoBtn);

        nutritionBtn.setOnClickListener( e -> {
                    Intent intent = new Intent(MainActivity.this, NutritionDatabase.class);
                    startActivity(intent);
                });

        Button toMovies = findViewById(R.id.goToMovie);
        Button toCBC = findViewById(R.id.goToCBC);

        toMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextScreen = new Intent(ctx, Movies.class);
                startActivityForResult(nextScreen, movieCode);
            }
        });

        toCBC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cbcIntent = new Intent(ctx, CBCActivity.class);
                startActivityForResult(cbcIntent, cbcCode);
            }
        });
    }





    //set on activity result button

}
