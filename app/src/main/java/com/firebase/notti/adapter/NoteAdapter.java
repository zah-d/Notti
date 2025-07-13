package com.firebase.notti.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.notti.R;
import com.firebase.notti.model.Note;
import com.firebase.notti.model.NoteMessage;
import com.firebase.notti.utils.DateUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

    private String lastMessageDate = "";

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

        String dateStr = DateUtil.getDateText(message.getTimestamp());
        holder.messageDay.setText(dateStr);
        holder.timestampText.setText(DateUtil.sdf_short_hour.format(new Date(message.getTimestamp())));


        if (position > 0) {
            NoteMessage message_before = note.getMessages().get(position-1);
            String message_before_dateStr = DateUtil.getDateText(message_before.getTimestamp());
            if (dateStr.equals(message_before_dateStr)) {
                holder.dateTimeContainer.setVisibility(View.GONE);
            }
            else {
                holder.dateTimeContainer.setVisibility(View.VISIBLE);
            }
        }
        else {
            holder.dateTimeContainer.setVisibility(View.VISIBLE);
        }
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
            messageContainer.setOnLongClickListener(view -> {
                showPopupMenu(view, messageText);
                return true; // Consume the event
            });
        }
        private void showPopupMenu(View view, TextView messageText) {

            LayoutInflater inflater = LayoutInflater.from(view.getContext());
            View menuView = inflater.inflate(R.layout.view_icon_menu, null);

            PopupWindow popup = new PopupWindow(menuView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true); // focusable

            popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popup.setOutsideTouchable(true);

            // Show it below your anchor view
            // Show under the view that was long-clicked (i.e., anchorView = view)
            popup.showAsDropDown(view, 0, 8);

            // Handle clicks
            menuView.findViewById(R.id.action_copy).setOnClickListener(v -> {
                // handle copy
                copyToClipboard(messageText.getText().toString(), view.getContext());
                popup.dismiss();
            });

            menuView.findViewById(R.id.action_share).setOnClickListener(v -> {
                // handle share
                shareMessage(messageText.getText().toString(), view.getContext());
                popup.dismiss();
            });

            menuView.findViewById(R.id.action_delete).setOnClickListener(v -> {
                // handle delete
                popup.dismiss();
            });




            /*PopupMenu popup = new PopupMenu(view.getContext(), view);
            popup.getMenuInflater().inflate(R.menu.message_options_menu, popup.getMenu());

            // Force icons to show using reflection
            try {
                Field[] fields = popup.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if ("mPopup".equals(field.getName())) {
                        field.setAccessible(true);
                        Object menuPopupHelper = field.get(popup);
                        Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                        Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                        setForceIcons.invoke(menuPopupHelper, true);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            popup.setOnMenuItemClickListener(item -> handleMenuItemClick(item, messageText, view.getContext()));

            popup.show();
        }

        private boolean handleMenuItemClick(MenuItem item, TextView messageText, Context context) {
            if (item.getItemId() == R.id.copy_message) {
                copyToClipboard(messageText.getText().toString(), context);
                return true;
            }
            else if (item.getItemId() == R.id.share_message) {
                shareMessage(messageText.getText().toString(), context);
                return true;
            }
            else if (item.getItemId() == R.id.delete_message) {
                Toast.makeText(context, "Message deleted", Toast.LENGTH_SHORT).show();
                return true;
            }
            else {
                return true;
            }
        }*/
        }
        private void copyToClipboard(String text, Context context) {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
        }

        private void shareMessage(String text, Context context) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            context.startActivity(Intent.createChooser(shareIntent, "Share via"));
        }

        private void deleteMessage(String text, Context context) {
            Toast.makeText(context, "Message deleted", Toast.LENGTH_SHORT).show();
        }
    }
}
