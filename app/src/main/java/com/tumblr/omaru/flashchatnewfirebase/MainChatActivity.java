package com.tumblr.omaru.flashchatnewfirebase;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.lang3.StringUtils;


public class MainChatActivity extends AppCompatActivity {

    // TODO: Add member variables here:
    private String mDisplayName;
    private ListView mChatListView;
    private EditText mInputText;
    private ImageButton mSendButton;
    private DatabaseReference databaseReference;
    private ChatListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        setupDisplayName();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Link the Views in the layout to the Java code
        mInputText = (EditText) findViewById(R.id.messageInput);
        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        mChatListView = (ListView) findViewById(R.id.chat_list_view);

        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                sendMessage();
                return false;
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("FlashChat", " I sent something");
                sendMessage();
            }
        });
    }

    // TODO: Retrieve the display name from the Shared Preferences
    private void setupDisplayName(){
        SharedPreferences prefs = getSharedPreferences(RegisterActivity.CHAT_PREFS,MODE_PRIVATE);
        mDisplayName = prefs.getString(RegisterActivity.DISPLAY_NAME_KEY,null);
        if(StringUtils.isBlank(mDisplayName)) {
            mDisplayName = "Anonymous";
        }
    }

    private void sendMessage() {
        String input = mInputText.getText().toString();
        if(!"".equals(input)){
            InstantMessage message = new InstantMessage(input,mDisplayName);
            databaseReference.child("messages").push().setValue(message);
            mInputText.setText("");
        }

    }
    @Override
    public void onStart(){
        super.onStart();
        adapter = new ChatListAdapter(this,databaseReference,mDisplayName);
        mChatListView.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.cleanup();
    }

}
