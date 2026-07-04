package com.dklyntly.random_item_generator_resurrected.block;

import com.dklyntly.random_item_generator_resurrected.config.RIGConfig;
import com.dklyntly.random_item_generator_resurrected.stat.RIGStats;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
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

import java.util.Random;

public class EnchantsBlock extends Block {

    public EnchantsBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .strength(0.5F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops()
        );
    }

    private void dropEnchantedBook(Level level, BlockPos pos) {
        ItemStack book = generateRandomEnchantedBook(level);
        if (!book.isEmpty()) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), book);
        }
    }

    private ItemStack generateRandomEnchantedBook(Level level) {
        var reg = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        var rng = level.getRandom();

        for (int attempts = 0; attempts < 64; attempts++) {
            var opt = reg.getRandom(rng);
            if (opt.isEmpty()) break;

            Holder<Enchantment> chosen = opt.get();

            // Filter curses
            if (RIGConfig.ENCHANTS_REMOVE_CURSES.get() && chosen.is(EnchantmentTags.CURSE)) continue;

            // Whitelist/denylist check
            if (!RIGConfig.isEnchantAllowed(chosen)) continue;

            int min = chosen.value().getMinLevel();
            int max = chosen.value().getMaxLevel();
            int lvl = min + rng.nextInt(max - min + 1);

            return EnchantedBookItem.createForEnchantment(new EnchantmentInstance(chosen, lvl));
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state,
                                       Level level,
                                       BlockPos pos,
                                       Player player,
                                       boolean willHarvest,
                                       FluidState fluid) {
        if (!level.isClientSide) {
            var reg = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
            Holder<Enchantment> H_SILK    = reg.getHolderOrThrow(Enchantments.SILK_TOUCH);
            Holder<Enchantment> H_FORTUNE = reg.getHolderOrThrow(Enchantments.FORTUNE);

            ItemStack mainHand = player.getMainHandItem();
            boolean hasSilkTouch = EnchantmentHelper.getItemEnchantmentLevel(H_SILK, mainHand) > 0;
            int fortuneLevel     = EnchantmentHelper.getItemEnchantmentLevel(H_FORTUNE, mainHand);

            if (!hasSilkTouch) {
                int rolls = calculateFortune(fortuneLevel);
                for (int j = 0; j < rolls; j++) dropEnchantedBook(level, pos);

                // Stat tracking
                if (player instanceof ServerPlayer sp) {
                    sp.awardStat(RIGStats.ENCHANT_BLOCK_USES);
                }

                level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
                level.levelEvent(2001, pos, Block.getId(state));
                level.playSound(null, pos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 0.8f, 1.0f);
                level.sendBlockUpdated(pos, state, state, 3);

                // Purple particles
                if (level instanceof ServerLevel server) {
                    server.sendParticles(ParticleTypes.WITCH,
                            pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                            20, 0.35, 0.35, 0.35, 0.05);
                }
                return false;

            } else {
                // Silk Touch: drop itself and remove
                ItemStack self = new ItemStack(this.asItem());
                if (!self.isEmpty()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), self);
                }
                level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
                level.levelEvent(2001, pos, Block.getId(state));
                level.playSound(null, pos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 0.8f, 1.0f);
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                return false;
            }
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    private int calculateFortune(int fortuneLevel) {
        if (fortuneLevel <= 0) return 1;
        return 1 + new Random().nextInt(fortuneLevel + 1);
    }
}