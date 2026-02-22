package de.fafasplugin;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ModerationServiceTest {

    @Test
    void blocksConfiguredWords() {
        assertTrue(ModerationService.containsBlockedWordStatic("du bist ein noob"));
        assertTrue(ModerationService.containsBlockedWordStatic("IDIOT"));
    }

    @Test
    void allowsNeutralText() {
        assertFalse(ModerationService.containsBlockedWordStatic("hallo zusammen"));
    }
}
