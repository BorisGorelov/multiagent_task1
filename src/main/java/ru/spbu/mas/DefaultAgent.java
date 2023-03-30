package ru.spbu.mas;


import jade.core.Agent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DefaultAgent extends Agent {

    private List<String> links;
    private int number;
    private Double sum;
    String name;
    private List<String> processedAgents;

    public List<String> getLinks() {
        return links;
    }
    public int getNumber() {
        return number;
    }
    public List<String> getProcessedAgents() {
        return processedAgents;
    }

    @Override
    protected void setup() {
        name = getAID().getLocalName();
        int id = getId();
        Object[] arguments = getArguments();
        links = Arrays.asList((String[]) arguments[0]);

        Random rand = new Random();
        number = rand.nextInt(5,10);
        sum = (double) number;
        processedAgents = new ArrayList<String>() {{
            add(getLocalName());
        }};

        addBehaviour(new FindAverage(this, 100));
        System.out.println("Agent " + id + " with number " + number);
    }

    public int getId() {
        return Integer.parseInt(this.name.substring(0, 1));
    }

    public void addToSum(Integer add) {
        this.sum += add;
    }

    public double getAverage() {
        return this.sum / this.getProcessedAgents().size();
    }

}