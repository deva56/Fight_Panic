package com.example.fightpanicnew.RecyclerViewClasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fightpanicnew.Entity.PillDiaryRecord;
import com.example.fightpanicnew.R;

import java.util.ArrayList;
import java.util.List;


public class PillDiaryAdapter extends RecyclerView.Adapter<PillDiaryAdapter.PillDiaryHolder> {

    private List<PillDiaryRecord> records = new ArrayList<>();

    private OnItemClickListener listener;

    @NonNull
    @Override
    public PillDiaryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pill_diary_item, parent, false);
        return new PillDiaryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PillDiaryHolder holder, int position) {
        PillDiaryRecord currentRecord = records.get(position);
        holder.pillDiaryItemDescription.setText(currentRecord.getDescription());
        holder.pillDiaryItemTitle.setText(currentRecord.getTitle());
        holder.pillDiaryItemDate.setText(currentRecord.getDateOfCreation());
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public void setRecords(List<PillDiaryRecord> records) {
        this.records = records;
        notifyDataSetChanged();
    }

    class PillDiaryHolder extends RecyclerView.ViewHolder {
        private TextView pillDiaryItemTitle;
        private TextView pillDiaryItemDate;
        private TextView pillDiaryItemDescription;

        public PillDiaryHolder(@NonNull View itemView) {
            super(itemView);
            pillDiaryItemTitle = itemView.findViewById(R.id.pill_diary_item_title);
            pillDiaryItemDate = itemView.findViewById(R.id.pill_diary_item_date);
            pillDiaryItemDescription = itemView.findViewById(R.id.pill_diary_item_description);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(records.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(PillDiaryRecord pillDiaryRecord);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;

    }
}
