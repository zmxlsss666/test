package com.example.saltplayerremote;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {

    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(String message);
    }

    // 严格按照提供的API端点
    public static void getNowPlaying(String ip, ApiCallback callback) {
        sendRequest("http://" + ip + "/api/now-playing", callback);
    }

    public static void togglePlayPause(String ip, ApiCallback callback) {
        sendRequest("http://" + ip + "/api/play-pause", callback);
    }

    public static void playNextTrack(String ip, ApiCallback callback) {
        sendRequest("http://" + ip + "/api/next-track", callback);
    }

    public static void playPreviousTrack(String ip, ApiCallback callback) {
        sendRequest("http://" + ip + "/api/previous-track", callback);
    }

    public static void volumeUp(String ip, ApiCallback callback) {
        sendRequest("http://" + ip + "/api/volume/up", callback);
    }

    public static void volumeDown(String ip, ApiCallback callback) {
        sendRequest("http://" + ip + "/api/volume/down", callback);
    }

    public static void toggleMute(String ip, ApiCallback callback) {
        sendRequest("http://" + ip + "/api/mute", callback);
    }

    private static void sendRequest(String urlString, ApiCallback callback) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(3000);
                connection.setReadTimeout(3000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.getString("status").equals("success")) {
                        callback.onSuccess(jsonResponse);
                    } else {
                        callback.onError(jsonResponse.optString("message", "API返回错误状态"));
                    }
                } else {
                    callback.onError("HTTP错误: " + responseCode);
                }
            } catch (IOException | JSONException e) {
                callback.onError(e.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }
}
