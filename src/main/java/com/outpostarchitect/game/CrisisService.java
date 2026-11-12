package com.outpostarchitect.game;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CrisisService {

    public Crisis getCrisisForDay(int day) {
        if (day == 7) return getLongShadow();
        if (day == 12) return getWaterFailure();
        if (day == 20) return getRadiationWarning();
        return null; // No crisis today
    }

    public MissionState resolveDecision(MissionState state, Crisis crisis, Decision decision) {
        // Apply effects
        state.power = Math.max(0, Math.min(100, state.power + decision.powerDelta));
        state.food = Math.max(0, Math.min(100, state.food + decision.foodDelta));
        state.crew = Math.max(0, Math.min(100, state.crew + decision.crewDelta));
        state.spareParts += decision.partsDelta;

        if (decision.systemToDisable != null) {
            if (decision.systemToDisable.equals("greenhouse")) state.greenhouseOperational = false;
            if (decision.systemToDisable.equals("water")) state.waterRecyclerOperational = false;
            if (decision.systemToDisable.equals("solar")) state.solarArrayOperational = false;
        }

        state.logDecision(decision.label);
        state.day += 5; // Advance time to the next phase

        return state;
    }

    // ==========================================
    // CRISIS DEFINITIONS (Data-driven structure)
    // ==========================================

    private Crisis getLongShadow() {
        return new Crisis(
            "crisis_1", 7, 
            "THE LONG SHADOW", 
            "The lunar south pole has entered an extended period of darkness. Our solar array is generating minimal power. We must decide which systems to prioritize.",
            "ELENA PARK", "images/char-elena.jpeg",
            "NASA DATA · LROC ILLUMINATION: Lunar terrain creates dramatic light and shadow. Some ridges receive sunlight 70% of the time, while crater floors remain in permanent shadow. The simulation assumes a temporary drop in solar generation.",
            List.of(
                new Decision("PROTECT LIFE SUPPORT", "Trade-off: Reduced food production.", 
                    "You diverted power to heating and life support. The greenhouse lights were dimmed, causing the crops to stall, but the crew stayed warm.", 
                    -10, -20, 0, 0, "greenhouse"),
                new Decision("MAINTAIN AGRICULTURE", "Trade-off: Consumes battery reserve.", 
                    "You kept the greenhouse fully lit. The food supply is safe, but your battery reserves took a massive hit keeping the lights on.", 
                    -30, 0, 0, 0, null)
            )
        );
    }

    private Crisis getWaterFailure() {
        return new Crisis(
            "crisis_2", 12, 
            "ONE LOOP BROKEN", 
            "The water recovery unit has developed a critical fault. Available recycled water is falling fast. We need a solution.",
            "DR. SAMIRA HADDAD", "images/char-samira.jpeg",
            "NASA DATA · ECLSS: NASA's ISS systems recover up to 98% of water. If a loop fails, water becomes a strictly limited resource. Repairing it requires spare parts and crew time.",
            List.of(
                new Decision("REPAIR THE SYSTEM", "Trade-off: Consumes spare parts.", 
                    "You assigned Samira to repairs. It took hours and our last spare filter, but water recovery is stable again.", 
                    -10, 0, -5, -1, null),
                new Decision("CONSERVE WATER", "Trade-off: Permanent reduction in food capacity.", 
                    "You isolated the broken unit. We are rationing water, which means the greenhouse cannot produce at full capacity anymore.", 
                    0, -15, 0, 0, "water")
            )
        );
    }

    private Crisis getRadiationWarning() {
        return new Crisis(
            "crisis_3", 20, 
            "THE WINDOW TO SHELTER", 
            "A solar energetic particle event is approaching. Mateo is outside repairing the solar array. He can finish the repair or head to the shelter.",
            "MATEO RUIZ", "images/char-mateo.jpeg",
            "NASA DATA · CRaTER: The lunar surface is exposed to harsh radiation. Solar particle events pose severe risks. The simulation treats this as a qualitative risk to crew health if they remain outside.",
            List.of(
                new Decision("ABORT THE EVA", "Trade-off: Solar array remains damaged.", 
                    "You ordered Mateo inside. The crew is safe in the shelter, but the solar array remains damaged, limiting our power.", 
                    -20, 0, 0, 0, "solar"),
                new Decision("COMPLETE THE REPAIR", "Trade-off: High risk of radiation exposure.", 
                    "Mateo finished the weld and made it inside. The array is fixed, but the crew absorbed a dangerous dose of radiation.", 
                    20, 0, -30, 0, null)
            )
        );
    }
}