package com.example.raphaelattali.rythmrun.activities.gui;

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

import static android.R.attr.data;

/**
 * Created by sun-haozhe on 18/01/2017.
 */

public class BluetoothActivity extends AppCompatActivity {

    private String[] listOfBluetoothDevices = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        
        Button button_detect_bluetooth = (Button)findViewById(R.id.button_detect_bluetooth_devices);
        button_detect_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO click on DETECT DEVICES
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                BluetoothActivity.this, android.R.layout.simple_list_item_1, listOfBluetoothDevices);
        ListView listViewBluetoothDevices = (ListView) findViewById(R.id.list_view_bluetooth_devices);
        listViewBluetoothDevices.setAdapter(adapter);
        listViewBluetoothDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO click on items of ViewList
                Toast.makeText(BluetoothActivity.this,"You clicked on this item",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
