package com.example.mpelu.androidgroupproject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CBCFragment extends Fragment {

    Boolean isTablet;
    public CBCFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Bundle bundle = getArguments();

        return inflater.inflate(R.layout.fragment_message_details, container, false);
    }


}
