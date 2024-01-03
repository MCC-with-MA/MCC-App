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
import jade.android.AgentHandler;
import jade.android.RuntimeCallback;
import jade.android.RuntimeService;
import jade.android.RuntimeServiceBinder;
import jade.core.Profile;
import jade.core.ProfileImpl;
import mcc.client.agent.AndroidMobileAgent;


public class HostStartActivity extends Activity {

    private RuntimeServiceBinder jadeBinder;
    private ServiceConnection serviceConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_start_task);

        String host = "192.168.1.1";
        String port = "99";

        TextView textView = findViewById(R.id.TextView02);
        textView.setText("$host:$port");

        Button startTaskButton = findViewById(R.id.startTaskButton);
        startTaskButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(HostStartActivity.this, HostRunActivity.class));
            }
        });
    }
}