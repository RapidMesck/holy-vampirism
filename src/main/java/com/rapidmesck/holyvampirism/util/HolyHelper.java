package com.rapidmesck.holyvampirism.util;

import com.rapidmesck.holyvampirism.HolyVampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.actions.IActionHandlerEntity;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.core.ModDamageTypes;
import de.teamlapen.vampirism.entity.action.EntityActions;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class HolyHelper {

    private HolyHelper() {
    }

    public static Optional<HunterPlayer> getHunter(Player player) {
        if (!Helper.isHunter(player)) {
            return Optional.empty();
        }
        return Optional.of(HunterPlayer.get(player));
    }

    public static int getHunterLevel(Player player) {
        if (!Helper.isHunter(player)) {
            return 0;
        }
        return FactionPlayerHandler.get(player).getCurrentLevel(VReference.HUNTER_FACTION);
    }

    public static boolean hasSkill(Player player, ISkill<IHunterPlayer> skill) {
        return getHunter(player)
                .map(h -> h.getSkillHandler().isSkillEnabled(skill))
                .orElse(false);
    }

    public static boolean isUnholyTarget(LivingEntity entity) {
        return Helper.isVampire(entity) || entity.getType().is(EntityTypeTags.UNDEAD);
    }

    public static boolean canHarm(Player source, LivingEntity target) {
        if (target == source || !target.isAlive()) {
            return false;
        }
        if (target instanceof Player targetPlayer) {
            return source.canHarmPlayer(targetPlayer);
        }
        return true;
    }

    public static void stripVampireStealth(LivingEntity target) {
        if (!Helper.isVampire(target)) {
            return;
        }
        if (target instanceof Player targetPlayer) {
            IActionHandler<IVampirePlayer> actionHandler = VampirePlayer.get(targetPlayer).getActionHandler();
            actionHandler.deactivateAction(VampireActions.DISGUISE_VAMPIRE.get());
            actionHandler.deactivateAction(VampireActions.VAMPIRE_INVISIBILITY.get());
        } else if (target instanceof IEntityActionUser actionUser) {
            IActionHandlerEntity handler = actionUser.getActionHandler();
            if (handler.isActionActive(EntityActions.ENTITY_INVISIBLE.get())) {
                handler.deactivateAction();
            }
        }
    }

    public static DamageSource attributedDamage(Level level, ResourceKey<DamageType> type, @Nullable Entity attacker) {
        var holder = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type);
        return attacker != null ? new DamageSource(holder, attacker) : new DamageSource(holder);
    }

    public static DamageSource holyWaterDamage(Level level, @Nullable Entity attacker) {
        return attributedDamage(level, ModDamageTypes.HOLY_WATER, attacker);
    }

    public static DamageSource sunDamage(Level level, @Nullable Entity attacker) {
        return attributedDamage(level, ModDamageTypes.SUN_DAMAGE, attacker);
    }

    public static void setModifier(Player player, Holder<Attribute> attribute, String path, double amount, AttributeModifier.Operation operation) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        ResourceLocation id = HolyVampirismMod.id(path);
        instance.removeModifier(id);
        if (amount != 0.0) {
            instance.addPermanentModifier(new AttributeModifier(id, amount, operation));
        }
    }

    public static void removeModifier(Player player, Holder<Attribute> attribute, String path) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            instance.removeModifier(HolyVampirismMod.id(path));
        }
    }
}
