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

import java.util.ArrayList;

import jade.android.AgentHandler;
import jade.android.RuntimeCallback;
import jade.android.RuntimeService;
import jade.android.RuntimeServiceBinder;
import jade.core.ContainerID;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import mcc.client.agent.AndroidMobileAgent;
import mcc.client.agent.MainAgent;
import mcc.client.agent.MainAgentInterface;
//import mcc.client.agent.AndroidMobileInterface;


public class HostStartActivity extends Activity {

    private RuntimeServiceBinder jadeBinder;
    private ServiceConnection serviceConnection;
    static MainAgentInterface mainAgentInterface;


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
                startMobileAgent("m1", AndroidMobileAgent.class.getName());
                startMobileAgent("m2", MainAgent.class.getName());
//                try {
//                    mainAgentInterface = getAgent("m2")
//                            .getO2AInterface(MainAgentInterface.class);
//                } catch (ControllerException e) {
//                    Log.i("T", "AndroidMobilityActivity - Error connecting to MainAgentInterface");
//                    throw new RuntimeException(e);
//                }
//                ArrayList<ContainerID> availableContainers = mainAgentInterface.getAvailableContainers();
//                Log.i("T", "Available containers: "+availableContainers);
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

    private void startMobileAgent(String name, String agentClass) {
        jadeBinder.getContainerHandler().createNewAgent(name, agentClass, null, new RuntimeCallback<AgentHandler>() {
            @Override
            public void onSuccess(AgentHandler agent) {
                Log.i("T", "AndroidMobilityActivity - Mobile agent ("+name+"/"+agentClass+") successfully created");
                agent.start(new RuntimeCallback<Void>(){
                    @Override
                    public void onSuccess(Void arg0) {
                        Log.i("T", "AndroidMobilityActivity - Mobile agent ("+name+"/"+agentClass+") successfully started");
                    }

                    @Override
                    public void onFailure(Throwable th) {
                        Log.w("T", "AndroidMobilityActivity - Error starting mobile agent ("+name+"/"+agentClass+")", th);
                    }
                });
            }

            @Override
            public void onFailure(Throwable th) {
                Log.w("T", "AndroidMobilityActivity - Error creating mobile agent ("+name+"/"+agentClass+")", th);
            }
        });
    }

    public AgentController getAgent(String agentName) throws ControllerException {
        return jadeBinder.getContainerHandler().getAgentContainer().getAgent(agentName);
    }
}