package com.example.saltplayerremote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements DeviceAdapter.OnDeviceClickListener {

    private Button btnScan;
    private TextView tvScanStatus;
    private RecyclerView deviceList;
    private LinearLayout playerSection, lyricsSection;
    private ImageView albumArt;
    private TextView tvLyrics, tvSongTitle, tvArtist, tvCurrentTime, tvTotalTime, tvFullLyrics;
    private SeekBar seekBar, volumeBar;
    private ImageButton btnPrev, btnPlayPause, btnNext, btnMute, btnVolumeUp, fabRefresh;

    private List<NetworkDevice> devices = new ArrayList<>();
    private DeviceAdapter deviceAdapter;
    private String currentDeviceIP = "";
    private Timer playerStateTimer;
    private Handler handler = new Handler();
    private boolean isMuted = false;
    private boolean isPlaying = false;
    private int currentPosition = 0;
    private int totalDuration = 0;
    private LyricsParser lyricsParser;
    private Map<Integer, String> lyricsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        deviceList.setLayoutManager(new LinearLayoutManager(this));
        deviceAdapter = new DeviceAdapter(devices, this);
        deviceList.setAdapter(deviceAdapter);

        btnScan.setOnClickListener(v -> scanNetworkDevices());
        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        btnPrev.setOnClickListener(v -> playPreviousTrack());
        btnNext.setOnClickListener(v -> playNextTrack());
        btnMute.setOnClickListener(v -> toggleMute());
        btnVolumeUp.setOnClickListener(v -> volumeUp());
        fabRefresh.setOnClickListener(v -> updatePlayerState());

        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setVolume(progress / 100.0f);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        updatePlayerStatePeriodically();
    }

    private void initViews() {
        btnScan = findViewById(R.id.btnScan);
        tvScanStatus = findViewById(R.id.tvScanStatus);
        deviceList = findViewById(R.id.deviceList);
        playerSection = findViewById(R.id.playerSection);
        lyricsSection = findViewById(R.id.lyricsSection);
        albumArt = findViewById(R.id.albumArt);
        tvLyrics = findViewById(R.id.tvLyrics);
        tvSongTitle = findViewById(R.id.tvSongTitle);
        tvArtist = findViewById(R.id.tvArtist);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        tvFullLyrics = findViewById(R.id.tvFullLyrics);
        seekBar = findViewById(R.id.seekBar);
        volumeBar = findViewById(R.id.volumeBar);
        btnPrev = findViewById(R.id.btnPrev);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnNext = findViewById(R.id.btnNext);
        btnMute = findViewById(R.id.btnMute);
        btnVolumeUp = findViewById(R.id.btnVolumeUp);
        fabRefresh = findViewById(R.id.fabRefresh);
    }

    private void scanNetworkDevices() {
        devices.clear();
        deviceAdapter.notifyDataSetChanged();
        tvScanStatus.setText("扫描中...");

        ExecutorService executor = Executors.newFixedThreadPool(20);
        String subnet = "192.168.1.";

        for (int i = 1; i < 255; i++) {
            String host = subnet + i;
            executor.execute(() -> {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(host, 35373), 500);
                    socket.close();
                    runOnUiThread(() -> {
                        devices.add(new NetworkDevice(host, "SaltPlayer"));
                        deviceAdapter.notifyDataSetChanged();
                        tvScanStatus.setText("找到 " + devices.size() + " 个设备");
                    });
                } catch (Exception e) {
                    // 设备未响应
                }
            });
        }
        executor.shutdown();
    }

    @Override
    public void onDeviceClick(NetworkDevice device) {
        currentDeviceIP = device.getIp();
        playerSection.setVisibility(View.VISIBLE);
        updatePlayerState();
    }

    private void togglePlayPause() {
        ApiClient.togglePlayPause(currentDeviceIP, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    isPlaying = response.getBoolean("isPlaying");
                    updatePlayPauseButton();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MainActivity.this, "操作失败: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void playPreviousTrack() {
        ApiClient.playPreviousTrack(currentDeviceIP, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                updatePlayerState();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MainActivity.this, "上一曲失败: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void playNextTrack() {
        ApiClient.playNextTrack(currentDeviceIP, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                updatePlayerState();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MainActivity.this, "下一曲失败: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleMute() {
        ApiClient.toggleMute(currentDeviceIP, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    isMuted = response.getBoolean("isMuted");
                    updateMuteButton();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MainActivity.this, "静音操作失败: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void volumeUp() {
        ApiClient.volumeUp(currentDeviceIP, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    float volume = (float) response.getDouble("currentVolume");
                    volumeBar.setProgress((int) (volume * 100));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MainActivity.this, "音量增加失败: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setVolume(float volume) {
        // 音量调整逻辑
    }

    private void updatePlayerState() {
        ApiClient.getNowPlaying(currentDeviceIP, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String title = response.getString("title");
                    String artist = response.getString("artist");
                    isPlaying = response.getBoolean("isPlaying");
                    currentPosition = response.getInt("position");
                    totalDuration = 300000;
                    float volume = (float) response.getDouble("volume");

                    runOnUiThread(() -> {
                        tvSongTitle.setText(title);
                        tvArtist.setText(artist);
                        volumeBar.setProgress((int) (volume * 100));
                        updatePlayPauseButton();
                        updateMuteButton();
                        updateSeekBarState();
                    });

                    NeteaseApi.searchSong(title, artist, new NeteaseApi.NeteaseCallback() {
                        @Override
                        public void onSongInfoReceived(String songId, String albumArtUrl, String lyrics) {
                            Bitmap bitmap = NeteaseApi.downloadImage(albumArtUrl);
                            runOnUiThread(() -> {
                                if (bitmap != null) {
                                    albumArt.setImageBitmap(bitmap);
                                } else {
                                    albumArt.setImageResource(R.drawable.album_placeholder);
                                }
                            });

                            lyricsParser = new LyricsParser(lyrics);
                            lyricsMap = lyricsParser.getLyricsMap();

                            runOnUiThread(() -> {
                                tvFullLyrics.setText(lyricsParser.getRawLyrics());
                                lyricsSection.setVisibility(View.VISIBLE);
                            });
                        }

                        @Override
                        public void onError(String message) {
                            Log.e("NeteaseApi", "Error: " + message);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MainActivity.this, "获取播放状态失败: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSeekBarState() {
        seekBar.setMax(totalDuration);
        seekBar.setProgress(currentPosition);
        tvCurrentTime.setText(formatTime(currentPosition));
        tvTotalTime.setText(formatTime(totalDuration));
    }

    private String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    private void updatePlayPauseButton() {
        btnPlayPause.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
    }

    private void updateMuteButton() {
        btnMute.setImageResource(isMuted ? R.drawable.ic_volume_off : R.drawable.ic_volume_up);
    }

    private void updatePlayerStatePeriodically() {
        playerStateTimer = new Timer();
        playerStateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!currentDeviceIP.isEmpty()) {
                    updatePlayerState();
                }
            }
        }, 0, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playerStateTimer != null) {
            playerStateTimer.cancel();
        }
    }
}