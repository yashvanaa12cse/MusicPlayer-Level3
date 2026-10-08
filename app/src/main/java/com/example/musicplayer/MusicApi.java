package com.example.musicplayer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MusicApi {

    public static ArrayList<OnlineSong> searchSongs(String query) throws Exception {
        ArrayList<OnlineSong> songs = new ArrayList<>();

        String encodedQuery = URLEncoder.encode(query, "UTF-8");
        String apiUrl = "https://itunes.apple.com/search?term="
                + encodedQuery + "&entity=song&limit=20";

        HttpURLConnection connection =
                (HttpURLConnection) new URL(apiUrl).openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            JSONObject json = new JSONObject(response.toString());
            JSONArray results = json.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject item = results.getJSONObject(i);

                String title = item.optString("trackName", "Unknown Song");
                String artist = item.optString("artistName", "Unknown Artist");
                String artwork = item.optString("artworkUrl100", "");
                String preview = item.optString("previewUrl", "");

                if (!preview.isEmpty()) {
                    songs.add(new OnlineSong(title, artist, artwork, preview));
                }
            }
        } finally {
            connection.disconnect();
        }

        return songs;
    }
}