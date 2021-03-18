package com.example.fightpanicnew.RecyclerViewClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fightpanicnew.Entity.ChatMessages;
import com.example.fightpanicnew.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {

    private final List<ChatMessages> chatList;
    private final int[] usernameColors;
    private final Context context;

    public ChatAdapter(Context context, List<ChatMessages> chatList) {
        this.chatList = chatList;
        this.context = context;
        usernameColors = context.getResources().getIntArray(R.array.username_colors);
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = null;
        switch (viewType) {
            case 0:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_user, parent, false);
                break;
            case 1:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_partner, parent, false);
                break;
            case 2:
            case 3:
            case 4:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_into_notification, parent, false);
                break;
            case 5:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_image, parent, false);
                break;
            case 6:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_partner_image, parent, false);
                break;
        }
        assert itemView != null;
        return new ChatHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        ChatMessages currentMessage = chatList.get(position);
        String userName = currentMessage.getUserName();
        String content = currentMessage.getMessageContent();
        int viewType = currentMessage.getViewType();
        String bitmapPath = currentMessage.getBitmapPath();

        switch (viewType) {
            case 0:
                holder.message.setText(content);
                break;
            case 1:
                holder.userName.setTextColor(getUsernameColor(userName));
                holder.userName.setText(userName);
                holder.message.setText(content);
                break;
            case 2:
                String text = userName + " has entered the room";
                holder.text.setText(text);
                break;
            case 3:
                String text2 = userName + " has leaved the room";
                holder.text.setText(text2);
                break;
            case 4:
                String text3 = userName + " is typing...";
                holder.text.setText(text3);
                break;
            case 5:
                Glide.with(context).load(bitmapPath).centerCrop().placeholder(R.drawable.ic_chat_image_loading_placeholder).into(holder.imageUser);
                break;
            case 6:
                Glide.with(context).load(bitmapPath).centerCrop().placeholder(R.drawable.ic_chat_image_loading_placeholder).into(holder.imagePartner);
                holder.userName.setTextColor(getUsernameColor(userName));
                holder.userName.setText(userName);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return chatList.get(position).getViewType();
    }

    static class ChatHolder extends RecyclerView.ViewHolder {
        private final TextView userName;
        private final TextView message;
        private final TextView text;
        private final ImageView imageUser;
        private final ImageView imagePartner;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.UserJoinedOrLeftRoomTextView);
            message = itemView.findViewById(R.id.PartnerAndUserChatMessage);
            userName = itemView.findViewById(R.id.PartnerUsername);
            imageUser = itemView.findViewById(R.id.UserChatMessageImage);
            imagePartner = itemView.findViewById(R.id.PartnerChatMessageImage);
        }

    }

    public int getUsernameColor(String username) {
        int hash = 7;
        for (int i = 0, len = username.length(); i < len; i++) {
            hash = username.codePointAt(i) + (hash << 5) - hash;
        }
        int index = Math.abs(hash % usernameColors.length);
        return usernameColors[index];
    }
}

