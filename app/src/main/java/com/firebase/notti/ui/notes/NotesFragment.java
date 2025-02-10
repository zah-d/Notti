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
import com.firebase.notti.databinding.FragmentNotesBinding;
import com.firebase.notti.model.Note;
import com.firebase.notti.model.NoteMessage;

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

        NoteMessage m = new NoteMessage("Zah_Darbiani","קשימה מעודכנת..", "String", System.currentTimeMillis());
        notesList.add(new Note("Zah_Darbiani", "קניות",m));

        m = new NoteMessage("Zah_Darbiani","ריקודים אמילי שעות..", "String", System.currentTimeMillis());
        notesList.add(new Note("Zah_Darbiani", "חוגים",m));

        m = new NoteMessage("Zah_Darbiani","Got it! Since your search bar is in the Main Activity and your note list is inside fragment_notes, \nyou need a way to communicate between them.", "String", System.currentTimeMillis());
        notesList.add(new Note("Zah_Darbiani", "Chat Gpt",m));

        m = new NoteMessage("Zah_Darbiani","להזמין קבלן בידוד..", "String", System.currentTimeMillis());
        notesList.add(new Note("Zah_Darbiani", "בניה",m));

        m = new NoteMessage("Zah_Darbiani","לתקן ציליה, תנור לבדוק..", "String", System.currentTimeMillis());
        notesList.add(new Note("Zah_Darbiani", "צהרון",m));

        m = new NoteMessage("Zah_Darbiani","מחשבים אין סתם פתק..", "String", System.currentTimeMillis());
        notesList.add(new Note("Zah_Darbiani", "מיחשוב",m));

        m = new NoteMessage("Zah_Darbiani","אני הולך לספר סיפור על פתק..", "String", System.currentTimeMillis());
        notesList.add(new Note("Zah_Darbiani", "סיפורים",m));

        m = new NoteMessage("Zah_Darbiani","משימות אצל אמא שלי..", "String", System.currentTimeMillis());
        notesList.add(new Note("Zah_Darbiani", "אמא",m));

        m = new NoteMessage("Zah_Darbiani","לדבר עם עידן על העבודה..", "String", System.currentTimeMillis());
        notesList.add(new Note("Zah_Darbiani", "עבודה",m));

        m = new NoteMessage("Zah_Darbiani","שיעורי בית בחשבון אמילי וגם שפה..", "String", System.currentTimeMillis());
        notesList.add(new Note("Zah_Darbiani", "בית ספר",m));

        notesAdapter.setNotesFullList(notesList);

        notesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}