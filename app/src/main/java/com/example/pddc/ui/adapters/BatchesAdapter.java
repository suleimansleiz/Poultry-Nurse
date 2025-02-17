package com.example.pddc.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pddc.R;
import com.example.pddc.ui.classes.BatchesModel;
import com.example.pddc.ui.classes.RVBatchesInterface;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class BatchesAdapter extends RecyclerView.Adapter<BatchesAdapter.MyViewHolder> {

    private final RVBatchesInterface RVBatchesInterface;
    private final List<BatchesModel> batchesModels;


    public BatchesAdapter( List<BatchesModel> batchesModels){
        this.batchesModels = batchesModels;
        this.RVBatchesInterface = null;
    }

    @NonNull
    @Override
    public BatchesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_batches, parent, false);
        return new BatchesAdapter.MyViewHolder(view, RVBatchesInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull BatchesAdapter.MyViewHolder holder, int position) {
        BatchesModel batchesModel = batchesModels.get(position);
        holder.tvBatchName.setText(batchesModel.getBatchName());
        holder.tvChicksAge.setText(batchesModel.getBatchAge());
        holder.tvChicksNo.setText(batchesModel.getChicksNo());
        holder.tvBatchDate.setText(batchesModel.getBatchDate());

    }

    @Override
    public int getItemCount() {
        return batchesModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvBatchName, tvBatchDate, tvChicksAge, tvChicksNo;
        MaterialCardView mcvBatches;
        ImageButton ibEditBatch;

        public MyViewHolder(@NonNull View itemView, RVBatchesInterface RVBatchesInterface) {
            super(itemView);

            tvBatchName = itemView.findViewById(R.id.tvBatchName);
            tvBatchDate = itemView.findViewById(R.id.tvBatchDate);
            tvChicksAge = itemView.findViewById(R.id.tvChicksAge);
            tvChicksNo = itemView.findViewById(R.id.tvChicksNo);
            mcvBatches = itemView.findViewById(R.id.mcvBatches);
            ibEditBatch = itemView.findViewById(R.id.ibEditBatch);

            ibEditBatch.setOnClickListener(v -> {
                //Editing Here.
            });

            itemView.setOnLongClickListener(v -> {

                if (RVBatchesInterface != null){
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION){
                        RVBatchesInterface.onItemLongClick(pos);
                    }
                }
                return true;
            });
        }
    }
}
