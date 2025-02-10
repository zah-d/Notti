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

import com.firebase.notti.NoteActivity;
import com.firebase.notti.NoteMainActivity;
import com.firebase.notti.R;
import com.firebase.notti.model.Note;
import com.firebase.notti.model.NoteMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private static NotesAdapter instance;

    public static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.US);

    private final Context context;
    private List<Note> notesList;
    private List<Note> fullNotesList; // Backup for filtering

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
        holder.lastMessageDateTime.setText(sdf.format(new Date(lastmessage.getTimestamp())));

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
            notifyItemChanged(position);
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

    public void updateNotes(List<Note> newNotes) {
        notesList.clear();
        notesList.addAll(newNotes);
        notifyDataSetChanged();
    }

    public void resetList() {
        updateNotes(fullNotesList);
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
