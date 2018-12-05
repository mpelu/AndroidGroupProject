package com.example.mpelu.androidgroupproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static android.app.Activity.RESULT_OK;

public class MessageFragment extends Fragment {
    private Activity parent;
    OcTranspo chat;

    public MessageFragment(){ }

    public MessageFragment(OcTranspo cw){
        chat=cw;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle passedInfo = getArguments();

        long id= 0;
        String message="";
        if(passedInfo != null) {
            id = passedInfo.getLong("ID");
            message = passedInfo.getString("Message");

        }
        Log.i("Passed key", ""+id);
        Log.i("Passed message",""+message);


        parent = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Bundle displayedInfo = getArguments();
        View v = inflater.inflate(R.layout.fragment_layout, null);
        Button b = (Button)v.findViewById(R.id.deleteButton);
        TextView messageTV = (TextView)v.findViewById(R.id.message);
        TextView iDTV = (TextView)v.findViewById(R.id.idNum);

        messageTV.setText(displayedInfo.getString("Message"));
        iDTV.setText(String.valueOf(displayedInfo.getLong("ID")));

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent deleteIntent = new Intent();
                deleteIntent.putExtras(displayedInfo);
                boolean isTablet=displayedInfo.getBoolean("IsTablet");

                if(isTablet){
                    chat.deleteDatabaseRecord(deleteIntent);
                    parent.getFragmentManager().beginTransaction().remove(MessageFragment.this).commit();
                }else {
                    getActivity().setResult(RESULT_OK, deleteIntent);
                    getActivity().finish();
                    parent.getFragmentManager().beginTransaction().remove(MessageFragment.this).commit();
                }
            }
        });
        return v;
    }
}
