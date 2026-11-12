package com.outpostarchitect.game;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CrisisService {

    public Crisis getCrisisForDay(MissionState state) {
        if (state.day == 1) return getTutorial(state);
        if (state.day == 7) return getLongShadow(state);
        if (state.day == 12) return getWaterFailure(state);
        if (state.day == 20) return getRadiationWarning(state);
        return null; 
    }

    public MissionState resolveDecision(MissionState state, Crisis crisis, Decision decision) {
        state.power = Math.max(0, Math.min(100, state.power + decision.powerDelta));
        state.food = Math.max(0, Math.min(100, state.food + decision.foodDelta));
        state.crew = Math.max(0, Math.min(100, state.crew + decision.crewDelta));
        state.morale = Math.max(0, Math.min(100, state.morale + decision.moraleDelta));
        state.spareParts += decision.partsDelta;

        if (decision.flagsToSet != null) {
            state.flags.addAll(decision.flagsToSet);
        }

        state.logDecision(decision.label);
        
        // Advance to the next crisis
        if (state.day == 1) state.day = 7;
        else if (state.day == 7) state.day = 12;
        else if (state.day == 12) state.day = 20;
        else if (state.day == 20) state.day = 31;

        return state;
    }

    // --- TUTORIAL (PRIMING THE PLAYER) ---
    private Crisis getTutorial(MissionState state) {
        return new Crisis(
            "tutorial", 1, "ARRIVAL AT ARES-01", 
            "Commander, welcome to the lunar south pole. The outpost is in rough shape. We need to decide how to spend our first 48 hours.",
            "ELENA PARK", "images/char-elena.jpeg",
            "NASA DATA · LROC: The lunar south pole is harsh. Sunlight is scarce. We must manage every watt of power.",
            List.of(
                new Decision("RUN DIAGNOSTICS", "Trade-off: -10 Power. +10 Morale.", 
                    "The crew spent the day checking systems. We lost power, but the crew feels confident knowing what's broken.", 
                    -10, 0, 0, 10, 0, List.of("diagnosticsRun")),
                new Decision("PUSH FORWARD IMMEDIATELY", "Trade-off: -15 Morale.", 
                    "You ordered the crew straight to work. Systems are online, but the crew is already exhausted and resentful.", 
                    10, 0, 0, -15, 0, null)
            )
        );
    }

    // --- CRISIS 1 ---
    private Crisis getLongShadow(MissionState state) {
        String desc = "The lunar south pole has entered an extended period of darkness. Our solar array is generating minimal power. We must decide which systems to prioritize.";
        if (state.flags.contains("diagnosticsRun")) {
            desc += " Good thing we ran diagnostics earlier—we know exactly which systems are drawing the most power.";
        }

        return new Crisis(
            "crisis_1", 7, "THE LONG SHADOW", desc,
            "ELENA PARK", "images/char-elena.jpeg",
            "NASA DATA · LROC ILLUMINATION: Lunar terrain creates dramatic light and shadow. The simulation assumes a temporary 80% drop in solar generation.",
            List.of(
                new Decision("PROTECT LIFE SUPPORT", "Trade-off: -25 Food. Greenhouse crippled.", 
                    "You diverted power to heating. The crew stayed warm, but the greenhouse lights went dark and the crops withered.", 
                    -15, -25, 0, 0, 0, List.of("greenhouseCrippled")),
                new Decision("MAINTAIN AGRICULTURE", "Trade-off: -40 Power. -10 Morale.", 
                    "You kept the greenhouse lit. The food is safe, but your battery reserves are critically depleted and the crew is shivering in the dark.", 
                    -40, 0, -5, -10, 0, null)
            )
        );
    }

    // --- CRISIS 2 (THE RIPPLE EFFECT) ---
    private Crisis getWaterFailure(MissionState state) {
        String desc = "The water recovery unit has developed a critical fault. Available recycled water is falling fast.";
        
        // RIPPLE EFFECT: Checking the flags set in Crisis 1
        if (state.flags.contains("greenhouseCrippled")) {
            desc += " Commander... our greenhouse is already crippled from the long shadow. If the water recycler breaks, we lose our food source entirely.";
        }

        return new Crisis(
            "crisis_2", 12, "ONE LOOP BROKEN", desc,
            "DR. SAMIRA HADDAD", "images/char-samira.jpeg",
            "NASA DATA · ECLSS: NASA's ISS systems recover up to 98% of water. If a loop fails, water becomes a strictly limited resource.",
            List.of(
                new Decision("REPAIR THE SYSTEM", "Trade-off: Uses LAST spare part. -10 Morale.", 
                    "You assigned Samira to repairs. It took hours and our last spare filter, but water recovery is stable again.", 
                    -10, 0, -5, -10, -1, List.of("waterFixed")),
                new Decision("CONSERVE WATER", "Trade-off: -30 Food. Permanent water rationing.", 
                    "You isolated the broken unit. We are rationing water, which means the greenhouse cannot produce food anymore.", 
                    0, -30, 0, -5, 0, List.of("waterCrippled"))
            )
        );
    }

    // --- CRISIS 3 ---
    private Crisis getRadiationWarning(MissionState state) {
        String desc = "A solar energetic particle event is approaching. Mateo is outside repairing the solar array. He can finish the repair or head to the shelter.";
        if (state.flags.contains("greenhouseCrippled") || state.flags.contains("waterCrippled")) {
            desc += " Our reserves are already paper-thin. If we abort this repair, we might not have enough power to survive the last 10 days.";
        }

        return new Crisis(
            "crisis_3", 20, "THE WINDOW TO SHELTER", desc,
            "MATEO RUIZ", "images/char-mateo.jpeg",
            "NASA DATA · CRaTER: The lunar surface is exposed to harsh radiation. Solar particle events pose severe risks.",
            List.of(
                new Decision("ABORT THE EVA", "Trade-off: -25 Power. Solar array remains damaged.", 
                    "You ordered Mateo inside. The crew is safe in the shelter, but the solar array remains damaged, limiting our power.", 
                    -25, 0, 0, 5, 0, List.of("solarCrippled")),
                new Decision("COMPLETE THE REPAIR", "Trade-off: -45 Crew Health. -20 Morale.", 
                    "Mateo finished the weld and made it inside. The array is fixed, but the crew absorbed a dangerous dose of radiation. They are furious you risked his life.", 
                    20, 0, -45, -20, 0, null)
            )
        );
    }
}