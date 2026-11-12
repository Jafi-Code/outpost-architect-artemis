package com.outpostarchitect.game;

public class Decision {
    public String label;
    public String tradeOff;
    public String outcomeNarrative;
    
    // Effects on state
    public int powerDelta;
    public int foodDelta;
    public int crewDelta;
    public int partsDelta;
    
    public String systemToDisable; // "greenhouse", "water", or "solar"

    public Decision(String label, String tradeOff, String outcomeNarrative, 
                    int powerDelta, int foodDelta, int crewDelta, int partsDelta, String systemToDisable) {
        this.label = label;
        this.tradeOff = tradeOff;
        this.outcomeNarrative = outcomeNarrative;
        this.powerDelta = powerDelta;
        this.foodDelta = foodDelta;
        this.crewDelta = crewDelta;
        this.partsDelta = partsDelta;
        this.systemToDisable = systemToDisable;
    }
}