package com.winterhavenmc.homestar.teleport;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.winterhavenmc.homestar.PluginMain;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TeleportManagerTests {
    @SuppressWarnings("FieldCanBeLocal")
    private ServerMock server;
    private PlayerMock player;
    private PluginMain plugin;

    @BeforeAll
    public void setUp() {
        // Start the mock server
        server = MockBukkit.mock();

        player = server.addPlayer("testy");

        // start the mock plugin
        plugin = MockBukkit.load(PluginMain.class);

    }

    @AfterAll
    public void tearDown() {
        // cancel all tasks for plugin
        server.getScheduler().cancelTasks(plugin);

        // Stop the mock server
        MockBukkit.unmock();
    }

    @Nested
    @DisplayName("Test Teleport Manager.")
    class HomeStarTests {

        @Nested
        class TeleportTests {

            @Test
            @DisplayName("teleport manager is not null.")
            void TeleportManagerNotNull() {
                Assertions.assertNotNull(plugin.teleportManager);
            }

            @Test
            @DisplayName("player is not warming up.")
            void PlayerIsNotWarmingUp() {
                Assertions.assertFalse(plugin.teleportManager.isWarmingUp(player));
            }

            @Test
            @DisplayName("player is warming up.")
            void PlayerIsWarmingUp() {
                plugin.teleportManager.putWarmup(player, 1234);
                Assertions.assertTrue(plugin.teleportManager.isWarmingUp(player));
                plugin.teleportManager.cancelTeleport(player);
            }
        }
    }
}

