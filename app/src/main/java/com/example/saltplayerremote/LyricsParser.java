package com.example.saltplayerremote;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricsParser {
    private String rawLyrics;
    private Map<Integer, String> lyricsMap = new HashMap<>();

    public LyricsParser(String rawLyrics) {
        this.rawLyrics = rawLyrics;
        parseLyrics();
    }

    private void parseLyrics() {
        String[] lines = rawLyrics.split("\n");
        Pattern pattern = Pattern.compile("\\[(\\d+):(\\d+).(\\d+)\\](.*)");

        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                int minutes = Integer.parseInt(matcher.group(1));
                int seconds = Integer.parseInt(matcher.group(2));
                int milliseconds = Integer.parseInt(matcher.group(3).substring(0, 2)) * 10;
                int totalMs = (minutes * 60 + seconds) * 1000 + milliseconds;
                String lyric = matcher.group(4).trim();
                lyricsMap.put(totalMs, lyric);
            }
        }
    }

    public String getLyricAtPosition(int positionMs) {
        int closestTime = 0;
        String currentLyric = "";
        for (Map.Entry<Integer, String> entry : lyricsMap.entrySet()) {
            if (entry.getKey() <= positionMs && entry.getKey() > closestTime) {
                closestTime = entry.getKey();
                currentLyric = entry.getValue();
            }
        }
        return currentLyric;
    }

    public Map<Integer, String> getLyricsMap() {
        return lyricsMap;
    }

    public String getRawLyrics() {
        return rawLyrics;
    }
}