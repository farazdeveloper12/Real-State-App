package com.example.realestateapp;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ChatMessage.TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_user, parent, false);
            return new UserMessageViewHolder(view);
        } else if (viewType == ChatMessage.TYPE_AI) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_ai, parent, false);
            return new AIMessageViewHolder(view);
        } else {
            // Typing indicator
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_typing, parent, false);
            return new TypingIndicatorViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        if (holder instanceof UserMessageViewHolder) {
            UserMessageViewHolder userHolder = (UserMessageViewHolder) holder;
            userHolder.messageText.setText(message.getMessage());

            // Set max width for message bubble based on screen width
            DisplayMetrics displayMetrics = userHolder.itemView.getContext().getResources().getDisplayMetrics();
            int screenWidth = displayMetrics.widthPixels;
            int maxWidth = (int)(screenWidth * 0.75); // 75% of screen width

            userHolder.messageText.setMaxWidth(maxWidth);
        } else if (holder instanceof AIMessageViewHolder) {
            AIMessageViewHolder aiHolder = (AIMessageViewHolder) holder;
            aiHolder.messageText.setText(message.getMessage());

            // Set max width for message bubble based on screen width
            DisplayMetrics displayMetrics = aiHolder.itemView.getContext().getResources().getDisplayMetrics();
            int screenWidth = displayMetrics.widthPixels;
            int maxWidth = (int)(screenWidth * 0.75); // 75% of screen width

            aiHolder.messageText.setMaxWidth(maxWidth);
        }
        // No binding needed for typing indicator
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;

        UserMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
        }
    }

    static class AIMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;

        AIMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
        }
    }

    static class TypingIndicatorViewHolder extends RecyclerView.ViewHolder {
        TypingIndicatorViewHolder(View itemView) {
            super(itemView);
        }
    }
}