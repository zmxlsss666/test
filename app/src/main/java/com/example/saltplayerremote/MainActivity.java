package com.example.saltplayerremote;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements DeviceAdapter.OnDeviceClickListener {

    // 视图变量声明
    private Button btnScan, btnConnectCustom;
    private EditText etCustomIp;
    private TextView tvScanStatus;
    private RecyclerView deviceList;
    private LinearLayout playerSection, lyricsSection;
    private ImageView albumArt;
    private TextView tvLyrics, tvSongTitle, tvArtist, tvCurrentTime, tvTotalTime, tvFullLyrics;
    private SeekBar seekBar, volumeBar;
    private ImageButton btnPrev, btnPlayPause, btnNext, btnMute, btnVolumeUp, btnVolumeDown, fabRefresh;

    // 数据变量
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
        btnConnectCustom.setOnClickListener(v -> connectCustomIp());
        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        btnPrev.setOnClickListener(v -> playPreviousTrack());
        btnNext.setOnClickListener(v -> playNextTrack());
        btnMute.setOnClickListener(v -> toggleMute());
        btnVolumeUp.setOnClickListener(v -> volumeUp());
        btnVolumeDown.setOnClickListener(v -> volumeDown());
        fabRefresh.setOnClickListener(v -> {
            if (!currentDeviceIP.isEmpty()) {
                updatePlayerState();
            }
        });

        // IP输入验证
        etCustomIp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String ip = s.toString();
                btnConnectCustom.setEnabled(isValidIp(ip));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && !currentDeviceIP.isEmpty()) {
                    setVolume(progress / 100.0f);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void initViews() {
        btnScan = findViewById(R.id.btnScan);
        etCustomIp = findViewById(R.id.etCustomIp);
        btnConnectCustom = findViewById(R.id.btnConnectCustom);
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
        btnVolumeDown = findViewById(R.id.btnVolumeDown);
        fabRefresh = findViewById(R.id.fabRefresh);
        
        // 初始禁用自定义连接按钮
        btnConnectCustom.setEnabled(false);
    }

    // 连接自定义IP
    private void connectCustomIp() {
        String ip = etCustomIp.getText().toString().trim();
        if (!isValidIp(ip)) {
            Toast.makeText(this, "请输入有效的IP地址", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 验证IP是否有效
        new Thread(() -> {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(ip, 35373), 1000);
                socket.close();
                
                runOnUiThread(() -> {
                    devices.add(new NetworkDevice(ip, "自定义设备"));
                    deviceAdapter.notifyDataSetChanged();
                    currentDeviceIP = ip;
                    playerSection.setVisibility(View.VISIBLE);
                    startPlayerStateTimer();
                    updatePlayerState();
                    tvScanStatus.setText("已连接到自定义设备: " + ip);
                });
            } catch (Exception e) {
                runOnUiThread(() -> 
                    Toast.makeText(MainActivity.this, "无法连接到设备: " + ip, Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
    
    // 验证IP格式
    private boolean isValidIp(String ip) {
        String ipPattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        return Pattern.matches(ipPattern, ip);
    }

    private void scanNetworkDevices() {
        devices.clear();
        deviceAdapter.notifyDataSetChanged();
        tvScanStatus.setText("正在扫描局域网...");

        // 获取本机IP和子网信息
        NetworkInfo networkInfo = getNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            tvScanStatus.setText("网络未连接");
            return;
        }

        // 计算局域网IP范围
        String[] ipRange = calculateLocalIpRange();
        if (ipRange == null) {
            tvScanStatus.setText("无法确定IP范围");
            return;
        }

        String startIp = ipRange[0];
        String endIp = ipRange[1];
        tvScanStatus.setText("扫描范围: " + startIp + " - " + endIp);

        // 解析IP地址
        long start = ipToLong(startIp);
        long end = ipToLong(endIp);

        ExecutorService executor = Executors.newFixedThreadPool(20);
        for (long i = start; i <= end; i++) {
            String host = longToIp(i);
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

    // 获取网络信息
    private NetworkInfo getNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    // 计算局域网IP范围
    private String[] calculateLocalIpRange() {
        try {
            String ipAddress = "";
            String subnetMask = "";

            // 尝试通过WifiManager获取信息
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                DhcpInfo dhcp = wifiManager.getDhcpInfo();
                if (dhcp != null) {
                    ipAddress = formatIpAddress(dhcp.ipAddress);
                    subnetMask = formatIpAddress(dhcp.netmask);
                }
            }

            // 如果通过WifiManager获取失败，尝试通过NetworkInterface获取
            if (ipAddress.isEmpty() || subnetMask.isEmpty()) {
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                for (NetworkInterface iface : Collections.list(interfaces)) {
                    if (iface.isLoopback() || !iface.isUp()) continue;
                    
                    for (InterfaceAddress address : iface.getInterfaceAddresses()) {
                        InetAddress inetAddr = address.getAddress();
                        if (!inetAddr.isLoopbackAddress() && inetAddr instanceof java.net.Inet4Address) {
                            ipAddress = inetAddr.getHostAddress();
                            subnetMask = formatIpAddress(address.getNetworkPrefixLength());
                            break;
                        }
                    }
                }
            }

            if (ipAddress.isEmpty() || subnetMask.isEmpty()) {
                return null;
            }

            // 计算网络地址和广播地址
            long ip = ipToLong(ipAddress);
            long mask = ipToLong(subnetMask);
            long network = ip & mask;
            long broadcast = network | ~mask;

            // 排除网络地址和广播地址
            String startIp = longToIp(network + 1);
            String endIp = longToIp(broadcast - 1);

            return new String[]{startIp, endIp};
        } catch (SocketException e) {
            Log.e("Network", "获取网络接口失败", e);
            return null;
        }
    }

    // 将IP地址转换为长整型
    private long ipToLong(String ipAddress) {
        String[] ipParts = ipAddress.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result |= (Long.parseLong(ipParts[i]) & 0xFF) << (24 - (8 * i));
        }
        return result;
    }

    // 将长整型转换为IP地址
    private String longToIp(long ip) {
        return ((ip >> 24) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                (ip & 0xFF);
    }

    // 格式化IP地址（用于WifiManager返回的int类型IP）
    private String formatIpAddress(int ip) {
        return String.format("%d.%d.%d.%d",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));
    }

    // 根据前缀长度计算子网掩码
    private String formatIpAddress(int prefixLength) {
        int value = 0xffffffff << (32 - prefixLength);
        return String.format("%d.%d.%d.%d",
                (value >> 24 & 0xff),
                (value >> 16 & 0xff),
                (value >> 8 & 0xff),
                (value & 0xff));
    }

    @Override
    public void onDeviceClick(NetworkDevice device) {
        currentDeviceIP = device.getIp();
        playerSection.setVisibility(View.VISIBLE);
        
        // 停止之前的定时器
        stopPlayerStateTimer();
        
        // 启动新的定时器
        startPlayerStateTimer();
        updatePlayerState();
        tvScanStatus.setText("已连接到: " + currentDeviceIP);
    }

    // 播放/暂停
    private void togglePlayPause() {
        if (currentDeviceIP.isEmpty()) {
            Toast.makeText(this, "未选择设备", Toast.LENGTH_SHORT).show();
            return;
        }
        
        ApiClient.togglePlayPause(currentDeviceIP, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    isPlaying = response.getBoolean("isPlaying");
                    runOnUiThread(() -> updatePlayPauseButton());
                    
                    String message = isPlaying ? "已播放" : "已暂停";
                    runOnUiThread(() -> 
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show()
                    );
                } catch (JSONException e) {
                    Log.e("API", "解析播放状态失败", e);
                }
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> 
                    Toast.makeText(MainActivity.this, "操作失败: " + message, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    // 上一曲
    private void playPreviousTrack() {
        if (currentDeviceIP.isEmpty()) {
            Toast.makeText(this, "未选择设备", Toast.LENGTH_SHORT).show();
            return;
        }
        
        ApiClient.playPreviousTrack(currentDeviceIP, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                runOnUiThread(() -> {
                    try {
                        String newTrack = response.getString("newTrack");
                        Toast.makeText(MainActivity.this, "已切换到上一曲: " + newTrack, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, "已切换到上一曲", Toast.LENGTH_SHORT).show();
                    }
                });
                updatePlayerState();
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> 
                    Toast.makeText(MainActivity.this, "上一曲失败: " + message, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    // 下一曲
    private void playNextTrack() {
        if (currentDeviceIP.isEmpty()) {
            Toast.makeText(this, "未选择设备", Toast.LENGTH_SHORT).show();
            return;
        }
        
        ApiClient.playNextTrack(currentDeviceIP, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                runOnUiThread(() -> {
                    try {
                        String newTrack = response.getString("newTrack");
                        Toast.makeText(MainActivity.this, "已切换到下一曲: " + newTrack, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, "已切换到下一曲", Toast.LENGTH_SHORT).show();
                    }
                });
                updatePlayerState();
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> 
                    Toast.makeText(MainActivity.this, "下一曲失败: " + message, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    // 静音切换
    private void toggleMute() {
        if (currentDeviceIP.isEmpty()) {
            Toast.makeText(this, "未选择设备", Toast.LENGTH_SHORT).show();
            return;
        }
        
        ApiClient.toggleMute(currentDeviceIP, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    isMuted = response.getBoolean("isMuted");
                    runOnUiThread(() -> {
                        updateMuteButton();
                        String message = isMuted ? "已静音" : "已取消静音";
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    });
                } catch (JSONException e) {
                    Log.e("API", "解析静音状态失败", e);
                }
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> 
                    Toast.makeText(MainActivity.this, "静音操作失败: " + message, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    // 音量增加
    private void volumeUp() {
        if (currentDeviceIP.isEmpty()) {
            Toast.makeText(this, "未选择设备", Toast.LENGTH_SHORT).show();
            return;
        }
        
        ApiClient.volumeUp(currentDeviceIP, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    float volume = (float) response.getDouble("currentVolume");
                    runOnUiThread(() -> {
                        volumeBar.setProgress((int) (volume * 100));
                        Toast.makeText(MainActivity.this, "音量已增加", Toast.LENGTH_SHORT).show();
                    });
                } catch (JSONException e) {
                    Log.e("API", "解析音量失败", e);
                }
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> 
                    Toast.makeText(MainActivity.this, "音量增加失败: " + message, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    // 音量减少
    private void volumeDown() {
        if (currentDeviceIP.isEmpty()) {
            Toast.makeText(this, "未选择设备", Toast.LENGTH_SHORT).show();
            return;
        }
        
        ApiClient.volumeDown(currentDeviceIP, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    float volume = (float) response.getDouble("currentVolume");
                    runOnUiThread(() -> {
                        volumeBar.setProgress((int) (volume * 100));
                        Toast.makeText(MainActivity.this, "音量已减少", Toast.LENGTH_SHORT).show();
                    });
                } catch (JSONException e) {
                    Log.e("API", "解析音量失败", e);
                }
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> 
                    Toast.makeText(MainActivity.this, "音量减少失败: " + message, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    // 设置音量
    private void setVolume(float volume) {
        // 这里可以添加直接设置音量的API调用
        // 当前API只支持增加/减少，所以这里不实现
    }

    // 更新播放器状态
    private void updatePlayerState() {
        if (currentDeviceIP.isEmpty()) {
            return;
        }
        
        ApiClient.getNowPlaying(currentDeviceIP, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String title = response.getString("title");
                    String artist = response.getString("artist");
                    isPlaying = response.getBoolean("isPlaying");
                    currentPosition = response.getInt("position");
                    float volume = (float) response.getDouble("volume");
                    long timestamp = response.getLong("timestamp");

                    runOnUiThread(() -> {
                        try {
                            tvSongTitle.setText(title);
                            tvArtist.setText(artist);
                            volumeBar.setProgress((int) (volume * 100));
                            updatePlayPauseButton();
                            updateMuteButton();
                            updateSeekBarState();
                        } catch (Exception e) {
                            Log.e("UI", "更新UI失败", e);
                        }
                    });

                    // 获取网易云歌曲信息
                    NeteaseApi.searchSong(title, artist, new NeteaseApi.NeteaseCallback() {
                        @Override
                        public void onSongInfoReceived(String songId, String albumArtUrl, String lyrics) {
                            Bitmap bitmap = NeteaseApi.downloadImage(albumArtUrl);
                            runOnUiThread(() -> {
                                try {
                                    if (bitmap != null) {
                                        albumArt.setImageBitmap(bitmap);
                                    } else {
                                        albumArt.setImageResource(R.drawable.album_placeholder);
                                    }
                                } catch (Exception e) {
                                    Log.e("UI", "设置专辑封面失败", e);
                                }
                            });

                            lyricsParser = new LyricsParser(lyrics);
                            lyricsMap = lyricsParser.getLyricsMap();

                            runOnUiThread(() -> {
                                try {
                                    tvFullLyrics.setText(lyricsParser.getRawLyrics());
                                    lyricsSection.setVisibility(View.VISIBLE);
                                } catch (Exception e) {
                                    Log.e("UI", "显示歌词失败", e);
                                }
                            });
                        }

                        @Override
                        public void onError(String message) {
                            Log.e("NeteaseApi", "Error: " + message);
                        }
                    });

                } catch (JSONException e) {
                    Log.e("API", "解析播放信息失败", e);
                }
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> 
                    Toast.makeText(MainActivity.this, "获取播放状态失败: " + message, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void updateSeekBarState() {
        // 设置总时长（示例值，实际应从API获取）
        totalDuration = 300000;
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

    private void startPlayerStateTimer() {
        stopPlayerStateTimer();
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

    private void stopPlayerStateTimer() {
        if (playerStateTimer != null) {
            playerStateTimer.cancel();
            playerStateTimer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlayerStateTimer();
    }
}
