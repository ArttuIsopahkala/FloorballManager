package com.ardeapps.floorballcoach.fragments;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.adapters.DeviceListAdapter;
import com.ardeapps.floorballcoach.services.FragmentListeners;
import com.ardeapps.floorballcoach.utils.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static com.ardeapps.floorballcoach.services.FragmentListeners.MY_PERMISSION_ACCESS_BLUETOOTH;
import static com.ardeapps.floorballcoach.services.FragmentListeners.MY_PERMISSION_ACCESS_COARSE_LOCATION;

public class BluetoothFragment extends Fragment {

    Button discoverButton;
    Button btnFindUnpairedDevices;
    Button btnDiscoverable_on_off;
    ListView lvNewDevices;
    Button btnEnableDisable_Discoverable;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;

    BluetoothAdapter bluetooth;
    long millisToCompleteDiscovery = 5000;
    private Handler discoverTimerHandler = new Handler();
    Timer completeDiscoverTimer = new Timer();


    BluetoothSocket bsock;

    private class CompleteDiscoverTask extends TimerTask {
        @Override
        public void run() {
            discoverTimerHandler.post(new TimerTask() {
                @Override
                public void run() {
                    onDiscoverCompleted();
                }
            });
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentListeners.getInstance().setPermissionHandledListener(new FragmentListeners.PermissionHandledListener() {
            @Override
            public void onPermissionGranted(int MY_PERMISSION) {
                switch (MY_PERMISSION) {
                    case MY_PERMISSION_ACCESS_BLUETOOTH:
                        checkBluetoothAvailable();
                        break;
                }
            }

            @Override
            public void onPermissionDenied(int MY_PERMISSION) {}
        });
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        discoverButton = v.findViewById(R.id.discoverButton);
        btnDiscoverable_on_off = v.findViewById(R.id.btnDiscoverable_on_off);
        btnFindUnpairedDevices = v.findViewById(R.id.btnFindUnpairedDevices);
        btnEnableDisable_Discoverable = v.findViewById(R.id.btnDiscoverable_on_off);
        lvNewDevices = v.findViewById(R.id.lvNewDevices);
        mBTDevices = new ArrayList<>();

        bluetooth = BluetoothAdapter.getDefaultAdapter();

        discoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBluetoothAvailable();
            }
        });

        btnDiscoverable_on_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEnableDisable_Discoverable();
            }
        });

        btnFindUnpairedDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAvailableDevices();
            }
        });

        return v;
    }


    private void checkBluetoothAvailable() {
        bluetooth = BluetoothAdapter.getDefaultAdapter();
        // No bluetooth available
        if(bluetooth == null) {
            Logger.log("BLUETOOTH EI KÄYTETTÄVISSÄ");
            Logger.toast("BLUETOOTH EI KÄYTETTÄVISSÄ");
            return;
        }

        // No permission to use bluetooth
        int permissionCheck = ContextCompat.checkSelfPermission(AppRes.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionCheck += ContextCompat.checkSelfPermission(AppRes.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck != 0 && Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_ACCESS_COARSE_LOCATION);
            return;
        }

        // Bluetooth is not enabled
        if (!bluetooth.isEnabled()) {
            Intent askEnableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(askEnableBluetooth, MY_PERMISSION_ACCESS_BLUETOOTH);
            //startActivity(enableBTIntent);
            return;
        }

        getAvailableDevices();
    }

    private void getPairedDevices() {
        Set<BluetoothDevice> pairedDevices = bluetooth.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
    }

    private void getAvailableDevices() {
        Logger.log("getAvailableDevices: Looking for unpaired devices.");

        // If already discovering, start it again
        if(bluetooth.isDiscovering()){
            bluetooth.cancelDiscovery();
        }

        bluetooth.startDiscovery();
        IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        AppRes.getActivity().registerReceiver(discoverReceiver, discoverDevicesIntent);
    }

    /**
     * Broadcast Receiver for listing devices that are not yet paired.
     */
    private BroadcastReceiver discoverReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Logger.log("onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);

                mBTDevices.add(device);
                String name = device.getName() + "\n" + device.getAddress();
                Logger.log("BLUETOOTH laite: " + name);
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);

                reScheduleCompleteDiscoverTimer();
            }
        }
    };

    private void reScheduleCompleteDiscoverTimer() {
        completeDiscoverTimer.cancel();
        completeDiscoverTimer = new Timer();
        completeDiscoverTimer.schedule(new CompleteDiscoverTask(), millisToCompleteDiscovery);
    }

    private void onDiscoverCompleted() {
        // Cancel discover
        Logger.log("LISÄÄ LAITTEITA EI LÖYDY");
        Logger.toast("LISÄÄ LAITTEITA EI LÖYDY");
        bluetooth.cancelDiscovery();
        AppRes.getActivity().unregisterReceiver(discoverReceiver);

        try {
            bsock = mBTDevices.get(0).createRfcommSocketToServiceRecord(UUID.fromString("00002415-0000-1000-8000-00805F9B34FB"));
            bsock.connect();
            //Send and receive data logic follows

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnEnableDisable_Discoverable() {
        Logger.log("btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(bluetooth.ACTION_SCAN_MODE_CHANGED);
        AppRes.getActivity().registerReceiver(mBroadcastReceiver2,intentFilter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //AppRes.getActivity().unregisterReceiver(mBroadcastReceiver2);
        //bluetooth.cancelDiscovery();
    }

    /**
     * Broadcast Receiver for changes made to bluetooth states such as:
     * 1) Discoverability mode on/off or expire.
     */
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Logger.log("mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Logger.log("mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Logger.log("mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Logger.log("mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Logger.log("mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };
}
