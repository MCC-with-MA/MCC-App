package mcc.client.gui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.dashboard);

        CardView hostCard = findViewById(R.id.host_card);
        hostCard.setOnClickListener(v -> startActivity(new Intent(Dashboard.this, HostActivity.class)));

        CardView workerCard = findViewById(R.id.worker_card);
        workerCard.setOnClickListener(v -> startActivity(new Intent(Dashboard.this, WorkerActivity.class)));
    }
}