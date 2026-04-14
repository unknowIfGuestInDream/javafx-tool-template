package com.tlcsdm.fxtemplate.controller;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for MainController helper logic.
 */
class MainControllerTest {

    @Test
    void testFilterRestartVmArgumentsRemovesAgentOptions() {
        List<String> filteredArguments = MainController.filterRestartVmArguments(List.of(
            "-Xms64m",
            "-javaagent:/tmp/agent.jar",
            "-agentlib:jdwp=transport=dt_socket",
            "-agentpath:/tmp/native-agent.so",
            "--add-opens=java.base/java.lang=ALL-UNNAMED"));

        assertEquals(List.of("-Xms64m", "--add-opens=java.base/java.lang=ALL-UNNAMED"), filteredArguments);
    }

    @Test
    void testBuildRestartCommandUsesJarLaunchPath() {
        List<String> command = MainController.buildRestartCommand(
            "/opt/jdk",
            Path.of("/tmp/javafx-tool-template.jar"),
            "/tmp/classes:/tmp/lib/*",
            List.of("-Xmx256m"));

        assertTrue(command.contains("-jar"), "Restart command should relaunch from the packaged jar");
        assertTrue(command.contains("/tmp/javafx-tool-template.jar"), "Restart command should include the jar path");
        assertFalse(command.contains("-cp"), "Jar restart should not use the classpath launcher mode");
    }

    @Test
    void testBuildRestartCommandUsesClasspathForExplodedLaunchPath() {
        List<String> command = MainController.buildRestartCommand(
            "/opt/jdk",
            Path.of("/tmp/target/classes"),
            "/tmp/classes:/tmp/lib/*",
            List.of("-Xmx256m"));

        assertTrue(command.contains("-cp"), "Restart command should use classpath launcher mode for exploded classes");
        assertTrue(command.contains("/tmp/classes:/tmp/lib/*"), "Restart command should preserve the current classpath");
        assertEquals("com.tlcsdm.fxtemplate.Launcher", command.get(command.size() - 1));
    }
}
