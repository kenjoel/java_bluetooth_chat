package com.kenjoel.chat_service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.kenjoel.ui.MainChatActivity;

import java.io.IOException;
import java.util.UUID;



public class ChatUtil {
    private Context context;
    private Handler handler;

    private ConnectThread connectThread;
    private HandleAcceptedDevices handleAcceptedDevices;

    private String TAG = "ChatUtil";
    private BluetoothAdapter bluetoothAdapter;

    private final String APP_NAME = "BluetoothAppName";

    public static final int STATE_HOME = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    private final UUID uuid = UUID.randomUUID();

    private int state;

    public ChatUtil(Context context, Handler handler){
        this.context = context;
        this.handler = handler;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        state = STATE_HOME;
    }

    public int getState() {
        return state;
    }

    public synchronized void setState(int state) {
        this.state = state;
        handler.obtainMessage(MainChatActivity.MESSAGE_STATE_CHANGED, state, -1).sendToTarget();
    }


    private synchronized void start(){
        if (connectThread != null){
            connectThread.cancel();
            connectThread = null;
        }

        if (handleAcceptedDevices == null){
            handleAcceptedDevices = new HandleAcceptedDevices();
            handleAcceptedDevices.start();

            setState(STATE_LISTEN);
        }
    }

    public synchronized void stop(){
        if (connectThread != null){
            connectThread.cancel();
            connectThread = null;
        }

        if (handleAcceptedDevices != null){
            handleAcceptedDevices = new HandleAcceptedDevices();
            handleAcceptedDevices.cancel();
            handleAcceptedDevices = null;
        }

        setState(STATE_HOME);
    }



    private class ConnectThread extends Thread{
        private final BluetoothSocket bluetoothSocket;
        private final BluetoothDevice device;


        public ConnectThread(BluetoothDevice bluetoothDevice){
            this.device = bluetoothDevice;

            BluetoothSocket tmp = null;

            try{
                tmp = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                Log.e(TAG,  e.toString());
            }

            bluetoothSocket = tmp;
        }



        @Override
        public void run() {
            super.run();

            try {
                bluetoothSocket.connect();
            } catch (IOException e) {
                Log.d(TAG, "connection -> run: " + e);
                try {
                    bluetoothSocket.close();
                } catch (IOException ioException) {
                    Log.d(TAG, "Bluetooth close" + ioException);
                    ioException.printStackTrace();
                }
                connection_failed();
                return;
            }

            synchronized (ChatUtil.this) {
                connectThread = null;
            }

            connect(device);
        }
        public void cancel(){
            try{
                bluetoothSocket.close();
            }catch (IOException e){
                Log.e(TAG, "cancel: " + e.toString() );

            }
        }
    }




        public synchronized void connect(BluetoothDevice device) {
        if (state == STATE_CONNECTING){
            connectThread.cancel();
            connectThread = null;
        }

        connectThread = new ConnectThread(device);
        connectThread.start();

        setState(STATE_CONNECTING);

        }


    private synchronized void connection_failed(){
        Message message = handler.obtainMessage(MainChatActivity.MESSAGE_TOAST);

        Bundle bundle = new Bundle();

        bundle.putString(MainChatActivity.TOAST, "Can't connect to device");

        message.setData(bundle);

        handler.sendMessage(message);

        ChatUtil.this.start();
    }




    public synchronized void connected(BluetoothDevice device){
        if (connectThread != null){
            connectThread.cancel();
            connectThread = null;
        }

        Message message = handler.obtainMessage(MainChatActivity.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(MainChatActivity.DeviceNAme, device.getName());
        message.setData(bundle);
        handler.sendMessage(message);

        setState(STATE_CONNECTED);

    }

    private class HandleAcceptedDevices extends Thread{
        private BluetoothServerSocket bluetoothServerSocket;

        public HandleAcceptedDevices(){
            BluetoothServerSocket tmp = null;
            try{
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, uuid);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "HandleAcceptedDevices: ", e);
            }
           bluetoothServerSocket = tmp;
        }

        public void run(){
            BluetoothSocket socket = null;
            try{
                bluetoothServerSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Socket connection: ",e );
                try{
                    socket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    Log.e(TAG, "Close Socket Connection: ", e );
                }
            }
            if (socket != null){
                switch (state){
                    case STATE_LISTEN:
                    case STATE_CONNECTING:
                        connect(socket.getRemoteDevice());
                        break;
                    case STATE_CONNECTED:
                        try{
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        }
        public void cancel(){
            try{
                bluetoothServerSocket.close();
            }catch (IOException e){
                Log.e(TAG, "cancel: " + e.toString() );

            }
        }
    }

}


