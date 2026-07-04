package com.dklyntly.random_item_generator_resurrected.block;

import com.dklyntly.random_item_generator_resurrected.config.RIGConfig;
import com.dklyntly.random_item_generator_resurrected.stat.RIGStats;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemBlock extends Block {

    public ItemBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.GOLD)
                .strength(0.5F)
                .requiresCorrectToolForDrops());
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state,
                                       Level level,
                                       BlockPos pos,
                                       Player player,
                                       boolean willHarvest,
                                       FluidState fluid) {
        if (!level.isClientSide) {
            if (player.isCreative()) {
                return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
            }

            Holder<Enchantment> H_SILK =
                    level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH);
            Holder<Enchantment> H_FORTUNE =
                    level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE);

            ItemStack mainHand = player.getMainHandItem();
            boolean hasSilkTouch = EnchantmentHelper.getItemEnchantmentLevel(H_SILK, mainHand) > 0;
            int fortuneLevel     = EnchantmentHelper.getItemEnchantmentLevel(H_FORTUNE, mainHand);

            if (!hasSilkTouch) {
                // Build the drop pool
                List<Item> pool = new ArrayList<>();
                for (Item it : BuiltInRegistries.ITEM) {
                    if (it == Items.AIR) continue;
                    if (!RIGConfig.isItemAllowed(it)) continue;
                    pool.add(it);
                }

                if (!pool.isEmpty()) {
                    int rolls = calculateFortune(fortuneLevel);
                    Random rng = new Random();
                    for (int j = 0; j < rolls; j++) {
                        Item randomItem = pool.get(rng.nextInt(pool.size()));
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(),
                                new ItemStack(randomItem));
                    }
                }

                // Stat tracking
                if (player instanceof ServerPlayer sp) {
                    sp.awardStat(RIGStats.ITEM_BLOCK_USES);
                }

                level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
                level.levelEvent(2001, pos, Block.getId(state));
                level.playSound(null, pos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 0.8f, 1.0f);
                level.sendBlockUpdated(pos, state, state, 3);

                // Gold particles
                if (level instanceof ServerLevel server) {
                    server.sendParticles(ParticleTypes.WAX_ON,
                            pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                            15, 0.35, 0.35, 0.35, 0.05);
                }

                return false;
            }

            // Silk Touch: drop self and remove
            Item self = this.asItem();
            if (self != null && self != Items.AIR) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(self));
            }
            level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
            level.levelEvent(2001, pos, Block.getId(state));
            level.playSound(null, pos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 0.8f, 1.0f);
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            return false;
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    private int calculateFortune(int fortuneLevel) {
        if (fortuneLevel <= 0) return 1;
        return 1 + new Random().nextInt(fortuneLevel + 1);
    }
}