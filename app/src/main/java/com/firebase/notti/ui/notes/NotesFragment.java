package com.firebase.notti.ui.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.notti.R;
import com.firebase.notti.adapter.NotesAdapter;
import com.firebase.notti.databinding.FragmentNotesBinding;
import com.firebase.notti.model.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NotesFragment extends Fragment {

    private RecyclerView recyclerNotes;
    private NotesAdapter notesAdapter;
    private List<Note> notesList = new ArrayList<>();

    private FragmentNotesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        recyclerNotes = view.findViewById(R.id.recyclerNotes);

        notesAdapter = new NotesAdapter(getContext(), notesList);
        recyclerNotes.setAdapter(notesAdapter);

        loadNotesFromFirebase();
        return view;

        /*NotesViewModel notesViewModel =
                new ViewModelProvider(this).get(NotesViewModel.class);

        binding = FragmentNotesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotes;
        notesViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;*/
    }

    private void loadNotesFromFirebase() {
        notesList.clear();
        /*FirebaseFirestore.getInstance().collection("notes")
                .orderBy("timestamp")
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    notesList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        Note note = doc.toObject(Note.class);
                        notesList.add(note);
                    }
                    notesAdapter.notifyDataSetChanged();
                });*/

        notesList.add(new Note(UUID.randomUUID().toString(), "Note1", "Hi My Name is Note 1", System.currentTimeMillis()));
        notesList.add(new Note(UUID.randomUUID().toString(), "Note2", "Hi My Name is Note 2", System.currentTimeMillis()));
        notesList.add(new Note(UUID.randomUUID().toString(), "Note3", "Hi My Name is Note 3", System.currentTimeMillis()));
        notesList.add(new Note(UUID.randomUUID().toString(), "Note4", "Hi My Name is Note 4", System.currentTimeMillis()));
        notesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}