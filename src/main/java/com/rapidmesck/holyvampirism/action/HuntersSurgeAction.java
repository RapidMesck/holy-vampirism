package com.rapidmesck.holyvampirism.action;

import com.rapidmesck.holyvampirism.config.HolyVampirismConfig;
import com.rapidmesck.holyvampirism.registry.HolyDataAttachments;
import de.teamlapen.vampirism.api.entity.player.hunter.DefaultHunterAction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class HuntersSurgeAction extends DefaultHunterAction {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public int getCooldown(IHunterPlayer player) {
        return HolyVampirismConfig.SERVER.huntersSurgeCooldownSeconds.get() * 20;
    }

    @Override
    public boolean canBeUsedBy(IHunterPlayer player) {
        return !player.asEntity().isSpectator();
    }

    @Override
    protected boolean activate(@NotNull IHunterPlayer hunter, ActivationContext context) {
        Player player = hunter.asEntity();
        double speed = HolyVampirismConfig.SERVER.huntersSurgeVelocity.get();
        Vec3 look = player.getLookAngle().normalize();

        double vy = Math.max(0.28, look.y * speed + 0.18);
        Vec3 dash = new Vec3(look.x * speed, Math.min(1.1, vy), look.z * speed);

        player.setDeltaMovement(dash);
        player.hurtMarked = true;
        player.resetFallDistance();

        long immunityUntil = player.level().getGameTime() + 70L;
        player.getData(HolyDataAttachments.HUNTER_DATA).setFallImmunityUntilTick(immunityUntil);

        addEffectInstance(hunter, new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 80, 2, false, false, true));

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1.0, player.getZ(), 18, 0.35, 0.5, 0.35, 0.08);
            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_RIPTIDE_2.value(), SoundSource.PLAYERS, 0.9F, 1.25F);
        }
        return true;
    }

    @Override
    public boolean showHudCooldown(Player player) {
        return true;
    }
}
