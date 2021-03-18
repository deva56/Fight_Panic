package com.example.fightpanicnew.RecyclerViewClasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fightpanicnew.Models.Room;
import com.example.fightpanicnew.R;

import java.util.List;

public class FightClubAdapter extends RecyclerView.Adapter<FightClubAdapter.FightClubHolder> {

    private List<Room> records;

    private OnItemClickListener listener;

    public FightClubAdapter(List<Room> records) {
        this.records = records;
    }

    @NonNull
    @Override
    public FightClubHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_item, parent, false);
        return new FightClubHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FightClubHolder holder, int position) {
        Room currentRecord = records.get(position);

        String text = "";
        holder.roomTitle.setText(currentRecord.getRoomName());
        holder.roomDescription.setText(currentRecord.getRoomDescription());
        if (currentRecord.getRoomPrivate()) {
            text = "CLOSED";
            holder.roomLock.setImageResource(R.drawable.ic_room_lock_closed);
        } else {
            text = "OPEN";
            holder.roomLock.setImageResource(R.drawable.ic_room_open);
        }

        holder.roomPrivate.setText(text);
    }

    @Override
    public int getItemCount() {
        if (records != null) {
            return records.size();
        } else {
            return 0;
        }
    }

    public void setRecords(List<Room> records) {
        this.records = records;
        notifyDataSetChanged();
    }

    class FightClubHolder extends RecyclerView.ViewHolder {
        private TextView roomTitle;
        private TextView roomDescription;
        private TextView roomPrivate;
        private ImageView roomLock;

        public FightClubHolder(@NonNull View itemView) {
            super(itemView);
            roomTitle = itemView.findViewById(R.id.fight_club_room_name);
            roomDescription = itemView.findViewById(R.id.fight_club_room_description);
            roomPrivate = itemView.findViewById(R.id.fight_club_room_status);
            roomLock = itemView.findViewById(R.id.fight_club_room_lock);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(records.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Room room);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;

    }
}
