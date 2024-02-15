package mcc.client.gui;

import android.app.Activity;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import jade.android.RuntimeServiceBinder;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;


public class WorkerStartActivity extends Activity {

    private RuntimeServiceBinder jadeBinder;
    private ServiceConnection serviceConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.worker_start);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String host = bundle.getString("HOST");
        String port = bundle.getString("PORT");

        TextView textView = findViewById(R.id.host_and_port_msg);
        textView.setText("Connected to " + host + ":" + port);

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String containerName = jadeBinder.getContainerHandler().getAgentContainer().getName();
                Log.i("T",String.valueOf(containerName));
                try {
                    AgentController agent = jadeBinder.getContainerHandler().getAgentContainer().getAgent(containerName);
                    agent.start();
                } catch (ControllerException e) {
                    throw new RuntimeException(e);
                }

                startActivity(new Intent(WorkerStartActivity.this, WorkerRunActivity.class));
            }
        });
    }
}