package com.dklyntly.random_item_generator_resurrected.stat;

import com.dklyntly.random_item_generator_resurrected.RIGR;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = RIGR.MODID)
public final class RIGStats {

    public static Stat<ResourceLocation> ITEM_BLOCK_USES;
    public static Stat<ResourceLocation> ENTITY_BLOCK_USES;
    public static Stat<ResourceLocation> ENCHANT_BLOCK_USES;
    public static Stat<ResourceLocation> LOOT_BLOCK_USES;

    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        event.register(BuiltInRegistries.CUSTOM_STAT.key(), helper -> {
            ITEM_BLOCK_USES    = registerStat("item_block_uses");
            ENTITY_BLOCK_USES  = registerStat("entity_block_uses");
            ENCHANT_BLOCK_USES = registerStat("enchant_block_uses");
            LOOT_BLOCK_USES    = registerStat("loot_block_uses");
        });
    }

    private static Stat<ResourceLocation> registerStat(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(RIGR.MODID, name);
        net.minecraft.core.Registry.register(BuiltInRegistries.CUSTOM_STAT, id, id);
        return Stats.CUSTOM.get(id, StatFormatter.DEFAULT);
    }

    private RIGStats() {}
}