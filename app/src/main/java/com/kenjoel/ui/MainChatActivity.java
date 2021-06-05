package com.kenjoel.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.kenjoel.chat_service.ChatUtil;
import com.kenjoel.eighth.R;

public class MainChatActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private final int LOCATION_FINE_PERMISSIONS = 101;
    private final int CLICK_TO_START_CHAT = 102;

    //Chat Services
    private ChatUtil chatUtil;

    public static final int MESSAGE_STATE_CHANGED = 0;
    public static final int MESSAGE_READ = 1;
    public static final int MESSAGE_WRITE = 2;
    public static final int MESSAGE_DEVICE_NAME = 3;
    public static final int MESSAGE_TOAST = 4;

    private String connectedDevices;
    public static final String DeviceNAme = "deviceName";
    public static final String TOAST = "toast";




    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case MESSAGE_STATE_CHANGED:
                    break;
                case MESSAGE_READ:
                    switch (msg.arg1){
                        case ChatUtil
                                .STATE_HOME:
                            setState("Not Connected");
                        case ChatUtil.STATE_LISTEN:
                            setState("Waiting for device");
                        case ChatUtil.STATE_CONNECTING:
                            setState("Connecting...");
                        case ChatUtil.STATE_CONNECTED:
                            setState("Connected" + DeviceNAme);
                    }
                case  MESSAGE_WRITE:
                    break;
                case  MESSAGE_DEVICE_NAME:
                    connectedDevices = msg.getData().getString(DeviceNAme);
                    Toast.makeText(MainChatActivity.this, connectedDevices, Toast.LENGTH_LONG).show();
                case MESSAGE_TOAST:
                    Toast.makeText(MainChatActivity.this, msg.getData().getString(TOAST), Toast.LENGTH_LONG).show();
            }
            return false;
        }
    });

    private void setState(CharSequence subTitle){
        getSupportActionBar().setSubtitle(subTitle);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        init();
        chatUtil = new ChatUtil(this, handler);
    }

    private void init(){
        if (bluetoothAdapter == null){
            Toast.makeText(this, "No Devices Found", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Sure we can do bluetooth", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.search_dev:
                    blueToothEnabled();
                case R.id.connected_dev:
                    checkPermissions();
                default:
                    return super.onOptionsItemSelected(item);

            }
    }



    private void blueToothEnabled(){
        if(!bluetoothAdapter.isEnabled()){
            bluetoothAdapter.enable();
        }

        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
            Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoveryIntent);
        }
    }


    private void checkPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainChatActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_FINE_PERMISSIONS);
        }else{
            Intent intent = new Intent(this, ListActivity.class);
            startActivityForResult(intent, CLICK_TO_START_CHAT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == LOCATION_FINE_PERMISSIONS){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(MainChatActivity.this, ListActivity.class);
                startActivityForResult(intent, CLICK_TO_START_CHAT  );
            }else{
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("Location Permission is required, por favor grant them")
                        .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkPermissions();
                            }
                        })
                        .setNegativeButton("Denied", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainChatActivity.this.finish();
                            }
                        }).show();
            }
        }else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CLICK_TO_START_CHAT && resultCode == RESULT_OK){
            String address = getIntent().getStringExtra("deviceAddress");
//            chatUtil.connect();
            Toast.makeText(this, "Address" + address, Toast.LENGTH_SHORT).show();
        }
    }
}