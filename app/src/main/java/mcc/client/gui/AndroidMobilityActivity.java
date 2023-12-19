package mcc.client.gui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import mcc.client.agent.AndroidMobileAgent;
import jade.android.AgentContainerHandler;
import jade.android.AgentHandler;
import jade.android.AndroidHelper;
import jade.android.RuntimeCallback;
import jade.android.RuntimeService;
import jade.android.RuntimeServiceBinder;
import jade.core.Profile;
import jade.core.ProfileImpl;


public class AndroidMobilityActivity extends Activity {

    private RuntimeServiceBinder jadeBinder;
    private ServiceConnection serviceConnection;

    private TextView connectionStatus;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        connectionStatus = (TextView) this.findViewById(R.id.connection_status);

        Log.i("T", "AndroidMobilityActivity - onCreate()");

        if (jadeBinder == null) {
            Log.i("T", "AndroidMobilityActivity - JADE binder null. Initialize it");
            serviceConnection = new ServiceConnection() {
                public void onServiceConnected(ComponentName className, IBinder service) {
                    jadeBinder = (RuntimeServiceBinder) service;
                    Log.i("T", "AndroidMobilityActivity - JADE binder initialized");
                    if (jadeBinder.getContainerHandler() == null) {
                        // Container is not yet there --> Start it
                        startContainer();
                    }
//                    else {
//                        // Container is already running
//                        updateConnectionStatus("Connected");
//                    }
                };

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

        Profile p = new ProfileImpl("192.168.8.109", 1099, null, false);

        if (AndroidHelper.isEmulator()) {
            // Emulator: this is needed to work with emulated devices
            p.setParameter(Profile.LOCAL_HOST, AndroidHelper.LOOPBACK);
        } else {
            p.setParameter(Profile.LOCAL_HOST,
                    AndroidHelper.getLocalIPAddress());
        }
        // Emulator: this is not really needed on a real device
//        p.setParameter(Profile.LOCAL_PORT, "1099");


        jadeBinder.createAgentContainer(p, new RuntimeCallback<AgentContainerHandler>   () {
            @Override
            public void onSuccess(AgentContainerHandler handler) {
                Log.i("T", "AndroidMobilityActivity - Container successfully created");
//                updateConnectionStatus("Connected !!!");
                startMobileAgent(handler);
            }

            @Override
            public void onFailure(Throwable th) {
                Log.w("T", "AndroidMobilityActivity - Error creating container", th);
//                updateConnectionStatus("Connection error");
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


    private void updateConnectionStatus(final String status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectionStatus.setText(status);
            }
        });
    }
}