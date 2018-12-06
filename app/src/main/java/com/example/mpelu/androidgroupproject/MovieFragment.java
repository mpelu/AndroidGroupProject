package com.example.mpelu.androidgroupproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MovieFragment extends Fragment {

    //view, need to override onbackbutton method
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        Bundle infoToPass = getArguments();
        final int positionPassed = infoToPass.getInt("position");
        final long idPassed = infoToPass.getLong("id");

        String passedTitle = infoToPass.getString("title");
        int passedYear = infoToPass.getInt("year");
        String passedRated = infoToPass.getString("rated");
        int passedRuntime = infoToPass.getInt("runtime");
        String passedActors = infoToPass.getString("actors");
        String passedPlot = infoToPass.getString("plot");

        View screen = inflater.inflate(R.layout.movie_fragment_helper, container, false);
        TextView title = screen.findViewById(R.id.mFragTitle);
        TextView year = screen.findViewById(R.id.mFragYear);
        TextView rated = screen.findViewById(R.id.mFragRated);
        TextView runtime = screen.findViewById(R.id.mFragRuntime);
        TextView actors = screen.findViewById(R.id.mFragActors);
        TextView plot = screen.findViewById(R.id.mFragPlot);

        title.setText(passedTitle);
        year.setText(String.valueOf(passedYear));
        rated.setText(passedRated);
        runtime.setText(String.valueOf(passedRuntime));
        actors.setText(passedActors);
        plot.setText(passedPlot);


        Button delete = screen.findViewById(R.id.deleteMovie);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra("id", idPassed);
                result.putExtra("position", positionPassed);
                getActivity().setResult(999, result);
                getActivity().finish();
            }
        });
        return screen;
    }

    public void onAttach(Activity context){ super.onAttach(context); }
}
