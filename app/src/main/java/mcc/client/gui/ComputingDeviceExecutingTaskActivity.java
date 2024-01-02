package mcc.client.gui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import jade.android.RuntimeService;
import jade.android.RuntimeServiceBinder;

public class ComputingDeviceTerminateActivity extends Activity {
    private RuntimeServiceBinder jadeBinder;
    private ServiceConnection serviceConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.computing_device_terminate);


        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(ComputingDeviceStartTaskActivity.this, ComputingDeviceTerminateActivity.class));
                // setContentView(R.layout.computing_device_start_task);
            }
        });

        Log.i("T", "AndroidMobilityActivity - onCreate()");

        if (jadeBinder == null) {
            Log.i("T", "AndroidMobilityActivity - JADE binder null. Initialize it");
            serviceConnection = new ServiceConnection() {
                public void onServiceConnected(ComponentName className, IBinder service) {
                    jadeBinder = (RuntimeServiceBinder) service;
                    Log.i("T", "AndroidMobilityActivity - JADE binder initialized");
                };
                public void onServiceDisconnected(ComponentName className) {
                    jadeBinder = null;
                    Log.i("T", "AndroidMobilityActivity - JADE binder cleared");
                }
            };
            bindService(new Intent(this, RuntimeService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }
}
