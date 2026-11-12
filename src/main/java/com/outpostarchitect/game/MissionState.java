package com.outpostarchitect.game;

import java.util.ArrayList;
import java.util.List;

public class MissionState {
    public int day = 1;
    public int power = 100;
    public int food = 100;
    public int crew = 100;
    public int spareParts = 2;
    
    // System statuses
    public boolean greenhouseOperational = true;
    public boolean waterRecyclerOperational = true;
    public boolean solarArrayOperational = true;

    public List<String> decisionHistory = new ArrayList<>();

    public void logDecision(String decision) {
        decisionHistory.add("Day " + day + ": " + decision);
    }
}