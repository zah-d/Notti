package com.firebase.notti;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


public class NewNoteActivity extends AppCompatActivity {

    private EditText etNoteTitle;
    private Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        // Initialize Views
        etNoteTitle = findViewById(R.id.etNoteTitle);
        btnCreate = findViewById(R.id.btnCreate);

        // Set Click Listener
        btnCreate.setOnClickListener(view -> {
            String noteTitle = etNoteTitle.getText().toString().trim();
            if (noteTitle.isEmpty()) {
                etNoteTitle.setError("Note title cannot be empty");
                return;
            }
            // Pass the note title back to the previous activity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("note_title", noteTitle);
            setResult(Activity.RESULT_OK, resultIntent);
            finish(); // Close activity
        });

        // Start animation from bottom to top
        overridePendingTransition(R.anim.slide_up, R.anim.stay);
    }

    @Override
    public void finish() {
        super.finish();
        // Close with bottom-to-top animation
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }
}