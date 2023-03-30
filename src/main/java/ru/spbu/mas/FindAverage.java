package ru.spbu.mas;

import jade.lang.acl.ACLMessage;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.io.Serializable;

public class FindAverage extends TickerBehaviour {
    private final DefaultAgent agent;
    private int currentTick;

    FindAverage(DefaultAgent agent, long period) {
        super(agent, period);
        this.agent = agent;
        this.currentTick = 0;
    }

    @Override
    protected void onTick() {
        this.currentTick++;
        if (currentTick > 7) {
            this.stop((DefaultAgent) this.myAgent);
            return;
        }

        Map<String, Integer> data = new HashMap<>();
        int linksLength = ((DefaultAgent) myAgent).getLinks().size();
        for (int i = 0; i < linksLength; i++) {
            ACLMessage msgRes = myAgent.receive();
            if (msgRes == null) {
                continue;
            }
            try {
                Object receivedContent = msgRes.getContentObject();
                if (receivedContent instanceof Map) {
                    data.putAll((Map) receivedContent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (data.isEmpty() && currentTick > 1) {
            this.stop((DefaultAgent) myAgent);
            return;
        }

        processReceivedData(data, (DefaultAgent) myAgent);

        if (currentTick <= 1) {
            data.put(myAgent.getLocalName(), ((DefaultAgent) myAgent).getNumber());
        }

        List<String> linkedAgents = ((DefaultAgent) myAgent).getLinks();
        if (linkedAgents == null) {
            return;
        }

        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        for (String i : linkedAgents) {
            message.addReceiver(new AID(i, AID.ISLOCALNAME));
        }
        try {
            message.setContentObject((Serializable) data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        myAgent.send(message);
    }

    private void processReceivedData(Map<String, Integer> content, DefaultAgent agent) {
        if (content.isEmpty()) {
            return;
        }
        List<String> numProcessedAgents = agent.getProcessedAgents();
        for (Map.Entry<String, Integer> entry : new HashSet<>(content.entrySet())) {
            String key = entry.getKey();
            if (numProcessedAgents.contains(key)) {
                content.remove(key);
                continue;
            }
            agent.addToSum(entry.getValue());
            numProcessedAgents.add(key);
        }
    }

    private void stop(DefaultAgent currentAgent) {
        if (currentAgent.getName().contains("Main")) {
            System.out.println("\nComputed average value: " + currentAgent.getAverage());
        }
        currentAgent.doDelete();
        this.stop();
    }
}