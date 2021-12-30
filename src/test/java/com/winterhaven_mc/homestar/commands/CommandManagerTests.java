package com.winterhaven_mc.homestar.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.winterhaven_mc.homestar.PluginMain;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommandManagerTests {
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
        // Stop the mock server
        MockBukkit.unmock();
    }

    @Test
    void HelpCommandTest() {
        server.dispatchCommand(server.getConsoleSender(), "/HomeStar help");
    }

    @Test
    void StatusCommandTest() {
        server.dispatchCommand(server.getConsoleSender(), "/HomeStar status");
    }

    @Test
    void GiveCommandTest() {
        server.dispatchCommand(server.getConsoleSender(), "/HomeStar give testy");
    }

    @Test
    void ReloadCommandTest() {
        server.dispatchCommand(server.getConsoleSender(), "/HomeStar reload");
    }

}
