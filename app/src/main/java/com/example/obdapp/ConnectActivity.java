package com.example.obdapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class ConnectActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public static class SocketHandler {
        private static BluetoothSocket socket;

        public static synchronized BluetoothSocket getSocket(){
            return socket;
        }

        public static synchronized void setSocket(BluetoothSocket socket){
            SocketHandler.socket = socket;
        }
    }


    private static final String TAG = "testActivity";
    BluetoothAdapter btAdapter;
    NavigationView navigationView;
    DrawerLayout drawer;
    FrameLayout fragmentConnection;
    BluetoothSocket socket;
    TextView status;
    String deviceAddress;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.nav_home:
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_connection:
                Intent intent = new Intent(this, ConnectActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_retry:
                Intent intent2 = new Intent(this, MainActivity.class);
                startActivity(intent2);
                break;

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        findViewByIds();

        navigationView.setNavigationItemSelectedListener(this);

        status.setTextColor(Color.rgb(128,128,128));
        status.setText("Connecting...");


        ArrayList deviceStrs = new ArrayList();
        final ArrayList devices = new ArrayList();

        //get list of paired devices
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0)
        {
            for (BluetoothDevice device : pairedDevices)
            {
                deviceStrs.add(device.getName() + "\n" + device.getAddress());
                devices.add(device.getAddress());
            }
        }

        // show list
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice,
                deviceStrs.toArray(new String[deviceStrs.size()]));

        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                int position = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                deviceAddress = (String) devices.get(position);

                SharedPreferences pref = getApplicationContext().getSharedPreferences("BTAddress", 0);
                SharedPreferences.Editor editor = pref.edit();
                // Storing string
                editor.putString("device_address", deviceAddress);
                // commit changes
                editor.commit();

                connectBT(deviceAddress);
            }
        });
        SharedPreferences pref = getApplicationContext().getSharedPreferences("BTAddress", 0);
        SharedPreferences.Editor editor = pref.edit();

        //show paired devices list
        alertDialog.setTitle("Choose Bluetooth device");
        alertDialog.show();


    }


    private void connectBT(String deviceAddress) {


        btAdapter = BluetoothAdapter.getDefaultAdapter();

        BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);

        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


        try {

            socket = device.createInsecureRfcommSocketToServiceRecord(uuid);

        } catch (IOException e) {

            e.printStackTrace();
        }

        btAdapter.cancelDiscovery();


        try {

            socket.connect();

            status.setTextColor(Color.rgb(0,255,0));
            status.setText("Connected");

            SocketHandler.setSocket(socket);

        } catch (IOException e) {
            e.printStackTrace();

            status.setTextColor(Color.rgb(255,0,0));
            status.setText("Connection Failed");

            SocketHandler.setSocket(socket);
        }


    }

    //menu drawer
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    private void findViewByIds() {

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        fragmentConnection = findViewById(R.id.fragment_container);
        status = findViewById(R.id.status);
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }

    //toast message function
    private void showToast(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}