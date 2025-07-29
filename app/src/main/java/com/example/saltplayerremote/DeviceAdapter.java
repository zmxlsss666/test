package com.example.saltplayerremote;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.ArrayList; // FIXED: Added import

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private List<NetworkDevice> deviceList;
    private OnDeviceClickListener listener;

    public interface OnDeviceClickListener {
        void onDeviceClick(NetworkDevice device);
    }

    public DeviceAdapter(List<NetworkDevice> deviceList, OnDeviceClickListener listener) {
        this.deviceList = deviceList != null ? deviceList : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        if (deviceList != null && position < deviceList.size()) {
            NetworkDevice device = deviceList.get(position);
            holder.deviceName.setText(device.getIp());
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeviceClick(device);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return deviceList != null ? deviceList.size() : 0;
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView deviceName;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(android.R.id.text1);
            if (deviceName != null) {
                deviceName.setTextColor(itemView.getResources().getColor(android.R.color.white));
            }
        }
    }
}
