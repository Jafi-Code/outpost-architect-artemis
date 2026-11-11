package com.outpostarchitect.game;

import org.springframework.stereotype.Service;

@Service
public class GameService {

    // Harder balance
    private static final int BASE_SOLAR_AT_FULL_SUN   = 35;
    private static final int LIFE_SUPPORT_DRAW        = 30;
    private static final int LOW_POWER_THRESHOLD      = 20;
    private static final int LOW_HEAT_THRESHOLD       = 30;
    private static final int LOW_CROPS_THRESHOLD      = 20;
    private static final int LOW_POWER_HEAT_PENALTY   = 5;
    private static final int LOW_HEAT_CROP_PENALTY    = 5;
    private static final int LOW_CROPS_CREW_PENALTY   = 3;
    private static final int RADIATION_CREW_PENALTY   = 1;

    // Deadlier shadow
    private static final int LONG_SHADOW_START_DAY   = 7;
    private static final int LONG_SHADOW_END_DAY     = 14;
    private static final double LONG_SHADOW_FACTOR   = 0.30;

    public GameState advanceDay(GameState state) {
        if (state.getStatus() != GameStatus.IN_PROGRESS) {
            return state;
        }

        applyScheduledEvents(state);
        applySolarAndPower(state);
        
        // Apply player actions
        if (state.isBoostHeatingThisTurn()) {
            if (state.getPower() >= 10) {
                state.setPower(state.getPower() - 10);
                state.setHeat(state.getHeat() + 15);
                state.log("Action: Diverted 10 power to heating systems.");
            } else {
                state.log("Action failed: Not enough power to boost heating.");
            }
        }

        applyCascade(state);
        applyRadiationExposure(state);

        state.setDay(state.getDay() + 1);
        
        // Reset actions for next turn
        state.setConservePowerThisTurn(false);
        state.setBoostHeatingThisTurn(false);

        checkEndConditions(state);
        return state;
    }

    private void applyScheduledEvents(GameState s) {
        if (s.getDay() == LONG_SHADOW_START_DAY) {
            s.setIllumination(s.getBaseIllumination() * LONG_SHADOW_FACTOR);
            s.log("ALERT: Extended low-illumination period detected. "
                    + "LROC data confirms your ridge has entered shadow. "
                    + "Solar output reduced.");
        }
        if (s.getDay() == LONG_SHADOW_END_DAY + 1) {
            s.setIllumination(s.getBaseIllumination());
            s.log("Illumination restored. Solar generation returning to nominal.");
        }
    }

    private void applySolarAndPower(GameState s) {
        int solar = (int) Math.round(BASE_SOLAR_AT_FULL_SUN * s.getIllumination());
        
        int draw = LIFE_SUPPORT_DRAW;
        if (s.isConservePowerThisTurn()) {
            draw -= 10;
            s.log("Action: Conserved power (Life support draw reduced to " + draw + ").");
        }

        s.setPower(s.getPower() + solar - draw);
        s.log("Solar generation: +" + solar + ". Life support draw: -" + draw + ".");
    }

    private void applyCascade(GameState s) {
        if (s.getPower() < LOW_POWER_THRESHOLD) {
            s.setHeat(s.getHeat() - LOW_POWER_HEAT_PENALTY);
            s.log("Power critically low — heating systems failing.");
        }
        if (s.getHeat() < LOW_HEAT_THRESHOLD) {
            s.setCrops(s.getCrops() - LOW_HEAT_CROP_PENALTY);
            s.log("Habitat temperature low — crop growth has stalled.");
        }
        if (s.getCrops() < LOW_CROPS_THRESHOLD) {
            s.setCrew(s.getCrew() - LOW_CROPS_CREW_PENALTY);
            s.log("Food reserves low — crew health declining.");
        }
    }

    private void applyRadiationExposure(GameState s) {
        int exposure = RADIATION_CREW_PENALTY * (3 - s.getShieldingLevel());
        if (exposure > 0) {
            s.setCrew(s.getCrew() - exposure);
            s.log("Radiation exposure: crew health -" + exposure + ".");
        }
    }

    private void checkEndConditions(GameState s) {
        if (s.getCrew() <= 0) {
            s.setStatus(GameStatus.DEFEAT);
            s.log("MISSION FAILED — crew health reached zero.");
            return;
        }
        if (s.getDay() > GameState.TOTAL_DAYS) {
            s.setStatus(GameStatus.VICTORY);
            s.log("MISSION COMPLETE — crew survived 30 days. Outstanding work, Junior Astronaut.");
        }
    }
}