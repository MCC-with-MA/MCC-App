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
import android.widget.ImageButton;

import jade.android.AgentContainerHandler;
import jade.android.AgentHandler;
import jade.android.RuntimeCallback;
import jade.android.RuntimeService;
import jade.android.RuntimeServiceBinder;
import jade.core.Profile;
import jade.core.ProfileImpl;
import mcc.client.agent.AndroidMobileAgent;


public class HostActivity extends Activity {

    private RuntimeServiceBinder jadeBinder;
    private ServiceConnection serviceConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_create_task);

        Button createTaskButton = findViewById(R.id.createTaskButton);
        createTaskButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startContainer();
                startActivity(new Intent(HostActivity.this, HostStartActivity.class));

            }
        });

        ImageButton backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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

    private void startContainer() {
        Log.i("T", "AndroidMobilityActivity - Starting container ...");

        Profile p = new ProfileImpl("192.168.8.109", 1099, null, true);
        jadeBinder.createAgentContainer(p, new RuntimeCallback<AgentContainerHandler>   () {
            @Override
            public void onSuccess(AgentContainerHandler handler) {
                Log.i("T", "AndroidMobilityActivity - Container successfully created");
                startMobileAgent(handler);
            }

            @Override
            public void onFailure(Throwable th) {
                Log.w("T", "AndroidMobilityActivity - Error creating container", th);
            }
        });
    }

    private void startMobileAgent(AgentContainerHandler handler) {
        handler.createNewAgent("m", AndroidMobileAgent.class.getName(), null, new RuntimeCallback<AgentHandler>() {
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