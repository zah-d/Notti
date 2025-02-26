package com.firebase.notti.cache;

import com.firebase.notti.db.NottiDBService;
import com.firebase.notti.model.Note;
import com.firebase.notti.model.NoteMessage;
import com.firebase.notti.utils.FilterUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NottiCacheService {
    private List<Note> notes = new ArrayList<>();

    private List<Note> notesFilter = new ArrayList<>();

    private static NottiCacheService instance;

    public NottiCacheService(List<Note> dbNotes) {
        notes.addAll(dbNotes);
        FilterUtil.filter(notes);
        instance = this;
    }

    public static NottiCacheService getInstance() {
        return instance;
    }

    private void updateLists(List<Note> notesN){
        notes.clear();
        //notesFilter.clear();
        notes.addAll(notesN);
        //notesFilter.addAll(notesN);
        FilterUtil.filter(notes);
        //FilterUtil.filter(notesFilter);
    }

    public List<Note> getNotesClonedInstance() {
        return new ArrayList<>(notes);
    }

    public List<Note> getNotesListInstance() {
        return notes;
    }

    public Note getNote(String noteId) {
        for (Note n : notes) {
            if (n.getId().equals(noteId)) {
                return n;
            }
        }
        return null;
    }
    public void addNote(Note note) {
        notes.add(note);
        NottiDBService.getInstance().saveOrUpdateNote(note);
        //notesFilter.add(note);
        FilterUtil.filter(notes);
        //FilterUtil.filter(notesFilter);
    }
    
    public void removeNote(String id) {
        List<Note> nList = new ArrayList<>(notes);
        Iterator<Note> iterNote = nList.iterator();
        while (iterNote.hasNext()) {
            Note n = iterNote.next();
            if (n.getId().equals(id)) {
                iterNote.remove();
                break;
            }
        }
        updateLists(nList);
    }

    public void addNoteMessage(Note note, NoteMessage message) {
        note.addMessage(message);
        NottiDBService.getInstance().saveNoteMessage(note, message);
    }

    public void filterNoteMessages(String noteId, String query) {
        if (query == null) {
            Note dbNote = NottiDBService.getInstance().getNoteCloned(noteId);
            Note cacheNote = getNote(noteId);
            cacheNote.setMessages(dbNote.getMessages());
        }
        else {
            Note note = getNote(noteId);
            List<NoteMessage> filteredMessages = new ArrayList<>();
            for (NoteMessage message : note.getMessages()) {
                if (message.getMessage() instanceof String) {String msg = (String)message.getMessage();
                    if (msg.toLowerCase().contains(query.toLowerCase())) {
                        filteredMessages.add(message);
                    }
                }
            }
            note.setMessages(filteredMessages);

            /*Iterator<NoteMessage> messageIter = note.getMessages().iterator();
            while (messageIter.hasNext()) {
                NoteMessage message = messageIter.next();
                if (message.getMessage() instanceof String) {String msg = (String)message.getMessage();
                    if (!msg.toLowerCase().contains(query.toLowerCase())) {
                        messageIter.remove();
                    }
                }
                else {
                    messageIter.remove();
                }
            }*/
        }
    }

    public void filterNotes(String query) {
        if (query == null){
            FilterUtil.filter(notes);
        }
        else {
            Iterator<Note> notesIter = notes.iterator();
            while (notesIter.hasNext()) {
                Note note = notesIter.next();
                if (!note.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    notesIter.remove();
                }
                else {
                    for (NoteMessage message : note.getMessages()) {
                        if (message.getMessage() instanceof String) {String msg = (String)message.getMessage();
                            if (!msg.toLowerCase().contains(query.toLowerCase())) {
                                notesIter.remove();
                            }
                        }
                        else {
                            notesIter.remove();
                        }
                    }
                }
            }
        }
    }

    public void resetList() {
        updateLists(NottiDBService.getInstance().getNotesCloned());
    }
}
