package com.example.pddc.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
            holder.textViewMessageAgent.setText(message.getContent());
            holder.textViewMessageUser.setText(message.getContent());
            holder.tvTimeUser.setText(message.getTime());
            holder.tvTimeAgent.setText(message.getTime());
            // Align messages based on user/agent
            if (message.isUser()) {
                holder.linearLayoutAgent.setVisibility(View.GONE);
                holder.linearLayoutUser.setVisibility(View.VISIBLE);
            } else {
//                holder.textViewMessageAgent.setBackgroundResource(R.drawable.bubble_agent);
                holder.linearLayoutUser.setVisibility(View.GONE);
                holder.linearLayoutAgent.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        public static class MessageViewHolder extends RecyclerView.ViewHolder {
            TextView textViewMessageAgent, textViewMessageUser, tvTimeUser, tvTimeAgent;
            LinearLayout linearLayoutAgent, linearLayoutUser;

            public MessageViewHolder(@NonNull View itemView) {
                super(itemView);

                linearLayoutAgent = itemView.findViewById(R.id.llMessageAgent);
                linearLayoutUser = itemView.findViewById(R.id.llMessageUser);
                textViewMessageAgent = itemView.findViewById(R.id.tvMessageAgent);
                textViewMessageUser = itemView.findViewById(R.id.tvMessageUser);
                tvTimeUser = itemView.findViewById(R.id.tvTimeUser);
                tvTimeAgent = itemView.findViewById(R.id.tvTimeAgent);
            }
        }

}
