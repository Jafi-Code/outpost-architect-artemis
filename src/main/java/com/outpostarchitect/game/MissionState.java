package com.outpostarchitect.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MissionState {
    public int day = 1;
    
    // Extreme Scarcity (The Frostpunk Principle)
    public int power = 45; 
    public int food = 40;
    public int crew = 90;
    public int morale = 70; 
    public int spareParts = 1; // Only 1 for the whole game!
    
    // Persistent Ripple Effect Flags
    public Set<String> flags = new HashSet<>();

    public List<String> decisionHistory = new ArrayList<>();

    public void logDecision(String decision) {
        decisionHistory.add("Day " + day + ": " + decision);
    }
}