package com.example.saltplayerremote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NeteaseApi {

    public interface NeteaseCallback {
        void onSongInfoReceived(String songId, String albumArtUrl, String lyrics);
        void onError(String message);
    }

    public static void searchSong(String title, String artist, NeteaseCallback callback) {
        new Thread(() -> {
            try {
                String searchUrl = "https://music.163.com/api/search/get?s=" + 
                                  title + " " + artist + "&type=1&limit=1";
                JSONObject searchResult = fetchJsonFromUrl(searchUrl);
                if (searchResult == null) {
                    callback.onError("搜索失败");
                    return;
                }

                JSONArray songs = searchResult.getJSONObject("result").getJSONArray("songs");
                if (songs.length() == 0) {
                    callback.onError("未找到歌曲");
                    return;
                }

                JSONObject song = songs.getJSONObject(0);
                String songId = song.getString("id");
                String albumArtUrl = song.getJSONObject("album").getString("picUrl");

                String lyricUrl = "https://music.163.com/api/song/lyric?id=" + songId + "&lv=-1";
                JSONObject lyricResult = fetchJsonFromUrl(lyricUrl);
                String lyrics = "";
                if (lyricResult.has("lrc")) {
                    lyrics = lyricResult.getJSONObject("lrc").getString("lyric");
                }

                callback.onSongInfoReceived(songId, albumArtUrl, lyrics);

            } catch (JSONException e) {
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public static Bitmap downloadImage(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            Log.e("NeteaseApi", "图片下载失败: " + e.getMessage());
            return null;
        }
    }

    private static JSONObject fetchJsonFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return new JSONObject(response.toString());
        } catch (IOException | JSONException e) {
            Log.e("NeteaseApi", "API请求失败: " + e.getMessage());
            return null;
        }
    }
}