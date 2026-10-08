package com.example.musicplayer;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class MusicPlaybackService extends Service {

    private static final String CHANNEL_ID = "music_playback";
    private static final int NOTIFICATION_ID = 1;
    private static final String TAG = "MusicService";

    private MediaPlayer mediaPlayer;
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        MusicPlaybackService getService() {
            return MusicPlaybackService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "SERVICE CREATED");
        createNotificationChannel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "SERVICE UNBOUND");
        return super.onUnbind(intent);
    }

    public void play(String audioUrl) {
        stopPlayer();
        startPlaybackNotification();

        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(audioUrl);
            setPlayerListeners();

            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                Log.d(TAG, "ONLINE PLAYBACK STARTED");
                updateNotification("Playing online music");
            });

            mediaPlayer.prepareAsync();

        } catch (Exception e) {
            Log.e(TAG, "ERROR STARTING ONLINE PLAYBACK", e);
            handlePlaybackError();
        }
    }

    public void playLocal(int resourceId) {
        stopPlayer();
        startPlaybackNotification();

        mediaPlayer = MediaPlayer.create(this, resourceId);

        if (mediaPlayer == null) {
            Log.e(TAG, "Unable to load local audio");
            handlePlaybackError();
            return;
        }

        setPlayerListeners();

        mediaPlayer.start();
        Log.d(TAG, "LOCAL PLAYBACK STARTED");
        updateNotification("Playing local music");
    }

    private void setPlayerListeners() {
        mediaPlayer.setOnCompletionListener(mp -> {
            Log.d(TAG, "PLAYBACK COMPLETED");
            stopPlayer();
            stopForeground(STOP_FOREGROUND_REMOVE);
            stopSelf();
        });

        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            Log.e(TAG, "PLAYBACK ERROR: " + what + ", " + extra);
            handlePlaybackError();
            return true;
        });
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            updateNotification("Music paused");
        }
    }

    public void resume() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.start();
                updateNotification("Playing music");
            } catch (IllegalStateException e) {
                Log.e(TAG, "Unable to resume playback", e);
            }
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    private void startPlaybackNotification() {
        Notification notification =
                buildNotification("Preparing music");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            );
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
    }

    private void updateNotification(String status) {
        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(NOTIFICATION_ID, buildNotification(status));
    }

    private Notification buildNotification(String status) {
        Intent intent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT |
                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                                ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Music Player")
                .setContentText(status)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Playback",
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager =
                    getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);
        }
    }

    private void handlePlaybackError() {
        stopPlayer();
        stopForeground(STOP_FOREGROUND_REMOVE);
        stopSelf();
    }

    private void stopPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "SERVICE DESTROYED");
        stopPlayer();
        super.onDestroy();
    }
}

