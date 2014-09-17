package com.github.nrudenko.spottracker.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.github.nrudenko.spottracker.R;
import com.github.nrudenko.spottracker.model.HotSpot;

public class NotificationService extends IntentService {

    private static final String HOT_SPOT = "key_hot_spot";

    public NotificationService() {
        super(NotificationService.class.getSimpleName());
    }

    public static void notify(Context context, HotSpot hotSpot) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.putExtra(HOT_SPOT, hotSpot);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        HotSpot hotSpot = intent.getParcelableExtra(HOT_SPOT);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getString(R.string.you_are_near))
                        .setContentText(hotSpot.name)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setSound(soundUri)
                        .setVibrate(new long[]{0, 100, 1000});

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(hotSpot.name.hashCode(), mBuilder.build());
    }
}
