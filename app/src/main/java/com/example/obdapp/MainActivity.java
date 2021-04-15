package com.example.obdapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.sax.TextElementListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.control.DistanceSinceCCCommand;
import com.github.pires.obd.commands.engine.MassAirFlowCommand;
import com.github.pires.obd.commands.engine.OilTempCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.fuel.AirFuelRatioCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.pressure.BarometricPressureCommand;
import com.github.pires.obd.commands.pressure.IntakeManifoldPressureCommand;
import com.github.pires.obd.commands.protocol.CloseCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.SpacesOffCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.github.pires.obd.exceptions.UnableToConnectException;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static java.util.Map.entry;

@RequiresApi(api = Build.VERSION_CODES.R)
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_ENABLE_BT = 0;

    private static final String TAG = "MainActivity";

    //DECLARE ALL ELEMENTS
    TextView RPM,MPH, MPG, temp, gear, status, advice, advice_title, maf_view, oil_temp_view, throttle_view, air_fuel_view, barometric_view, intake_view;;
    Button menu;
    BluetoothSocket socket;
    BluetoothAdapter btAdapter;
    NavigationView navigationView;
    DrawerLayout drawer;
    FrameLayout fragmentConnection;
    SwitchCompat advice_switch, maf_switch, oil_temp_switch, throttle_switch, air_fuel_switch, barometric_switch, intake_switch;

    SharedPreferences pref ;
    SharedPreferences.Editor editor ;

    int speed;
    int rpm;
    int temp_advice=0;
    int speed_advice=0;
    float fuel_level=0;
    float distance;
    String device_address;
    long gear_currentTime = 0;

    BluetoothDevice[] btArray;
    int check1, check_connection=0;

    private int textCount;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;

    int REQUEST_ENABLE_BLUETOOTH = 1;

    Map<Integer, Integer> first = Map.ofEntries(
            entry(5,770),
            entry(10,1800),
            entry(15,2600),
            entry(20,3500),
            entry(25,4400)
    );
    Map<Integer, Integer> second = Map.ofEntries(
            entry(5,400),
            entry(10,900),
            entry(15,1400),
            entry(20,1800),
            entry(25,2300),
            entry(30,2700),
            entry(35,3200),
            entry(40,3600),
            entry(45,4100),
            entry(50,4550),
            entry(55,5000)
    );
    Map<Integer, Integer> third = Map.ofEntries(
            entry(5,300),
            entry(10,550),
            entry(15,850),
            entry(20,1200),
            entry(25,1450),
            entry(30,1750),
            entry(35,2050),
            entry(40,2350),
            entry(45,2650),
            entry(50,2900),
            entry(55,3200),
            entry(60,3500),
            entry(65,3800),
            entry(70,4100),
            entry(75,4400),
            entry(80,4700),
            entry(85,5000)
    );
    Map<Integer, Integer> fourth = Map.ofEntries(
            entry(10,400),
            entry(15,600),
            entry(20,800),
            entry(25,1000),
            entry(30,1200),
            entry(35,1400),
            entry(40,1600),
            entry(45,1800),
            entry(50,2000),
            entry(55,2200),
            entry(60,2400),
            entry(65,2600),
            entry(70,2800),
            entry(75,3000),
            entry(80,3250),
            entry(85,3450),
            entry(90,3650),
            entry(95,3850),
            entry(100,4050),
            entry(105,4250),
            entry(110,4450),
            entry(115,4650),
            entry(120,4850)
    );
    Map<Integer, Integer> fifth = Map.ofEntries(
            entry(10,330),
            entry(15,480),
            entry(20,650),
            entry(25,800),
            entry(30,950),
            entry(35,1100),
            entry(40,1270),
            entry(45,1450),
            entry(50,1600),
            entry(55,1750),
            entry(60,1900),
            entry(65,2100),
            entry(70,2250),
            entry(75,2400),
            entry(80,2550),
            entry(85,2700),
            entry(90,2850),
            entry(95,3030),
            entry(100,3200),
            entry(105,3350),
            entry(110,3500),
            entry(115,3650),
            entry(120,3800),
            entry(125,4000),
            entry(130,4150),
            entry(135,4300),
            entry(140,4450),
            entry(145,4600),
            entry(150,4800),
            entry(155,5000)
    );

    Map<Integer, Integer> sixth = Map.ofEntries(
            entry(10,250),
            entry(15,400),
            entry(20,550),
            entry(25,680),
            entry(30,800),
            entry(35,950),
            entry(40,1080),
            entry(45,1200),
            entry(50,1350),
            entry(55,1480),
            entry(60,1600),
            entry(65,1750),
            entry(70,1880),
            entry(75,2000),
            entry(80,2150),
            entry(85,2280),
            entry(90,2400),
            entry(95,2550),
            entry(100,2680),
            entry(105,2800),
            entry(110,2950),
            entry(115,3080)
    );
    /*

   Map<Integer, Integer> first = Map.ofEntries(
           entry(5,460),
           entry(10,970),
           entry(15,2),
           entry(20,4),
           entry(25,2),
           entry(30,4)
            );
    Map<Integer, Integer> second = Map.ofEntries(
            entry(5,750),
            entry(10,2),
            entry(15,4),
            entry(20,2000),
            entry(25,2500),
            entry(30,3000),
            entry(35,3500),
            entry(40,4000),
            entry(45,2)
    );
    Map<Integer, Integer> third = Map.ofEntries(
            entry(5,750),
            entry(10,800),
            entry(15,1000),
            entry(20,1300),
            entry(25,1600),
            entry(30,1750),
            entry(35,2100),
            entry(40,2500),
            entry(45,2800),
            entry(50,3000),
            entry(55,3500),
            entry(60,3700),
            entry(65,2),
            entry(70,2)
    );
    Map<Integer, Integer> fourth = Map.ofEntries(
            entry(5,750),
            entry(10,750),
            entry(15,750),
            entry(20,750),
            entry(25,750),
            entry(30,1),
            entry(35,2),
            entry(40,1600),
            entry(45,1850),
            entry(50,2100),
            entry(55,2300),
            entry(60,2500),
            entry(65,2750),
            entry(70,2900),
            entry(75,2),
            entry(80,2),
            entry(85,2)
    );
    Map<Integer, Integer> fifth = Map.ofEntries(
            entry(5,750),
            entry(10,750),
            entry(15,750),
            entry(20,750),
            entry(25,750),
            entry(30,750),
            entry(35,750),
            entry(40,1250),
            entry(45,1400),
            entry(50,1550),
            entry(55,1750),
            entry(60,1900),
            entry(65,2),
            entry(70,2),
            entry(75,2),
            entry(80,2),
            entry(85,2)
    );

     */



    //NAVIGATION MENU
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
        new Thread(new Runnable()
        {
            public void run()
            {
                connectBT(device_address);
                if (socket.isConnected())
                {
                    try
                    {
                        execCommand();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        break;
    }
    drawer.closeDrawer(GravityCompat.START);
    return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getApplicationContext().getSharedPreferences("BTAddress", 0); // 0 - for private mode
        editor = pref.edit();

        //CREATE BLUETOOTH ADAPTER
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        //GET EACH ELEMENT
        findViewByIds();

        //AUTOMATICALLY TURN BLUETOOTH ON
        if (!btAdapter.isEnabled())
        {
            showToast("Turning OnBluetooth...");
            //intent to on Bluetooth
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }


        //SET LISTENER FOR MENU BUTTON
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //OPEN AND CLOSE DRAWER MENU
                if(drawer.isDrawerOpen(GravityCompat.START))

                {
                    drawer.closeDrawer(GravityCompat.START);
                }
                else
                {
                    drawer.openDrawer(GravityCompat.START);
                }


            }
        });

        //set switch position to match saved position
        if(pref.getString("advice", null).equals("active"))
        {
            advice_switch.setChecked(true);
            advice.setVisibility(View.VISIBLE);

            //make other view invisible
            maf_view.setVisibility(View.INVISIBLE);
            maf_switch.setVisibility(View.INVISIBLE);
            oil_temp_view.setVisibility(View.INVISIBLE);
            oil_temp_switch.setVisibility(View.INVISIBLE);
            throttle_view.setVisibility(View.INVISIBLE);
            throttle_switch.setVisibility(View.INVISIBLE);
            air_fuel_view.setVisibility(View.INVISIBLE);
            air_fuel_switch.setVisibility(View.INVISIBLE);
            barometric_view.setVisibility(View.INVISIBLE);
            barometric_switch.setVisibility(View.INVISIBLE);
            intake_view.setVisibility(View.INVISIBLE);
            intake_switch.setVisibility(View.INVISIBLE);



        }
        else if(pref.getString("advice", null).equals("not active"))
        {
            advice_switch.setChecked(false);
            advice.setVisibility(View.INVISIBLE);

            //make the other views visible
            maf_view.setVisibility(View.VISIBLE);
            maf_switch.setVisibility(View.VISIBLE);
            oil_temp_view.setVisibility(View.VISIBLE);
            oil_temp_switch.setVisibility(View.VISIBLE);
            throttle_view.setVisibility(View.VISIBLE);
            throttle_switch.setVisibility(View.VISIBLE);
            air_fuel_view.setVisibility(View.VISIBLE);
            air_fuel_switch.setVisibility(View.VISIBLE);
            barometric_view.setVisibility(View.VISIBLE);
            barometric_switch.setVisibility(View.VISIBLE);
            intake_view.setVisibility(View.VISIBLE);
            intake_switch.setVisibility(View.VISIBLE);
        }





        //when advice switch position changes
        advice_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    //save advice state and make advice box visible
                    editor.putString("advice", "active"); // Storing string
                    advice.setVisibility(View.VISIBLE);

                    maf_view.setVisibility(View.INVISIBLE);
                    maf_switch.setVisibility(View.INVISIBLE);
                    oil_temp_view.setVisibility(View.INVISIBLE);
                    oil_temp_switch.setVisibility(View.INVISIBLE);
                    throttle_view.setVisibility(View.INVISIBLE);
                    throttle_switch.setVisibility(View.INVISIBLE);
                    air_fuel_view.setVisibility(View.INVISIBLE);
                    air_fuel_switch.setVisibility(View.INVISIBLE);
                    barometric_view.setVisibility(View.INVISIBLE);
                    barometric_switch.setVisibility(View.INVISIBLE);
                    intake_view.setVisibility(View.INVISIBLE);
                    intake_switch.setVisibility(View.INVISIBLE);
                }
                else
                {
                    //save advice state and make advice box invisible
                    editor.putString("advice", "not active"); // Storing string
                    advice.setVisibility(View.INVISIBLE);

                    maf_view.setVisibility(View.VISIBLE);
                    maf_switch.setVisibility(View.VISIBLE);
                    oil_temp_view.setVisibility(View.VISIBLE);
                    oil_temp_switch.setVisibility(View.VISIBLE);
                    throttle_view.setVisibility(View.VISIBLE);
                    throttle_switch.setVisibility(View.VISIBLE);
                    air_fuel_view.setVisibility(View.VISIBLE);
                    air_fuel_switch.setVisibility(View.VISIBLE);
                    barometric_view.setVisibility(View.VISIBLE);
                    barometric_switch.setVisibility(View.VISIBLE);
                    intake_view.setVisibility(View.VISIBLE);
                    intake_switch.setVisibility(View.VISIBLE);
                }

                editor.commit(); // commit changes
            }
        });

        advice.setMovementMethod(new ScrollingMovementMethod());

        navigationView.setNavigationItemSelectedListener(this);


        if(btAdapter.isEnabled()) {
            new Thread(new Runnable() {
                public void run() {
                    if (pref.getString("device_address", null) != null) {

                        device_address = pref.getString("device_address", null);
                        connectBT(device_address);
                        if (socket.isConnected()) {
                            try {
                                execCommand();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }).start();


        }

    }

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
        RPM = findViewById(R.id.RPM);
        MPG = findViewById(R.id.MPG);
        MPH = findViewById(R.id.MPH);
        gear = findViewById(R.id.gear);
        temp = findViewById(R.id.temp);
        status = findViewById(R.id.status);
        menu = findViewById(R.id.Menu);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        fragmentConnection = findViewById(R.id.fragment_container);
        advice = findViewById(R.id.advice);
        advice_title = findViewById(R.id.advice_title);
        maf_view =  findViewById(R.id.maf_view);
        oil_temp_view =  findViewById(R.id.oil_temp_view);
        throttle_view =  findViewById(R.id.throttle_view);
        air_fuel_view =  findViewById(R.id.air_fuel_view);
        barometric_view =  findViewById(R.id.barometric_view);
        intake_view =  findViewById(R.id.intake_view);
        advice_switch =  findViewById(R.id.advice_switch);
        maf_switch =  findViewById(R.id.maf_switch);
        oil_temp_switch =  findViewById(R.id.oil_temp_switch);
        throttle_switch =  findViewById(R.id.throttle_switch);
        air_fuel_switch =  findViewById(R.id.air_fuel_switch);
        barometric_switch =  findViewById(R.id.barometric_switch);
        intake_switch =  findViewById(R.id.intake_switch);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode)
        {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK)
                {
                    showToast("Bluetooth is on");
                }
                else
                {
                    //user is denied to turn bluetooth on
                    showToast("Couldn't turn bluetooth on");
                }
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
/*
        if(btAdapter.isEnabled()) {
            new Thread(new Runnable() {
                public void run() {
                    if (pref.getString("device_address", null) != null) {

                        device_address = pref.getString("device_address", null);
                        connectBT(device_address);
                        if (socket.isConnected()) {
                            try {
                                execCommand();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }).start();




        }

 */
    }


    //Connect to Bluetooth Device
    private void connectBT(String deviceAddress) {
        //set status information
        status.setTextColor(Color.rgb(128,128,128));
        status.setText(" Connecting...");

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
            check_connection=1;

            status.setTextColor(Color.rgb(0,255,0));
            status.setText("Connected");

        } catch (IOException e) {
            e.printStackTrace();

            status.setTextColor(Color.rgb(255,0,0));
            status.setText("Connection Failed");
        }
    }

    private void execCommand() throws IOException, InterruptedException {
        if(socket.isConnected())
        {
            // execute commands
            try {
            //    new ObdResetCommand().run(socket.getInputStream(),socket.getOutputStream());
                new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                new SpacesOffCommand().run(socket.getInputStream(), socket.getOutputStream());
             //   new HeadersOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                new CloseCommand().run(socket.getInputStream(), socket.getOutputStream());
                new TimeoutCommand(125).run(socket.getInputStream(), socket.getOutputStream());
               // new RPMCommand();
                new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
              //  new AmbientAirTemperatureCommand().run(socket.getInputStream(), socket.getOutputStream());
                check1=1;
             //   new RPMCommand().run(socket.getInputStream(), socket.getOutputStream());
            } catch (Exception e) {
                status.append("Executing Commands Failed");
                check1=0;
                // handle errors
            }

            if(check1==1) {
                while (!Thread.currentThread().isInterrupted())
                {

                    getRPM();
                    getSPEED();
                    //getMPG();
                    getGEAR();
                    getCoolTEMP();

                    //MASS AIR FLOW
                    //when maf switch is on
                    if(maf_switch.isChecked())
                    {
                        try {
                            getMAF();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //when maf switch is off display text
                    maf_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked==false)
                            {
                                maf_view.setText("Mass Air Flow");
                            }
                        }
                    });

                    //OIL TEMPERATURE
                    //when oil temp switch is on
                    if(oil_temp_switch.isChecked())
                    {

                        try {

                            getOilTemp();

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }catch (UnableToConnectException e)
                        {
                            oil_temp_view.setText("Not Available");
                        }

                    }

                    //when oil switch is off display text
                    oil_temp_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked==false)
                            {
                                oil_temp_view.setText("Oil Temperature");
                            }

                        }
                    });

                    //THROTTLE POSITION
                    //when throttle switch is on
                    if(throttle_switch.isChecked())
                    {

                        try {

                            getThrottle();

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                    //when throttle switch is off display text
                    throttle_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked==false)
                            {
                                throttle_view.setText("Throttle Position");
                            }

                        }
                    });

                    // AIR/FUEL RATIO
                    //when air fuel switch is on
                    if(air_fuel_switch.isChecked())
                    {

                        try {

                            getAirFuel();

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                    //when air fuel switch is off display text
                    air_fuel_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked==false)
                            {
                                air_fuel_view.setText("Air/Fuel Ratio");
                            }

                        }
                    });

                    //BAROMATRIC PRESSURE
                    //when barometric switch is on
                    if(barometric_switch.isChecked())
                    {

                        try {

                            getBarometric();

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                    //when barometric switch is off display text
                    barometric_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked==false)
                            {
                                barometric_view.setText("Barometric Pressure");
                            }

                        }
                    });

                    //INTAKE MANIFOLD PRESSURE
                    //when intake switch is on
                    if(intake_switch.isChecked())
                    {

                        try {

                            getIntakePress();

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                    //when intake switch is off display text
                    intake_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked==false)
                            {
                                intake_view.setText("Intake Pressure");
                            }

                        }
                    });
                }
            }

        }
        else
        {
            status.append("\nNot Connected ");
        }


    }

    //getting the Revolutions per Minute
    private void getRPM() throws IOException, InterruptedException {
        RPMCommand engineRpmCommand = new RPMCommand();
        try {

            engineRpmCommand.run(socket.getInputStream(), socket.getOutputStream());
            /*
            int temp_rpm = (int)engineRpmCommand.getRPM();
            RPM.setText(temp_rpm + "\n rpm");
            rpm=engineRpmCommand.getRPM();

             */

            rpm=engineRpmCommand.getRPM();
            RPM.setText(rpm + "\n rpm");


            //test
            CloseCommand closeCommand = new CloseCommand();
            closeCommand.run(this.socket.getInputStream(), this.socket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //getting the miles per hour
    private void getSPEED() throws IOException, InterruptedException {
        SpeedCommand speedCommand = new SpeedCommand();

        try {
            //  showToast("Executing Speed...");
            speedCommand.run(socket.getInputStream(), socket.getOutputStream());

            //test
            //int result = (int)speedCommand.getImperialSpeed();
            speed= (int)speedCommand.getImperialSpeed();
            MPH.setText(speed + "\n mph");

            //if advice feature is turned on
            if(pref.getString("advice", null).equals("active"))
            {
                //Advice on excessive speed
                if (speed >= 80 && speed_advice != 1)
                {
                    advice.append("\n Slow Down.");
                    speed_advice = 1;
                }
                else if (speed < 80 && speed_advice == 1)
                {
                    speed_advice = 0;
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (UnableToConnectException e)
        {

        }
    }

    //getting the Miles per Gallon
    private void getMPG() throws IOException, InterruptedException {
        FuelLevelCommand FuelCommand = new FuelLevelCommand();
        DistanceSinceCCCommand DistanceCommand = new DistanceSinceCCCommand();
        try {

            FuelCommand.run(socket.getInputStream(), socket.getOutputStream());
            DistanceCommand.run(socket.getInputStream(), socket.getOutputStream());
            if(fuel_level==0)
            {
                //get initial fuel level and mileage
                fuel_level = FuelCommand.getFuelLevel();
                distance = DistanceCommand.getImperialUnit();
            }
            else
            {
                //get current fuel level
                float fuel_level2 = FuelCommand.getFuelLevel();
                if(fuel_level!=fuel_level2)
                {
                    //if the fuel level changed, get new mileage
                    float distance2 = DistanceCommand.getImperialUnit();

                    float fuel_difference = fuel_level - fuel_level2;
                    fuel_difference = (float) (fuel_difference * 50 * 0.219969);
                    float dist_difference = distance2 - distance2;

                    float mpg = dist_difference/fuel_difference;
                    MPG.setText((int) mpg);
                    //MPG.append(" mpg");

                }

                fuel_level = fuel_level2;
            }



            //test
            CloseCommand closeCommand = new CloseCommand();
            closeCommand.run(this.socket.getInputStream(), this.socket.getOutputStream());
            //RPM.invalidate();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (UnableToConnectException e)
        {

        }

    }

    //getting the gear the transmission is in
    private void getGEAR() throws IOException, InterruptedException {
        int gear_speed = speed;
        //make sure the speed % 5 is 0
        if(gear_speed>=8){
            if(gear_speed % 5 == 0)
            {
                //do nothing
            }
            //if %5 over 3, take it to the next value
            else if (gear_speed % 5 >= 3)
            {
                gear_speed = gear_speed + (5-gear_speed % 5);
            }
            //if %5 under 2, take to the previous value
            else if (gear_speed % 5 <= 2)
            {
                gear_speed = gear_speed - (gear_speed % 5);
            }
        }

        //find the correct speed and rpm pair
        if(gear_speed>=10) {

            if (first.containsKey(gear_speed - 5) && first.containsKey(gear_speed + 5) && rpm >= first.get(gear_speed - 5) && rpm <= first.get(gear_speed + 5))
            {
                if (rpm >= 2100)
                {
                    gear.setText("1►2");
                }
                else if (rpm < 2100)
                {
                    gear.setText("1");
                }
            }
            else if (second.containsKey(gear_speed - 5) && second.containsKey(gear_speed + 5) && rpm >= second.get(gear_speed - 5) && rpm <= second.get(gear_speed + 5))
            {

                if (rpm >= 2100)
                {
                    gear.setText("2►3");
                }
                else if (rpm <= 1200)
                {
                    gear.setText("2►1");
                }
                else if (rpm > 1200 && rpm < 2100)
                {
                    gear.setText("2");
                }
            }
            else if (third.containsKey(gear_speed - 5) && third.containsKey(gear_speed + 5) && rpm >= third.get(gear_speed - 5) && rpm <= third.get(gear_speed + 5))
            {
                if (rpm >= 2100)
                {
                    gear.setText("3►4");
                }
                else if (rpm <= 1200)
                {
                    gear.setText("3►2");
                }
                else if (rpm > 1200 && rpm < 2100)
                {
                    gear.setText("3");
                }
            }
            else if (rpm >= fourth.get(gear_speed - 5) && rpm <= fourth.get(gear_speed + 5) && fourth.containsKey(gear_speed - 5) && fourth.containsKey(gear_speed + 5))
            {
                if(rpm>=2100)
                {
                    gear.setText("4►5");
                }
                else if(rpm<=1200)
                {
                    gear.setText("4►3");
                }
                else if (rpm > 1200 && rpm < 2100)
                {
                    gear.setText("4");
                }
            }
            else if (rpm >= fifth.get(gear_speed - 5) && rpm <= fifth.get(gear_speed + 5) && fifth.containsKey(gear_speed - 5) && fifth.containsKey(gear_speed + 5))
            {
                if(rpm>=2100)
                {
                    gear.setText("5►6");
                }
                else if(rpm<=1200)
                {
                    gear.setText("5►4");
                }
                else if (rpm > 1200 && rpm < 2100)
                {
                    gear.setText("5");
                }
            }
            else if (rpm >= sixth.get(gear_speed - 5) && rpm <= sixth.get(gear_speed + 5) && sixth.containsKey(gear_speed - 5) && sixth.containsKey(gear_speed + 5))
            {
                if (rpm <= 1200)
                {
                    gear.setText("6►5");
                }
                else if (rpm > 1200)
                {
                    gear.setText("6");
                }
            }
            else
            {
                gear.setText("---");
            }
        }
        else
        {
            gear.setText("---");
        }

        /*
        //find the correct speed and rpm pair
        if(gear_speed>=10) {

            if (first.containsKey(gear_speed - 5) && first.containsKey(gear_speed + 5))
            {
                if(rpm >= first.get(gear_speed - 5) && rpm <= first.get(gear_speed + 5))
                {
                    if (rpm >= 2100)
                    {
                        gear.setText("1►2");
                    }
                    else if (rpm < 2100)
                    {
                        gear.setText("1");
                    }
                }
            }
            else if (second.containsKey(gear_speed - 5) && second.containsKey(gear_speed + 5))
            {
                if (rpm >= second.get(gear_speed - 5) && rpm <= second.get(gear_speed + 5))
                {
                    if (rpm >= 2100)
                    {
                        gear.setText("2►3");
                    }
                    else if (rpm <= 1200)
                    {
                        gear.setText("2►1");
                    }
                    else if (rpm > 1200 && rpm < 2100)
                    {
                        gear.setText("2");
                    }
                }
            }
            else if (third.containsKey(gear_speed - 5) && third.containsKey(gear_speed + 5))
            {
                if (rpm >= third.get(gear_speed - 5) && rpm <= third.get(gear_speed + 5))
                {
                    if (rpm >= 2100)
                    {
                        gear.setText("3►4");
                    }
                    else if (rpm <= 1200)
                    {
                        gear.setText("3►2");
                    }
                    else if (rpm > 1200 && rpm < 2100)
                    {
                        gear.setText("3");
                    }
                }
            }
            else if (rpm >= fourth.get(gear_speed - 5) && rpm <= fourth.get(gear_speed + 5))
            {
                if (fourth.containsKey(gear_speed - 5) && fourth.containsKey(gear_speed + 5))
                {
                    if(rpm>=2100)
                    {
                        gear.setText("4►5");
                    }
                    else if(rpm<=1200)
                    {
                        gear.setText("4►3");
                    }
                    else if (rpm > 1200 && rpm < 2100)
                    {
                        gear.setText("4");
                    }
                }
            }
            else if (rpm >= fifth.get(gear_speed - 5) && rpm <= fifth.get(gear_speed + 5))
            {
                if (fifth.containsKey(gear_speed - 5) && fifth.containsKey(gear_speed + 5))
                {
                    if(rpm>=2100)
                    {
                        gear.setText("5►6");
                    }
                    else if(rpm<=1200)
                    {
                        gear.setText("5►4");
                    }
                    else if (rpm > 1200 && rpm < 2100)
                    {
                        gear.setText("5");
                    }
                }
            }
            else if (rpm >= sixth.get(gear_speed - 5) && rpm <= sixth.get(gear_speed + 5))
            {
                if (fifth.containsKey(gear_speed - 5) && fifth.containsKey(gear_speed + 5))
                {
                    if (rpm <= 1200)
                    {
                        gear.setText("6►5");
                    }
                    else if (rpm > 1200)
                    {
                        gear.setText("6");
                    }
                }
            }
            else
            {
                gear.setText("---");
            }
        }
        else
        {
            gear.setText("---");
        }

         */

        //if advice is active
        if(pref.getString("advice", null).equals("active"))
        {
            //check if over 2100 rpm for more than a minute
            if (rpm >= 2100)
            {
                if (gear_currentTime == 0)
                {
                    gear_currentTime = Calendar.getInstance().getTimeInMillis();
                    //advice.append("Time 1 " + gear_currentTime + "\n");
                }
                else if (gear_currentTime != 0)
                {
                    long gear_time2 = Calendar.getInstance().getTimeInMillis();
                    //advice.append("Time 2 " + gear_time2 + "\n");

                    //check time difference
                    if ((gear_time2 - gear_currentTime) / 60000 >= 1)
                    {
                        //Offer advice and reset time
                        advice.append("Check gear display\n");
                        gear_currentTime = 0;
                    }
                }
            }
            else
            {
                gear_currentTime = 0;
            }
        }
    }

    //getting coolant temperature
    private void getCoolTEMP() throws IOException, InterruptedException {
        EngineCoolantTemperatureCommand coolantTempCommand = new EngineCoolantTemperatureCommand();
        try {
            //get coolant temp
            coolantTempCommand.run(socket.getInputStream(), socket.getOutputStream());
            int celsius =  (int) coolantTempCommand.getKelvin();
            //transform from kelvin
            celsius = celsius - 273;
            //update TextView
            temp.setText(celsius + "°C");

            //if advice feature is turned on
            if(pref.getString("advice", null).equals("active"))
            {
                //Advice on when it is safe to start driving
                if (celsius < 25 && temp_advice == 0)
                {
                    advice.append("Coolant Temperature is too low.");
                    temp_advice = 1;
                }
                else if (celsius >= 25 && temp_advice != 2)
                {
                    advice.append("The Engine is ready.");
                    temp_advice = 2;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (UnableToConnectException e)
        {

        }

    }

    //getting the Mass Air Flow value
    private void getMAF() throws IOException, InterruptedException
    {
    MassAirFlowCommand engineMAFCommand = new MassAirFlowCommand();
    try
    {
        engineMAFCommand.run(socket.getInputStream(), socket.getOutputStream());

        String maf;
        maf = engineMAFCommand.getFormattedResult();

        maf_view.setText(maf);

    } catch (IOException e) {
        e.printStackTrace();
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    }

    //getting the Oil Temperature value
    private void getOilTemp() throws IOException, InterruptedException {
        OilTempCommand engineOilTempCommand = new OilTempCommand();
        if(engineOilTempCommand==null)
        {
            oil_temp_view.setText("Not Available first");
        }
        else
        {
            try {


                engineOilTempCommand.run(socket.getInputStream(), socket.getOutputStream());

                if(engineOilTempCommand==null)
                {
                    oil_temp_view.setText("Not Available second");
                }
                else
                {
                    String oil_temp;
                    oil_temp = engineOilTempCommand.getFormattedResult();

                    oil_temp_view.setText(oil_temp);
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //getting the Throttle position value
    private void getThrottle() throws IOException, InterruptedException {
        ThrottlePositionCommand engineThrottleCommand = new ThrottlePositionCommand();
        try {

            engineThrottleCommand.run(socket.getInputStream(), socket.getOutputStream());

            String throttle;
            throttle = engineThrottleCommand.getFormattedResult();

            throttle_view.setText(throttle);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (UnableToConnectException e)
        {

        }

    }

    //getting the Air/Fuel ratio value
    private void getAirFuel() throws IOException, InterruptedException {
        AirFuelRatioCommand engineAirFuelCommand = new AirFuelRatioCommand();
        try {

            engineAirFuelCommand.run(socket.getInputStream(), socket.getOutputStream());

            String air_fuel;
            air_fuel = engineAirFuelCommand.getFormattedResult();

            air_fuel_view.setText(air_fuel);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (UnableToConnectException e)
        {

        }

    }

    //getting the Barometric Pressure value
    private void getBarometric() throws IOException, InterruptedException {
        BarometricPressureCommand engineBarometricCommand = new BarometricPressureCommand();
        try {

            engineBarometricCommand.run(socket.getInputStream(), socket.getOutputStream());

            String barometric;
            barometric = engineBarometricCommand.getFormattedResult();

            barometric_view.setText(barometric);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (UnableToConnectException e)
        {

        }

    }

    //getting the Intake Manifold Pressure value
    private void getIntakePress() throws IOException, InterruptedException {
        IntakeManifoldPressureCommand engineIntakeCommand = new IntakeManifoldPressureCommand();
        try {

            engineIntakeCommand.run(socket.getInputStream(), socket.getOutputStream());

            String intake;
            intake = engineIntakeCommand.getFormattedResult();

            intake_view.setText(intake);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (UnableToConnectException e)
        {

        }

    }



    //toast message function
    private void showToast(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}


