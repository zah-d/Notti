package com.firebase.notti;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.firebase.notti.R;
import com.firebase.notti.R;

public class NoteActivity extends AppCompatActivity {

    private TextView chatTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_navigation));
        chatTitle = findViewById(R.id.chatTitle);

        String noteTitle = getIntent().getStringExtra("noteTitle");
        chatTitle.setText(noteTitle);


        // TODO: Implement chat functionalities (text, images, docs)
    }
}