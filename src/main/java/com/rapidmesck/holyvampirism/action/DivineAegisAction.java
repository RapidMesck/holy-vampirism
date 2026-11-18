package com.rapidmesck.holyvampirism.action;

import com.rapidmesck.holyvampirism.config.HolyVampirismConfig;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.hunter.DefaultHunterAction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class DivineAegisAction extends DefaultHunterAction implements ILastingAction<IHunterPlayer> {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public int getDuration(IHunterPlayer player) {
        return HolyVampirismConfig.SERVER.divineAegisDurationSeconds.get() * 20;
    }

    @Override
    public int getCooldown(IHunterPlayer player) {
        return HolyVampirismConfig.SERVER.divineAegisCooldownSeconds.get() * 20;
    }

    @Override
    protected boolean activate(@NotNull IHunterPlayer hunter, ActivationContext context) {
        Player player = hunter.asEntity();
        applyEffects(hunter);
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ENCHANT, player.getX(), player.getY() + 1.0, player.getZ(), 24, 0.5, 0.7, 0.5, 0.2);
            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_NETHERITE.value(), SoundSource.PLAYERS, 1.0F, 1.2F);
        }
        return true;
    }

    @Override
    public boolean onUpdate(@NotNull IHunterPlayer hunter) {
        if (!hunter.isRemote() && hunter.asEntity().tickCount % 20 == 0) {
            applyEffects(hunter);
        }
        return false;
    }

    private void applyEffects(IHunterPlayer hunter) {
        addEffectInstance(hunter, new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 45, 1, false, false, true));
        addEffectInstance(hunter, new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 45, 0, false, false, true));
        if (!hunter.asEntity().hasEffect(MobEffects.ABSORPTION)) {
            addEffectInstance(hunter, new MobEffectInstance(MobEffects.ABSORPTION, getDuration(hunter), 2, false, false, true));
        }
    }

    @Override
    public void onDeactivated(@NotNull IHunterPlayer player) {
        removePotionEffect(player, MobEffects.DAMAGE_RESISTANCE);
        removePotionEffect(player, MobEffects.FIRE_RESISTANCE);
        removePotionEffect(player, MobEffects.ABSORPTION);
    }

    @Override
    public void onReActivated(@NotNull IHunterPlayer player) {
        applyEffects(player);
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
