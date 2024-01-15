package mcc.client.gui;

import android.app.Activity;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import jade.android.RuntimeServiceBinder;


public class HostRunActivity extends Activity {

    private RuntimeServiceBinder jadeBinder;
    private ServiceConnection serviceConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_run);

        Button terminateButton = findViewById(R.id.terminateButton);
        terminateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(HostRunActivity.this, MainActivity.class));
            }
        });
    }
}