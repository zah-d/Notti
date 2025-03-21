package com.firebase.notti.utils;

import com.firebase.notti.model.Note;
import com.firebase.notti.model.NoteMessage;
import com.google.gson.Gson;

public class ParsingUtil {

    public static Gson gson = new Gson();

    public static Note jsonToNote(String json) {
        return gson.fromJson(json,Note.class);
    }

    public static String noteToJson(Note note) {
        return gson.toJson(note);
    }

    public static NoteMessage jsonToMessage(String json) {
        return gson.fromJson(json, NoteMessage.class);
    }

    public static String noteMessageToJson(NoteMessage noteMessage) {
        return gson.toJson(noteMessage);
    }

}
