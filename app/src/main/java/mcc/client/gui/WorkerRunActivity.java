package mcc.client.gui;

import android.app.Activity;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import jade.android.RuntimeServiceBinder;


public class WorkerRunActivity extends Activity {

    private RuntimeServiceBinder jadeBinder;
    private ServiceConnection serviceConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.worker_run);

        Button terminateButton = findViewById(R.id.terminateButton);
        terminateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(new Intent(WorkerRunActivity.this, MainActivity.class));

                // Inflate the layout for the pop-up window
                /*
                View popupView = LayoutInflater.from(WorkerRunActivity.this).inflate(R.layout.terminate, null);
                */

                // Create a PopupWindow
                /*
                PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        true);
                        
                 */

                // Show the pop-up window at the center of the screen
                /*popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);*/

                // Set up the dismiss listener for the pop-up window

                /*
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        // Code to be executed when the pop-up window is dismissed

                    }
                });
                */

            }
        });
    }
}