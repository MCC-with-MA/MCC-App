package mcc.client.agent;

import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.Location;
import jade.core.behaviours.OneShotBehaviour;
import jade.util.Logger;

public class AndroidMobileAgent extends Agent {
    private static final long serialVersionUID = 877464243646L;

    private transient Logger myLogger = Logger.getJADELogger(getClass().getName());

    @Override
    public void setup() {
        myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Location = "+here().getName());

        addBehaviour(new OneShotBehaviour(){
            private static final long serialVersionUID = 435357658778L;

            @Override
            public void action() {
                myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Move request received");
                Location dest = new ContainerID("Container-1", null);
                myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Moving to "+dest.getName());
                doMove(dest);
            }
        });
    }

    @Override
    public void beforeMove() {
        myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Leaving location = "+here().getName()+" ...");
    }

    @Override
    public void afterMove() {
        myLogger = Logger.getJADELogger(getClass().getName());
        myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Just arrived in location = "+here().getName());
    }
}