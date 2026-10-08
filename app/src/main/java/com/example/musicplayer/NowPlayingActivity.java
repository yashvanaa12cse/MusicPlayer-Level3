
package com.example.musicplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NowPlayingActivity extends AppCompatActivity {

    private static final String TAG = "APP_CHECK";

    private MusicPlaybackService playbackService;
    private boolean serviceBound = false;
    private boolean playing = false;
    private boolean isOnline = false;

    private Button playButton;
    private String previewUrl;
    private String song;
    private int audioResource;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            MusicPlaybackService.LocalBinder localBinder =
                    (MusicPlaybackService.LocalBinder) binder;

            playbackService = localBinder.getService();
            serviceBound = true;

            Log.e(TAG, "SERVICE CONNECTED");

            playing = playbackService.isPlaying();
            updatePlayButton();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playbackService = null;
            serviceBound = false;
            playing = false;
            Log.e(TAG, "SERVICE DISCONNECTED");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        song = getIntent().getStringExtra("song");
        String artist = getIntent().getStringExtra("artist");
        previewUrl = getIntent().getStringExtra("previewUrl");

        isOnline = previewUrl != null && !previewUrl.isEmpty();

        TextView detailSong = findViewById(R.id.detailSong);
        TextView detailArtist = findViewById(R.id.detailArtist);

        playButton = findViewById(R.id.playButton);
        Button backButton = findViewById(R.id.backButton);

        detailSong.setText(song);
        detailArtist.setText(artist);

        if (!isOnline) {
            if ("Midnight Drive".equals(song)) {
                audioResource = R.raw.song1;
            } else if ("Golden Hour".equals(song)) {
                audioResource = R.raw.song2;
            } else {
                audioResource = R.raw.song3;
            }
        }

        Intent serviceIntent =
                new Intent(this, MusicPlaybackService.class);

        startService(serviceIntent);
        bindService(serviceIntent, connection, BIND_AUTO_CREATE);

        playButton.setEnabled(true);
        playButton.setText("▶ Play");

        playButton.setOnClickListener(v -> {
            Log.e(TAG, "PLAY BUTTON CLICKED");

            if (!serviceBound || playbackService == null) {
                Toast.makeText(
                        this,
                        "Player is still connecting",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (playing) {
                playbackService.pause();
                playing = false;
                updatePlayButton();
            } else {
                if (isOnline) {
                    playbackService.play(previewUrl);
                } else {
                    playbackService.playLocal(audioResource);
                }

                playing = true;
                updatePlayButton();
            }
        });

        backButton.setOnClickListener(v -> finish());
    }

    private void updatePlayButton() {
        if (playButton != null) {
            playButton.setText(playing ? "❚❚ Pause" : "▶ Play");
        }
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "NowPlayingActivity destroyed");

        if (serviceBound) {
            unbindService(connection);
            serviceBound = false;
        }

        super.onDestroy();
    }
}