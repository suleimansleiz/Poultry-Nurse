package com.example.pddc.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pddc.R;
import com.example.pddc.ui.classes.Message;

import java.util.List;
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder>{
        private final List<Message> messages;

        public ChatAdapter(List<Message> messages) {
            this.messages = messages;
        }

        @NonNull
        @Override
        public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
            Message message = messages.get(position);
            holder.textViewMessage.setText(message.getContent());

            // Align messages based on user/agent
            if (message.isUser()) {
                holder.textViewMessage.setBackgroundResource(R.drawable.bubble_user);
            } else {
                holder.textViewMessage.setBackgroundResource(R.drawable.bubble_agent);
            }
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        public static class MessageViewHolder extends RecyclerView.ViewHolder {
            TextView textViewMessage;

            public MessageViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewMessage = itemView.findViewById(R.id.textViewMessage);
            }
        }

}
