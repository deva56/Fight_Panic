package com.example.fightpanicnew.RecyclerViewClasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fightpanicnew.Entity.PanicAttackRecord;
import com.example.fightpanicnew.R;

import java.util.ArrayList;
import java.util.List;


public class PanicCalendarAdapter extends RecyclerView.Adapter<PanicCalendarAdapter.PanicCalendarHolder> {

    private List<PanicAttackRecord> records = new ArrayList<>();

    private OnItemClickListener listener;

    @NonNull
    @Override
    public PanicCalendarHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.panic_calendar_item, parent, false);
        return new PanicCalendarHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PanicCalendarHolder holder, int position) {
        PanicAttackRecord currentRecord = records.get(position);
        holder.panicCalendarItemDescription.setText(currentRecord.getDescription());
        holder.panicCalendarItemTitle.setText(currentRecord.getTitle());
        holder.panicCalendarItemDate.setText(currentRecord.getDateOfCreation());
        holder.panicCalendarItemAttackStrength.setText(String.valueOf(currentRecord.getAttackStrength()));
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public void setRecords(List<PanicAttackRecord> records) {
        this.records = records;
        notifyDataSetChanged();
    }

    class PanicCalendarHolder extends RecyclerView.ViewHolder {
        private TextView panicCalendarItemTitle;
        private TextView panicCalendarItemDate;
        private TextView panicCalendarItemDescription;
        private TextView panicCalendarItemAttackStrength;

        public PanicCalendarHolder(@NonNull View itemView) {
            super(itemView);
            panicCalendarItemTitle = itemView.findViewById(R.id.panic_calendar_item_title);
            panicCalendarItemDate = itemView.findViewById(R.id.panic_calendar_item_date);
            panicCalendarItemDescription = itemView.findViewById(R.id.panic_calendar_item_description);
            panicCalendarItemAttackStrength = itemView.findViewById(R.id.panic_calendar_item_attack_strength);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(records.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(PanicAttackRecord panicAttackRecord);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;

    }
}
