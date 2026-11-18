package com.rapidmesck.holyvampirism.action;

import com.rapidmesck.holyvampirism.config.HolyVampirismConfig;
import com.rapidmesck.holyvampirism.util.HolyHelper;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HolySmiteAction extends DefaultHunterAction {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public int getCooldown(IHunterPlayer player) {
        return HolyVampirismConfig.SERVER.holySmiteCooldownSeconds.get() * 20;
    }

    @Override
    protected boolean activate(@NotNull IHunterPlayer hunter, ActivationContext context) {
        Player player = hunter.asEntity();
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return true;
        }

        double radius = HolyVampirismConfig.SERVER.holySmiteRadius.get();
        float damage = HolyVampirismConfig.SERVER.holySmiteBaseDamage.get().floatValue() + hunter.getLevel() * 0.85F;
        DamageSource source = HolyHelper.holyWaterDamage(serverLevel, player);

        AABB area = player.getBoundingBox().inflate(radius, 3.0, radius);
        List<LivingEntity> targets = serverLevel.getEntitiesOfClass(LivingEntity.class, area, e -> HolyHelper.canHarm(player, e));

        for (LivingEntity target : targets) {
            if (target.distanceToSqr(player) > radius * radius) {
                continue;
            }
            boolean unholy = HolyHelper.isUnholyTarget(target);
            float dealt = unholy ? damage : damage * 0.45F;
            target.hurt(source, dealt);

            Vec3 push = target.position().subtract(player.position()).normalize().scale(0.85);
            target.push(push.x, 0.35, push.z);
            target.hurtMarked = true;

            if (Helper.isVampire(target)) {
                HolyHelper.stripVampireStealth(target);
                target.igniteForSeconds(4);
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));
            }
        }

        addEffectInstance(hunter, new MobEffectInstance(MobEffects.DAMAGE_BOOST, 160, 1, false, false, true));
        addEffectInstance(hunter, new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 160, 1, false, false, true));

        serverLevel.sendParticles(ParticleTypes.FLASH, player.getX(), player.getY() + 1.0, player.getZ(), 1, 0, 0, 0, 0);
        serverLevel.sendParticles(ParticleTypes.FLAME, player.getX(), player.getY() + 0.5, player.getZ(), 36, radius * 0.4, 0.4, radius * 0.4, 0.06);
        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 0.7F, 1.35F);

        return true;
    }

    @Override
    public boolean showHudCooldown(Player player) {
        return true;
    }
}
