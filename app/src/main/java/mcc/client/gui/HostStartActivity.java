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

import jade.android.AgentHandler;
import jade.android.RuntimeCallback;
import jade.android.RuntimeService;
import jade.android.RuntimeServiceBinder;
import mcc.client.agent.AndroidMobileAgent;
//import mcc.client.agent.AndroidMobileInterface;


public class HostStartActivity extends Activity {

    private RuntimeServiceBinder jadeBinder;
    private ServiceConnection serviceConnection;
//    private AndroidMobileInterface androidMobileInterface;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_start);

        String host = "192.168.1.1";
        String port = "99";

        TextView textView = findViewById(R.id.TextView02);
        textView.setText("$host:$port");

        Button startTaskButton = findViewById(R.id.startTaskButton);
        startTaskButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startMobileAgent();
                startActivity(new Intent(HostStartActivity.this, HostRunActivity.class));
            }
        });

        Log.i("T", "AndroidMobilityActivity - onCreate()");

        if (jadeBinder == null) {
            Log.i("T", "AndroidMobilityActivity - JADE binder null. Initialize it");
            serviceConnection = new ServiceConnection() {
                public void onServiceConnected(ComponentName className, IBinder service) {
                    jadeBinder = (RuntimeServiceBinder) service;
                    Log.i("T", "AndroidMobilityActivity - JADE binder initialized");
                }
                public void onServiceDisconnected(ComponentName className) {
                    jadeBinder = null;
                    Log.i("T", "AndroidMobilityActivity - JADE binder cleared");
                }
            };
            bindService(new Intent(this, RuntimeService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void startMobileAgent() {
        jadeBinder.getContainerHandler().createNewAgent("m", AndroidMobileAgent.class.getName(), null, new RuntimeCallback<AgentHandler>() {
            @Override
            public void onSuccess(AgentHandler agent) {
                Log.i("T", "AndroidMobilityActivity - Mobile agent successfully created");
                agent.start(new RuntimeCallback<Void>(){
                    @Override
                    public void onSuccess(Void arg0) {
                        Log.i("T", "AndroidMobilityActivity - Mobile agent successfully started");
                    }

                    @Override
                    public void onFailure(Throwable th) {
                        Log.w("T", "AndroidMobilityActivity - Error starting mobile agent", th);
                    }
                });
            }

            @Override
            public void onFailure(Throwable th) {
                Log.w("T", "AndroidMobilityActivity - Error creating mobile agent", th);
            }
        });
    }
}