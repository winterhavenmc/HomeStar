package com.winterhaven_mc.homestar.util;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.winterhaven_mc.homestar.PluginMain;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HomeStarFactoryTests {
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
    class Mocking {

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
    @DisplayName("Test spawn star factory methods.")
    class HomeStarFactory {

        ItemStack HomeStarItem = plugin.homeStarFactory.create();

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
            Assertions.assertTrue(plugin.homeStarFactory.isItem(HomeStarItem));
        }
    }

}
