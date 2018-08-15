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
import android.renderscript.ScriptC;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class LocalizeActivity extends Activity {

    /************************************CONSTANTS*************************************************/
    public static final String MODULE_1 = "9C:1D:58:94:3D:3F"; //MODULE NAMES
    public static final String MODULE_2 = "ENTER NAME 2";
    public static final String MODULE_3 = "ENTER NAME 3";
    public static final String MODULE_4 = "ENTER NAME 4";
    public static final String MODULE_5 = "ENTER NAME 5";
    public static final String MODULE_6 = "ENTER NAME 6";
    public static final String MODULE_7 = "ENTER NAME 7";
    public static final String MODULE_8 = "ENTER NAME 8";
    public static final String MODULE_9 = "ENTER NAME 9";
    private static final String TAG = "LocalizationActivity"; //TAG FOR CONSOLE LOGGING
    public static final int SCAN_COUNT = 5; //SCAN UPDATE (AVERAGE)
    public static final int NUM_DEVICES = 9; //NUMBER OF TOTAL BT DEVICES
    public static final int PERMISSION_REQUEST_COARSE_LOCATION = 456; //PERMISSION CODE FOR BT
    public static final int ERROR_THRESHOLD = 5; //ERROR THRESHOLD IN -dBM

    /**************************************OBJECTS*************************************************/
    Map<Vertex, ArrayList<Integer>> trueMap = new HashMap<Vertex, ArrayList<Integer>>();
    Vector<String> BTDeviceAddresses = new Vector<String>();

    /**************************************METHODS*************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localize);

        //Load trueMap from text
        trueMap = loadBTMapping();

        //DECOMPOSE TO ONE FUNCTION???
        BluetoothAdapter mBluetoothAdapter; //Allows discovery, instantiate a BluetoothDevice
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        }

        //Adding all filters to build
        ScanFilter.Builder builder1 = new ScanFilter.Builder();
        builder1.setDeviceAddress(MODULE_1); //Specific to my BLE device
        ScanFilter.Builder builder2 = new ScanFilter.Builder();
        builder2.setDeviceAddress(MODULE_2);
        ScanFilter.Builder builder3 = new ScanFilter.Builder();
        builder3.setDeviceAddress(MODULE_3);
        ScanFilter.Builder builder4 = new ScanFilter.Builder();
        builder4.setDeviceAddress(MODULE_4);
        ScanFilter.Builder builder5 = new ScanFilter.Builder();
        builder5.setDeviceAddress(MODULE_5);
        ScanFilter.Builder builder6 = new ScanFilter.Builder();
        builder6.setDeviceAddress(MODULE_6);
        ScanFilter.Builder builder7 = new ScanFilter.Builder();
        builder7.setDeviceAddress(MODULE_7);
        ScanFilter.Builder builder8 = new ScanFilter.Builder();
        builder8.setDeviceAddress(MODULE_8);

        //Adding builders to filter
        List<ScanFilter> filter = new ArrayList<ScanFilter>();
        filter.add(builder1.build());
        filter.add(builder2.build());
        filter.add(builder3.build());
        filter.add(builder4.build());
        filter.add(builder5.build());
        filter.add(builder6.build());
        filter.add(builder7.build());
        filter.add(builder8.build());

        //Improving scan speed
        ScanSettings.Builder settings = new ScanSettings.Builder();
        settings.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);

        //Start discovery
        scanner.startScan(filter, settings.build(), callback);
    }

/************************************CALLBACK FUNCTIONS********************************************/
    public ScanCallback callback = new ScanCallback() {
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
            ArrayList<List<ScanResult>> queue = new ArrayList<>(); //Stores circular queue of max 5 readings from 9 devices

            ArrayList<ScanResult>  orderedDevices = new ArrayList<>(); //Sorts devices in module name order
            orderedDevices = sortEntry(results);

            ArrayList<Double> average = new ArrayList<Double>();
            average = moving_average(orderedDevices, queue);
            for(int i = 0; i < average.size(); i++){
                Log.d(TAG,Double.toString(average.get(i)));
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            System.out.println("BLE// onScanFailed");
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

//    //Loads in bluetooth device names
//    public Vector<String> loadBTDevices() {
//        String data = "";
//        StringBuffer sbuffer = new StringBuffer();
//        InputStream is = this.getResources().openRawResource(R.raw.sample2); //Change to device names
//        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//        Vector<String> devices = new Vector<String>();
//
//        if (is != null) {
//            try {
//                while ((data = reader.readLine()) != null) {
//                    devices.add(data);
//                }
//                is.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return devices;
//    }

    //Loads in truth data to truMap
    public Map<Vertex, ArrayList<Integer>> loadBTMapping() {
        Map<Vertex, ArrayList<Integer>> map = new HashMap<Vertex, ArrayList<Integer>>();

        String data = "";
        StringBuffer sbuffer = new StringBuffer();
        InputStream is = this.getResources().openRawResource(R.raw.truemap); //Change to device names
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        if (is != null) {
            try {
                while ((data = reader.readLine()) != null) {
                    ArrayList<Integer> signals = new ArrayList<Integer>();
                    int delimiter = data.indexOf(";");
                    Vertex gridBox = new Vertex();
                    gridBox.setName(data.substring(0, delimiter));
                    data = data.substring(delimiter + 1);
                    while ((delimiter = data.indexOf(",")) != -1) {
                        signals.add(Integer.parseInt(data.substring(0, delimiter)));
                    }
                    map.put(gridBox, signals);
                }
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    //Moving average function
    public ArrayList<Double> moving_average(List<ScanResult> entry, ArrayList<List<ScanResult>> queue) {
        if( queue.size() < SCAN_COUNT){ //If not big enough sample, add to queue and calculate avg
            queue.add(entry);
        }

        else{
            queue.remove(0); //Too large, so remove and calc average
            queue.add(entry);
        }
        return calcAverage(queue); //Improvement: store average variable to reduce # of calculations
    }

    //Finds closest box according to trueMap and within threshold
    public Vertex findClosestGrid(ArrayList<Double> userScan, int threshold) {
        boolean flag = false;
        for (Vertex curr : trueMap.keySet()) {
            ArrayList<Integer> current = trueMap.get(curr);
            for (int i = 0; i < userScan.size(); i++) {
                if (Math.abs(userScan.get(i) - current.get(i)) > threshold) {
                    flag = true; //Flag triggered if NOT a valid square
                }
            }
            if ( !flag ){ //If within threshold for ALL modules, this is the closest square
                return curr;
            }
        }
        return null; //Returns null if user position not found
    }

    //Sorts BT devices in order module 1 to 8
    public ArrayList<ScanResult> sortEntry(List<ScanResult>results){
        ArrayList<ScanResult> sorted = new ArrayList<ScanResult>();
        for(ScanResult curr: results){
            String currentDeviceName = curr.getDevice().getAddress();
            switch(currentDeviceName){

                case MODULE_1: sorted.add(0,curr); break;
                case MODULE_2: sorted.add(1, curr); break;
                case MODULE_3: sorted.add(2, curr); break;
                case MODULE_4: sorted.add(3, curr); break;
                case MODULE_5: sorted.add(4, curr); break;
                case MODULE_6: sorted.add(5, curr); break;
                case MODULE_7: sorted.add(6, curr); break;
                case MODULE_8: sorted.add(7, curr); break;
                default: System.out.println("ERROR");
            }
        }
        return sorted;
    }

    public ArrayList<Double> calcAverage(ArrayList<List<ScanResult>> queue){
        ArrayList<Double> average = new ArrayList<>();
        double avg = 0;
        int sum = 0;
        for(int i = 0; i < NUM_DEVICES; i++){ //Loop each successive scan
            for( int j = 0; j < SCAN_COUNT; j++){ //Loop through each device ~(1-9)
                sum += queue.get(j).get(i).getRssi();
            }
            avg = (double)sum/queue.size();
            average.add(avg);
        }
        return average;
    }
}