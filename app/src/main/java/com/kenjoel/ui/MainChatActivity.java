package com.kenjoel.ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.kenjoel.chat_service.ChatUtil;
import com.kenjoel.eighth.R;

public class MainChatActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;

    //CHAT UI
    private ListView mListView;
    private EditText mEditText;
    private Button mSendButton;
    private ArrayAdapter<String> adapterMainChat;


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
                    switch (msg.arg1){
                        case ChatUtil
                                .STATE_HOME:
                            setState("Not Connected");
                        case ChatUtil.STATE_LISTEN:
                            setState("Waiting for device");
                        case ChatUtil.STATE_CONNECTING:
                            setState("Connecting...");
                        case ChatUtil.STATE_CONNECTED:
                            setState("Connected" + connectedDevices);
                    }
                    break;
                case MESSAGE_READ:
                    byte[] buffer = (byte[]) msg.obj;
                    String inputBuffer = new String(buffer, 0, msg.arg1);
                    adapterMainChat.add(connectedDevices + " " + inputBuffer);
                    break;
                case  MESSAGE_WRITE:
                    System.out.println("Didn't find any connections");
                    break;
                case  MESSAGE_DEVICE_NAME:
                    connectedDevices = msg.getData().getString(DeviceNAme);
                    Toast.makeText(MainChatActivity.this, connectedDevices, Toast.LENGTH_LONG).show();
                case MESSAGE_TOAST:
                    Toast.makeText(MainChatActivity.this, msg.getData().getString(TOAST), Toast.LENGTH_LONG).show();
                    break;
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
        initBluetooth();
        chatUtil = new ChatUtil(MainChatActivity.this, handler);
    }

    private void initChat(){
        mListView = findViewById(R.id.chat_list);
        mEditText = findViewById(R.id.edit_text);
        mSendButton = findViewById(R.id.mSendButton);
        adapterMainChat = new ArrayAdapter<String>(MainChatActivity.this, R.layout.message_layout);
        mListView.setAdapter(adapterMainChat);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditText.getText().toString();
                if (!message.isEmpty()){

                }
            }
        });
    }

    private void initBluetooth(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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


    ActivityResultLauncher<Intent> requestForPermissions = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK){

                assert result.getData() != null;
                String address = result.getData().getStringExtra("itsAddress");
                Log.d("TAG", "onActivityResult: " + address);
                chatUtil.connect(bluetoothAdapter.getRemoteDevice(address));
                Toast.makeText(MainChatActivity.this, "Address" + address, Toast.LENGTH_SHORT).show();

            }
        }
    });

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


    private void checkPermissions() {
        requestForPermissions.launch(new Intent(MainChatActivity.this, ListActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatUtil != null){
            chatUtil.stop();
        }
    }
}