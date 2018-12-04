package com.example.mpelu.androidgroupproject;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.mpelu.androidgroupproject.ocDatabase.TABLE_NAME;

public class OcTranspo extends Activity {

    ListView outputView;
    EditText inputText;
    Button sendButton;
    ArrayList<String> list = new ArrayList<>();
    FrameLayout frame;
    boolean isPhone;
    Cursor c;
    final static int REQUESTED_RESULT_FRAGMENT = 10;
    ocDatabase dbHelper;
    protected static final String ACTIVITY_NAME = "OcTranspo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oc_transpo);

        outputView=(ListView) findViewById(R.id.lvOC);
        inputText=(EditText) findViewById(R.id.etOC);
        sendButton=(Button) findViewById(R.id.btnOC);
        isPhone=(FrameLayout) findViewById(R.id.frame_layout)==null;

        dbHelper = new ocDatabase(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        c = db.query(false, "chat_message_tabele",
                new String[] {ocDatabase.KEY_MESSAGE, ocDatabase.KEY_ID},null,null,
                null,null,null,null);
        final int messageIndex = c.getColumnIndex("MESSAGE");

        outputView.setOnItemClickListener((parent, view, position, id) -> {
            Bundle b=new Bundle();
            b.putLong("ID",id);
            b.putString("Message", list.get(position));
            if(isPhone){
                Intent goToDetails = new Intent (OcTranspo.this, MessageDetails.class);
                goToDetails.putExtras(b);
                startActivityForResult(goToDetails, REQUESTED_RESULT_FRAGMENT);
            }else{
                b.putBoolean("IsTablet", true);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                MessageFragment mf = new MessageFragment(OcTranspo.this);
                mf.setArguments(b); //pass the id to the fragment

                ft.replace(R.id.frame_layout, mf)
                        .addToBackStack("")
                        .commit();
            }
        });

        c.moveToFirst();
        while(!c.isAfterLast()) {
            Log.i(ACTIVITY_NAME, "SQL MESSAGE:" + c.getString(
                    c.getColumnIndex(ocDatabase.KEY_MESSAGE)));
            String chatMessage=c.getString(messageIndex);
            list.add(chatMessage);
            c.moveToNext();
            Log.i(ACTIVITY_NAME, "Cursor’s column count ="+c.getColumnCount());
        }
        for(int i=0; i<c.getColumnCount();i++){
            Log.i(ACTIVITY_NAME,"Column name: "+c.getColumnName(i));
        }

        final ChatAdapter messageAdapter =new ChatAdapter( this );
        outputView.setAdapter(messageAdapter);

        sendButton.setOnClickListener((View v) -> {
            String m = inputText.getText().toString();
            ContentValues cValues = new ContentValues();
            cValues.put("MESSAGE", m);
            db.insert(TABLE_NAME,null, cValues);
            c = db.query(false, "chat_message_tabele",
                    new String[] {ocDatabase.KEY_MESSAGE, ocDatabase.KEY_ID},null,null,
                    null,null,null,null);
            c.moveToFirst();

            list.add(m);
            inputText.setText("");
            messageAdapter.notifyDataSetChanged();
        });

        ProgressBar ocpb = findViewById(R.id.pbOC);

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Bundle passedInfo = data.getExtras();
        String[] whereArgs = new String[]{String.valueOf(passedInfo.getLong("ID"))};

        if(requestCode == REQUESTED_RESULT_FRAGMENT && resultCode == RESULT_OK){
            //db.delete("chat_message_tabele", "_id=?", whereArgs);
            deleteDatabaseRecord(data);
        }
    }
    public void deleteDatabaseRecord(Intent data){
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Bundle passedInfo = data.getExtras();
        String[] whereArgs = new String[]{String.valueOf(passedInfo.getLong("ID"))};
        final ChatAdapter messageAdapter =new ChatAdapter( this );
        int deletedMessage = c.getColumnIndex(String.valueOf(passedInfo.getLong("ID")));

        db.delete("chat_message_tabele", "_id=?", whereArgs);

        list.remove(deletedMessage+1);

        outputView.setAdapter(messageAdapter);

    }

    private class ChatAdapter extends ArrayAdapter<String> {

        ChatAdapter(Context ctx) {
            super(ctx,0);
        }
        public int getCount(){
            return list.size();
        }
        public String getItem(int position) {
            return list.get(position);
        }
        public long getItemId(int position){
            c.moveToPosition(position);
            return c.getInt(c.getColumnIndex(ocDatabase.KEY_ID));
            //return position;
        }
        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = OcTranspo.this.getLayoutInflater();

            View result = null ;
            if(position%2 == 0)
                result = inflater.inflate(R.layout.chat_row_incoming, null);
            else
                result = inflater.inflate(R.layout.chat_row_outgoing, null);

            TextView message = (TextView)result.findViewById(R.id.message_text);
            message.setText(   getItem(position)  );
            return result;

        }
    }
}
