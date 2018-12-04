package com.example.mpelu.androidgroupproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MovieFragment extends Fragment {

    //view, need to override onbackbutton method
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        Bundle infoToPass = getArguments();
        String passedMessage = infoToPass.getString("title");
        final long idPassed = infoToPass.getLong("id");
        View screen = inflater.inflate(R.layout.activity_movie_details, container, false);//no idea which layout
        //Textviews
        //tv.setText
        //Button

//        delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent result = new Intent();
//                result.putExtra("id", idPassed);
//                getActivity().setResult(26, result);
//                getActivity().finish();
//            }
//        });

        return screen;
    }

    public void onAttach(Activity context){
        super.onAttach(context);
    }
}
