package com.firebase.notti;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.firebase.notti.R;
import com.firebase.notti.R;

public class NoteActivity extends AppCompatActivity {

    private TextView chatTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        chatTitle = findViewById(R.id.chatTitle);

        String noteTitle = getIntent().getStringExtra("noteTitle");
        chatTitle.setText(noteTitle);


        // TODO: Implement chat functionalities (text, images, docs)
    }
}