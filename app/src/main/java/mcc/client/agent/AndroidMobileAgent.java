package mcc.client.agent;

import jade.core.Agent;
import jade.core.AgentContainer;
import jade.core.ContainerID;
import jade.core.Location;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class AndroidMobileAgent extends Agent {
    private static final long serialVersionUID = 877464243646L;

    private transient Logger myLogger = Logger.getJADELogger(getClass().getName());
    private Location home;

    @Override
    public void setup() {
        home = here();

//        addBehaviour(new TickerBehaviour(this, 10000) {
//            private static final long serialVersionUID = 87575342545L;
//
//            protected void onTick() {
//                myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Location = "+here().getName()+", tick = "+getTickCount());
//            }
//        });

        addBehaviour(new CyclicBehaviour(){
            private static final long serialVersionUID = 435357658778L;

            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Move request received");
                    Location dest = null;
                    if (here().equals(home)) {
                        // I'm at home --> move to the Main Container
                        dest = new ContainerID(AgentContainer.MAIN_CONTAINER_NAME, null);
                    }
                    else {
                        // I'm elsewhere --> go home
                        dest = home;
                    }
                    myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Moving to "+dest.getName());
                    doMove(dest);
                }
                else {
                    block();
                }
            }
        });
    }

    @Override
    public void beforeMove() {
        myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Leaving location = "+here().getName()+" ...");
    }

    @Override
    public void afterMove() {
        // Restore the logger
        myLogger = Logger.getJADELogger(getClass().getName());
        myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Just arrived in location = "+here().getName());
    }
}