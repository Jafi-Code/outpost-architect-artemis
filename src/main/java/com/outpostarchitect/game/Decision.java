package com.outpostarchitect.game;

import java.util.List;

public class Decision {
    public String label;
    public String tradeOff;
    public String outcomeNarrative;
    
    // State changes
    public int powerDelta;
    public int foodDelta;
    public int crewDelta;
    public int moraleDelta;
    public int partsDelta;
    
    public List<String> flagsToSet;

    public Decision(String label, String tradeOff, String outcomeNarrative, 
                    int powerDelta, int foodDelta, int crewDelta, int moraleDelta, int partsDelta, 
                    List<String> flagsToSet) {
        this.label = label;
        this.tradeOff = tradeOff;
        this.outcomeNarrative = outcomeNarrative;
        this.powerDelta = powerDelta;
        this.foodDelta = foodDelta;
        this.crewDelta = crewDelta;
        this.moraleDelta = moraleDelta;
        this.partsDelta = partsDelta;
        this.flagsToSet = flagsToSet;
    }
}