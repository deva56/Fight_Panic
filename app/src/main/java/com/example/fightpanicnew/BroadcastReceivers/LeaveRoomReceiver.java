/*Broadcast receiver za detekciju klika na leave room gumb u notifikaciji foreground servisa chat aktivnosti. Pri njegovom kliku
korisnika se vraća natrag u aktivnost i daje odabir kao i unutar aktivnosti.
*/

package com.example.fightpanicnew.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.fightpanicnew.Activity.Chat;

import static com.example.fightpanicnew.Constants.FOREGROUND_SERVICE_ROOM_NAME;
import static com.example.fightpanicnew.Constants.FOREGROUND_SERVICE_USER_NAME;

public class LeaveRoomReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String username = intent.getStringExtra(FOREGROUND_SERVICE_USER_NAME);
        String roomName = intent.getStringExtra(FOREGROUND_SERVICE_ROOM_NAME);

        Intent chatActivity = new Intent(context, Chat.class);
        chatActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        chatActivity.putExtra(FOREGROUND_SERVICE_USER_NAME, username);
        chatActivity.putExtra(FOREGROUND_SERVICE_ROOM_NAME, roomName);
        chatActivity.putExtra("LEAVE_ROOM_BOOLEAN", true);
        context.startActivity(chatActivity);
    }
}
