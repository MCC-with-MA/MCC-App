package mcc.client.agent;

import java.util.ArrayList;

import jade.core.ContainerID;

public interface MainAgentInterface {
    ArrayList<ContainerID> getAvailableContainers();
}
