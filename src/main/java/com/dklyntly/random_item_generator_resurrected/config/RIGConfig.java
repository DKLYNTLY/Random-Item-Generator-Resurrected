package com.dklyntly.random_item_generator_resurrected.config;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class RIGConfig {
    public static final ModConfigSpec COMMON_SPEC;

    // ==================== ITEM BLOCK ====================
    public static final ModConfigSpec.BooleanValue ITEM_USE_WHITELIST;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_DENYLIST;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_MOD_DENYLIST;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_WHITELIST;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_MOD_WHITELIST;

    // ==================== ENTITY BLOCK ====================
    public static final ModConfigSpec.BooleanValue ENTITY_BLOCK_NON_MOBS;
    public static final ModConfigSpec.BooleanValue ENTITY_BLOCK_LIGHTNING;
    public static final ModConfigSpec.BooleanValue ENTITY_BLOCK_EXPLOSIVES;
    public static final ModConfigSpec.BooleanValue ENTITY_BLOCK_FIREBALLS;
    public static final ModConfigSpec.BooleanValue ENTITY_BLOCK_PROJECTILES;
    public static final ModConfigSpec.BooleanValue ENTITY_BLOCK_SPLASH_POTIONS;
    public static final ModConfigSpec.BooleanValue ENTITY_BLOCK_XP_BOTTLES;
    public static final ModConfigSpec.BooleanValue ENTITY_BLOCK_ARMOR_STANDS;
    public static final ModConfigSpec.BooleanValue ENTITY_BLOCK_VEHICLES;
    public static final ModConfigSpec.BooleanValue ENTITY_BLOCK_FALLING_BLOCKS;
    public static final ModConfigSpec.BooleanValue ENTITY_BLOCK_ITEM_FRAMES;
    public static final ModConfigSpec.BooleanValue ENTITY_BLOCK_PAINTINGS;
    public static final ModConfigSpec.BooleanValue ENTITY_BLOCK_CREATURES;
    public static final ModConfigSpec.BooleanValue ENTITY_BLOCK_MONSTERS;
    public static final ModConfigSpec.BooleanValue ENTITY_USE_WHITELIST;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ENTITY_DENYLIST;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ENTITY_MOD_DENYLIST;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ENTITY_WHITELIST;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ENTITY_MOD_WHITELIST;

    // ==================== ENCHANTS BLOCK ====================
    public static final ModConfigSpec.BooleanValue ENCHANTS_REMOVE_CURSES;
    public static final ModConfigSpec.BooleanValue ENCHANTS_USE_WHITELIST;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ENCHANT_DENYLIST;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ENCHANT_MOD_DENYLIST;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ENCHANT_WHITELIST;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ENCHANT_MOD_WHITELIST;

    // ==================== LOOT BLOCK ====================
    public static final ModConfigSpec.BooleanValue LOOT_USE_WHITELIST;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> LOOT_DENYLIST;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> LOOT_MOD_DENYLIST;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> LOOT_WHITELIST;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> LOOT_MOD_WHITELIST;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();

        // ==================== ITEM BLOCK ====================
        b.comment("Settings for the Random Item Block — controls which items can drop.").push("item_block");

        ITEM_USE_WHITELIST = b
                .comment("Set to true to use the whitelist instead of the denylist.",
                        "When true, only items in 'allowed_items' or 'allowed_mods' will drop.",
                        "When false (default), all items drop except those in 'blocked_items' or 'blocked_mods'.")
                .define("use_whitelist", false);
        ITEM_DENYLIST = b
                .comment("Items that will never drop. Use full IDs like \"minecraft:command_block\".",
                        "Only used when use_whitelist = false.")
                .defineList("blocked_items", List.of("minecraft:air"),
                        o -> o instanceof String s && ResourceLocation.tryParse(s) != null);
        ITEM_MOD_DENYLIST = b
                .comment("Mods whose items will never drop. Use the mod's namespace/ID, e.g. \"create\".",
                        "Only used when use_whitelist = false.")
                .defineList("blocked_mods", List.of(),
                        o -> o instanceof String s && !s.isBlank());
        ITEM_WHITELIST = b
                .comment("Only these items can drop. Use full IDs like \"minecraft:diamond\".",
                        "Only used when use_whitelist = true.")
                .defineList("allowed_items", List.of(),
                        o -> o instanceof String s && ResourceLocation.tryParse(s) != null);
        ITEM_MOD_WHITELIST = b
                .comment("Only items from these mods can drop. Use the mod's namespace/ID, e.g. \"minecraft\".",
                        "Only used when use_whitelist = true.")
                .defineList("allowed_mods", List.of(),
                        o -> o instanceof String s && !s.isBlank());

        b.pop(); // item_block

        // ==================== ENTITY BLOCK ====================
        b.comment("Settings for the Random Entity Block — controls which entities can spawn.").push("entity_block");

        b.comment("Master toggle — the simplest way to control what spawns.").push("master");
        ENTITY_BLOCK_NON_MOBS = b
                .comment("When true (recommended), only actual mobs (animals, monsters, etc.) can spawn.",
                        "This blocks everything else: boats, minecarts, projectiles, item frames, etc.",
                        "Turn this off if you want to use the fine-grained toggles below.")
                .define("only_allow_mobs", true);
        b.pop(); // master

        b.comment("Hazard toggles — always checked, even when only_allow_mobs is on.").push("hazards");
        ENTITY_BLOCK_LIGHTNING = b
                .comment("Block lightning bolts from spawning. Recommended: true.")
                .define("block_lightning", true);
        ENTITY_BLOCK_EXPLOSIVES = b
                .comment("Block explosive entities (TNT, TNT Minecart, End Crystal). Recommended: true.")
                .define("block_explosives", true);
        ENTITY_BLOCK_FIREBALLS = b
                .comment("Block fireball entities (Ghast, Blaze, Wither, Dragon). Recommended: true.")
                .define("block_fireballs", true);
        b.pop(); // hazards

        b.comment("Fine-grained toggles — only used when only_allow_mobs = false.").push("non_mob_filters");
        ENTITY_BLOCK_PROJECTILES = b
                .comment("Block all projectiles (arrows, tridents, etc.) from spawning.")
                .define("block_projectiles", true);
        ENTITY_BLOCK_SPLASH_POTIONS = b
                .comment("Block splash and lingering potions from spawning.")
                .define("block_splash_potions", true);
        ENTITY_BLOCK_XP_BOTTLES = b
                .comment("Block experience bottles from spawning.")
                .define("block_xp_bottles", true);
        ENTITY_BLOCK_ARMOR_STANDS = b
                .comment("Block armor stands from spawning.")
                .define("block_armor_stands", true);
        ENTITY_BLOCK_VEHICLES = b
                .comment("Block vehicles (boats, minecarts) from spawning.")
                .define("block_vehicles", true);
        ENTITY_BLOCK_FALLING_BLOCKS = b
                .comment("Block falling block entities from spawning.")
                .define("block_falling_blocks", true);
        ENTITY_BLOCK_ITEM_FRAMES = b
                .comment("Block item frames and glow item frames from spawning.")
                .define("block_item_frames", true);
        ENTITY_BLOCK_PAINTINGS = b
                .comment("Block paintings from spawning.")
                .define("block_paintings", false);
        b.pop(); // non_mob_filters

        b.comment("Mob category filters — applies to actual mobs regardless of only_allow_mobs.").push("mob_categories");
        ENTITY_BLOCK_CREATURES = b
                .comment("Block passive and neutral creatures (cows, pigs, wolves, etc.) from spawning.")
                .define("block_creatures", false);
        ENTITY_BLOCK_MONSTERS = b
                .comment("Block hostile monsters (zombies, skeletons, creepers, etc.) from spawning.")
                .define("block_monsters", false);
        b.pop(); // mob_categories

        b.comment("Explicit lists — always applied regardless of other settings.").push("lists");
        ENTITY_USE_WHITELIST = b
                .comment("Set to true to use the whitelist instead of the denylist.",
                        "When true, only entities in 'allowed_entities' or 'allowed_mods' can spawn.",
                        "When false (default), all entities spawn except those in 'blocked_entities' or 'blocked_mods'.")
                .define("use_whitelist", false);
        ENTITY_DENYLIST = b
                .comment("Entities that will never spawn, by full ID. e.g. \"minecraft:ender_dragon\".",
                        "Only used when use_whitelist = false.")
                .defineList("blocked_entities",
                        List.of("minecraft:ender_dragon", "minecraft:wither", "minecraft:giant"),
                        o -> o instanceof String s && ResourceLocation.tryParse(s) != null);
        ENTITY_MOD_DENYLIST = b
                .comment("Mods whose entities will never spawn. Use the mod's namespace/ID.",
                        "Only used when use_whitelist = false.")
                .defineList("blocked_mods", List.of(),
                        o -> o instanceof String s && !s.isBlank());
        ENTITY_WHITELIST = b
                .comment("Only these entities can spawn, by full ID. e.g. \"minecraft:cow\".",
                        "Only used when use_whitelist = true.")
                .defineList("allowed_entities", List.of(),
                        o -> o instanceof String s && ResourceLocation.tryParse(s) != null);
        ENTITY_MOD_WHITELIST = b
                .comment("Only entities from these mods can spawn. Use the mod's namespace/ID.",
                        "Only used when use_whitelist = true.")
                .defineList("allowed_mods", List.of(),
                        o -> o instanceof String s && !s.isBlank());
        b.pop(); // lists

        b.pop(); // entity_block

        // ==================== ENCHANTS BLOCK ====================
        b.comment("Settings for the Random Enchants Block — controls which enchantments can appear.").push("enchants_block");

        ENCHANTS_REMOVE_CURSES = b
                .comment("When true, curse enchantments (Curse of Binding, Curse of Vanishing) will never appear.")
                .define("remove_curses", false);
        ENCHANTS_USE_WHITELIST = b
                .comment("Set to true to use the whitelist instead of the denylist.",
                        "When true, only enchantments in 'allowed_enchants' or 'allowed_mods' can appear.",
                        "When false (default), all enchantments appear except those in 'blocked_enchants' or 'blocked_mods'.")
                .define("use_whitelist", false);
        ENCHANT_DENYLIST = b
                .comment("Enchantments that will never appear, by full ID. e.g. \"minecraft:mending\".",
                        "Only used when use_whitelist = false.")
                .defineList("blocked_enchants", List.of(),
                        o -> o instanceof String s && ResourceLocation.tryParse(s) != null);
        ENCHANT_MOD_DENYLIST = b
                .comment("Mods whose enchantments will never appear. Use the mod's namespace/ID.",
                        "Only used when use_whitelist = false.")
                .defineList("blocked_mods", List.of(),
                        o -> o instanceof String s && !s.isBlank());
        ENCHANT_WHITELIST = b
                .comment("Only these enchantments can appear, by full ID. e.g. \"minecraft:sharpness\".",
                        "Only used when use_whitelist = true.")
                .defineList("allowed_enchants", List.of(),
                        o -> o instanceof String s && ResourceLocation.tryParse(s) != null);
        ENCHANT_MOD_WHITELIST = b
                .comment("Only enchantments from these mods can appear. Use the mod's namespace/ID.",
                        "Only used when use_whitelist = true.")
                .defineList("allowed_mods", List.of(),
                        o -> o instanceof String s && !s.isBlank());

        b.pop(); // enchants_block

        // ==================== LOOT BLOCK ====================
        b.comment("Settings for the Random Loot Block — controls which loot tables can be rolled.").push("loot_block");

        LOOT_USE_WHITELIST = b
                .comment("Set to true to use the whitelist instead of the denylist.",
                        "When true, only loot tables in 'allowed_tables' or 'allowed_mods' can be rolled.",
                        "When false (default), all loot tables are rolled except those in 'blocked_tables' or 'blocked_mods'.")
                .define("use_whitelist", false);
        LOOT_DENYLIST = b
                .comment("Loot tables that will never be rolled, by full ID. e.g. \"minecraft:chests/simple_dungeon\".",
                        "Only used when use_whitelist = false.")
                .defineList("blocked_tables", List.of(),
                        o -> o instanceof String s && ResourceLocation.tryParse(s) != null);
        LOOT_MOD_DENYLIST = b
                .comment("Mods whose loot tables will never be rolled. Use the mod's namespace/ID.",
                        "Only used when use_whitelist = false.")
                .defineList("blocked_mods", List.of(),
                        o -> o instanceof String s && !s.isBlank());
        LOOT_WHITELIST = b
                .comment("Only these loot tables can be rolled, by full ID. e.g. \"minecraft:chests/end_city_treasure\".",
                        "Only used when use_whitelist = true.")
                .defineList("allowed_tables", List.of(),
                        o -> o instanceof String s && ResourceLocation.tryParse(s) != null);
        LOOT_MOD_WHITELIST = b
                .comment("Only loot tables from these mods can be rolled. Use the mod's namespace/ID.",
                        "Only used when use_whitelist = true.")
                .defineList("allowed_mods", List.of(),
                        o -> o instanceof String s && !s.isBlank());

        b.pop(); // loot_block

        COMMON_SPEC = b.build();
    }

    // ==================== HELPERS ====================

    public static boolean isItemAllowed(Item item) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
        if (key == null) return false;
        String id = key.toString();
        String ns = key.getNamespace();
        if (ITEM_USE_WHITELIST.get()) {
            return ITEM_WHITELIST.get().stream().anyMatch(s -> s.equalsIgnoreCase(id))
                    || ITEM_MOD_WHITELIST.get().stream().anyMatch(s -> s.equalsIgnoreCase(ns));
        } else {
            return ITEM_DENYLIST.get().stream().noneMatch(s -> s.equalsIgnoreCase(id))
                    && ITEM_MOD_DENYLIST.get().stream().noneMatch(s -> s.equalsIgnoreCase(ns));
        }
    }

    public static boolean isEntityAllowed(EntityType<?> type) {
        ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        if (key == null) return false;
        String id = key.toString();
        String ns = key.getNamespace();
        if (ENTITY_USE_WHITELIST.get()) {
            return ENTITY_WHITELIST.get().stream().anyMatch(s -> s.equalsIgnoreCase(id))
                    || ENTITY_MOD_WHITELIST.get().stream().anyMatch(s -> s.equalsIgnoreCase(ns));
        } else {
            return ENTITY_DENYLIST.get().stream().noneMatch(s -> s.equalsIgnoreCase(id))
                    && ENTITY_MOD_DENYLIST.get().stream().noneMatch(s -> s.equalsIgnoreCase(ns));
        }
    }

    public static boolean isEnchantAllowed(Holder<Enchantment> ench) {
        var rk = ench.unwrapKey().orElse(null);
        if (rk == null) return false;
        String id = rk.location().toString();
        String ns = rk.location().getNamespace();
        if (ENCHANTS_USE_WHITELIST.get()) {
            return ENCHANT_WHITELIST.get().stream().anyMatch(s -> s.equalsIgnoreCase(id))
                    || ENCHANT_MOD_WHITELIST.get().stream().anyMatch(s -> s.equalsIgnoreCase(ns));
        } else {
            return ENCHANT_DENYLIST.get().stream().noneMatch(s -> s.equalsIgnoreCase(id))
                    && ENCHANT_MOD_DENYLIST.get().stream().noneMatch(s -> s.equalsIgnoreCase(ns));
        }
    }

    public static boolean isLootTableAllowed(ResourceLocation id) {
        String idStr = id.toString();
        String ns    = id.getNamespace();
        if (LOOT_USE_WHITELIST.get()) {
            return LOOT_WHITELIST.get().stream().anyMatch(s -> s.equalsIgnoreCase(idStr))
                    || LOOT_MOD_WHITELIST.get().stream().anyMatch(s -> s.equalsIgnoreCase(ns));
        } else {
            return LOOT_DENYLIST.get().stream().noneMatch(s -> s.equalsIgnoreCase(idStr))
                    && LOOT_MOD_DENYLIST.get().stream().noneMatch(s -> s.equalsIgnoreCase(ns));
        }
    }

    // Legacy aliases — kept so nothing else breaks
    @Deprecated public static boolean isBlacklisted(EntityType<?> type)             { return !isEntityAllowed(type); }
    @Deprecated public static boolean isItemBlacklisted(Item item)                  { return !isItemAllowed(item); }
    @Deprecated public static boolean isItemModBlacklisted(Item item)               { return !isItemAllowed(item); }
    @Deprecated public static boolean isEnchantBlacklisted(Holder<Enchantment> e)   { return !isEnchantAllowed(e); }
    @Deprecated public static boolean isEnchantModBlacklisted(Holder<Enchantment> e){ return !isEnchantAllowed(e); }

    private RIGConfig() {}
}