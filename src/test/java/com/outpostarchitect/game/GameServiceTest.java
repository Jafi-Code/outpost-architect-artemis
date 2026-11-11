package com.outpostarchitect.game;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    private final GameService service = new GameService();

    @Test
    void longShadowTriggersOnDay8() {
        GameState s = new GameState("Ridge Site A", 0.70);
        for (int i = 1; i < 8; i++) service.advanceDay(s);
        assertEquals(0.70, s.getIllumination(), 0.001);
        service.advanceDay(s);
        assertTrue(s.getIllumination() < 0.70,
                "Illumination should drop when Long Shadow begins");
    }

    @Test
    void cascadesPowerToHeat() {
        GameState s = new GameState("Test", 0.10);
        s.setPower(15);
        int heatBefore = s.getHeat();
        service.advanceDay(s);
        assertTrue(s.getHeat() < heatBefore,
                "Low power should cause heat to drop");
    }

    @Test
    void victoryOnDay31() {
        GameState s = new GameState("Ridge Site A", 0.70);
        for (int i = 0; i < 31; i++) service.advanceDay(s);
        assertEquals(GameStatus.VICTORY, s.getStatus());
    }

    @Test
    void defeatWhenCrewHitsZero() {
        GameState s = new GameState("Test", 0.10);
        s.setCrew(1);
        s.setShieldingLevel(0);
        service.advanceDay(s);
        assertEquals(GameStatus.DEFEAT, s.getStatus());
    }
}
