package com.kunoff.lupal.plasickakun;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;

import com.kunoff.lupal.plasickakun.objects.ItemMedia;
import com.kunoff.lupal.plasickakun.utils.AppUtils;

import java.io.IOException;
import java.util.Random;


public class SoundReceiver extends BroadcastReceiver {

    int positionToPlay;
    PowerManager.WakeLock wl;

    @Override
    public void onReceive(final Context context, Intent intent) {

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "kunoff:wakelockTag");
        wl.acquire();

        Intent intentAlarm = new Intent(context, MainActivity.class);
        intentAlarm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (MainActivity.itemsMedia == null) return;
        if (MainActivity.itemsMedia.isEmpty()) return;
        if (MainActivity.enabledItemsPositions == null) return;
        if (MainActivity.enabledItemsPositions.isEmpty()) return;

        findNextItemToPlay();

        if (positionToPlay < 0 || positionToPlay >= MainActivity.itemsMedia.size()) return;

        setItemPlay(context, positionToPlay);
    }

    public void setItemPlay(final Context context, int position) {
        if (MainActivity.itemsMedia == null) return;
        if (MainActivity.itemsMedia.isEmpty()) return;
        if (!MainActivity.isRunning) return;

        ItemMedia itemMediaToPlay;

        itemMediaToPlay = MainActivity.itemsMedia.get(position);

        if (!MainActivity.appPrefs.customPlaylist().get()) {
            MainActivity.m = MediaPlayer.create(context, itemMediaToPlay.getId());
        } else {
            try {
                MainActivity.m = new MediaPlayer();
                MainActivity.m.setAudioStreamType(AudioManager.STREAM_MUSIC);
                MainActivity.m.setDataSource(context, Uri.parse(itemMediaToPlay.getPath()));
                MainActivity.m.prepare();
            } catch (IOException e) {
                findNextItemToPlay();
                setItemPlay(context, positionToPlay);
                return;
            } catch (NullPointerException e) {
                findNextItemToPlay();
                setItemPlay(context, positionToPlay);
                return;
            } catch (IllegalArgumentException e) {
                findNextItemToPlay();
                setItemPlay(context, positionToPlay);
                return;
            }
        }

        MainActivity.m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                MainActivity.m.release();
                MainActivity.m = null;
                AlarmActions.enableAlarm(context, AppUtils.getTimeValueInMillis(MainActivity.appPrefs.delay().get(), MainActivity.appPrefs.delayUnit().get()));
                if (wl != null) wl.release();
            }
        });

        MainActivity.m.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                MainActivity.m = null;
                AlarmActions.enableAlarm(context, AppUtils.getTimeValueInMillis(MainActivity.appPrefs.delay().get(), MainActivity.appPrefs.delayUnit().get()));
                if (wl != null) wl.release();
                return false;
            }
        });

        MainActivity.m.start();
    }

    private void findNextItemToPlay() {
        if (MainActivity.appPrefs.random().get()) setRandomPosition();
        else getNextPositionToPlay();
    }

    private void getNextPositionToPlay() {
        positionToPlay = MainActivity.nextPlayPosition;

        for (Integer im : MainActivity.enabledItemsPositions) {
            if (im.intValue() > positionToPlay) {
                MainActivity.nextPlayPosition = im.intValue();
                break;
            }
        }

        if (positionToPlay == MainActivity.nextPlayPosition) {
            MainActivity.nextPlayPosition = MainActivity.enabledItemsPositions.get(0);
        }
    }

    private void setRandomPosition() {
        int tempPos = new Random().nextInt(MainActivity.enabledItemsPositions.size());
        positionToPlay = MainActivity.enabledItemsPositions.get(tempPos);
    }
}
