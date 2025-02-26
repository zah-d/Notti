package com.firebase.notti.db;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.DeniedByServerException;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.firebase.notti.MainActivity;
import com.firebase.notti.NoteMainActivity;
import com.firebase.notti.cache.NottiCacheService;
import com.firebase.notti.model.Note;
import com.firebase.notti.model.NoteMessage;
import com.firebase.notti.utils.ParsingUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NottiDBService {

    private Context context;

    private String userId;

    private List<Note> dbNotes;
    public static NottiDBService instance;

    private boolean initialized = false;

    public NottiDBService(String userId) {
        this.userId = userId;
        instance = this;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public static NottiDBService getInstance() {
        return instance;
    }
    public void initService(Context context) throws Exception {
        this.context = context;
        //connect to firebase
        FirebaseApp.initializeApp(context);
        //loadData();
        loadNotesFromDB();
    }

    public void saveOrUpdateNote(Note note) {

        if (note.getId() == null) {
            saveNote(note);
        }
        else {
            updateNote(note);
        }
    }

    public void saveNote(Note note) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notes")
                .add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String id = documentReference.getId();
                        note.setId(id);
                        dbNotes.add(note);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error adding note", e);
                    }
                });
    }

    public void updateNote(Note note) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notes")
                .document(note.getId()) // Make sure you're using the Firestore document ID
                .set(note, SetOptions.merge()) // ✅ Merges new data with existing fields
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i("Firestore", "Note updated successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error adding note", e);
                    }
                });
    }
    public void saveNoteMessage(Note note, NoteMessage message) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notes")
                .document(note.getId()) // Firestore document ID
                .update("messages", FieldValue.arrayUnion(message)) // ✅ Adds the new message
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i("Firestore", "Note updated successfully");
                        //getNote(note.getId()).addMessage(message);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error adding note", e);
                    }
                });

        /*String json_note_message = ParsingUtil.noteMessageToJson(message);
        for (Note n : dbNotes) {
            if (n.getId().equals(note.getId())) {
                n.addMessage(ParsingUtil.jsonToMessage(json_note_message));
                return;
            }
        }*/
    }
    private void loadNotesFromDB() throws Exception{
        if (dbNotes == null) {
            dbNotes = new ArrayList<>();
        }
        else {
            dbNotes.clear();
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notes")
                .whereEqualTo("userId", "Zah_Darbiani")
                //.orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Note note = document.toObject(Note.class);
                            note.setId(document.getId());
                            dbNotes.add(note);
                            Log.d("Firestore", "Note: " + note.getMessages());
                        }
                        // Init Cache Service
                        new NottiCacheService(dbNotes);
                        initialized = true;
                    } else {
                        Log.w("Firestore", "Error getting notes.", task.getException());
                    }
                });
    }

    public List<Note> getNotes() {
        return this.dbNotes;
    }

    public List<Note> getNotesCloned() {
        List<Note> db_zerializwedNotes = new ArrayList<>();
        for (Note n : this.dbNotes) {
            String json_note = ParsingUtil.noteToJson(n);
            db_zerializwedNotes.add(ParsingUtil.jsonToNote(json_note));
        }
        return db_zerializwedNotes;
    }

    public Note getNote(String noteId) {
        for (Note n : dbNotes) {
            if (n.getId().equals(noteId)) {
                return n;
            }
        }
        return null;
    }

    public Note getNoteCloned(String noteId) {
        for (Note n : dbNotes) {
            if (n.getId().equals(noteId)) {
                String json_note = ParsingUtil.noteToJson(n);
                return ParsingUtil.jsonToNote(json_note);
            }
        }
        return null;
    }

    private void loadData() throws Exception {
        if (dbNotes == null) {
            dbNotes = new ArrayList<>();
        }
        else {
            dbNotes.clear();
        }

        Calendar calNote = Calendar.getInstance();
        calNote.setTime(new Date(System.currentTimeMillis()));

        for (int i = 1; i <= 10; i++) {
            List<NoteMessage> lst = new ArrayList<>();
            for (int j = 1; j <= 10; j++) {
                calNote.add(Calendar.DAY_OF_YEAR, -1);
                lst.add(0, new NoteMessage("Zah_Darbiani", "- קשימה מעודכנת.." + j, "String", calNote.getTime().getTime()));
            }
            Note n = new Note("Zah_Darbiani", "קניות", lst);
            NoteMessage m = new NoteMessage("Zah_Darbiani", "- קשימה מעודכנת.." + i, "String", calNote.getTime().getTime());

            dbNotes.add(n);


            // Subtract 2 hours
            //calNote.add(Calendar.HOUR_OF_DAY, -2);
            m = new NoteMessage("Zah_Darbiani", "- ריקודים אמילי שעות.." + i, "String", calNote.getTime().getTime());
            dbNotes.add(new Note("Zah_Darbiani", "חוגים", m));

            m = new NoteMessage("Zah_Darbiani", "Got it! Since your search bar is in the Main Activity and your note list is inside fragment_notes, \nyou need a way to communicate between them.", "String", calNote.getTime().getTime());
            dbNotes.add(new Note("Zah_Darbiani", "Chat Gpt", m));

            calNote.add(Calendar.DAY_OF_YEAR, -1);
            m = new NoteMessage("Zah_Darbiani", "- להזמין קבלן בידוד.." + i, "String", calNote.getTime().getTime());
            dbNotes.add(new Note("Zah_Darbiani", "בניה", m));

            calNote.add(Calendar.DAY_OF_YEAR, -1);
            m = new NoteMessage("Zah_Darbiani", "- לתקן ציליה, תנור לבדוק.." + i, "String", calNote.getTime().getTime());
            dbNotes.add(new Note("Zah_Darbiani", "צהרון", m));

            calNote.add(Calendar.DAY_OF_YEAR, -1);
            m = new NoteMessage("Zah_Darbiani", "- מחשבים אין סתם פתק.." + i, "String", calNote.getTime().getTime());
            dbNotes.add(new Note("Zah_Darbiani", "מיחשוב", m));

            calNote.add(Calendar.DAY_OF_YEAR, -1);
            m = new NoteMessage("Zah_Darbiani", "- אני הולך לספר סיפור על פתק.." + i, "String", calNote.getTime().getTime());
            dbNotes.add(new Note("Zah_Darbiani", "סיפורים", m));

            m = new NoteMessage("Zah_Darbiani", "- משימות אצל אמא שלי.." + i, "String", calNote.getTime().getTime());
            dbNotes.add(new Note("Zah_Darbiani", "אמא", m));

            m = new NoteMessage("Zah_Darbiani", "- לדבר עם עידן על העבודה.." + i, "String", calNote.getTime().getTime());
            dbNotes.add(new Note("Zah_Darbiani", "עבודה", m));

            m = new NoteMessage("Zah_Darbiani", "- שיעורי בית בחשבון אמילי וגם שפה.." + i, "String", calNote.getTime().getTime());
            dbNotes.add(new Note("Zah_Darbiani", "בית ספר", m));
        }
    }
}
