package com.example.mpelu.androidgroupproject;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Button;

public class MessageDetails extends Activity {

    Button deleteButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);

        deleteButton = (Button)findViewById(R.id.deleteButton);

        Bundle info = getIntent().getExtras();

        FragmentTransaction ft =  getFragmentManager().beginTransaction();
        MessageFragment mf = new MessageFragment();
        mf.setArguments(info);
        ft.add(R.id.frame_layout, mf );
        ft.commit();
    }
}
