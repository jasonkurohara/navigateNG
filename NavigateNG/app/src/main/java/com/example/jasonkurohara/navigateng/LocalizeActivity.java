package com.example.jasonkurohara.navigateng;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class LocalizeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localize);

        Button btnONOFF = (Button) findViewById(R.id.btnONOFF);
        Button btnScanDevices = (Button) findViewById(R.id.btnScanDevices);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        btnONOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: enabling/disabling bluetooth.");
                enableDisableBT();
            }
        });




        btnScanDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }

                //Adding filter called builder
                ScanFilter.Builder builder = new ScanFilter.Builder();
                builder.setDeviceAddress("9C:1D:58:94:3D:3F"); //Specific to my BLE device

                //Adding builders to filter
                List<ScanFilter> filter = new ArrayList<ScanFilter>();
                filter.add(builder.build());

                //Improving scan speed
                ScanSettings.Builder settings = new ScanSettings.Builder();
                settings.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);

                //Start discovery
                scanner.startScan(filter, settings.build(), callback );
            }
        });
    }
    private static final String TAG = "LocalizationActivity";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 456;
    BluetoothAdapter mBluetoothAdapter; //Allows discovery, instantiate a BluetoothDevice

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device, multiple states of BT on device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);
                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

//    @Override
//    protected void onDestroy() {
//        Log.d(TAG, "onDestroy: called.");
//      //  super.onDestroy();
//        unregisterReceiver(mBroadcastReceiver1); //
//    }

    public void enableDisableBT(){
        if(mBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }
        if(!mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if(mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: disabling BT.");
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }

    }

    private ScanCallback callback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            //   System.out.println("BLE// onScanResult");
            //    Log.i("callbackType", String.valueOf(callbackType));
            String readout = "\nDevice Address: " + result.getDevice().getAddress();
            readout += "\nDevice RSSI: " + Integer.toString(result.getRssi());
            Log.i("result", readout);
            TextView viewRssi = (TextView) findViewById(R.id.viewRssi);
            viewRssi.setText(Integer.toString(result.getRssi()));

            TextView viewDevice = (TextView) findViewById(R.id.viewDevice);
            viewDevice.setText(result.getDevice().getAddress());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            System.out.println("BLE// onBatchScanResults");
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            System.out.println("BLE// onScanFailed");
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };
}