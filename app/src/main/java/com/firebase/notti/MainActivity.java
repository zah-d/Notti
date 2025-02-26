package com.firebase.notti;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.firebase.notti.cache.NottiCacheService;
import com.firebase.notti.db.NottiDBService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_navigation));

        // init DB
        NottiDBService db = new NottiDBService("Zah_Darbiani");
        try {
            db.initService(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Init Cache Service
        //new NottiCacheService(db.getNotesCloned());

        //Start the note activity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(MainActivity.this, NoteMainActivity.class));
            finish();
        }, 3000);
    }
}