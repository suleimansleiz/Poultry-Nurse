package com.example.pddc.ui.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pddc.R;

import java.util.ArrayList;

public class N_RecyclerViewAdapter extends RecyclerView.Adapter<N_RecyclerViewAdapter.MyViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    ArrayList<NotificationModel> notificationModels;
    public N_RecyclerViewAdapter(Context context, ArrayList<NotificationModel> notificationModels, RecyclerViewInterface recyclerViewInterface){
        this.context = context;
        this.notificationModels = notificationModels;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public N_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_batches, parent, false);
        return new N_RecyclerViewAdapter.MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull N_RecyclerViewAdapter.MyViewHolder holder, int position) {

        holder.tv_AIName.setText(notificationModels.get(position).getAiName());
        holder.tv_AINote.setText(notificationModels.get(position).getAiNote());
    }

    @Override
    public int getItemCount() {
        return notificationModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_AIName, tv_AINote;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            tv_AIName = itemView.findViewById(R.id.tvAIName);
            tv_AINote = itemView.findViewById(R.id.tvRecyclerNote);

            itemView.setOnClickListener(v -> {

                if (recyclerViewInterface != null){
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(pos);
                    }
                }
            });
            itemView.setOnLongClickListener(v -> {

                if (recyclerViewInterface != null){
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemLongClick(pos);
                    }
                }
                return true;
            });
        }
    }
}
