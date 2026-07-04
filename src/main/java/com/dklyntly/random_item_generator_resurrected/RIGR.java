package com.dklyntly.random_item_generator_resurrected;

import com.dklyntly.random_item_generator_resurrected.block.EnchantsBlock;
import com.dklyntly.random_item_generator_resurrected.block.EntityBlock;
import com.dklyntly.random_item_generator_resurrected.block.ItemBlock;
import com.dklyntly.random_item_generator_resurrected.block.LootBlock;
import com.dklyntly.random_item_generator_resurrected.config.RIGConfig;
import com.dklyntly.random_item_generator_resurrected.stat.RIGStats;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.RegistryObject;

@Mod(RIGR.MODID)
public class RIGR {
    public static final String MODID = "random_item_generator_resurrected";

    public static final ItemGroup RIG_TAB = new ItemGroup(MODID + ".rig_tab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(RIG_ITEM.get());
        }
    };

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Block> ITEM_BLOCK = BLOCKS.register("item_block", ItemBlock::new);
    public static final RegistryObject<Block> ENTITY_BLOCK = BLOCKS.register("entity_block", EntityBlock::new);
    public static final RegistryObject<Block> ENCHANTS_BLOCK = BLOCKS.register("enchants_block", EnchantsBlock::new);
    public static final RegistryObject<Block> LOOT_BLOCK = BLOCKS.register("loot_block", LootBlock::new);

    public static final RegistryObject<Item> RIG_ITEM = ITEMS.register("item_block",
            () -> new BlockItem(ITEM_BLOCK.get(), new Item.Properties().tab(RIG_TAB)));
    public static final RegistryObject<Item> ENTITY_ITEM = ITEMS.register("entity_block",
            () -> new BlockItem(ENTITY_BLOCK.get(), new Item.Properties().tab(RIG_TAB)));
    public static final RegistryObject<Item> ENCHANTS_ITEM = ITEMS.register("enchants_block",
            () -> new BlockItem(ENCHANTS_BLOCK.get(), new Item.Properties().tab(RIG_TAB)));
    public static final RegistryObject<Item> LOOT_ITEM = ITEMS.register("loot_block",
            () -> new BlockItem(LOOT_BLOCK.get(), new Item.Properties().tab(RIG_TAB)));

    public RIGR() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::commonSetup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, RIGConfig.COMMON_SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(RIGStats::registerStats);
    }
}
