package com.example.mpelu.androidgroupproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar projBar = findViewById(R.id.projToolbar);
        setSupportActionBar(projBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.proj_nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.CBC:
             //   Intent cbc = new Intent(MainActivity.this, CBCActivity.class);
              //  startActivityForResult(cbc, 100);
                break;
            case R.id.Movies:
                //Intent movies = new Intent(MainActivity.this, Movies.class);
                //startActivityForResult(movies, 200);
                break;
            case R.id.Nutrition:
               // Intent nutrition = new Intent(MainActivity.this, NutritionDatabase.class);
               // startActivityForResult(nutrition, 300);
                break;
            case R.id.OCTranspo:
                Intent octranspo = new Intent(MainActivity.this, OcTranspo.class);
                startActivityForResult(octranspo, 400);
                break;
            default:
        }
        return false;
    }


}
