package com.winterhavenmc.homestar;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

import com.winterhavenmc.homestar.sounds.SoundId;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;

import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HomeStarPluginTests {

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
        @DisplayName("language handler not null.")
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
        @DisplayName("player event listener not null.")
        void PlayerEventListenerNotNull() {
            Assertions.assertNotNull(plugin.playerEventListener);
        }

        @Test
        @DisplayName("spawn star factory not null.")
        void HomeStarFactoryNotNull() {
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



    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("Test Sounds config.")
    class SoundTests {

        // collection of enum sound name strings
        final Collection<String> enumSoundNames = new HashSet<>();

        // class constructor
        SoundTests() {
            // add all SoundId enum values to collection
            for (SoundId soundId : SoundId.values()) {
                enumSoundNames.add(soundId.name());
            }
        }

        @Test
        @DisplayName("Sounds config is not null.")
        void SoundConfigNotNull() {
            Assertions.assertNotNull(plugin.soundConfig);
        }

        @SuppressWarnings("unused")
        Collection<String> GetConfigFileKeys() {
            return plugin.soundConfig.getSoundConfigKeys();
        }

        @ParameterizedTest
        @MethodSource("GetConfigFileKeys")
        @DisplayName("get enum member names of SoundId as list of string")
        void SoundConfigTest12(String soundName) {
            Assertions.assertTrue(enumSoundNames.contains(soundName));
        }

        @ParameterizedTest
        @EnumSource(SoundId.class)
        @DisplayName("all SoundId enum members have matching key in sound config file")
        void SoundConfigContainsAllEnumSounds(SoundId soundId) {
            Assertions.assertTrue(plugin.soundConfig.getSoundConfigKeys().contains(soundId.name()));
        }
    }



    @Nested
    @DisplayName("Test spawn star factory methods.")
    class HomeStarFactoryTests {

        final ItemStack HomeStarItem = plugin.homeStarUtility.create();

        @Test
        @DisplayName("new item type is nether star.")
        void ItemSetDefaultType() {
            Assertions.assertEquals(Material.NETHER_STAR, HomeStarItem.getType());
        }

        @Test
        @DisplayName("new item name is HomeStar.")
        void NewItemHasDefaultName() {
            Assertions.assertNotNull(HomeStarItem.getItemMeta());
            Assertions.assertNotNull(HomeStarItem.getItemMeta().getDisplayName());
            Assertions.assertEquals("HomeStar",
                    ChatColor.stripColor(HomeStarItem.getItemMeta().getDisplayName()));
        }

        @Test
        @DisplayName("new item has lore.")
        void NewItemHasDefaultLore() {
            Assertions.assertNotNull(HomeStarItem.getItemMeta());
            Assertions.assertNotNull(HomeStarItem.getItemMeta().getLore());
            Assertions.assertEquals("Use to Return to Home",
                    ChatColor.stripColor(String.join(" ",
                            HomeStarItem.getItemMeta().getLore())));
        }

        @Test
        void CreateAndTestValidItem() {
            Assertions.assertTrue(plugin.homeStarUtility.isItem(HomeStarItem));
        }
    }


    @Nested
    @DisplayName("Test MessageBuilder.")
    class MessageBuilderTests {

        @Test
        @DisplayName("language manager is not null.")
        void LanguageManagerNotNull() {
            Assertions.assertNotNull(plugin.messageBuilder);
        }

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


// Command tests

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
