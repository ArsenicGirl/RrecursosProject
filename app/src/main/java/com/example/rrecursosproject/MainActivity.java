package com.example.rrecursosproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private Activity activity;

    //Version Android
    private TextView versionAndroid;
    private int versionSDK;

    //Bateria
    private ProgressBar pbLevelBaterry;
    private TextView tvLevelBaterry;
    IntentFilter baterryFilter;

    //Conexión
    private TextView tvConexion;
    ConnectivityManager conexion;

    //Linterna
    CameraManager cameraManager;
    String cameraId;
    private Button onFlash;
    private Button offFlash;


    //File
    private EditText nameFile;

    //private ClFile clFile;

    private BluetoothAdapter btAdapter;
    private ImageButton ibStorage;
    private Button btOn;
    private Button btOff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = getApplicationContext();
        this.activity = this;
        objInit();
        onFlash.setOnClickListener(this::onLigth);
        offFlash.setOnClickListener(this::offLigth);
        baterryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(broReceiver, baterryFilter);
        ibStorage.setOnClickListener(this::saveFile);
        btOn.setOnClickListener(this::btOn);
        btOff.setOnClickListener(this::btOff);
    }

    BroadcastReceiver broReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int levelBaterry = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            pbLevelBaterry.setProgress(levelBaterry);
            tvLevelBaterry.setText("Level Battery:" + levelBaterry + "%");
        }
    };

    //Conexion
    private void checkConnection() {
        conexion = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conexion.getActiveNetworkInfo();
        boolean stateNet = networkInfo != null && networkInfo.isConnectedOrConnecting();
        if (stateNet) tvConexion.setText("State ON");
        else tvConexion.setText("State OFF");
    }


    //Versión Android
    @Override
    protected void onResume() {
        super.onResume();
        String versionSO = Build.VERSION.RELEASE;
        versionSDK = Build.VERSION.SDK_INT;
        versionAndroid.setText("Version SO:" + versionSO + " / SDK:" + versionSDK);
        checkConnection();
    }
/*
    BroadcastReceiver broadReceiv = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int levelBaterry = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            pbLevelBaterry.setProgress(levelBaterry);
            tvLevelBaterry.setText("Level Baterry: "+levelBaterry+"%");
        }
    };*/

    private void offLigth(View view) {
        try {
            cameraManager.setTorchMode(cameraId,false);
        } catch (CameraAccessException e) {
            throw new RuntimeException("En la linterna"+e);
        }
    }


    //Flash ligth metodo on
    private void onLigth(View view) {
        try {
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveFile(View view) {
        String name = nameFile.getText().toString() + ".txt";
        String dateBattery = tvLevelBaterry.getText().toString();
        ClFile clFile = new ClFile(context, this);
        clFile.saveFile(name, dateBattery);
    }


    private void BtPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED)
            if (Build.VERSION.SDK_INT >= 31) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
                return;
            }
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        if (Build.VERSION.SDK_INT >= 31) {
            btAdapter = bluetoothManager.getAdapter();
        } else {
            btAdapter = BluetoothAdapter.getDefaultAdapter();
        }
    }

    public void btOn(View view) {
        BtPermissions();
        try {
            if (btAdapter.isEnabled()) {
                Toast.makeText(context, "Bluetooth already On", Toast.LENGTH_SHORT).show();
            } else {
                //Puede que aparezca como un error independientemente de como lo escriba, pero deja ejecutar
                boolean enable = btAdapter.enable();
                Toast.makeText(context, "Bluetooth On", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.i("****ERROR BT", "bluetoothOn: " + e);
        }
    }

    public void btOff(View view) {
        BtPermissions();
        try {
            if (btAdapter.isEnabled()) {
                //Puede que aparezca como un error independientemente de como lo escriba, pero deja ejecutar
                boolean disable = btAdapter.disable();
                Toast.makeText(context, "Bluetooth Off", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Bluetooth already Off", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.i("****ERROR BT", "bluetoothOff: " + e);
        }
    }


    private void objInit(){
        this.versionAndroid =  findViewById(R.id.tvAndroidVersion);
        this.pbLevelBaterry = findViewById(R.id.pbBattery);
        this.tvLevelBaterry = findViewById(R.id.tvBattery);
        this.tvConexion = findViewById(R.id.tvConexion);
        this.onFlash = findViewById(R.id.btLightOn);
        this.offFlash = findViewById(R.id.btLightOff);
        this.nameFile = findViewById(R.id.etStorage);
        this.ibStorage         = findViewById(R.id.ibStorage);
        this.btOn   = findViewById(R.id.btnBTOn);
        this.btOff  = findViewById(R.id.btnBTOff);
    }
}