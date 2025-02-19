package com.firebase.notti.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.notti.NewNoteActivity;
import com.firebase.notti.NoteActivity;
import com.firebase.notti.NoteMainActivity;
import com.firebase.notti.R;
import com.firebase.notti.model.Note;
import com.firebase.notti.model.NoteMessage;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private static NotesAdapter instance;

    public static Gson gson = new Gson();

    public static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
    public static SimpleDateFormat sdf_short = new SimpleDateFormat("dd.MM", Locale.US);

    private final Context context;
    private List<Note> notesList;
    private List<Note> fullNotesList; // Backup for filtering

    private static Note focusedNote;

    private static int focusedNoteLocation;

    private List<Note> favoritNotesList;

    public NotesAdapter(Context context, List<Note> notesList) {
        this.context = context;
        this.notesList = notesList;
        if (instance == null) {
            instance = this;
        }
    }

    public static NotesAdapter getInstance() {
        return instance;
    }

    public static Note getClickedNote(){
        return focusedNote;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notesList.get(position);
        NoteMessage lastmessage = note.getLastMessage();
        holder.title.setText(note.getTitle());
        holder.lastMessage.setText((String)lastmessage.getMessage());

        holder.lastMessageDateTime.setText(getDateText(lastmessage.getTimestamp()));

        Drawable newIcon = ContextCompat.getDrawable(holder.itemView.getContext(),
                note.isFavorite() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off
        );
        holder.favoriteIcon.setBackground(newIcon);
        holder.favoriteIcon.invalidate();


        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NoteActivity.class);
            intent.putExtra("noteId", note.getId());
            intent.putExtra("noteTitle", note.getTitle());
            intent.putExtra("selected_note", gson.toJson(note));
            focusedNote = note;
            focusedNoteLocation = position;
            context.startActivity(intent);
        });

        // Handle click event
        holder.favoriteIcon.setOnClickListener(v -> {
            boolean newState = !note.isFavorite();
            note.setFavorite(newState);

            Drawable newIcon2 = ContextCompat.getDrawable(holder.itemView.getContext(),
                    note.isFavorite() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off
            );
            holder.favoriteIcon.setBackground(newIcon2);
            holder.favoriteIcon.invalidate();
            //notifyItemChanged(position);

            notesList.sort((note1, note2) -> {
                // 1. Compare by favorite (true first)
                if (note1.isFavorite() != note2.isFavorite()) {
                    return note1.isFavorite() ? -1 : 1; // Favorite notes first
                }

                // 2. If both are the same favorite status, compare by dateTime (latest first)
                return new Date(note2.getLastMessage().getTimestamp()).compareTo(new Date(note1.getLastMessage().getTimestamp()));
            });
            notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public List<Note> getNotesList() {
        return notesList;
    }

    public List<Note> getFullNotesList() {
        return fullNotesList;
    }

    public void setNotesFullList(List<Note> notesFromDB){
        this.fullNotesList = new ArrayList<>(notesFromDB);
    }

    public void addNoteMessage(NoteMessage message, boolean reset) {
            Note note = getClickedNote();
            note.addMessage(message);

            Note cacheNote = notesList.get(focusedNoteLocation);
            if (cacheNote != null) {
                cacheNote.addMessage(message);
                if (reset) {
                    fullNotesList = notesList;
                }
            }
            notifyDataSetChanged();
    }

    public void updateNotes(List<Note> newNotes) {
        notesList.clear();
        notesList.addAll(newNotes);
        notesList.sort((note1, note2) -> {
            // 1. Compare by favorite (true first)
            if (note1.isFavorite() != note2.isFavorite()) {
                return note1.isFavorite() ? -1 : 1; // Favorite notes first
            }

            // 2. If both are the same favorite status, compare by dateTime (latest first)
            return new Date(note2.getLastMessage().getTimestamp()).compareTo(new Date(note1.getLastMessage().getTimestamp()));
        });
        notifyDataSetChanged();
    }

    public void resetList() {
        updateNotes(fullNotesList);
    }

    public static String getDateText(long d_note_timestamp) {
        String textToShow = "";
        Date d_now = new Date(System.currentTimeMillis());
        Date d_note = new Date(d_note_timestamp);
        // Get calendar instances for comparison
        Calendar calNow = Calendar.getInstance();
        calNow.setTime(d_now);

        Calendar calNote = Calendar.getInstance();
        calNote.setTime(d_note);

        // Check if d_note is today
        if (calNow.get(Calendar.YEAR) == calNote.get(Calendar.YEAR) && calNow.get(Calendar.MONTH) == calNote.get(Calendar.MONTH)) {
            if (calNow.get(Calendar.DAY_OF_YEAR) == calNote.get(Calendar.DAY_OF_YEAR)) {
                textToShow = "היום";
            }
            else {
                // Format the date as "dd/MM" (or another preferred format)
                textToShow = sdf_short.format(d_note);
            }
        }
        // Check if d_note is yesterday
        else {
            calNow.add(Calendar.DAY_OF_YEAR, -1); // Move one day back
            if (calNow.get(Calendar.YEAR) == calNote.get(Calendar.YEAR) && calNow.get(Calendar.MONTH) == calNote.get(Calendar.MONTH) &&
                    calNow.get(Calendar.DAY_OF_YEAR) == calNote.get(Calendar.DAY_OF_YEAR)) {
                textToShow = "אתמול";
            }
            else {
                // Format the date as "dd/MM/yyyy" (or another preferred format)
                textToShow = sdf.format(d_note);
            }
        }

        return textToShow;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView title, lastMessage, lastMessageDateTime;
        ImageView favoriteIcon;

        public NoteViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.noteTitle);
            lastMessage = itemView.findViewById(R.id.noteLastMessage);
            lastMessageDateTime = itemView.findViewById(R.id.noteLastMessageDateTime);
            favoriteIcon = itemView.findViewById(R.id.favoriteIcon);
        }
    }
}
