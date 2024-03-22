package mcc.client.agent;

import android.util.Log;

import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.Location;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class ReceiverAgent extends Agent {

    private transient Logger myLogger = Logger.getJADELogger(getClass().getName());

    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    Log.i("T", "Message Received ...");
                    Log.i("T", msg.getContent());
                }
            }
        });
    }

}
