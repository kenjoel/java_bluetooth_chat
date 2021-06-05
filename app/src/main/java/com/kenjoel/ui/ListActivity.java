package com.kenjoel.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kenjoel.eighth.R;

import java.util.Set;

public class ListActivity extends AppCompatActivity {


    private ListView paired_devices;
    private String TAG = "What I want";

    private ListView available_devices;

    private ArrayAdapter<String> adapter_paired, adapter_available;

    private BluetoothAdapter bluetoothAdapter;

    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //Launch the progressbar
        mProgress = findViewById(R.id.progress_scan_devices);

       //Get Paired devices
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> get_paired_devices = bluetoothAdapter.getBondedDevices();

        paired_devices = findViewById(R.id.paired_devices);
        available_devices = findViewById(R.id.available_devices);

        adapter_available = new ArrayAdapter<String>(this, R.layout.device_list);
        adapter_paired = new ArrayAdapter<String>(this, R.layout.device_list);

        //Setting Adapter
        paired_devices.setAdapter(adapter_paired);
        available_devices.setAdapter(adapter_available);

        //Set onClickListener on the listviews
        paired_devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String clicked_device = ((TextView) view).getText().toString();
                String address = clicked_device.substring(clicked_device.length() - 17);
                Toast.makeText(ListActivity.this, "The device name" + clicked_device + "\n" + address, Toast.LENGTH_LONG).show();

                Intent intent = new Intent();
                intent.putExtra("itsAddress", address);
                setResult(RESULT_OK, intent);
                finish();
            }
        });







        if (get_paired_devices != null && get_paired_devices.size() > 0){
            //loop through paired devices
            for (BluetoothDevice brd: get_paired_devices){
                adapter_paired.add(brd.getName() + "\n" + brd.getAddress());
            }

        }

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter intentFilter1 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(bluetoothReceiver, intentFilter);
        registerReceiver(bluetoothReceiver, intentFilter1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_available_devices, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.bluetooth_search:
                getAvailableDevices();
        }
        return super.onOptionsItemSelected(item);
    }


    private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            System.out.println(action);

            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device.getBondState() != BluetoothDevice.BOND_BONDED){
                    adapter_available.add(device.getName() + "\n" + device.getAddress());
                }
            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){

                mProgress.setVisibility(View.GONE);
                if (adapter_available.getCount() == 0){
                    Toast.makeText(ListActivity.this, "No new devices found", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ListActivity.this, "Hey, You wanna chat? click on anyone and begin chat", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };



    private void getAvailableDevices(){
        mProgress.setVisibility(View.VISIBLE);
        adapter_paired.clear();
        Toast.makeText(this, "Scan in progress", Toast.LENGTH_SHORT).show();

        if (bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
        }

        bluetoothAdapter.startDiscovery();
    }

}