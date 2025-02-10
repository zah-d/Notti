package com.firebase.notti;

import static com.firebase.notti.R.id.favoriteIcon;

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
import com.firebase.notti.model.Note;
import com.firebase.notti.model.NoteMessage;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.widget.Toolbar;

import com.firebase.notti.databinding.ActivityNoteMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class NoteMainActivity extends AppCompatActivity {

    private ImageView searchIcon;

    private EditText searchBar;

    private boolean isFavorite = false;

    private ActivityNoteMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        userMenuButton.setOnClickListener(view -> {
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
        });

        FloatingActionButton fab = findViewById(R.id.fab_add_note);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NoteMainActivity.this, "New Note Clicked!", Toast.LENGTH_SHORT).show();

                // Example: Open a New Note Activity
                Intent intent = new Intent(NoteMainActivity.this, NewNoteActivity.class);
                startActivity(intent);
            }
        });

        searchBar = findViewById(R.id.search_bar);
        searchIcon = findViewById(R.id.search_icon);
        searchIcon.setOnClickListener(v -> {
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
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    NotesAdapter.getInstance().resetList(); // Reset to full list
                } else {
                    filterNotes(s.toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_note_main);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    private void filterNotes(String query) {
        List<Note> filteredList = new ArrayList<>();
        List<Note> allNotes = NotesAdapter.getInstance().getFullNotesList(); // Get full unfiltered list

        for (Note note : allNotes) {
            if (note.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(note);
            }
            else{
                for (NoteMessage message : note.getMessages()) {
                    if (message.getMessage() instanceof String) {String msg = (String)message.getMessage();
                        if (msg.toLowerCase().contains(query.toLowerCase())) {
                            filteredList.add(note);
                        }
                    }
                }
            }
        }

        NotesAdapter.getInstance().updateNotes(filteredList);
    }
}