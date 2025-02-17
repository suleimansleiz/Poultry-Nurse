package com.example.pddc.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pddc.R;
import com.example.pddc.ui.classes.Schedules;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class SchedulesAdapter extends RecyclerView.Adapter<SchedulesAdapter.SchedulesViewHolder> {

    private final List<Schedules> schedule;
    public SchedulesAdapter(List<Schedules> schedule){
        this.schedule = schedule;
    }
    @NonNull
    @Override
    public SchedulesAdapter.SchedulesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_schedules, parent, false);
        return new SchedulesAdapter.SchedulesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SchedulesAdapter.SchedulesViewHolder holder, int position) {
        Schedules schedules = schedule.get(position);
        holder.tvScheduleName.setText(schedules.getScheduleName());
        holder.tvScheduleTime.setText(schedules.getScheduleTime());
        holder.tvDate.setText(schedules.getScheduleDate());
    }

    @Override
    public int getItemCount() {
        return schedule.size();
    }

    public static class SchedulesViewHolder extends RecyclerView.ViewHolder{
        TextView tvScheduleName, tvScheduleTime, tvDate;
        MaterialCardView mcvSchedule;
        public SchedulesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvScheduleName = itemView.findViewById(R.id.tvScheduleName);
            tvScheduleTime = itemView.findViewById(R.id.tvScheduleTime);
            tvDate = itemView.findViewById(R.id.tvDate);
            mcvSchedule = itemView.findViewById(R.id.mcvSchedule);
        }
    }
}
