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

import jade.android.AgentContainerHandler;
import jade.android.RuntimeCallback;
import jade.android.RuntimeService;
import jade.android.RuntimeServiceBinder;
import jade.core.Profile;
import jade.core.ProfileImpl;


public class ComputingDeviceStartActivity extends Activity {

    private RuntimeServiceBinder jadeBinder;
    private ServiceConnection serviceConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.computing_device_start_task);

        String host = "192.168.1.1";
        String port = "99";

        TextView textView = findViewById(R.id.TextView01);
        textView.setText("Connected to $host:$port");

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(ComputingDeviceStartActivity.this, ComputingDeviceRunActivity.class));
            }
        });
    }
}