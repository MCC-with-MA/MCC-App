package mcc.client.agent;

import java.util.ArrayList;

import jade.core.ContainerID;
import jade.lang.acl.ACLMessage;

public interface SenderInterface {
    void sendMessage(ACLMessage message);
}
