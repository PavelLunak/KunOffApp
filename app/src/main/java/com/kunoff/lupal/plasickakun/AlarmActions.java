package com.kunoff.lupal.plasickakun;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.kunoff.lupal.plasickakun.utils.AppConstants;
import com.kunoff.lupal.plasickakun.utils.AppUtils;

import java.text.SimpleDateFormat;

import java.util.Date;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;


public class AlarmActions implements AppConstants {

    public static void enableAlarm(Context context, long delay) {

        Intent intent = new Intent(context, SoundReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setWindow(AlarmManager.RTC_WAKEUP, new Date().getTime() + delay, 0, pi);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, new Date().getTime() + delay, pi);
        }

        showNotification(context, "Next Alarm: " + getNextSoundTime(new Date().getTime() + delay));
    }

    public static void disableAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, SoundReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(pi);

        removeNotification(context);
    }

    public static void showNotification(Context context, String message) {
        Intent intentStart = new Intent(context.getApplicationContext(), MainActivity.class);
        intentStart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, intentStart, 0);

        Notification n = new Notification.Builder(context)
                .setContentTitle("KunOFF")
                .setContentText(message)
                .setSmallIcon(AppUtils.isLollipop() ? R.mipmap.ic_launcher_white_foreground : R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .setOngoing(false)
                .setAutoCancel(true)
                .build();

        n.flags |= Notification.FLAG_NO_CLEAR;

        NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_ID, n);
    }

    public static void removeNotification(Context context) {
        NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(NOTIFICATION_ID);
    }

    public static String getNextSoundTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("d.M.yyyy  H:mm:ss");
        return sdf.format(new Date(time));
    }

    public static void createAppNotificationChanel(
            Context context,
            final String chanelId,
            final String chanelName,
            final String chanelDescription,
            final int chanelImportance) {

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O)return;

        NotificationChannel channel = new NotificationChannel(chanelId, chanelName, chanelImportance);
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        channel.setDescription(chanelDescription);
        notificationManager.createNotificationChannel(channel);
    }

    public static void removeAppNotificationChanel(Context context) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O)return;

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.deleteNotificationChannel(NOTIFICATION_CHANEL_ID);
    }
}
