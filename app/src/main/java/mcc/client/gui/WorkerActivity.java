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
import android.widget.EditText;
import android.widget.ImageButton;

import jade.android.AgentContainerHandler;
import jade.android.AgentHandler;
import jade.android.AndroidHelper;
import jade.android.RuntimeCallback;
import jade.android.RuntimeService;
import jade.android.RuntimeServiceBinder;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import mcc.client.agent.MainAgent;
import mcc.client.agent.ReceiverAgent;
import mcc.client.agent.SenderAgent;

public class WorkerActivity extends Activity {

    private RuntimeServiceBinder jadeBinder;
    private ServiceConnection serviceConnection;

    private String host;
    private String port;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.worker);

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(WorkerActivity.this, WorkerStartActivity.class);

                EditText hostEditText = findViewById(R.id.hostEditText);
                host = hostEditText.getText().toString();

                EditText portEditText = findViewById(R.id.portEditText);
                port = portEditText.getText().toString();

                startContainer(host, port);


                intent.putExtra("HOST", host);
                intent.putExtra("PORT", port);

                startActivity(intent);
            }
        });

        Log.i("T", "AndroidMobilityActivity - onCreate()");

        if (jadeBinder == null) {
            Log.i("T", "AndroidMobilityActivity - JADE binder null. Initialize it");
            serviceConnection = new ServiceConnection() {
                public void onServiceConnected(ComponentName className, IBinder service) {
                    jadeBinder = (RuntimeServiceBinder) service;
                    Log.i("T", "AndroidMobilityActivity - JADE binder initialized");
                };
                public void onServiceDisconnected(ComponentName className) {
                    jadeBinder = null;
                    Log.i("T", "AndroidMobilityActivity - JADE binder cleared");
                }
            };
            bindService(new Intent(this, RuntimeService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void startContainer(String host, String port) {
        Log.i("T", "AndroidMobilityActivity - Starting container ...");

        int portInt = Integer.parseInt(port);
        Profile p = new ProfileImpl(host, portInt, null, false);

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
                try {
                    startMainAgent(handler, "m-"+jadeBinder.getContainerHandler().getAgentContainer().getContainerName(), MainAgent.class.getName());
                    startSenderAgent(handler, "sender-"+jadeBinder.getContainerHandler().getAgentContainer().getContainerName(), SenderAgent.class.getName());
                    startReceiverAgent(handler, "receiver-"+jadeBinder.getContainerHandler().getAgentContainer().getContainerName(), ReceiverAgent.class.getName());
                } catch (ControllerException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Throwable th) {
                Log.w("T", "AndroidMobilityActivity - Error creating container", th);
            }
        });
    }

    private void startMainAgent(AgentContainerHandler handler, String name, String agentClass) {
        handler.createNewAgent(name, agentClass, null, new RuntimeCallback<AgentHandler>() {
            @Override
            public void onSuccess(AgentHandler agent) {
                Log.i("T", "AndroidMobilityActivity - Mobile agent (" + name + "/" + agentClass + ") successfully created");
                try {
                    Log.i("T", "AndroidMobilityActivity - Mobile agent " + agent.getAgentController().getName());
                } catch (StaleProxyException e) {
                    throw new RuntimeException(e);
                }
                agent.start(new RuntimeCallback<Void>() {
                    @Override
                    public void onSuccess(Void arg0) {
                        Log.i("T", "AndroidMobilityActivity - Mobile agent (" + name + "/" + agentClass + ") successfully started");
                    }

                    @Override
                    public void onFailure(Throwable th) {
                        Log.w("T", "AndroidMobilityActivity - Error starting mobile agent (" + name + "/" + agentClass + ")", th);
                    }
                });
            }

            @Override
            public void onFailure(Throwable th) {
                Log.w("T", "AndroidMobilityActivity - Error creating mobile agent (m/mcc.client.agent.MainAgent)", th);
            }
        });
    }

    private void startSenderAgent(AgentContainerHandler handler, String name, String agentClass) {
        handler.createNewAgent(name, agentClass, null, new RuntimeCallback<AgentHandler>() {

            @Override
            public void onSuccess(AgentHandler agent) {
                Log.i("T", "AndroidMobilityActivity - Mobile agent (" + name + "/" + agentClass + ") successfully created");
                try {
                    Log.i("T", "AndroidMobilityActivity - Mobile agent " + agent.getAgentController().getName());
                } catch (StaleProxyException e) {
                    throw new RuntimeException(e);
                }
                agent.start(new RuntimeCallback<Void>() {
                    @Override
                    public void onSuccess(Void arg0) {
                        Log.i("T", "AndroidMobilityActivity - Mobile agent (" + name + "/" + agentClass + ") successfully started");
                    }

                    @Override
                    public void onFailure(Throwable th) {
                        Log.w("T", "AndroidMobilityActivity - Error starting mobile agent (" + name + "/" + agentClass + ")", th);
                    }
                });
            }

            @Override
            public void onFailure(Throwable th) {
                Log.w("T", "AndroidMobilityActivity - Error creating mobile agent (" + name + "/" + agentClass + ")", th);
            }
        });

    }

    private void startReceiverAgent(AgentContainerHandler handler, String name, String agentClass) {
        handler.createNewAgent(name, agentClass, null, new RuntimeCallback<AgentHandler>() {

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