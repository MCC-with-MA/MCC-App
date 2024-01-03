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
import jade.android.AgentHandler;
import jade.android.RuntimeCallback;
import jade.android.RuntimeService;
import jade.android.RuntimeServiceBinder;
import jade.core.Profile;
import jade.core.ProfileImpl;
import mcc.client.agent.AndroidMobileAgent;


public class HostRunActivity extends Activity {

    private RuntimeServiceBinder jadeBinder;
    private ServiceConnection serviceConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_executing_task);

        Button terminateButton = findViewById(R.id.terminateButton);
        terminateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(HostRunActivity.this, MainActivity.class));
            }
        });
    }
}