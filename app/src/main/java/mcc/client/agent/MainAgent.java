package mcc.client.agent;

import java.util.ArrayList;

import jade.core.Agent;
import jade.core.ContainerID;
import jade.domain.introspection.AMSSubscriber;
import jade.domain.introspection.AddedContainer;
import jade.domain.introspection.Event;
import jade.domain.introspection.IntrospectionVocabulary;
import jade.domain.introspection.RemovedContainer;
import jade.util.Logger;
import jade.util.leap.Map;

public class MainAgent extends Agent implements MainAgentInterface {

    private transient Logger myLogger = Logger.getJADELogger(getClass().getName());
    private ArrayList<ContainerID> availableContainers = new ArrayList<ContainerID>();

    /**
     * Agent initializations
     **/
    protected void setup() {
        registerO2AInterface(MainAgentInterface.class, this);

        AMSSubscriber subscriber = new AMSSubscriber(){
            @Override

            protected void installHandlers(java.util.Map handlers){
                EventHandler addedHandler = new EventHandler(){
                    public void handle(Event event){
                        AddedContainer addedContainer = (AddedContainer) event;
                        availableContainers.add(addedContainer.getContainer());
                    }
                };
                handlers.put(IntrospectionVocabulary.ADDEDCONTAINER,addedHandler);


                EventHandler removedHandler = new EventHandler(){
                    public void handle(Event event){
                        RemovedContainer removedContainer = (RemovedContainer) event;
                        ArrayList<ContainerID> temp = new ArrayList<ContainerID>(availableContainers);
                        for(ContainerID container : temp){
                            if(container.getID().equalsIgnoreCase(removedContainer.getContainer().getID()))
                                availableContainers.remove(container);
                        }
                    }
                };
                handlers.put(IntrospectionVocabulary.REMOVEDCONTAINER,removedHandler);
            }
        };
        addBehaviour(subscriber);
//        myLogger.log(Logger.INFO, "Agent "+getLocalName()+" - Total containers: "+getAvailableContainers());
    }

    public ArrayList<ContainerID> getAvailableContainers() {
        return availableContainers;
    }
}