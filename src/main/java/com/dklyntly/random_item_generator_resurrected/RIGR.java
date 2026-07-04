package com.dklyntly.random_item_generator_resurrected;

import com.dklyntly.random_item_generator_resurrected.block.ItemBlock;
import com.dklyntly.random_item_generator_resurrected.block.EntityBlock;
import com.dklyntly.random_item_generator_resurrected.block.EnchantsBlock;
import com.dklyntly.random_item_generator_resurrected.block.LootBlock;
import com.dklyntly.random_item_generator_resurrected.config.RIGConfig;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(RIGR.MODID)
public class RIGR {
    public static final String MODID = "random_item_generator_resurrected";

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<Block, ItemBlock> ITEM_BLOCK =
            BLOCKS.register("item_block", ItemBlock::new);
    public static final DeferredHolder<Block, EntityBlock> ENTITY_BLOCK =
            BLOCKS.register("entity_block", EntityBlock::new);
    public static final DeferredHolder<Block, EnchantsBlock> ENCHANTS_BLOCK =
            BLOCKS.register("enchants_block", EnchantsBlock::new);
    public static final DeferredHolder<Block, LootBlock> LOOT_BLOCK =
            BLOCKS.register("loot_block", LootBlock::new);

    public static final DeferredHolder<Item, Item> RIG_ITEM =
            ITEMS.register("item_block", () -> new BlockItem(ITEM_BLOCK.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> ENTITY_ITEM =
            ITEMS.register("entity_block", () -> new BlockItem(ENTITY_BLOCK.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> ENCHANTS_ITEM =
            ITEMS.register("enchants_block", () -> new BlockItem(ENCHANTS_BLOCK.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> LOOT_ITEM =
            ITEMS.register("loot_block", () -> new BlockItem(LOOT_BLOCK.get(), new Item.Properties()));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> RIG_TAB =
            TABS.register("rig_tab", () ->
                    CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup." + MODID + ".rig_tab"))
                            .icon(() -> new ItemStack(RIG_ITEM.get()))
                            .displayItems((params, output) -> {
                                output.accept(RIG_ITEM.get());
                                output.accept(ENTITY_ITEM.get());
                                output.accept(ENCHANTS_ITEM.get());
                                output.accept(LOOT_ITEM.get());
                            })
                            .build()
            );

    public RIGR(IEventBus bus, ModContainer container) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TABS.register(bus);

        container.registerConfig(ModConfig.Type.COMMON, RIGConfig.COMMON_SPEC);
    }
}