package com.outpostarchitect.game;

import org.springframework.stereotype.Service;

@Service
public class GameService {

    private static final int BASE_SOLAR_AT_FULL_SUN   = 30;
    private static final int LIFE_SUPPORT_DRAW        = 20;
    private static final int LOW_POWER_THRESHOLD      = 20;
    private static final int LOW_HEAT_THRESHOLD       = 30;
    private static final int LOW_CROPS_THRESHOLD      = 20;
    private static final int LOW_POWER_HEAT_PENALTY   = 5;
    private static final int LOW_HEAT_CROP_PENALTY    = 5;
    private static final int LOW_CROPS_CREW_PENALTY   = 3;
    private static final int RADIATION_CREW_PENALTY   = 1;

    private static final int LONG_SHADOW_START_DAY   = 8;
    private static final int LONG_SHADOW_END_DAY     = 12;
    private static final double LONG_SHADOW_FACTOR   = 0.55;

    public GameState advanceDay(GameState state) {
        if (state.getStatus() != GameStatus.IN_PROGRESS) {
            return state;
        }

        applyScheduledEvents(state);
        applySolarAndPower(state);
        applyCascade(state);
        applyRadiationExposure(state);

        state.setDay(state.getDay() + 1);

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
        s.setPower(s.getPower() + solar - LIFE_SUPPORT_DRAW);
        s.log("Solar generation: +" + solar + ". Life support draw: -" + LIFE_SUPPORT_DRAW + ".");
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