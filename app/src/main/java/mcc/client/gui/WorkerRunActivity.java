package mcc.client.gui;

import static jade.core.MicroRuntime.getAgent;

import android.app.Activity;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import jade.android.RuntimeServiceBinder;
import jade.core.AID;
import jade.core.ContainerID;
import jade.lang.acl.ACLMessage;
import jade.wrapper.ControllerException;
import mcc.client.agent.MainInterface;
import mcc.client.agent.SenderInterface;


public class WorkerRunActivity extends Activity {

    private RuntimeServiceBinder jadeBinder;
    private ServiceConnection serviceConnection;
    private CountDownTimer timer;
    private TextView remainingTimeTextView;
    private MainInterface mainInterface;
    private SenderInterface senderInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.worker_run);

        remainingTimeTextView = findViewById(R.id.worker_remainingTime);

        // Start a countdown timer for 2 minutes
        timer = new CountDownTimer(2 * 60 * 1000, 1000) {
            long startTime = System.currentTimeMillis();

            public void onTick(long millisUntilFinished) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                long minutes = elapsedTime / (60 * 1000);
                long seconds = (elapsedTime % (60 * 1000)) / 1000;
                remainingTimeTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            }
            public void onFinish() {
                // Go back to the dashboard activity
                startActivity(new Intent(WorkerRunActivity.this, Dashboard.class));
                finish();
            }
        }.start();

        Button terminateButton = findViewById(R.id.terminateButton);
        terminateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showPopup(terminateButton);
            }
        });
    }

    private void showPopup(View anchorView) {
        // Inflate the popup layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.terminate_popup, null);

        // Create the popup window
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Set up the animation
        Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.popup_anim);
        popupView.startAnimation(fadeIn);

        // Show the popup at the specified location
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        Button yesBtn = popupView.findViewById(R.id.yesbtn);
        yesBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    mainInterface = getAgent("m")
                            .getO2AInterface(MainInterface.class);
                } catch (ControllerException e) {
                    Log.i("T", "AndroidMobilityActivity - Error connecting to MainInterface");
                    throw new RuntimeException(e);
                }
                ArrayList<ContainerID> availableContainers = mainInterface.getAvailableContainers();
                Log.i("T", jadeBinder.getContainerHandler().getAgentContainer().getName());


                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                for (ContainerID containerId : availableContainers) {
                    String ID =  "receiver-"+String.valueOf(containerId.getName())+'@'+jadeBinder.getContainerHandler().getAgentContainer().getName();
                    Log.i("T", ID);
                    AID receiver = new AID(ID);
                    Log.i("T", receiver.getName());
                    msg.addReceiver(receiver);
                }
                msg.setContent("Request to Continue Task");

                try {
                    senderInterface = getAgent("sender")
                            .getO2AInterface(SenderInterface.class);
                } catch (
                        ControllerException e) {
                    Log.i("T", "AndroidMobilityActivity - Error connecting to MainInterface");
                    throw new RuntimeException(e);
                }
                senderInterface.sendMessage(msg);
                startActivity(new Intent(WorkerRunActivity.this, Dashboard.class));
                popupWindow.dismiss();
            }
        });

        Button noBtn = popupView.findViewById(R.id.nobtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }
}