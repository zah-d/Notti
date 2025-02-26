package com.firebase.notti.ui.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.notti.R;
import com.firebase.notti.adapter.NotesAdapter;
import com.firebase.notti.cache.NottiCacheService;
import com.firebase.notti.databinding.FragmentNotesBinding;
import com.firebase.notti.model.Note;
import com.firebase.notti.model.NoteMessage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class NotesFragment extends Fragment {

    private RecyclerView recyclerNotes;
    private NotesAdapter notesAdapter;
    private FragmentNotesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        recyclerNotes = view.findViewById(R.id.recyclerNotes);

        notesAdapter = new NotesAdapter(getContext());
        recyclerNotes.setAdapter(notesAdapter);

        notesAdapter.notifyDataSetChanged();

        return view;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}