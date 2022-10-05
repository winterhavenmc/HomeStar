package com.winterhavenmc.homestar;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

import org.bukkit.configuration.Configuration;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.Set;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PluginMainTest {

    private ServerMock server;
    private PluginMain plugin;

    @BeforeAll
    public void setUp() {
        // Start the mock server
        server = MockBukkit.mock();

        // start the mock plugin
        plugin = MockBukkit.load(PluginMain.class);

    }

    @AfterAll
    public void tearDown() {
        // Stop the mock server
        MockBukkit.unmock();
    }

    @Nested
    @DisplayName("Test mocking setup.")
    class MockingTests {

        @Test
        @DisplayName("mock server is not null.")
        void MockServerNotNull() {
            Assertions.assertNotNull(server);
        }

        @Test
        @DisplayName("mock plugin is not null.")
        void MockPluginNotNull() {
            Assertions.assertNotNull(plugin);
        }
    }


    @Nested
    @DisplayName("Test plugin main objects.")
    class PluginMainObjectTests {

        @Test
        @DisplayName("message builder not null.")
        void LanguageHandlerNotNull() {
            Assertions.assertNotNull(plugin.messageBuilder);
        }

        @Test
        @DisplayName("sound config not null.")
        void SoundConfigNotNull() {
            Assertions.assertNotNull(plugin.soundConfig);
        }

        @Test
        @DisplayName("teleport manager not null.")
        void TeleportManagerNotNull() {
            Assertions.assertNotNull(plugin.teleportHandler);
        }

        @Test
        @DisplayName("world manager not null.")
        void WorldManagerNotNull() {
            Assertions.assertNotNull(plugin.worldManager);
        }

        @Test
        @DisplayName("command manager not null.")
        void commandManagerNotNull() {
            Assertions.assertNotNull(plugin.commandManager);
        }

        @Test
        @DisplayName("home star utility not null.")
        void HomeStarUtilityNotNull() {
            Assertions.assertNotNull(plugin.homeStarUtility);
        }
    }

    @Nested
    @DisplayName("Test plugin config.")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ConfigTests {

        final Configuration config = plugin.getConfig();
        final Set<String> enumConfigKeyStrings = new HashSet<>();

        public ConfigTests() {
            for (ConfigSetting configSetting : ConfigSetting.values()) {
                this.enumConfigKeyStrings.add(configSetting.getKey());
            }
        }

        @Test
        @DisplayName("config not null.")
        void ConfigNotNull() {
            Assertions.assertNotNull(config);
        }

        @Test
        @DisplayName("test configured language.")
        void GetLanguage() {
            Assertions.assertEquals("en-US", config.getString("language"));
        }

        @SuppressWarnings("unused")
        Set<String> ConfigFileKeys() {
            return config.getKeys(false);
        }

        @ParameterizedTest
        @DisplayName("file config key is contained in enum.")
        @MethodSource("ConfigFileKeys")
        void ConfigFileKeyNotNull(String key) {
            Assertions.assertNotNull(key);
            Assertions.assertTrue(enumConfigKeyStrings.contains(key));
        }

        @ParameterizedTest
        @EnumSource(ConfigSetting.class)
        @DisplayName("ConfigSetting enum matches config file key/value pairs.")
        void ConfigFileKeysContainsEnumKey(ConfigSetting configSetting) {
            Assertions.assertEquals(configSetting.getValue(), plugin.getConfig().getString(configSetting.getKey()));
        }
    }


    @Nested
    @DisplayName("Test MessageBuilder.")
    class MessageBuilderTests {

        @Test
        @DisplayName("item name is not null.")
        void ItemNameNotNull() {
            Assertions.assertNotNull(plugin.messageBuilder.getItemName());
        }

        @Test
        @DisplayName("item lore is not null.")
        void ItemLoreNotNull() {
            Assertions.assertNotNull(plugin.messageBuilder.getItemLore());
        }
    }

}
