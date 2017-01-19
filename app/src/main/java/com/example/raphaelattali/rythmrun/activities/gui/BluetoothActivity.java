package com.example.raphaelattali.rythmrun.activities.gui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.raphaelattali.rythmrun.R;
import com.example.raphaelattali.rythmrun.activities.MainActivity;

import java.util.*;

import static android.R.attr.data;
import static android.bluetooth.BluetoothDevice.ACTION_FOUND;

/**
 * Created by sun-haozhe on 18/01/2017.
 */

public class BluetoothActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter = null;
    private Set<BluetoothDevice> bondedBluetoothDevices = null;
    private ArrayList<String> devices;
    private ArrayAdapter<String> arrayAdapter;
    private ListView listViewBluetoothDevices;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        //sees if this device supports Bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(BluetoothActivity.this,"This device does not support Bluetooth",Toast.LENGTH_SHORT).show();
            return;
        }

        //tests if Bluetooth is actived, if not, starts/actives the Bluetooth
        int REQUEST_CODE_BLUETOOTH = 1;
        if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_CODE_BLUETOOTH);
            //Toast.makeText(BluetoothActivity.this,"Bluetooth has been actived",Toast.LENGTH_SHORT).show();
        }

        //searches all bonded devices and adds them to the list
        devices = new ArrayList<String>();
        bondedBluetoothDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : bondedBluetoothDevices) {
            devices.add(device.getName() + "-" + device.getAddress());
        }
        //searches devices who have not yet been bonded
        bluetoothAdapter.startDiscovery();

        //sets up the DETECT/REFRESH DEVICES button
        Button button_detect_bluetooth = (Button)findViewById(R.id.button_detect_bluetooth_devices);
        button_detect_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //searches devices who have not yet been bonded
                bluetoothAdapter.startDiscovery();
            }
        });

        //sets up the listView of Bluetooth devices
        arrayAdapter = new ArrayAdapter<String>(BluetoothActivity.this, android.R.layout.simple_list_item_1, devices);
        listViewBluetoothDevices = (ListView) findViewById(R.id.list_view_bluetooth_devices);
        listViewBluetoothDevices.setAdapter(arrayAdapter);
        listViewBluetoothDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO click on items of ViewList
                Toast.makeText(BluetoothActivity.this,"You clicked on this item",Toast.LENGTH_SHORT).show();

                //Intent intent = new Intent(BluetoothActivity.this, /* TODO the next activity */);
                //startActivity(intent);
            }
        });

        //creates a BroadcastReceiver for ACTION_FOUND
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                //when discovery finds a device
                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    //gets the BluetoothDevice object from the intent
                    BluetoothDevice newBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    bondedBluetoothDevices.add(newBluetoothDevice);
                    String theNewDevice = newBluetoothDevice.getName() + "-" + newBluetoothDevice.getAddress();
                    devices.add(theNewDevice);
                    arrayAdapter.add(theNewDevice);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        };
        // Register the BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

}
