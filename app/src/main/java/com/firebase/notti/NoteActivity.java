package com.firebase.notti;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.notti.adapter.NoteAdapter;
import com.firebase.notti.adapter.NotesAdapter;
import com.firebase.notti.cache.NottiCacheService;
import com.firebase.notti.model.Note;
import com.firebase.notti.model.NoteMessage;

import java.util.ArrayList;
import java.util.List;

public class NoteActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private NoteAdapter noteAdapter;
    private EditText messageInput;
    private ImageButton sendButton, attachButton;
    private Note currentNote;

    private ImageView searchIcon;

    private EditText searchBar;

    private TextView noteTitle;

    private TextView messageDay;

    private LinearLayout dateLayout;

    public NoteActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.note_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_navigation));

        noteTitle = findViewById(R.id.note_toolbar_title);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.inputMessage);
        sendButton = findViewById(R.id.sendButton);
        attachButton = findViewById(R.id.attachButton);

        String noteId = getIntent().getStringExtra("noteId");
        currentNote = NottiCacheService.getInstance().getNote(noteId);

        noteTitle.setText(currentNote.getTitle());
        noteAdapter = new NoteAdapter(this, currentNote);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);  // Ensures new messages appear at the bottom
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(noteAdapter);

        chatRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            chatRecyclerView.requestLayout();
        });

        sendButton.setOnClickListener(v -> sendMessage());
        attachButton.setOnClickListener(v -> attachMedia());

        // User Image as Menu Button
        FrameLayout userMenuButton = findViewById(R.id.note_user_menu_button);
        userMenuButton.setOnClickListener(this::menu);

        searchBar = findViewById(R.id.note_search_bar);
        searchIcon = findViewById(R.id.note_search_icon);
        searchIcon.setOnClickListener(v -> searchClick(searchBar));
        searchBar.addTextChangedListener(searchText());
        noteAdapter.notifyDataSetChanged();
    }

    private TextWatcher searchText() {
        return new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    NottiCacheService.getInstance().filterNoteMessages(currentNote.getId(), null);
                    NoteAdapter.getInstance().notifyDataSetChanged();
                } else {
                    NottiCacheService.getInstance().filterNoteMessages(currentNote.getId(), null);
                    NottiCacheService.getInstance().filterNoteMessages(currentNote.getId(), s.toString());
                    NoteAdapter.getInstance().notifyDataSetChanged();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    private void searchClick(EditText searchBar) {
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
            searchBar.setText("");
            //toolbarTitle.setVisibility(View.VISIBLE);

            // Hide keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
            NottiCacheService.getInstance().filterNoteMessages(currentNote.getId(),null);
            NoteAdapter.getInstance().notifyDataSetChanged();
            if (NoteAdapter.getInstance().getItemCount() > 2) {
                chatRecyclerView.scrollToPosition(NoteAdapter.getInstance().getItemCount()-1);
            }
        }
    }

    private void menu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.note_menu_settings, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.note_action_profile) {
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

    private void sendMessage() {
        String text = messageInput.getText().toString().trim();
        if (!text.isEmpty()) {
            NoteMessage newMessage = new NoteMessage("Zah_Darbiani",text, "String", System.currentTimeMillis());
            // ✅ Add message to the actual note list
            NottiCacheService.getInstance().addNoteMessage(currentNote, newMessage);

            // ✅ No need to update messageList separately, since it's already referencing currentNote.getMessages()
            noteAdapter.notifyItemInserted(-1);

            // ✅ Ensure RecyclerView scrolls properly
            chatRecyclerView.post(() -> chatRecyclerView.smoothScrollToPosition(currentNote.getMessages().size() - 1));

            noteAdapter.notifyDataSetChanged();

            // Clear input field
            messageInput.setText("");
        }
    }

    private void attachMedia() {
        // Handle media attachments (images, docs, etc.)
        Toast.makeText(this, "Attach media feature not implemented yet", Toast.LENGTH_SHORT).show();
    }
}