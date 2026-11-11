package com.outpostarchitect.game;

import java.util.ArrayList;
import java.util.List;

public class GameState {

    public static final int TOTAL_DAYS = 30;

    private int power = 100;
    private int heat  = 80;
    private int crops = 70;
    private int crew  = 100;

    private int day = 1;
    private double illumination;
    private final double baseIllumination;
    private int shieldingLevel = 1;
    private GameStatus status = GameStatus.IN_PROGRESS;
    private boolean conservePowerThisTurn = false;
    private boolean boostHeatingThisTurn = false;

    private final List<String> missionLog = new ArrayList<>();

    public GameState(String siteName, double baseIllumination) {
        this.baseIllumination = baseIllumination;
        this.illumination = baseIllumination;
        log("Mission Start — Site: " + siteName
                + " (LROC illumination: " + Math.round(baseIllumination * 100) + "%)");
        log("Welcome, Junior Astronaut. Your crew of 4 is counting on you.");
    }

    public void log(String entry) {
        missionLog.add("Day " + day + ": " + entry);
    }

    public int getPower() { return power; }
    public void setPower(int v) { this.power = clamp(v); }

    public int getHeat() { return heat; }
    public void setHeat(int v) { this.heat = clamp(v); }

    public int getCrops() { return crops; }
    public void setCrops(int v) { this.crops = clamp(v); }

    public int getCrew() { return crew; }
    public void setCrew(int v) { this.crew = clamp(v); }

    public int getDay() { return day; }
    public void setDay(int v) { this.day = v; }

    public double getIllumination() { return illumination; }
    public void setIllumination(double v) { this.illumination = v; }

    public double getBaseIllumination() { return baseIllumination; }

    public int getShieldingLevel() { return shieldingLevel; }
    public void setShieldingLevel(int v) { this.shieldingLevel = v; }

    public GameStatus getStatus() { return status; }
    public void setStatus(GameStatus v) { this.status = v; }

    public boolean isConservePowerThisTurn() { return conservePowerThisTurn; }
    public void setConservePowerThisTurn(boolean v) { this.conservePowerThisTurn = v; }

    public boolean isBoostHeatingThisTurn() { return boostHeatingThisTurn; }
    public void setBoostHeatingThisTurn(boolean v) { this.boostHeatingThisTurn = v; }

    public List<String> getMissionLog() { return missionLog; }

    private static int clamp(int v) { return Math.max(0, Math.min(100, v)); }
}