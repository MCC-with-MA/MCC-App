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

import jade.android.AgentContainerHandler;
import jade.android.RuntimeCallback;
import jade.android.RuntimeService;
import jade.android.RuntimeServiceBinder;
import jade.core.Profile;
import jade.core.ProfileImpl;


public class ComputingDeviceRunActivity extends Activity {

    private RuntimeServiceBinder jadeBinder;
    private ServiceConnection serviceConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.computing_device_executing_task);

        Button terminateButton = findViewById(R.id.terminateButton);
        terminateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(ComputingDeviceRunActivity.this, MainActivity.class));
            }
        });
    }
}