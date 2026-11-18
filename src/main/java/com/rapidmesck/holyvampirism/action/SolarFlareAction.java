package com.rapidmesck.holyvampirism.action;

import com.rapidmesck.holyvampirism.config.HolyVampirismConfig;
import com.rapidmesck.holyvampirism.util.HolyHelper;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.hunter.DefaultHunterAction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SolarFlareAction extends DefaultHunterAction {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public int getCooldown(IHunterPlayer player) {
        return HolyVampirismConfig.SERVER.solarFlareCooldownSeconds.get() * 20;
    }

    @Override
    protected boolean activate(@NotNull IHunterPlayer hunter, ActivationContext context) {
        Player player = hunter.asEntity();
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return true;
        }

        HitResult hit = UtilLib.getPlayerLookingSpot(player, 24);
        Vec3 center = hit.getType() != HitResult.Type.MISS ? hit.getLocation() : player.position();

        double radius = HolyVampirismConfig.SERVER.solarFlareRadius.get();
        float damage = HolyVampirismConfig.SERVER.solarFlareBaseDamage.get().floatValue() + hunter.getLevel();

        DamageSource sunSource = HolyHelper.sunDamage(serverLevel, player);
        DamageSource holySource = HolyHelper.holyWaterDamage(serverLevel, player);

        AABB area = AABB.ofSize(center, radius * 2.0, radius * 1.5, radius * 2.0);
        List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class, area, e -> HolyHelper.canHarm(player, e));

        for (LivingEntity target : targets) {
            if (target.distanceToSqr(center) > radius * radius) {
                continue;
            }
            if (Helper.isVampire(target)) {
                target.hurt(sunSource, damage);
                target.addEffect(new MobEffectInstance(ModEffects.GARLIC, 120, 1));
                target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 80, 0));
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
                target.igniteForSeconds(5);
                HolyHelper.stripVampireStealth(target);
            } else if (HolyHelper.isUnholyTarget(target)) {
                target.hurt(holySource, damage * 0.85F);
                target.igniteForSeconds(5);
            }
        }

        addEffectInstance(hunter, new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, false, false, true));
        addEffectInstance(hunter, new MobEffectInstance(MobEffects.DIG_SPEED, 300, 1, false, false, true));

        serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, center.x, center.y + 0.5, center.z, 1, 0, 0, 0, 0);
        serverLevel.sendParticles(ParticleTypes.LAVA, center.x, center.y + 0.5, center.z, 32, radius * 0.35, 0.5, radius * 0.35, 0.1);
        serverLevel.playSound(null, center.x, center.y, center.z, SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.2F, 0.85F);

        return true;
    }

    @Override
    public boolean showHudCooldown(Player player) {
        return true;
    }
}
