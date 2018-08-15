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
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class LocalizeActivity extends Activity {

    private static final String TAG = "LocalizationActivity";

    Map<Vertex, ArrayList<Integer>> trueMap = new HashMap<Vertex, ArrayList<Integer>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localize);

        //Load BT device names from text
        Vector<String> BTDeviceAddresses = new Vector<String>();
        BTDeviceAddresses = loadBTDevices();

        //Load trueMap from text
        trueMap = loadBTMapping();

        int PERMISSION_REQUEST_COARSE_LOCATION = 456;
        BluetoothAdapter mBluetoothAdapter; //Allows discovery, instantiate a BluetoothDevice
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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
        scanner.startScan(filter, settings.build(),  callback);
    }

    //Callback function executed when result detected
    public ScanCallback callback =  new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            //   System.out.println("BLE// onScanResult");
            //    Log.i("callbackType", String.valueOf(callbackType));
            String readout = "\nDevice Address: " + result.getDevice().getAddress();
            readout += "\nDevice RSSI: " + Integer.toString(result.getRssi());
            Log.i("result", readout);
            TextView viewRssi = (TextView) findViewById(R.id.viewRssi);
            viewRssi.setText(Integer.toString(result.getRssi()));

            TextView availableDevices = (TextView) findViewById(R.id.availableDevices);
            availableDevices.setText("1");

            TextView viewDevice = (TextView) findViewById(R.id.viewDevice);
            viewDevice.setText(result.getDevice().getAddress());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            // System.out.println("BLE// onBatchScanResults");
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

    //Loads in bluetooth device names
    public Vector<String> loadBTDevices(){
        String data = "";
        StringBuffer sbuffer = new StringBuffer();
        InputStream is = this.getResources().openRawResource(R.raw.sample2); //Change to device names
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        Vector<String> devices = new Vector<String>();

        if( is != null){
            try{
                while( (data=reader.readLine()) != null){
                    devices.add(data);
                }
                is.close();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        return devices;
    }

    //Loads in truth data
    public Map<Vertex, ArrayList<Integer>> loadBTMapping(){
        Map<Vertex,ArrayList<Integer>> map = new HashMap<Vertex,ArrayList<Integer>>();

        String data = "";
        StringBuffer sbuffer = new StringBuffer();
        InputStream is = this.getResources().openRawResource(R.raw.sample2); //Change to device names
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
   //     Vector<String> devices = new Vector<String>();

        if( is != null){
            try{
                while( (data=reader.readLine()) != null){
                    ArrayList<Integer> signals = new ArrayList<Integer>();
                    int delimiter = data.indexOf(";");
                    Vertex gridBox = new Vertex();
                    gridBox.setName(data.substring(0,delimiter));
                    data = data.substring(delimiter+1);
                    while( (delimiter = data.indexOf(",")) !=  -1) {
                        signals.add(Integer.parseInt(data.substring(0,delimiter)));
                    }
                    map.put(gridBox,signals);
                }
                is.close();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        return map;
    }
}