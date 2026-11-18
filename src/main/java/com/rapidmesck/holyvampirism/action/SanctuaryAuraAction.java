package com.rapidmesck.holyvampirism.action;

import com.rapidmesck.holyvampirism.config.HolyVampirismConfig;
import com.rapidmesck.holyvampirism.util.HolyHelper;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.hunter.DefaultHunterAction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SanctuaryAuraAction extends DefaultHunterAction implements ILastingAction<IHunterPlayer> {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public int getDuration(IHunterPlayer player) {
        return HolyVampirismConfig.SERVER.sanctuaryAuraDurationSeconds.get() * 20;
    }

    @Override
    public int getCooldown(IHunterPlayer player) {
        return HolyVampirismConfig.SERVER.sanctuaryAuraCooldownSeconds.get() * 20;
    }

    @Override
    protected boolean activate(@NotNull IHunterPlayer hunter, ActivationContext context) {
        Player player = hunter.asEntity();
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.85F, 1.3F);
        }
        pulseAura(hunter);
        return true;
    }

    @Override
    public boolean onUpdate(@NotNull IHunterPlayer hunter) {
        Player player = hunter.asEntity();
        if (!player.level().isClientSide && player.tickCount % 20 == 0) {
            pulseAura(hunter);
        }
        return false;
    }

    private void pulseAura(IHunterPlayer hunter) {
        Player player = hunter.asEntity();
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        double radius = HolyVampirismConfig.SERVER.sanctuaryAuraRadius.get();
        float auraDamage = HolyVampirismConfig.SERVER.sanctuaryAuraVampireDamage.get().floatValue() + hunter.getLevel() * 0.25F;
        DamageSource source = HolyHelper.holyWaterDamage(serverLevel, player);

        player.heal(1.5F);
        addEffectInstance(hunter, new MobEffectInstance(MobEffects.REGENERATION, 45, 1, false, false, true));
        addEffectInstance(hunter, new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 45, 0, false, false, true));

        AABB area = player.getBoundingBox().inflate(radius, 3.0, radius);
        List<LivingEntity> entities = serverLevel.getEntitiesOfClass(LivingEntity.class, area, LivingEntity::isAlive);

        for (LivingEntity entity : entities) {
            if (entity == player || entity.distanceToSqr(player) > radius * radius) {
                continue;
            }
            if (Helper.isHunter(entity) || entity instanceof Villager) {
                entity.heal(1.0F);
                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 45, 0, true, false));
            } else if (HolyHelper.isUnholyTarget(entity) && HolyHelper.canHarm(player, entity)) {
                entity.hurt(source, auraDamage);
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1));
                HolyHelper.stripVampireStealth(entity);
            }
        }

        for (int i = 0; i < 16; i++) {
            double angle = (Math.PI * 2.0 * i) / 16.0;
            double px = player.getX() + Math.cos(angle) * (radius * 0.65);
            double pz = player.getZ() + Math.sin(angle) * (radius * 0.65);
            serverLevel.sendParticles(ParticleTypes.GLOW, px, player.getY() + 0.35, pz, 1, 0.05, 0.15, 0.05, 0.01);
        }
    }

    @Override
    public void onDeactivated(@NotNull IHunterPlayer player) {
        removePotionEffect(player, MobEffects.REGENERATION);
        removePotionEffect(player, MobEffects.DAMAGE_RESISTANCE);
    }

    @Override
    public void onReActivated(@NotNull IHunterPlayer player) {
    }

    @Override
    public void onActivatedClient(@NotNull IHunterPlayer player) {
    }

    @Override
    public boolean showHudCooldown(Player player) {
        return true;
    }

    @Override
    public boolean showHudDuration(Player player) {
        return true;
    }
}
