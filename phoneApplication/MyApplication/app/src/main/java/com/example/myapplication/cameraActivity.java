package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class cameraActivity extends AppCompatActivity {

    String hardwareaddress;
    public static Camera mCamera ;
    Button stopButton;
    Button startButton;
    Button pauseButton;
    Button recordButton;
    TextView textView;

    /*Handle to update UI Bases on bluetooth RP server returning successfull message.*/
    @SuppressLint("HandlerLeak")
    public static Handler handlers = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            Log.wtf("The message is: ", msg.obj.toString());
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        stopButton = (Button) findViewById(R.id.stopButton);
        startButton = (Button) findViewById(R.id.button2);
        textView = findViewById(R.id.editText2);
        //pauseButton = findViewById(R.id.pauseButton);
        recordButton = findViewById(R.id.recordButton);

        //hide stop recording button
        stopButton.setVisibility(View.GONE);
        //pauseButton.setVisibility(View.GONE);
        recordButton.setVisibility(View.GONE);

    }


    private void updatemodel() {

    }

    /** Check if this device has a camera */
    @SuppressLint("UnsupportedChromeOsCameraSystemFeature")
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }


    public BluetoothDevice getDevice() {
        BluetoothDevice mmDevice;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.i("Error no bluetooth", "");
        } else {

            //check that the device is already paired with the phone
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address

                    //set hardware address to raspberry pies address
                    Log.i("Device name ", deviceName);
                    Log.i("hdAddress ", deviceHardwareAddress);
                    if (deviceName.equals("raspberrypi")) {
                        hardwareaddress = deviceHardwareAddress;
                        mmDevice = device;
                        return mmDevice;
                    }
                }
            }
        }
        return null;
    }
    public void recordMoreService(View view){
        //check for bluetooth adapter

        String nameOfActivity = textView.getText().toString();
        String message = "Record";

        Log.wtf("message sentoS ", message);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.i("Error no bluetooth", "");
        } else {

            BluetoothDevice mmDevice = getDevice();
            //Using the BluetoothDevice, get a BluetoothSocket by calling createRfcommSocketToServiceRecord(UUID).
            ConnectThread thread = new ConnectThread(mmDevice, message);
            new Thread(thread).run();

            //make the stop button for this intent visible hide start recording button/text


        }
    }

    public void stopRecorderService(View view){
        //check for bluetooth adapter

        String nameOfActivity = textView.getText().toString();
        String message = "Stop";

        Log.wtf("message sentoS ", message);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.i("Error no bluetooth", "");
        } else {

            BluetoothDevice mmDevice = getDevice();
            //Using the BluetoothDevice, get a BluetoothSocket by calling createRfcommSocketToServiceRecord(UUID).
            ConnectThread thread = new ConnectThread(mmDevice, message);
            new Thread(thread).run();

            //hide stop recording button display start recording button
            stopButton.setVisibility(View.GONE);
            //pauseButton.setVisibility(View.GONE);
            recordButton.setVisibility(View.GONE);

            textView.setVisibility(View.VISIBLE);
            startButton.setVisibility(View.VISIBLE);

        }
    }

    public void pauseRecorderService(View view) {
        //check for bluetooth adapter

        String nameOfActivity = textView.getText().toString();
        String message = "Pause";

        Log.wtf("message sentoS ", message);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.i("Error no bluetooth", "");
        } else {

            BluetoothDevice mmDevice = getDevice();
            //Using the BluetoothDevice, get a BluetoothSocket by calling createRfcommSocketToServiceRecord(UUID).
            ConnectThread thread = new ConnectThread(mmDevice, message);
            new Thread(thread).run();

            //make the pause button hidden
            //hide stop recording button display start recording button
            //pauseButton.setVisibility(View.GONE);


        }
    }

    public void uploadRecorderService(View view) {
        //check for bluetooth adapter

        String nameOfActivity = textView.getText().toString();
        String message = "upload";

        Log.wtf("message sentoS ", message);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.i("Error no bluetooth", "");
        } else {

            BluetoothDevice mmDevice = getDevice();
            //Using the BluetoothDevice, get a BluetoothSocket by calling createRfcommSocketToServiceRecord(UUID).
            ConnectThread thread = new ConnectThread(mmDevice, message);
            new Thread(thread).run();

            //make the pause button hidden
            //hide stop recording button display start recording button
            //pauseButton.setVisibility(View.GONE);


        }
    }


    public void startRecorderService(View view) {
        //check for bluetooth adapter

        String nameOfActivity = textView.getText().toString();
        String message = "Start " + nameOfActivity;

        Log.wtf("message sentoS ", message);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.i("Error no bluetooth", "");
        } else {

            BluetoothDevice mmDevice = getDevice();
            //Using the BluetoothDevice, get a BluetoothSocket by calling createRfcommSocketToServiceRecord(UUID).
            ConnectThread thread = new ConnectThread(mmDevice, message);
            new Thread(thread).run();

            //make the stop button for this intent visible hide start recording button/text
            stopButton.setVisibility(View.VISIBLE);
            //pauseButton.setVisibility(View.VISIBLE);
            recordButton.setVisibility(View.VISIBLE);

            startButton.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);

        }
    }
}