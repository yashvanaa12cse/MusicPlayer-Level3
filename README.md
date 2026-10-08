# Music Player — Level 3

An Android music player application built using **Java and XML** in Android Studio.

## Features

* **Local Music Playback:** Play built-in audio files stored in the application.
* **Online Music Search:** Search for songs using the Apple iTunes Search API.
* **Music Preview:** Stream available online song previews.
* **Background Playback:** Continue listening to music after leaving the Now Playing screen using an Android foreground service.
* **Playback Controls:** Play and pause music.
* **Custom UI:** XML-based layouts for browsing songs and viewing song details.

## Tech Stack

* **Language:** Java
* **UI:** XML
* **IDE:** Android Studio
* **Audio Playback:** Android MediaPlayer
* **API:** Apple iTunes Search API
* **Background Audio:** Android Foreground Service
* **Build System:** Gradle

## Project Structure

```text
MusicPlayer_Level3/
├── app/
│   └── src/main/
│       ├── java/com/example/musicplayer/
│       │   ├── MainActivity.java
│       │   ├── MusicApi.java
│       │   ├── MusicPlaybackService.java
│       │   ├── NowPlayingActivity.java
│       │   └── OnlineSong.java
│       ├── res/
│       │   ├── drawable/
│       │   ├── layout/
│       │   ├── raw/
│       │   └── values/
│       └── AndroidManifest.xml
├── build.gradle
├── settings.gradle
└── README.md
```

## Running the Application

1. Clone or download this repository.
2. Open the project in Android Studio.
3. Allow Gradle to sync.
4. Run the application on an Android emulator or compatible device.

An active internet connection is required for online music search and streaming. Online previews depend on availability through the API.

## Local Audio

The application includes sample audio files in `app/src/main/res/raw/` for local playback.
