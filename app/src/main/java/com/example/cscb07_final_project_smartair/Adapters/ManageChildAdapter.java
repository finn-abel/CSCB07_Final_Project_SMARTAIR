package com.example.cscb07_final_project_smartair.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cscb07_final_project_smartair.R;
import com.example.cscb07_final_project_smartair.Users.ChildSpinnerOption;

import java.util.List;

public class ManageChildAdapter extends RecyclerView.Adapter<ManageChildAdapter.ViewHolder> {

    public List<ChildSpinnerOption> childrenList;
    public OnChildClickListener listener;
    public int selectedPos = RecyclerView.NO_POSITION; //position of child clicked

    public interface OnChildClickListener {
        void onChildClick(int position, String childId);
    }

    public ManageChildAdapter(List<ChildSpinnerOption> childrenList, OnChildClickListener listener) {
        this.childrenList = childrenList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.manage_child_selectchild, parent, false); //set up row
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChildSpinnerOption child = childrenList.get(position);

        // click handling
        holder.nameText.setText(child.fullName); //get name

        //selection handling
        holder.itemView.setSelected(selectedPos == position);
        if (selectedPos == position) {
            holder.itemView.setBackgroundColor(Color.parseColor("#60cef0")); // highlighted
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT); // when not selected
        }

        // click handling
        holder.itemView.setOnClickListener(v -> {
            int previous = selectedPos;
            selectedPos = holder.getAdapterPosition();

            // refresh row
            notifyItemChanged(previous);
            notifyItemChanged(selectedPos);

            // send ID to activity
            listener.onChildClick(selectedPos, child.userID);
        });
    }

    @Override
    public int getItemCount() {
        return childrenList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        public ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.tv_child_name);
        }
    }
}