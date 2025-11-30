package com.example.cscb07_final_project_smartair.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cscb07_final_project_smartair.DataObjects.CheckInData;
import com.example.cscb07_final_project_smartair.R;

import java.util.ArrayList;

public class CheckInHistoryAdapter extends RecyclerView.Adapter<CheckInHistoryAdapter.CheckInViewHolder> {

    private final ArrayList<CheckInData> checkInList;

    public CheckInHistoryAdapter(ArrayList<CheckInData> checkInList) {
        this.checkInList = checkInList;
    }

    @NonNull
    @Override
    public CheckInViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_in_item_layout, parent, false);
        return new CheckInViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckInViewHolder holder, int position) {
        CheckInData currentItem = checkInList.get(position);
        holder.bind(currentItem);
    }

    @Override
    public int getItemCount() {
        return checkInList.size();
    }

    public void updateData(ArrayList<CheckInData> newCheckInList) {
        this.checkInList.clear();
        this.checkInList.addAll(newCheckInList);
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }


    public static class CheckInViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewDate;
        private final TextView textViewSymptoms;
        private final TextView textViewTriggers;

        public CheckInViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.Date);
            textViewSymptoms = itemView.findViewById(R.id.Symptoms);
            textViewTriggers = itemView.findViewById(R.id.Triggers);
        }

        // Binds a CheckInData object to the views
        public void bind(CheckInData checkInData) {
            textViewDate.setText("Date: " + checkInData.date);

            // Format lists for display
            String symptoms = "Symptoms: " + String.join(", ", checkInData.symptoms);
            String triggers = "Triggers: " + String.join(", ", checkInData.triggers);

            textViewSymptoms.setText(symptoms);
            textViewTriggers.setText(triggers);
        }
    }
}

