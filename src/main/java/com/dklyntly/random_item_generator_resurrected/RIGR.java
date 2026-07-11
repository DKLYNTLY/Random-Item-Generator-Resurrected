package com.dklyntly.random_item_generator_resurrected;

import com.dklyntly.random_item_generator_resurrected.block.EnchantsBlock;
import com.dklyntly.random_item_generator_resurrected.block.EntityBlock;
import com.dklyntly.random_item_generator_resurrected.block.LootBlock;
import com.dklyntly.random_item_generator_resurrected.config.RIGConfig;
import com.dklyntly.random_item_generator_resurrected.stat.RIGStats;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(
        modid = RIGR.MODID,
        name = RIGR.NAME,
        version = RIGR.VERSION,
        acceptedMinecraftVersions = "[1.12.2]"
)
@EventBusSubscriber(modid = RIGR.MODID)
public class RIGR {
    public static final String MODID = "random_item_generator_resurrected";
    public static final String NAME = "Random Item Generator Resurrected";
    public static final String VERSION = "2.0.1-1.12.2";

    public static Block ITEM_BLOCK;
    public static Block ENTITY_BLOCK;
    public static Block ENCHANTS_BLOCK;
    public static Block LOOT_BLOCK;

    public static final CreativeTabs RIG_TAB = new CreativeTabs(MODID + ".rig_tab") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(ITEM_BLOCK);
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        RIGConfig.init(event.getSuggestedConfigurationFile());
        RIGStats.init();
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        ITEM_BLOCK = new com.dklyntly.random_item_generator_resurrected.block.ItemBlock()
                .setRegistryName(MODID, "item_block")
                .setUnlocalizedName(MODID + ".item_block")
                .setCreativeTab(RIG_TAB);

        ENTITY_BLOCK = new EntityBlock()
                .setRegistryName(MODID, "entity_block")
                .setUnlocalizedName(MODID + ".entity_block")
                .setCreativeTab(RIG_TAB);

        ENCHANTS_BLOCK = new EnchantsBlock()
                .setRegistryName(MODID, "enchants_block")
                .setUnlocalizedName(MODID + ".enchants_block")
                .setCreativeTab(RIG_TAB);

        LOOT_BLOCK = new LootBlock()
                .setRegistryName(MODID, "loot_block")
                .setUnlocalizedName(MODID + ".loot_block")
                .setCreativeTab(RIG_TAB);

        event.getRegistry().registerAll(ITEM_BLOCK, ENTITY_BLOCK, ENCHANTS_BLOCK, LOOT_BLOCK);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                makeItemBlock(ITEM_BLOCK),
                makeItemBlock(ENTITY_BLOCK),
                makeItemBlock(ENCHANTS_BLOCK),
                makeItemBlock(LOOT_BLOCK)
        );
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerModels(ModelRegistryEvent event) {
        registerBlockItemModel(ITEM_BLOCK);
        registerBlockItemModel(ENTITY_BLOCK);
        registerBlockItemModel(ENCHANTS_BLOCK);
        registerBlockItemModel(LOOT_BLOCK);
    }

    @SideOnly(Side.CLIENT)
    private static void registerBlockItemModel(Block block) {
        if (block == null || block.getRegistryName() == null) {
            return;
        }
        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(block),
                0,
                new ModelResourceLocation(block.getRegistryName(), "inventory")
        );
    }

    private static Item makeItemBlock(Block block) {
        return new net.minecraft.item.ItemBlock(block).setRegistryName(block.getRegistryName());
    }
}
