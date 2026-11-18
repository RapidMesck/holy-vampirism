package com.rapidmesck.holyvampirism.event;

import com.rapidmesck.holyvampirism.attachment.HolyHunterData;
import com.rapidmesck.holyvampirism.attachment.SolarBurnData;
import com.rapidmesck.holyvampirism.config.HolyVampirismConfig;
import com.rapidmesck.holyvampirism.registry.HolyActions;
import com.rapidmesck.holyvampirism.registry.HolyDataAttachments;
import com.rapidmesck.holyvampirism.registry.HolySkills;
import com.rapidmesck.holyvampirism.util.HolyHelper;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.event.PlayerFactionEvent;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.LevelAttributeModifier;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.items.StakeItem;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;

public final class HolyHunterEvents {

    private static final String MOD_MAX_HEALTH = "hunter_level_health";
    private static final String MOD_ARMOR = "hunter_level_armor";
    private static final String MOD_TOUGHNESS = "hunter_level_toughness";
    private static final String MOD_SPEED = "hunter_level_speed";
    private static final String MOD_ATTACK_SPEED = "hunter_level_attack_speed";
    private static final String MOD_KNOCKBACK = "hunter_level_knockback";

    private HolyHunterEvents() {
    }

    public static void syncHunterAttributes(Player player) {
        if (player.level().isClientSide) {
            return;
        }
        int level = HolyHelper.getHunterLevel(player);
        if (level <= 0) {
            HolyHelper.removeModifier(player, Attributes.MAX_HEALTH, MOD_MAX_HEALTH);
            HolyHelper.removeModifier(player, Attributes.ARMOR, MOD_ARMOR);
            HolyHelper.removeModifier(player, Attributes.ARMOR_TOUGHNESS, MOD_TOUGHNESS);
            HolyHelper.removeModifier(player, Attributes.MOVEMENT_SPEED, MOD_SPEED);
            HolyHelper.removeModifier(player, Attributes.ATTACK_SPEED, MOD_ATTACK_SPEED);
            HolyHelper.removeModifier(player, Attributes.KNOCKBACK_RESISTANCE, MOD_KNOCKBACK);
            if (player.getHealth() > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }
            return;
        }

        int maxLevel = HunterPlayer.get(player).getMaxLevel();
        double exp = HolyVampirismConfig.SERVER.hunterAttributeScalingExponent.get();

        double health = Math.round(LevelAttributeModifier.calculateModifierValue(level, maxLevel, HolyVampirismConfig.SERVER.hunterMaxHealthBonus.get(), exp) / 2.0) * 2.0;
        double armor = LevelAttributeModifier.calculateModifierValue(level, maxLevel, HolyVampirismConfig.SERVER.hunterArmorBonus.get(), exp);
        double toughness = LevelAttributeModifier.calculateModifierValue(level, maxLevel, HolyVampirismConfig.SERVER.hunterArmorToughnessBonus.get(), exp);
        double speed = LevelAttributeModifier.calculateModifierValue(level, maxLevel, HolyVampirismConfig.SERVER.hunterSpeedBonus.get(), exp);
        double attackSpeed = LevelAttributeModifier.calculateModifierValue(level, maxLevel, HolyVampirismConfig.SERVER.hunterAttackSpeedBonus.get(), exp);
        double knockback = LevelAttributeModifier.calculateModifierValue(level, maxLevel, HolyVampirismConfig.SERVER.hunterKnockbackResistanceBonus.get(), exp);

        HolyHelper.setModifier(player, Attributes.MAX_HEALTH, MOD_MAX_HEALTH, health, AttributeModifier.Operation.ADD_VALUE);
        HolyHelper.setModifier(player, Attributes.ARMOR, MOD_ARMOR, armor, AttributeModifier.Operation.ADD_VALUE);
        HolyHelper.setModifier(player, Attributes.ARMOR_TOUGHNESS, MOD_TOUGHNESS, toughness, AttributeModifier.Operation.ADD_VALUE);
        HolyHelper.setModifier(player, Attributes.MOVEMENT_SPEED, MOD_SPEED, speed, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        HolyHelper.setModifier(player, Attributes.ATTACK_SPEED, MOD_ATTACK_SPEED, attackSpeed, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        HolyHelper.setModifier(player, Attributes.KNOCKBACK_RESISTANCE, MOD_KNOCKBACK, knockback, AttributeModifier.Operation.ADD_VALUE);
    }

    @SubscribeEvent
    public static void onFactionLevelChanged(PlayerFactionEvent.FactionLevelChanged event) {
        Player player = event.getPlayer().asEntity();
        syncHunterAttributes(player);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide) {
            FactionPlayerHandler.get(player).checkSkillTreeLocks();
            syncHunterAttributes(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        syncHunterAttributes(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            HolyHunterData oldData = event.getOriginal().getData(HolyDataAttachments.HUNTER_DATA);
            HolyHunterData newData = event.getEntity().getData(HolyDataAttachments.HUNTER_DATA);
            newData.setUndyingResolveReadyTick(oldData.getUndyingResolveReadyTick());
            newData.setFallImmunityUntilTick(oldData.getFallImmunityUntilTick());
        }
        syncHunterAttributes(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide || !Helper.isHunter(player)) {
            return;
        }

        // Iron Will: gentle passive health recovery below 50% HP
        if (player.tickCount % 40 == 0
                && HolyHelper.hasSkill(player, HolySkills.IRON_WILL.get())
                && player.isAlive()
                && player.getHealth() < player.getMaxHealth() * 0.5F
                && player.getFoodData().getFoodLevel() >= 6) {
            player.heal(1.0F);
        }

        // Vampiric Ward: immunity to Vampire Freeze and rapid cleansing of Neonatal/Poison debuffs
        if (player.tickCount % 10 == 0 && HolyHelper.hasSkill(player, HolySkills.VAMPIRIC_WARD.get())) {
            if (player.hasEffect(ModEffects.FREEZE)) {
                player.removeEffect(ModEffects.FREEZE);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (entity.level().isClientSide || !(entity instanceof LivingEntity living) || !living.isAlive()) {
            return;
        }
        SolarBurnData burn = living.getData(HolyDataAttachments.SOLAR_BURN);
        if (burn.getRemainingTicks() <= 0) {
            return;
        }
        int remaining = burn.getRemainingTicks();
        burn.tick();
        if (remaining % 20 == 0 && Helper.isVampire(living)) {
            living.hurt(HolyHelper.sunDamage(living.level(), null), burn.getDamagePerSecond());
            if (living.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.FLAME, living.getX(), living.getY() + living.getBbHeight() * 0.5, living.getZ(), 6, 0.25, 0.3, 0.25, 0.02);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            long until = player.getData(HolyDataAttachments.HUNTER_DATA).getFallImmunityUntilTick();
            if (player.level().getGameTime() <= until) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            if (event.getSource().is(DamageTypeTags.IS_FALL)) {
                long until = player.getData(HolyDataAttachments.HUNTER_DATA).getFallImmunityUntilTick();
                if (player.level().getGameTime() <= until) {
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide) {
            return;
        }

        // Offensive Hunter skill bonuses
        Entity attackerEntity = event.getSource().getEntity();
        if (attackerEntity instanceof Player attacker && Helper.isHunter(attacker)) {
            float damage = event.getNewDamage();
            boolean unholy = HolyHelper.isUnholyTarget(target);
            boolean vampire = Helper.isVampire(target);
            boolean projectile = event.getSource().is(DamageTypeTags.IS_PROJECTILE);

            if (unholy && HolyHelper.hasSkill(attacker, HolySkills.SILVER_EDGE.get())) {
                damage *= (float) (1.0 + HolyVampirismConfig.SERVER.silverEdgeBonusDamageMultiplier.get());
                target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0, false, false));
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0, false, false));
            }

            if (HolyHelper.hasSkill(attacker, HolySkills.BLESSED_QUIVER.get())) {
                if (projectile && unholy) {
                    damage *= (float) (1.0 + HolyVampirismConfig.SERVER.blessedQuiverDamageBonus.get());
                } else if (!projectile && vampire) {
                    ItemStack mainHand = attacker.getMainHandItem();
                    if (mainHand.getItem() instanceof StakeItem || mainHand.is(ModTags.Items.VAMPIRE_SLAYER_ITEMS)) {
                        damage *= (float) (1.0 + HolyVampirismConfig.SERVER.blessedQuiverStakeDamageBonus.get());
                    }
                }
            }

            event.setNewDamage(damage);
        }

        // Defensive Hunter bonuses
        if (target instanceof Player defender && Helper.isHunter(defender)) {
            float damage = event.getNewDamage();
            if (attackerEntity instanceof LivingEntity livingAttacker && Helper.isVampire(livingAttacker)) {
                if (HolyHelper.hasSkill(defender, HolySkills.VAMPIRIC_WARD.get())) {
                    damage *= (float) Math.max(0.15, 1.0 - HolyVampirismConfig.SERVER.vampiricWardDamageReduction.get());
                }
            }
            event.setNewDamage(damage);
        }
    }

    @SubscribeEvent
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide) {
            return;
        }

        // Solar Infusion on-hit burn & garlic debuff
        if (event.getSource().getEntity() instanceof Player attacker && Helper.isHunter(attacker) && Helper.isVampire(target)) {
            if (HolyHelper.hasSkill(attacker, HolySkills.SOLAR_INFUSION.get())) {
                int ticks = HolyVampirismConfig.SERVER.solarInfusionDurationTicks.get();
                float tickDmg = HolyVampirismConfig.SERVER.solarInfusionTickDamage.get().floatValue();
                target.getData(HolyDataAttachments.SOLAR_BURN).ignite(ticks, tickDmg);
                target.addEffect(new MobEffectInstance(ModEffects.GARLIC, 60, 0));
            }
        }

        // Divine Aegis thorns retaliation
        if (target instanceof Player defender && Helper.isHunter(defender)) {
            HunterPlayer hunter = HunterPlayer.get(defender);
            if (hunter.getActionHandler().isActionActive(HolyActions.DIVINE_AEGIS.get())
                    && event.getSource().getDirectEntity() instanceof LivingEntity meleeAttacker
                    && HolyHelper.isUnholyTarget(meleeAttacker)
                    && HolyHelper.canHarm(defender, meleeAttacker)) {
                float thorns = HolyVampirismConfig.SERVER.divineAegisThornsDamage.get().floatValue() + hunter.getLevel() * 0.4F;
                meleeAttacker.hurt(HolyHelper.holyWaterDamage(defender.level(), defender), thorns);
                meleeAttacker.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
                meleeAttacker.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0));
            }
        }
    }

    @SubscribeEvent
    public static void onCriticalHit(CriticalHitEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide || !event.isCriticalHit() || !Helper.isHunter(player)) {
            return;
        }
        if (event.getTarget() instanceof LivingEntity target && Helper.isVampire(target)) {
            if (HolyHelper.hasSkill(player, HolySkills.HOLY_FERVOR.get())) {
                float heal = HolyVampirismConfig.SERVER.holyFervorCritHeal.get().floatValue();
                if (heal > 0) {
                    player.heal(heal);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onArrowJoinWorld(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide || !(event.getEntity() instanceof AbstractArrow arrow)) {
            return;
        }
        if (!(arrow.getOwner() instanceof Player shooter) || !Helper.isHunter(shooter)) {
            return;
        }
        if (!HolyHelper.hasSkill(shooter, HolySkills.BLESSED_QUIVER.get())) {
            return;
        }

        double velMult = HolyVampirismConfig.SERVER.blessedQuiverVelocityMultiplier.get();
        if (velMult > 1.0) {
            arrow.setDeltaMovement(arrow.getDeltaMovement().scale(velMult));
        }

        if (!shooter.getAbilities().instabuild && arrow.pickup == AbstractArrow.Pickup.ALLOWED) {
            double saveChance = HolyVampirismConfig.SERVER.blessedQuiverAmmoSaveChance.get();
            if (shooter.getRandom().nextDouble() < saveChance) {
                ItemStack pickupStack = arrow.getPickupItemStackOrigin();
                if (!pickupStack.isEmpty()) {
                    ItemStack refund = pickupStack.copyWithCount(1);
                    if (!shooter.getInventory().add(refund)) {
                        shooter.getInventory().add(new ItemStack(Items.ARROW));
                    }
                    arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.level().isClientSide) {
            return;
        }
        if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        }
        if (!Helper.isHunter(player) || !HolyHelper.hasSkill(player, HolySkills.UNDYING_RESOLVE.get())) {
            return;
        }

        long now = player.level().getGameTime();
        HolyHunterData data = player.getData(HolyDataAttachments.HUNTER_DATA);
        if (now < data.getUndyingResolveReadyTick()) {
            return;
        }

        event.setCanceled(true);
        long cooldownTicks = HolyVampirismConfig.SERVER.undyingResolveCooldownSeconds.get() * 20L;
        data.setUndyingResolveReadyTick(now + cooldownTicks);

        float restored = Math.max(4.0F, player.getMaxHealth() * HolyVampirismConfig.SERVER.undyingResolveRestoreFraction.get().floatValue());
        player.setHealth(restored);
        player.removeAllEffects();
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 120, 3));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 120, 2));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 120, 2));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 120, 1));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 120, 0));

        ServerLevel serverLevel = player.serverLevel();
        AABB area = player.getBoundingBox().inflate(6.0, 3.0, 6.0);
        List<LivingEntity> nearby = serverLevel.getEntitiesOfClass(LivingEntity.class, area, e -> HolyHelper.canHarm(player, e));
        for (LivingEntity foe : nearby) {
            Vec3 push = foe.position().subtract(player.position()).normalize().scale(1.2);
            foe.push(push.x, 0.45, push.z);
            foe.hurtMarked = true;
            if (HolyHelper.isUnholyTarget(foe)) {
                foe.hurt(HolyHelper.holyWaterDamage(serverLevel, player), 10.0F);
            }
        }

        serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getY() + 1.0, player.getZ(), 45, 0.6, 0.8, 0.6, 0.2);
        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
        player.displayClientMessage(Component.translatable("message.holy_vampirism.undying_resolve.triggered"), true);
    }
}
