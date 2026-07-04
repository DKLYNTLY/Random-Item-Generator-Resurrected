package com.dklyntly.random_item_generator_resurrected.block;

import com.dklyntly.random_item_generator_resurrected.config.RIGConfig;
import com.dklyntly.random_item_generator_resurrected.stat.RIGStats;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.*;

@Mod.EventBusSubscriber(modid = "random_item_generator_resurrected", bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityBlock extends Block {

    private static List<EntityType<?>> RANDOM_ENTITIES = new ArrayList<>();

    public EntityBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.QUARTZ)
                .strength(0.6F)
                .requiresCorrectToolForDrops()
        );
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        RANDOM_ENTITIES = List.copyOf(BuiltInRegistries.ENTITY_TYPE.stream().toList());
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos,
                                       Player player, boolean willHarvest, FluidState fluid) {
        if (!level.isClientSide) {
            ItemStack mainHand = player.getMainHandItem();
            boolean hasSilkTouch = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, mainHand) > 0;
            int fortune = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, mainHand);

            if (!RANDOM_ENTITIES.isEmpty()) {
                if (!hasSilkTouch) {
                    spawnRandomEntities(level, pos, fortune);

                    // Stat tracking
                    if (player instanceof ServerPlayer sp) {
                        sp.awardStat(RIGStats.ENTITY_BLOCK_USES);
                    }

                    level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
                    level.levelEvent(2001, pos, Block.getId(state));
                    level.playSound(null, pos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 0.8f, 1.0f);
                    level.sendBlockUpdated(pos, state, state, 3);

                    // Silver/white particles
                    if (level instanceof ServerLevel server) {
                        server.sendParticles(ParticleTypes.SNOWFLAKE,
                                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                                15, 0.35, 0.35, 0.35, 0.05);
                    }
                    return false;

                } else {
                    // Silk Touch: drop itself and remove block
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
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    private void spawnRandomEntities(Level level, BlockPos pos, int fortuneLevel) {
        if (!(level instanceof ServerLevel server)) return;

        Random rng = new Random();
        int target = 1 + rng.nextInt(fortuneLevel + 1);
        int spawned = 0, attempts = 0, maxAttempts = target * 20;

        while (spawned < target && attempts++ < maxAttempts) {
            if (RANDOM_ENTITIES.isEmpty()) break;
            var type = RANDOM_ENTITIES.get(rng.nextInt(RANDOM_ENTITIES.size()));

            // Whitelist/denylist check
            if (!RIGConfig.isEntityAllowed(type)) continue;

            Entity e = type.create(server);
            if (e == null) continue;

            // Master rule: only allow actual mobs
            boolean isMob = e instanceof Mob;
            if (RIGConfig.ENTITY_BLOCK_NON_MOBS.get() && !isMob) { e.discard(); continue; }

            // Hazards — always checked
            if (RIGConfig.ENTITY_BLOCK_LIGHTNING.get()  && e instanceof LightningBolt)  { e.discard(); continue; }
            if (RIGConfig.ENTITY_BLOCK_EXPLOSIVES.get() && isExplosiveLike(e))           { e.discard(); continue; }
            if (RIGConfig.ENTITY_BLOCK_FIREBALLS.get()  && isFireballLike(e))            { e.discard(); continue; }

            // Fine-grained non-mob filters (only when master rule is off)
            if (!RIGConfig.ENTITY_BLOCK_NON_MOBS.get()) {
                if (RIGConfig.ENTITY_BLOCK_ITEM_FRAMES.get()    && e instanceof ItemFrame)           { e.discard(); continue; }
                if (RIGConfig.ENTITY_BLOCK_PAINTINGS.get()      && e instanceof Painting)            { e.discard(); continue; }
                if (RIGConfig.ENTITY_BLOCK_PROJECTILES.get()    && e instanceof Projectile)          { e.discard(); continue; }
                if (RIGConfig.ENTITY_BLOCK_SPLASH_POTIONS.get() && e instanceof ThrownPotion)        { e.discard(); continue; }
                if (RIGConfig.ENTITY_BLOCK_XP_BOTTLES.get()     && e instanceof ThrownExperienceBottle) { e.discard(); continue; }
                if (RIGConfig.ENTITY_BLOCK_ARMOR_STANDS.get()   && e instanceof ArmorStand)          { e.discard(); continue; }
                if (RIGConfig.ENTITY_BLOCK_VEHICLES.get()       && isVehicleLike(e))                 { e.discard(); continue; }
                if (RIGConfig.ENTITY_BLOCK_FALLING_BLOCKS.get() && isFallingBlockLike(e))            { e.discard(); continue; }
            }

            // Mob category filters
            if (e instanceof Mob mob) {
                switch (mob.getType().getCategory()) {
                    case CREATURE -> { if (RIGConfig.ENTITY_BLOCK_CREATURES.get()) { e.discard(); continue; } }
                    case MONSTER  -> { if (RIGConfig.ENTITY_BLOCK_MONSTERS.get())  { e.discard(); continue; } }
                    default -> { }
                }
            }

            e.moveTo(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                    server.random.nextFloat() * 360F, 0.0F);
            server.addFreshEntity(e);
            spawned++;
        }
    }

    private static boolean isVehicleLike(Entity e) {
        return e instanceof Boat || e instanceof AbstractMinecart;
    }

    private static boolean isExplosiveLike(Entity e) {
        return e instanceof PrimedTnt ||
                e.getType() == EntityType.TNT_MINECART ||
                e instanceof EndCrystal;
    }

    private static boolean isFireballLike(Entity e) {
        return e instanceof LargeFireball ||
                e instanceof SmallFireball ||
                e instanceof WitherSkull   ||
                e instanceof DragonFireball;
    }

    private static boolean isFallingBlockLike(Entity e) {
        return e.getType() == EntityType.FALLING_BLOCK;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        ItemStack tool = builder.getParameter(LootContextParams.TOOL);
        if (tool != null) {
            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0) {
                return Collections.singletonList(new ItemStack(this.asItem()));
            }
        }
        return super.getDrops(state, builder);
    }
}