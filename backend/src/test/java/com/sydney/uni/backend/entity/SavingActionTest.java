package com.sydney.uni.backend.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SavingActionTest {

    @Test
    void testEnumValues() {
        SavingAction[] actions = SavingAction.values();
        assertEquals(2, actions.length);
        assertEquals(SavingAction.SAVE, actions[0]);
        assertEquals(SavingAction.UNSAVE, actions[1]);
    }

    @Test
    void testEnumValueOf() {
        SavingAction save = SavingAction.valueOf("SAVE");
        assertEquals(SavingAction.SAVE, save);

        SavingAction unsave = SavingAction.valueOf("UNSAVE");
        assertEquals(SavingAction.UNSAVE, unsave);
    }
}