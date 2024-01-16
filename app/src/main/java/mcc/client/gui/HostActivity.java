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
import jade.android.AndroidHelper;
import jade.android.RuntimeCallback;
import jade.android.RuntimeService;
import jade.android.RuntimeServiceBinder;
import jade.core.Profile;
import jade.core.ProfileImpl;
import mcc.client.agent.AndroidMobileAgent;
import mcc.client.agent.MainAgent;


public class HostActivity extends Activity {

    private RuntimeServiceBinder jadeBinder;
    private ServiceConnection serviceConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host);

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

//        if (AndroidHelper.isEmulator()) {
//            // Emulator: this is needed to work with emulated devices
//            p.setParameter(Profile.LOCAL_HOST, AndroidHelper.LOOPBACK);
//        } else {
//            p.setParameter(Profile.LOCAL_HOST,
//                    AndroidHelper.getLocalIPAddress());
//        }
//        // Emulator: this is not really needed on a real device
//        p.setParameter(Profile.LOCAL_PORT, "2000");

        jadeBinder.createAgentContainer(p, new RuntimeCallback<AgentContainerHandler>   () {
            @Override
            public void onSuccess(AgentContainerHandler handler) {
                Log.i("T", "AndroidMobilityActivity - Container successfully created");
                startMainAgent(handler);
            }

            @Override
            public void onFailure(Throwable th) {
                Log.w("T", "AndroidMobilityActivity - Error creating container", th);
            }
        });
    }

    private void startMainAgent(AgentContainerHandler handler) {
        handler.createNewAgent("m", MainAgent.class.getName(), null, new RuntimeCallback<AgentHandler>() {
            @Override
            public void onSuccess(AgentHandler agent) {
                Log.i("T", "AndroidMobilityActivity - Mobile agent (m/mcc.client.agent.MainAgent) successfully created");
                agent.start(new RuntimeCallback<Void>(){
                    @Override
                    public void onSuccess(Void arg0) {
                        Log.i("T", "AndroidMobilityActivity - Mobile agent (m/mcc.client.agent.MainAgent) successfully started");
                    }

                    @Override
                    public void onFailure(Throwable th) {
                        Log.w("T", "AndroidMobilityActivity - Error starting mobile agent (m/mcc.client.agent.MainAgent)", th);
                    }
                });
            }

            @Override
            public void onFailure(Throwable th) {
                Log.w("T", "AndroidMobilityActivity - Error creating mobile agent (m/mcc.client.agent.MainAgent)", th);
            }
        });
    }
}