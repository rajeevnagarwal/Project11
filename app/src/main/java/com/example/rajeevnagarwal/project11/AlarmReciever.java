package com.example.rajeevnagarwal.project11;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.ArrayList;

public class AlarmReciever extends BroadcastReceiver {
    public AlarmReciever() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Intent i = intent;
        ArrayList<Devices> dev = (ArrayList<Devices>)i.getExtras().get("devlist");
        int pos = i.getExtras().getInt("pos");
        System.out.println("Hello"+dev.get(pos).getName());
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, AlarmReciever.class), 0);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle("xyz")
                        .setContentText("It will contain dummy content");
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

    }
}
