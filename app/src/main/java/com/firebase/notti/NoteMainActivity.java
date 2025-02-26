package com.firebase.notti;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.firebase.notti.adapter.NotesAdapter;
import com.firebase.notti.cache.NottiCacheService;
import com.firebase.notti.model.Note;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.widget.Toolbar;

import com.firebase.notti.databinding.ActivityNoteMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NoteMainActivity extends AppCompatActivity {

    private static final int REQUEST_NEW_NOTE = 100;

    private ImageView searchIcon;

    private EditText searchBar;

    private ActivityNoteMainBinding binding;

    private ActivityResultLauncher<Intent> newNoteLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {


            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_note_main);

            // Set up the Toolbar
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_navigation));

            // Set up BottomNavigationView
            BottomNavigationView navView = findViewById(R.id.nav_view);
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_notes, R.id.navigation_groups)
                    .build();

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_note_main);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);


            // User Image as Menu Button
            FrameLayout userMenuButton = findViewById(R.id.user_menu_button);
            userMenuButton.setOnClickListener(this::userMenu);

            // Initialize the ActivityResultLauncher
            newNoteLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            String noteTitle = result.getData().getStringExtra("note_title");
                            if (noteTitle != null && !noteTitle.isEmpty()) {
                                NottiCacheService.getInstance().addNote(new Note("Zah_Darbiani", noteTitle));
                                NotesAdapter.getInstance().notifyDataSetChanged();
                            }
                        }
                    }
            );

            FloatingActionButton fab = findViewById(R.id.fab_add_note);
            fab.setOnClickListener(addNote());

            searchBar = findViewById(R.id.search_bar);
            searchIcon = findViewById(R.id.search_icon);
            searchIcon.setOnClickListener(searchIconListener(searchBar));
            searchBar.addTextChangedListener(searchTextListener());
        }
        catch (Exception e) {
            String s="";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_NEW_NOTE && resultCode == RESULT_OK && data != null) {
            String noteTitle = data.getStringExtra("NOTE_TITLE");
            if (noteTitle != null) {
                Note newNote = new Note("Zah Darebiani", noteTitle);
                NottiCacheService.getInstance().addNote(newNote);
            }
        }
    }

    private TextWatcher searchTextListener() {
        return new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    NottiCacheService.getInstance().resetList(); // Reset to full list
                    NotesAdapter.getInstance().notifyDataSetChanged();
                } else {
                    NottiCacheService.getInstance().resetList();
                    NottiCacheService.getInstance().filterNotes(s.toString());
                    NotesAdapter.getInstance().notifyDataSetChanged();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        };
    }

    private View.OnClickListener searchIconListener(EditText searchBar) {
        return v -> {
            //Toast.makeText(NoteMainActivity.this, "New search Clicked!", Toast.LENGTH_SHORT).show();

            // Toggle search bar visibility
            if (searchBar.getVisibility() == View.GONE) {
                searchBar.setVisibility(View.VISIBLE);
                //toolbarTitle.setVisibility(View.GONE);
                searchBar.requestFocus();

                // Show keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);
            } else {
                searchBar.setVisibility(View.GONE);
                //toolbarTitle.setVisibility(View.VISIBLE);

                // Hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
                NottiCacheService.getInstance().resetList();
                NotesAdapter.getInstance().notifyDataSetChanged();
            }
        };
    }

    private View.OnClickListener addNote() {
        return view -> {
            Intent intent = new Intent(this, NewNoteActivity.class);
            newNoteLauncher.launch(intent);
       };
    }

    private void userMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_settings, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_profile) {
                Toast.makeText(this, "Profile Clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.action_logout) {
                Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_note_main);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}