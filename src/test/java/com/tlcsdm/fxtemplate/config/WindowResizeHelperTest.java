package com.tlcsdm.fxtemplate.config;

import com.tlcsdm.fxtemplate.config.WindowResizeHelper.ResizeDirection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the WindowResizeHelper resize direction detection and enum.
 */
class WindowResizeHelperTest {

    @Test
    void testResizeDirectionCursorNotNull() {
        for (ResizeDirection dir : ResizeDirection.values()) {
            assertNotNull(dir.getCursor(), "Cursor should not be null for " + dir.name());
        }
    }

    @Test
    void testResizeDirectionFlags() {
        assertTrue(ResizeDirection.N.isTop());
        assertFalse(ResizeDirection.N.isBottom());
        assertFalse(ResizeDirection.N.isLeft());
        assertFalse(ResizeDirection.N.isRight());

        assertTrue(ResizeDirection.S.isBottom());
        assertFalse(ResizeDirection.S.isTop());

        assertTrue(ResizeDirection.W.isLeft());
        assertFalse(ResizeDirection.W.isRight());

        assertTrue(ResizeDirection.E.isRight());
        assertFalse(ResizeDirection.E.isLeft());

        assertTrue(ResizeDirection.NW.isTop());
        assertTrue(ResizeDirection.NW.isLeft());
        assertFalse(ResizeDirection.NW.isBottom());
        assertFalse(ResizeDirection.NW.isRight());

        assertTrue(ResizeDirection.NE.isTop());
        assertTrue(ResizeDirection.NE.isRight());

        assertTrue(ResizeDirection.SW.isBottom());
        assertTrue(ResizeDirection.SW.isLeft());

        assertTrue(ResizeDirection.SE.isBottom());
        assertTrue(ResizeDirection.SE.isRight());
    }

    @Test
    void testNoneDirectionHasNoFlags() {
        assertFalse(ResizeDirection.NONE.isTop());
        assertFalse(ResizeDirection.NONE.isBottom());
        assertFalse(ResizeDirection.NONE.isLeft());
        assertFalse(ResizeDirection.NONE.isRight());
    }

    @Test
    void testAllDirectionsPresent() {
        assertEquals(9, ResizeDirection.values().length,
            "Should have 9 directions: NONE + 4 edges + 4 corners");
    }

    @Test
    void testAddResizeListenerWithNullStage() {
        // Should not throw
        WindowResizeHelper.addResizeListener(null);
    }
}
