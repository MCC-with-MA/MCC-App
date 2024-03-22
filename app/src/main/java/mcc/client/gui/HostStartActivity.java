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
import jade.core.AID;
import jade.core.ContainerID;
import jade.core.MicroRuntime;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import mcc.client.agent.AndroidMobileAgent;
import mcc.client.agent.AndroidMobileInterface;
import mcc.client.agent.MainAgent;
import mcc.client.agent.MainInterface;
//import mcc.client.agent.AndroidMobileInterface;

import jade.lang.acl.ACLMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import android.content.res.AssetManager;
import mcc.client.agent.SenderAgent;
import mcc.client.agent.SenderInterface;
import java.io.InputStream;

public class HostStartActivity extends Activity {

    private RuntimeServiceBinder jadeBinder;
    private ServiceConnection serviceConnection;
    private MainInterface mainInterface;
    private SenderInterface senderInterface;
    private AndroidMobileInterface androidMobileInterface;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_start);

        Button startTaskButton = findViewById(R.id.host_start_tasks_btn);
        startTaskButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                startTaskButton.setText("Start Tasks");
                try {
                    mainInterface = getAgent("m")
                            .getO2AInterface(MainInterface.class);
                } catch (ControllerException e) {
                    Log.i("T", "AndroidMobilityActivity - Error connecting to MainInterface");
                    throw new RuntimeException(e);
                }
                ArrayList<ContainerID> availableContainers = mainInterface.getAvailableContainers();
                Log.i("T", jadeBinder.getContainerHandler().getAgentContainer().getName());
                Log.i("T", "Available containers: "+availableContainers);

                startSenderAgent("sender", SenderAgent.class.getName());
                Context appContext = getApplicationContext();
                String javaCode = readJavaCodeFromFile(appContext.getAssets());
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                for (ContainerID containerId : availableContainers) {
                    AID receiver = new AID("receiver@"+String.valueOf(containerId));
                    msg.addReceiver(receiver);
                }
                msg.setContent(javaCode);

                try {
                    senderInterface = getAgent("m")
                            .getO2AInterface(SenderInterface.class);
                } catch (ControllerException e) {
                    Log.i("T", "AndroidMobilityActivity - Error connecting to MainInterface");
                    throw new RuntimeException(e);
                }
                senderInterface.sendMessage(msg);

                for (ContainerID containerId : availableContainers) {
                    startMobileAgent(String.valueOf(containerId), AndroidMobileAgent.class.getName());
                    Log.i("T",String.valueOf(containerId));
                }

                startTaskButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            for (ContainerID containerId : availableContainers) {
                                getAgent(String.valueOf(containerId)).getO2AInterface(AndroidMobileInterface.class).migrate(containerId);
                            }
                        } catch (ControllerException e) {
                            Log.i("T", "AndroidMobilityActivity - Error connecting to AndroidMobileInterface");
                            throw new RuntimeException(e);
                        }
                        startActivity(new Intent(HostStartActivity.this, HostRunActivity.class));
                    }
                });
            }
        });

        Log.i("T", "AndroidMobilityActivity - onCreate()");

        if (jadeBinder == null) {
            Log.i("T", "AndroidMobilityActivity - JADE binder null. Initialize it");
            serviceConnection = new ServiceConnection() {
                public void onServiceConnected(ComponentName className, IBinder service) {
                    jadeBinder = (RuntimeServiceBinder) service;
                    Log.i("T", "AndroidMobilityActivity - JADE binder initialized");
//                    String containerName = jadeBinder.getContainerHandler().getAgentContainer().getName();
//                    String address = containerName.replace("/JADE", "");
//                    Log.i("T", jadeBinder.getContainerHandler().getAgentContainer().getName());
////                String host = "192.168.1.1";
////                String port = "99";
//                    String[] sep = address.split(":");
//                    String host = sep[0];
//                    String port = sep[1];
//
//                    TextView textView = findViewById(R.id.host_and_port);
//                    textView.setText(host + " : " + port);
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
                try {
                    Log.i("T", "AndroidMobilityActivity - Mobile agent "+agent.getAgentController().getName());
                } catch (StaleProxyException e) {
                    throw new RuntimeException(e);
                }
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

    public static String readJavaCodeFromFile(AssetManager assetManager) {
        StringBuilder content = new StringBuilder();

        try {
            InputStream inputStream = assetManager.open("file.txt");
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }

            br.close();
            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    private void startSenderAgent(String name, String agentClass) {
        jadeBinder.getContainerHandler().createNewAgent(name, agentClass, null, new RuntimeCallback<AgentHandler>() {

            @Override
            public void onSuccess(AgentHandler agent) {
                Log.i("T", "AndroidMobilityActivity - Mobile agent ("+name+"/"+agentClass+") successfully created");
                try {
                    Log.i("T", "AndroidMobilityActivity - Mobile agent "+agent.getAgentController().getName());
                } catch (StaleProxyException e) {
                    throw new RuntimeException(e);
                }
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
}