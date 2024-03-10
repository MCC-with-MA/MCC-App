package mcc.client.agent;

import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.Location;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class SenderAgent extends Agent implements SenderInterface{

    private transient Logger myLogger = Logger.getJADELogger(getClass().getName());

    @Override
    public void setup() {
        myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Running at "+here().getName()+" ...");
    }

    public void sendMessage(ACLMessage message){
        send(message);
    }


}