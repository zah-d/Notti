package com.firebase.notti;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
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
import java.util.concurrent.atomic.AtomicInteger;

public class NoteActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private NoteAdapter noteAdapter;
    private EditText messageInput;
    private ImageButton sendButton, attachButton, cameraButton;
    private Note currentNote;

    private ImageView searchIcon;

    private ImageView voiceMessageIcon;

    private EditText searchBar;

    private TextView noteTitle;

    private TextView noteKeyboardHint;

    private TextView messageDay;

    private LinearLayout dateLayout;

    private int screenHeight = 0;

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
        noteKeyboardHint = findViewById(R.id.inputMessageHint);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.inputMessage);
        sendButton = findViewById(R.id.sendButton);
        attachButton = findViewById(R.id.attachButton);
        cameraButton = findViewById(R.id.cameraButton);
        voiceMessageIcon = findViewById(R.id.note_voice_button);
        voiceMessageIcon.setVisibility(View.VISIBLE);
        sendButton.setVisibility(View.INVISIBLE);



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
        messageInput.addTextChangedListener(inputTextListener());

        // User Image as Menu Button
        FrameLayout userMenuButton = findViewById(R.id.note_user_menu_button);
        userMenuButton.setOnClickListener(this::menu);

        searchBar = findViewById(R.id.note_search_bar);
        searchIcon = findViewById(R.id.note_search_icon);
        searchIcon.setOnClickListener(v -> searchClick(searchBar));
        searchBar.addTextChangedListener(searchText());

        ImageButton backButton = findViewById(R.id.note_back_button);
        backButton.setOnClickListener(v -> {
            finish(); // Closes this activity and returns to MainNoteActivity
        });
        View rootView = findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight_new = rootView.getHeight();
                if (screenHeight == 0) {
                    screenHeight = screenHeight_new;
                }
                if (screenHeight_new != screenHeight && screenHeight_new < (screenHeight - 200)) {
                        // Keyboard is visible
                        handleKeyboardVisibility(true);
                }
                else {
                        // Keyboard is hidden
                        handleKeyboardVisibility(false);
                }
            }
        });
        noteAdapter.notifyDataSetChanged();
    }

    private void handleKeyboardVisibility(boolean isVisible) {
        if (isVisible) {
            // Keyboard is visible
            voiceMessageIcon.setVisibility(View.INVISIBLE);
            sendButton.setVisibility(View.VISIBLE);
        } else {
            // Keyboard is hidden
            if (messageInput.getText().toString().isEmpty()) {
                voiceMessageIcon.setVisibility(View.VISIBLE);
                sendButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    private TextWatcher inputTextListener() {
        return new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int lastNewLine = s.toString().lastIndexOf("\n");
                int lastLineLength = (lastNewLine == -1) ? s.length() : s.length() - lastNewLine - 1;

                if (lastLineLength >= 20) {
                    messageInput.append("\n"); // Automatically add a new line
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    noteKeyboardHint.setVisibility(View.VISIBLE);
                    cameraButton.setVisibility(View.VISIBLE);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) attachButton.getLayoutParams();
                    params.setMarginEnd(100);
                    attachButton.setLayoutParams(params);
                }
                else {
                    noteKeyboardHint.setVisibility(View.INVISIBLE);
                    cameraButton.setVisibility(View.INVISIBLE);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) attachButton.getLayoutParams();
                    params.setMarginEnd(10);
                    attachButton.setLayoutParams(params);
                }
            }
        };
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

    private void keyboardClick() {
        //Toast.makeText(NoteMainActivity.this, "New search Clicked!", Toast.LENGTH_SHORT).show();

        // Toggle search bar visibility
        if (searchBar.getVisibility() == View.GONE) {
            currentNote.messages_backup.clear();
            currentNote.messages_backup.addAll(currentNote.getMessages());
            noteTitle.setVisibility(View.INVISIBLE);
            searchBar.setVisibility(View.VISIBLE);
            //toolbarTitle.setVisibility(View.GONE);
            searchBar.requestFocus();

            // Show keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);
        } else {
            noteTitle.setVisibility(View.VISIBLE);
            searchBar.setVisibility(View.GONE);
            searchBar.setText("");
            //toolbarTitle.setVisibility(View.VISIBLE);

            // Hide keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
            NottiCacheService.getInstance().filterNoteMessages(currentNote.getId(), null);
            currentNote.messages_backup.clear();
            NoteAdapter.getInstance().notifyDataSetChanged();
            if (NoteAdapter.getInstance().getItemCount() > 2) {
                chatRecyclerView.scrollToPosition(NoteAdapter.getInstance().getItemCount() - 1);
            }
        }
    }
    private void searchClick(EditText searchBar) {
        //Toast.makeText(NoteMainActivity.this, "New search Clicked!", Toast.LENGTH_SHORT).show();

        // Toggle search bar visibility
        if (searchBar.getVisibility() == View.GONE) {
            currentNote.messages_backup.clear();
            currentNote.messages_backup.addAll(currentNote.getMessages());
            noteTitle.setVisibility(View.INVISIBLE);
            searchBar.setVisibility(View.VISIBLE);
            //toolbarTitle.setVisibility(View.GONE);
            searchBar.requestFocus();

            // Show keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);
        } else {
            noteTitle.setVisibility(View.VISIBLE);
            searchBar.setVisibility(View.GONE);
            searchBar.setText("");
            //toolbarTitle.setVisibility(View.VISIBLE);

            // Hide keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
            NottiCacheService.getInstance().filterNoteMessages(currentNote.getId(),null);
            currentNote.messages_backup.clear();
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
            NotesAdapter.getInstance().notifyDataSetChanged();

            // Clear input field
            messageInput.setText("");
            noteKeyboardHint.setVisibility(View.VISIBLE);
            cameraButton.setVisibility(View.VISIBLE);
        }
    }

    private void attachMedia() {
        // Handle media attachments (images, docs, etc.)
        Toast.makeText(this, "Attach media feature not implemented yet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}