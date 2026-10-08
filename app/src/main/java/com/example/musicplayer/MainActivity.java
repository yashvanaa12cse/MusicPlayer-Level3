
package com.example.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MUSIC_API";

    private final String[][] songs = {
            {"Midnight Drive", "Luna Waves"},
            {"Golden Hour", "The Skylines"},
            {"Ocean Eyes", "Mira"},
            {"Afterglow", "Nova Lane"},
            {"City Lights", "Echo Avenue"}
    };

    private LinearLayout songList;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songList = findViewById(R.id.songList);

        displayLocalSongs();
        loadOnlineSongs();
    }

    private void displayLocalSongs() {
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < songs.length; i++) {
            View item = inflater.inflate(
                    R.layout.item_song, songList, false
            );

            TextView songName = item.findViewById(R.id.songName);
            TextView artistName = item.findViewById(R.id.artistName);

            songName.setText(songs[i][0]);
            artistName.setText(songs[i][1]);

            final int position = i;

            item.setOnClickListener(v -> {
                Intent intent = new Intent(
                        MainActivity.this,
                        NowPlayingActivity.class
                );

                intent.putExtra("song", songs[position][0]);
                intent.putExtra("artist", songs[position][1]);

                startActivity(intent);
            });

            songList.addView(item);
        }
    }

    private void loadOnlineSongs() {
        Log.e(TAG, "Starting online song request");

        executor.execute(() -> {
            try {
                ArrayList<OnlineSong> onlineSongs =
                        MusicApi.searchSongs("popular music");

                Log.e(TAG, "Songs returned by API: "
                        + onlineSongs.size());

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    LayoutInflater inflater =
                            LayoutInflater.from(MainActivity.this);

                    for (OnlineSong song : onlineSongs) {
                        View item = inflater.inflate(
                                R.layout.item_song,
                                songList,
                                false
                        );

                        TextView songName =
                                item.findViewById(R.id.songName);

                        TextView artistName =
                                item.findViewById(R.id.artistName);

                        songName.setText(song.title);
                        artistName.setText(song.artist);

                        item.setOnClickListener(v -> {
                            Intent intent = new Intent(
                                    MainActivity.this,
                                    NowPlayingActivity.class
                            );

                            intent.putExtra("song", song.title);
                            intent.putExtra("artist", song.artist);
                            intent.putExtra("previewUrl", song.previewUrl);

                            startActivity(intent);
                        });

                        songList.addView(item);
                    }

                    if (onlineSongs.isEmpty()) {
                        Toast.makeText(
                                MainActivity.this,
                                "No online preview songs found",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Failed to load online songs: " + e.getMessage());
                e.printStackTrace();

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    Toast.makeText(
                            MainActivity.this,
                            "Couldn't load online songs. Check Logcat.",
                            Toast.LENGTH_LONG
                    ).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (executor != null) {
            executor.shutdownNow();
        }

        super.onDestroy();
    }
}