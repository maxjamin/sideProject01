package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

public class ConnectThread extends Thread {
    private static final String TAG = " ";
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    String msg;
    

    private BluetoothServerSocket mmServerSocket;

    public ConnectThread(BluetoothDevice device, String msg) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        mmDevice = device;
        this.msg = msg;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee"));
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        mmSocket = tmp;

    }


    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        //bluetoothAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the  socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        manageMyConnectedSocket();
        cancel();
    }

    private void manageMyConnectedSocket() {
        try {

            if (!mmSocket.isConnected()) {
                mmSocket.connect();
            }

            //String msg = "Testing sentence";
            //msg += "\n";
            OutputStream mmOutputStream = mmSocket.getOutputStream();
            mmOutputStream.write(msg.getBytes());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // Closes the client socket and causes the thread to finish.
    private void cancel() {
        try {
            mmSocket.close();

        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }


}
