package com.firebase.notti.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.notti.R;
import com.firebase.notti.model.Note;
import com.firebase.notti.model.NoteMessage;
import com.firebase.notti.utils.DateUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MessageViewHolder>{

    private Note note;

    private static NoteAdapter instance;

    private final Context context;

    public NoteAdapter(Context context, Note note) {
        this.context = context;
        this.note = note;
        if (instance == null) {
            instance = this;
        }
    }

    public static NoteAdapter getInstance() {
        return instance;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        //view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //LinearLayout dateTimeContainer = parent.findViewById(R.id.date_time_container);
        //LinearLayout messageContainer = parent.findViewById(R.id.messageContainer);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        NoteMessage message = note.getMessages().get(position);
        holder.messageText.setText((String)message.getMessage());
        if (position > 0) {
            Date d_now = new Date(message.getTimestamp());
            Date d_note = new Date(note.getMessages().get(position-1).getTimestamp());
            // Get calendar instances for comparison
            Calendar calNow = Calendar.getInstance();
            calNow.setTime(d_now);

            Calendar calNote = Calendar.getInstance();
            calNote.setTime(d_note);

            // Check if d_note is today
            if (calNow.get(Calendar.YEAR) == calNote.get(Calendar.YEAR) && calNow.get(Calendar.MONTH) == calNote.get(Calendar.MONTH)) {
                if (calNow.get(Calendar.DAY_OF_YEAR) == calNote.get(Calendar.DAY_OF_YEAR)) {
                    holder.dateTimeContainer.setVisibility(View.GONE);
                }
                else{
                    holder.dateTimeContainer.setVisibility(View.VISIBLE);
                }
            }
            else{
                holder.dateTimeContainer.setVisibility(View.VISIBLE);
            }
        }
        else{
            holder.dateTimeContainer.setVisibility(View.VISIBLE);
        }
        holder.messageDay.setText(DateUtil.getDateText(message.getTimestamp()));
        holder.timestampText.setText(DateUtil.sdf_short_hour.format(new Date(message.getTimestamp())));
    }

    @Override
    public int getItemCount() {
        return note.getMessages().size();
    }


    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timestampText, messageDay;
        LinearLayout dateTimeContainer, messageContainer;

        public MessageViewHolder(View itemView) {
            super(itemView);
            dateTimeContainer = itemView.findViewById(R.id.date_time_container);
            messageContainer = itemView.findViewById(R.id.messageContainer);
            messageDay = itemView.findViewById(R.id.date_time);
            messageText = itemView.findViewById(R.id.messageText);
            timestampText = itemView.findViewById(R.id.timestampText);

        }
    }
}
