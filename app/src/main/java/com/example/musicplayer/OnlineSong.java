package com.example.musicplayer;

public class OnlineSong {
    public String title;
    public String artist;
    public String artworkUrl;
    public String previewUrl;

    public OnlineSong(String title, String artist, String artworkUrl, String previewUrl) {
        this.title = title;
        this.artist = artist;
        this.artworkUrl = artworkUrl;
        this.previewUrl = previewUrl;
    }
}