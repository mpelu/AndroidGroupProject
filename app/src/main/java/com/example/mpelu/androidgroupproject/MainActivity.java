package com.example.mpelu.androidgroupproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Main page for application, user can access activities through graphical tool bar
 * @author Tristan Duck, Madeleine Peluso, Patricia Moshe, Justin Gamache
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Create activity, find toolbar
     * @param savedInstanceState - passed information
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar projBar = findViewById(R.id.projToolbar);
        setSupportActionBar(projBar);
    }

    /**
     * Inflate menu navigation
     * @param menu - menu resource file
     * @return - successful compile
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.proj_nav, menu);
        return true;
    }

    /**
     * Handle user's selected option item
     * @param item - menu item to be selected
     * @return - if built-in handling
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.CBC:
                Intent cbc = new Intent(MainActivity.this, CBCActivity.class);
                startActivityForResult(cbc, 100);
                break;
            case R.id.Movies:
                Intent movies = new Intent(MainActivity.this, Movies.class);
                startActivityForResult(movies, 200);
                break;
            case R.id.Nutrition:
               //Intent nutrition = new Intent(MainActivity.this, NutritionDatabase.class);
               //startActivityForResult(nutrition, 300);
                break;
            case R.id.OCTranspo:
                //Intent octranspo = new Intent(MainActivity.this, OcTranspo.class);
                //startActivityForResult(octranspo, 400);
                break;
            default:
        }
        return false;
    }
}
