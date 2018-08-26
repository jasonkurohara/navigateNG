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
import java.io.DataOutput;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class LocalizeActivity extends Activity {

    /****************************************CONSTANTS*********************************************/
   // public static final String MODULE_1 = "9C:1D:58:94:3D:3F"; //MODULE NAMES
  //  public static final String MODULE_2 = "C8:DF:84:2A:45:AF";
    public static final String MODULE_3 = "C8:DF:84:2A:43:D7";
 //   public static final String MODULE_4 = "C8:DF:84:2A:3D:15";
  //  public static final String MODULE_5 = "C8:DF:84:2A:48:90";
    public static final String MODULE_6 = "C8:DF:84:2A:3D:47";
    private static final String TAG = "LocalizationActivity"; //TAG FOR CONSOLE LOGGING
    public static final int SCAN_COUNT = 2; //SCAN UPDATE (AVERAGE)
    public static final int NUM_DEVICES = 2; //NUMBER OF TOTAL BT DEVICES
    public static final int PERMISSION_REQUEST_COARSE_LOCATION = 456; //PERMISSION CODE FOR BT
    public static final int ERROR_THRESHOLD = 5; //ERROR THRESHOLD IN -dBM
    public static final int NULL_COUNTER = 10;


    /**************************************OBJECTS*************************************************/
    ArrayList<GridBox> truthmap = new ArrayList<GridBox>();
    Vector<String> BTDeviceAddresses = new Vector<String>();
    ArrayList<List<ScanResult>> queue = new ArrayList<>(); //Stores circular queue of max 5 readings from 9 devices
    Graph graph = new Graph();
    ArrayList<GridBox> path = new ArrayList<GridBox>();
    GridBox start;
    GridBox currentBox = new GridBox();
    GridBox previousBox = new GridBox();


    /**************************************METHODS*************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        graph = textParser();
        System.out.println("ALL VERTEX");
        for( Vertex vert: graph.getAllVertex()) {

            System.out.println(vert.getName());
        }
        System.out.println("EDGES");
        for( Edge edg : graph.getAllEdges()) {
            System.out.println(edg.getStart().getName());
            System.out.println(edg.getEnd().getName());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localize);

        //Load trueMap from text
       // trueMap = loadBTMapping();

//        //Load BT device names from text
//        BTDeviceAddresses = loadBTDevices();
        truthmap = loadBTMapping(graph);
        currentBox=truthmap.get(0);
 //       System.out.println("FIRST ENTRY");
    //    System.out.println(truthmap.get(0).getLocation().getName());
//        path.add(truthmap.get(0));
//        path.add(truthmap.get(1));
//        path.add(truthmap.get(2));
//        path.add(truthmap.get(3));

        startLEscan();
    }

    /**********************************CALLBACK FUNCTIONS******************************************/
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

            String loc = result.getDevice().getAddress();
            if(MODULE_3.equals(loc)){
                TextView showDir = (TextView)findViewById(R.id.showDir);
                showDir.setText("MPR");
            }

            else{
                TextView showDir = (TextView)findViewById(R.id.showDir);
                showDir.setText("CAFETERIA");            }
           // callback.onBatchScanResults();



        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            // System.out.println("BLE// onBatchScanResults");

            ArrayList<ScanResult>  orderedDevices = new ArrayList<>(); //Sorts devices in module name order
            orderedDevices = sortEntry(results);

            for(ScanResult device: results){
                Log.d("FOUND DEVICES: ",device.getDevice().getAddress());
                Log.d(TAG,Integer.toString(device.getRssi()));
            }
            ArrayList<Double> average = new ArrayList<Double>();
            average = moving_average(orderedDevices, queue);

            String avgstring = "";
            TextView textView = (TextView)findViewById(R.id.viewDevice);
  //          Log.d(TAG, "AVG CALCULATED");
            for(int i = 0; i < average.size(); i++){
                avgstring += String.format("%.2f", average.get(i)) + " ";
            }
            textView.setText(avgstring);

            if( queue.size() >= 2 ){
                ArrayList<Double> standardDeviations = new ArrayList<>(6);
                //standardDeviations = calcStandardDeviation(average,queue);
                TextView availableDevices = (TextView)findViewById(R.id.availableDevices);
                String stds = "";
                for(int i = 0; i < standardDeviations.size(); i++){
                    stds += String.format("%.2f", standardDeviations.get(i)) + " ";
                }
                availableDevices.setText(stds);
            }

            TextView viewRssi = (TextView)findViewById(R.id.viewRssi);
            viewRssi.setText(Integer.toString(queue.size()));

         //   start = graph.getAllVertex().get(0);
         //   GridBox currentBox = truthmap.get(0);
            currentBox = null;
            GridBox curr = findBox(average,previousBox,truthmap);
            previousBox = curr;


            if( curr == null){
                TextView showDir = (TextView)findViewById(R.id.showDir);
                showDir.setText("Searching...");
                return;
            }
            String loc = curr.getLocation().getName();
            System.out.println(loc);
            String test = "BOX (1,0)";

            if(loc.equals(test)){
                System.out.println("FOUND");
                TextView showDir = (TextView)findViewById(R.id.showDir);
                showDir.setText("CAFETERIA");
            }
            else if(loc.equals("BOX (0,0)")){
                System.out.println("FOUND");
                TextView showDir = (TextView)findViewById(R.id.showDir);
                showDir.setText("MPR");

            }

        }

        @Override
        public void onScanFailed(int errorCode) {
            System.out.println("BLE// onScanFailed");
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };
/****************************************STATISTICS FUNCTIONS**************************************/
    //Moving average function
    public ArrayList<Double> moving_average(List<ScanResult> entry, ArrayList<List<ScanResult>> queue) {
        if( queue.size() < SCAN_COUNT){ //If not big enough sample, add to queue and calculate avg
            queue.add(entry);
        }
        else{
            queue.remove(0); //Too large, so remove and calc average
            queue.add(entry);
        }
        return calcAverageArray(queue); //Improvement: store average variable to reduce # of calculations
    }

    //This function returns the average of all 8 modules
    public ArrayList<Double> calcAverageArray(ArrayList<List<ScanResult>> queue){
        ArrayList<Double> average = new ArrayList<>();
        double avg = 0;
        int sum = 0;
        int nullcounter = 0;
        int validEntries = queue.size();
        for(int i = 0; i < NUM_DEVICES; i++){ //Loop each successive scan
            sum = 0;
            nullcounter = 0;
            validEntries = queue.size();
            for( int j = 0; j < queue.size(); j++){ //Loop through each device ~(1-8)
                if( queue.get(j).get(i) == null){
                    nullcounter++;
                    if( (double)nullcounter/validEntries > 0.8){
                        average.add(null);
                        j = queue.size();
                    }
                    else {
                        sum += 0;
                        validEntries--;
                    }
                }
                else{
                    sum += queue.get(j).get(i).getRssi();
                }
            }
            if( (double)nullcounter/validEntries <= 0.8){
                avg = (double)sum/validEntries;
                System.out.println("NULL COUNTER"+Integer.toString(nullcounter));
                average.add(avg);
            }
        }
        return average;
    }

    public ArrayList<Double> calcStandardDeviation(ArrayList<Double> average, ArrayList<List<ScanResult>> queue){
        ArrayList<Double> standardDeviations = new ArrayList<>();
        ArrayList<Double> demeaned = new ArrayList<>();
        for( int deviceNum = 0; deviceNum < NUM_DEVICES; deviceNum++ ){
            for( int queuePos = 0; queuePos < queue.size(); queuePos++){
                if(average.get(deviceNum) == null){
                    standardDeviations.add(null);
                    queuePos = queue.size();
                }
                else{
                    if(queue.get(queuePos).get(deviceNum)!= null){
                        double difference = Math.pow(queue.get(queuePos).get(deviceNum).getRssi() - average.get(deviceNum),2);
                        demeaned.add(difference);
                    }
                    else{
                        demeaned.add(null);
                    }
                }
            }
            if(average.get(deviceNum) != null){
                double std = Math.sqrt(getAverage(demeaned));
                standardDeviations.add(std);
            }
        }
        return standardDeviations; //Standard deviations corresponding to each BT module
    }

    public double getAverage(ArrayList<Double> demeaned){
        double sum = 0;
        int valid = demeaned.size();
        for( int i = 0; i < demeaned.size(); i++ ){
            if( demeaned.get(i) == null ){
                valid--;
            }
            else{
                sum+= demeaned.get(i);
            }
        }
        return sum/valid;
    }

    //Sorts BT devices in order module 1 to 8
    public ArrayList<ScanResult> sortEntry(List<ScanResult>results){
        ArrayList<ScanResult> sorted = new ArrayList<ScanResult>(2);
        for(int i = 0; i < 2; i++){
            sorted.add(0,null);
        }
        for(ScanResult curr: results){
            Log.d("SIZE: ", Integer.toString(results.size()));
            String currentDeviceName = curr.getDevice().getAddress();
            switch(currentDeviceName){
              //  case MODULE_1: sorted.set(0,curr); Log.d("ADDING DEVICE",MODULE_1);break;
       //         case MODULE_2: sorted.set(0, curr); break;
                case MODULE_3: sorted.set(0, curr); break;
       //         case MODULE_4: sorted.set(2, curr); break;
             //   case MODULE_5: sorted.set(4, curr); break;
                case MODULE_6: sorted.set(1, curr); Log.d("ADDING DEVICE",MODULE_6); break;
                default: System.out.println("ERROR");
            }
        }
        return sorted;
    }

    /********************************************************NAVIGATION SHIT***********************/
    public static GridBox findBox(ArrayList<Double> measurement, GridBox currentBox, ArrayList<GridBox> nodes)
    {
//        if(measurement.size()==0){
//            return null;
//        }
        System.out.println("MEASUREMENT SIZE: ");
        System.out.println(Integer.toString(measurement.size()));
        if(measurement.size() <= 1){
            return currentBox;
        }
        if(measurement.get(0) == null && measurement.get(1)==null){
            return null;
        }
        if(measurement.get(0) != null && measurement.get(1) != null){
            return null;
        }
        int index = -1;
        double multiplier = 0.35; //Number of standard deviations in the acceptable interval
        int maxSimScore = -1;
        int num_boxes = 4;
        ArrayList<Integer> simScore = new ArrayList<Integer>();
        for(int i = 0; i < num_boxes; i++){
            simScore.add(i,0);
        }
        ArrayList<GridBox> duplicates = new ArrayList<GridBox>();
        for(GridBox point:nodes) {
            index++; //INDEX STARTS AT 1?
            for(int i = 0; i < measurement.size(); i++) {
                if((measurement.get(i) == null) && (point.getAverage().get(i) == null)) {
                    simScore.add(index, simScore.get(index) + 1);
                    continue;
                }
                else if((measurement.get(i) == null) || point.getAverage().get(i) == null || (measurement.get(i) > point.getAverage().get(i) + multiplier*point.getStandardDeviations().get(i)|| measurement.get(i) < point.getAverage().get(i) - multiplier*point.getStandardDeviations().get(i))) {
                    continue; //If not within bounds, don't add to score
                }
                simScore.add(index, simScore.get(index)+1); //If within bounds, add to score.
            }
            if(simScore.get(index) > maxSimScore) {
                maxSimScore = simScore.get(index);
                if(duplicates.size() > 0) {
                    duplicates.clear();
                }
                duplicates.add(point);
            }
            else if(maxSimScore > 0 && simScore.get(index) == maxSimScore) {
                duplicates.add(point);
            }
        }
        if(duplicates.size() == 1) {
            return duplicates.get(0); //if no duplicates
        }
        return getClosestBox(currentBox, duplicates); //if u have duplicates
    }

    private static void changePosition() {

    }

    private static GridBox getClosestBox(GridBox currentBox, ArrayList<GridBox> duplicates) {
        if(duplicates.contains(currentBox)) {
            return currentBox;
        }
        double minimumDistance = 10000.0;
        GridBox closestBox = null;
        for(GridBox box:duplicates) {
            Vertex loc = box.getLocation();
            double distance = Math.sqrt((currentBox.getLocation().getX() - loc.getX())^2 + (currentBox.getLocation().getY() - loc.getY()^2)); //Distance from where?
            if(distance < minimumDistance) {
                minimumDistance = distance;
                closestBox = box;
            }
        }
        return closestBox;
    }
/********************************************BLUETOOTH IMPLEMENTATION******************************/
    public void startLEscan(){
        BluetoothAdapter mBluetoothAdapter; //Allows discovery, instantiate a BluetoothDevice
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        }

        //Adding filter called builder
   //     ScanFilter filter1 = new ScanFilter.Builder().setDeviceAddress(MODULE_1).build(); //Specific to my BLE device
   //     ScanFilter filter2 = new ScanFilter.Builder().setDeviceAddress(MODULE_2).build();
        ScanFilter filter3 = new ScanFilter.Builder().setDeviceAddress(MODULE_3).build();
   //     ScanFilter filter4 = new ScanFilter.Builder().setDeviceAddress(MODULE_4).build();
    //    ScanFilter filter5 = new ScanFilter.Builder().setDeviceAddress(MODULE_5).build();
        ScanFilter filter6 = new ScanFilter.Builder().setDeviceAddress(MODULE_6).build();
        //Adding builders to filter
        List<ScanFilter> filter = new ArrayList<ScanFilter>();
    //    filter.add(filter1);
   //     filter.add(filter2);
        filter.add(filter3);
   //     filter.add(filter4);
   //     filter.add(filter5);
        filter.add(filter6);

        //Improving scan speed
        ScanSettings.Builder settings = new ScanSettings.Builder();
        settings.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setReportDelay(100);

        //Start discovery
        scanner.startScan(filter, settings.build(), callback);
    }

    /******************************************************TEXT PARSER*****************************/
    public ArrayList<GridBox> loadBTMapping(Graph graph) {

        ArrayList<GridBox> truthmap = new ArrayList<GridBox>();

        String data = "";
        StringBuffer sbuffer = new StringBuffer();
        InputStream is = this.getResources().openRawResource(R.raw.truemap);

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        if (is != null) {
            try {
                while ((data = reader.readLine()) != null) {
                    GridBox gridEntry = new GridBox();
                    ArrayList<Double> signals = new ArrayList<Double>();
                    ArrayList<Double> stds = new ArrayList<Double>();
                    int delimiter = data.indexOf(";");
                    //    System.out.println(Integer.toString(delimiter));
                    Vertex gridBox = new Vertex();
                    System.out.println(data.substring(0,delimiter));

                    gridBox = graph.findVertex(data.substring(0,delimiter));
                    gridEntry.setLocation(gridBox);
                    // gridBox.setName(data.substring(0, delimiter));
                    System.out.println("GRID BOX: " + gridBox.getName());
                    data = data.substring(delimiter + 1);
                    System.out.println(data);

                    while ((delimiter = data.indexOf(",")) != -1 && data.indexOf(";") > data.indexOf(",")) {
                        if(data.substring(0,delimiter).equals("null")) {
                            System.out.println("INSERTED DATA: " + data.substring(0, delimiter));
                            signals.add(null);
                            data = data.substring(delimiter+1);
                        }
                        else {
                            System.out.println("INSERTED DATA: " + data.substring(0, delimiter));
                            signals.add(Double.parseDouble(data.substring(0, delimiter)));
                            data = data.substring(delimiter+1);
                            System.out.println("DELIMITER POS: " + Integer.toString(delimiter));
                        }


                    }
                    delimiter = data.indexOf(";");
                    System.out.println("INSERTED DATA: " + data.substring(0, delimiter));
                    if(data.substring(0,delimiter).equals("null")) {
                        System.out.println("INSERTED DATA: " + data.substring(0, delimiter));
                        signals.add(null);
                        data = data.substring(delimiter+1);
                    }
                    else{
                        signals.add(Double.parseDouble(data.substring(0,data.indexOf(";"))));
                        data = data.substring(data.indexOf(";")+1);
                    }

                    gridEntry.setAverage(signals);
                    while( (delimiter = data.indexOf(",")) != -1 && !data.isEmpty() ) {
                        if(data.substring(0,delimiter).equals("null")) {
                            System.out.println("INSERTED DATA: " + data.substring(0, delimiter));
                            stds.add(null);
                            data = data.substring(delimiter+1);
                        }
                        else {
                            System.out.println("INSERTED DATA: " + data.substring(0, delimiter));
                            stds.add(Double.parseDouble(data.substring(0, delimiter)));
                            data = data.substring(delimiter+1);
                            System.out.println("DELIMITER POS: " + Integer.toString(delimiter));
                        }


                    }
                    System.out.println("INSERTED DATA: " + data);
                    if(data.equals("null")){
                        stds.add(null);
                    }
                    else{
                        stds.add(Double.parseDouble(data));
                    }

                    gridEntry.setStandardDeviations(stds);
                    truthmap.add(gridEntry);

                    System.out.println("ENTRY");
                    System.out.println(truthmap.get(truthmap.size()-1).getLocation().getName());

                }
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        for(Vertex current: map.keySet()) {
//        	System.out.print("VERTEX " + current.getName() + ": ");
//        	for( Integer ints : map.get(current)) {
//        		System.out.print( Integer.toString(ints) + ", ");
//        	}
//        	System.out.println();
////        }
//      for(GridBox box: truthmap) {
//    	System.out.println("NAME: " + box.getLocation().getName());
//    	System.out.println("AVG SIZE: " + box.getAverage().size());
//    	System.out.println("STD SIZE: " + box.getStandardDeviations().size());
//    }

        return truthmap;
    }
    /*******************************************TEXT PARSING*******************************************/
    public Graph textParser() {
        String data = "";
        StringBuffer sbuffer = new StringBuffer();
        InputStream is = this.getResources().openRawResource(R.raw.vertandedges);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String mapName = "";
        Graph graph = new Graph();
        int state = 0;

        if (is != null) {
            try {
                while ((data = reader.readLine()) != null) {
                    if (data.equals("TITLE")) {
                        continue;
                    } else if (state == 0 && !data.equals("TITLE")) {
                        mapName = data;
                        state++;
                    } else if (state == 1) {
                        if (data.equals("VERTEX")) {
                            continue;
                        }

                        if (data.equals("EDGES")) {
                            state++;
                            continue;
                        }

                        if (!data.equals("EDGES")) {
                            int delimiter = 0;
                            Vertex newVert = new Vertex();
                            if ((delimiter = data.indexOf(";")) != -1) {
                                newVert.setName(data.substring(0, delimiter));
                            }
                            data = data.substring(delimiter + 1);
                            if ((delimiter = data.indexOf(",")) != -1) {
                                newVert.setX(Integer.parseInt(data.substring(0, delimiter)));
                            }

                            data = data.substring(delimiter + 1);
                            newVert.setY(Integer.parseInt(data));
                            graph.addVertex(newVert);
                        }
                    } else if (state == 2) {
                        int delimiter = 0;

                        if ((delimiter = data.indexOf(";")) != -1) {
                            //Finds corresponding vertex in allVertex based on name
                            String vert1name = data.substring(0, delimiter);
                            System.out.println(vert1name);
                            data = data.substring(delimiter + 1);
                            String vert2name = data;
                            System.out.println(vert2name);

                            Vertex start = new Vertex();
                            start = graph.findVertex(vert1name);
                            System.out.println(start.getName());
                            Vertex end = new Vertex();
                            end = graph.findVertex(vert2name);

                            if (start != null && end != null) {
                                start.addNeighbor(end);
                                end.addNeighbor(start);
                                double distance = Math.sqrt( Math.pow(start.getX() - end.getX(),2) + Math.pow(start.getY() - end.getY(),2));
                                Edge newEdge = new Edge(start,end,distance);
                                graph.addEdge(newEdge);
                            }
                        }
                    }
                    sbuffer.append(data + "n");
                }
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return graph;
    }


}