package com.example.mpelu.androidgroupproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends Activity {
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
    }
}
