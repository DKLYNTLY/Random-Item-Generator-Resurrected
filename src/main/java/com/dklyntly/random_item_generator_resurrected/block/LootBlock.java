package com.dklyntly.random_item_generator_resurrected.block;

import com.dklyntly.random_item_generator_resurrected.config.RIGConfig;
import com.dklyntly.random_item_generator_resurrected.stat.RIGStats;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootBlock extends Block {

    public LootBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_BROWN)
                .strength(0.5F)
                .sound(SoundType.WOOD)
                .requiresCorrectToolForDrops()
        );
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state,
                                       Level level,
                                       BlockPos pos,
                                       Player player,
                                       boolean willHarvest,
                                       FluidState fluid) {
        if (level.isClientSide) return false;
        if (!(level instanceof ServerLevel server)) return false;

        // Creative: actually remove the block
        if (player.isCreative()) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
            return false;
        }

        // Resolve enchantments
        Holder<Enchantment> H_SILK;
        Holder<Enchantment> H_FORTUNE;
        try {
            var enchLookup = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            H_SILK    = enchLookup.getOrThrow(Enchantments.SILK_TOUCH);
            H_FORTUNE = enchLookup.getOrThrow(Enchantments.FORTUNE);
        } catch (Exception e) {
            return false;
        }

        ItemStack mainHand = player.getMainHandItem();
        boolean hasSilkTouch = EnchantmentHelper.getItemEnchantmentLevel(H_SILK, mainHand) > 0;
        int fortuneLevel     = EnchantmentHelper.getItemEnchantmentLevel(H_FORTUNE, mainHand);

        if (hasSilkTouch) {
            // Silk Touch: drop itself and actually remove the block
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(),
                    new ItemStack(this.asItem()));
            level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
            level.levelEvent(2001, pos, Block.getId(state));
            level.playSound(null, pos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 0.8f, 1.0f);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            return false;
        }

        // Roll loot tables
        int rolls = calculateFortune(fortuneLevel);
        for (int i = 0; i < rolls; i++) {
            rollRandomLootTable(server, pos, player);
        }

        // Stat tracking
        if (player instanceof ServerPlayer sp) {
            sp.awardStat(RIGStats.LOOT_BLOCK_USES);
        }

        // Break feedback WITHOUT removing the block — ghost-block fix, same as other blocks
        level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
        level.levelEvent(2001, pos, Block.getId(state));
        level.playSound(null, pos, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.8f, 1.0f);
        level.sendBlockUpdated(pos, state, state, 3);

        server.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                15, 0.35, 0.35, 0.35, 0.05);

        return false;
    }

    private void rollRandomLootTable(ServerLevel server, BlockPos pos, Player player) {
        List<ResourceKey<LootTable>> pool = new ArrayList<>();

        // Use getServer().registryAccess() to get full datapack registry
        // Works on both singleplayer integrated server and dedicated server
        try {
            server.getServer().registryAccess()
                    .registryOrThrow(Registries.LOOT_TABLE)
                    .registryKeySet()
                    .forEach(key -> {
                        ResourceLocation id = key.location();
                        if (!id.getPath().startsWith("chests/")) return;
                        if (!RIGConfig.isLootTableAllowed(id)) return;
                        pool.add(key);
                    });
        } catch (Exception e) {
            // Fall back to hardcoded vanilla tables if registry fails
            addVanillaFallbackTables(pool);
        }

        if (pool.isEmpty()) {
            addVanillaFallbackTables(pool);
        }

        if (pool.isEmpty()) return;

        ResourceKey<LootTable> chosen = pool.get(new Random().nextInt(pool.size()));
        LootTable table = server.getServer().reloadableRegistries().getLootTable(chosen);

        if (table == LootTable.EMPTY) return;

        LootParams lootParams = new LootParams.Builder(server)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .withLuck(player.getLuck())
                .create(LootContextParamSets.CHEST);

        List<ItemStack> drops = table.getRandomItems(lootParams);
        for (ItemStack stack : drops) {
            if (!stack.isEmpty()) {
                Containers.dropItemStack(server,
                        pos.getX(), pos.getY(), pos.getZ(), stack);
            }
        }
    }

    private static void addVanillaFallbackTables(List<ResourceKey<LootTable>> pool) {
        String[] tables = {
                "chests/simple_dungeon",
                "chests/abandoned_mineshaft",
                "chests/desert_pyramid",
                "chests/jungle_temple",
                "chests/stronghold_corridor",
                "chests/stronghold_library",
                "chests/village_blacksmith",
                "chests/shipwreck_treasure",
                "chests/end_city_treasure",
                "chests/bastion_treasure",
                "chests/nether_fortress",
                "chests/pillager_outpost",
                "chests/woodland_mansion",
                "chests/ancient_city",
                "chests/ruined_portal"
        };
        for (String t : tables) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath("minecraft", t);
            if (RIGConfig.isLootTableAllowed(id)) {
                pool.add(ResourceKey.create(Registries.LOOT_TABLE, id));
            }
        }
    }

    private int calculateFortune(int fortuneLevel) {
        if (fortuneLevel <= 0) return 1;
        return 1 + new Random().nextInt(fortuneLevel + 1);
    }
}