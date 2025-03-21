package com.firebase.notti.utils;

import com.firebase.notti.model.Note;

import java.util.Date;
import java.util.List;

public class FilterUtil {

    public static void filter(List<Note> notesToFilter) {
        notesToFilter.sort((note1, note2) -> {
            // 1. Compare by favorite (true first)
            if (note1.isFavorite() != note2.isFavorite()) {
                return note1.isFavorite() ? -1 : 1; // Favorite notes first
            }

            if (note1.getLastMessage() == null)
            {
                return 1;
            }
            else if (note2.getLastMessage() == null) {
                return -1;
            }
            else {
                // 2. If both are the same favorite status, compare by dateTime (latest first)
                return new Date(note2.getLastMessage().getTimestamp()).compareTo(new Date(note1.getLastMessage().getTimestamp()));
            }
        });
    }
}
