package com.rapidmesck.holyvampirism.registry;

import com.mojang.datafixers.util.Either;
import com.rapidmesck.holyvampirism.HolyVampirismMod;
import com.rapidmesck.holyvampirism.config.HolyVampirismConfig;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.factions.ISkillNode;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.entity.player.skills.ActionSkill;
import de.teamlapen.vampirism.entity.player.skills.VampirismSkill;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class HolySkills {

    public static final DeferredRegister<ISkill<?>> SKILLS =
            DeferredRegister.create(VampirismRegistries.Keys.SKILL, HolyVampirismMod.MODID);

    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> HOLY_HUNTER_ROOT =
            SKILLS.register("holy_hunter_root", () -> new HolyHunterSkill(0, true));

    // Branch 1: Divine Combat
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> SILVER_EDGE =
            SKILLS.register("silver_edge", () -> new HolyHunterSkill(2, true));

    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> HOLY_FERVOR =
            SKILLS.register("holy_fervor", () -> new HolyHunterSkill(2, true)
                    .registerAttributeModifier(Attributes.ATTACK_SPEED, () -> HolyVampirismConfig.SERVER.holyFervorAttackSpeedBonus.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .registerAttributeModifier(Attributes.MOVEMENT_SPEED, () -> HolyVampirismConfig.SERVER.holyFervorMoveSpeedBonus.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> HOLY_SMITE =
            SKILLS.register("holy_smite", () -> new ActionSkill<>(HolyActions.HOLY_SMITE, Trees.HOLY_HUNTER, 3, true));

    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> HUNTERS_SURGE =
            SKILLS.register("hunters_surge", () -> new ActionSkill<>(HolyActions.HUNTERS_SURGE, Trees.HOLY_HUNTER, 3, true));

    // Branch 2: Sacred Bastion
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> IRON_WILL =
            SKILLS.register("iron_will", () -> new HolyHunterSkill(2, true)
                    .registerAttributeModifier(Attributes.MAX_HEALTH, () -> HolyVampirismConfig.SERVER.ironWillHealthBonus.get(), AttributeModifier.Operation.ADD_VALUE)
                    .registerAttributeModifier(Attributes.ARMOR, () -> HolyVampirismConfig.SERVER.ironWillArmorBonus.get(), AttributeModifier.Operation.ADD_VALUE)
                    .registerAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, () -> HolyVampirismConfig.SERVER.ironWillKnockbackBonus.get(), AttributeModifier.Operation.ADD_VALUE));

    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> VAMPIRIC_WARD =
            SKILLS.register("vampiric_ward", () -> new HolyHunterSkill(2, true));

    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> SANCTUARY_AURA =
            SKILLS.register("sanctuary_aura", () -> new ActionSkill<>(HolyActions.SANCTUARY_AURA, Trees.HOLY_HUNTER, 3, true));

    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> DIVINE_AEGIS =
            SKILLS.register("divine_aegis", () -> new ActionSkill<>(HolyActions.DIVINE_AEGIS, Trees.HOLY_HUNTER, 3, true));

    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> UNDYING_RESOLVE =
            SKILLS.register("undying_resolve", () -> new HolyHunterSkill(4, true));

    // Branch 3: Arcane Arsenal
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> BLESSED_QUIVER =
            SKILLS.register("blessed_quiver", () -> new HolyHunterSkill(2, true));

    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> SOLAR_INFUSION =
            SKILLS.register("solar_infusion", () -> new HolyHunterSkill(3, true));

    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> SOLAR_FLARE =
            SKILLS.register("solar_flare", () -> new ActionSkill<>(HolyActions.SOLAR_FLARE, Trees.HOLY_HUNTER, 3, true));

    public static class HolyHunterSkill extends VampirismSkill<IHunterPlayer> {
        public HolyHunterSkill(int skillPointCost, boolean desc) {
            super(Either.left(Trees.HOLY_HUNTER), skillPointCost, desc);
        }

        @Override
        public @NotNull Optional<IPlayableFaction<?>> getFaction() {
            return Optional.of(VReference.HUNTER_FACTION);
        }
    }

    public static final class Trees {
        public static final ResourceKey<ISkillTree> HOLY_HUNTER =
                ResourceKey.create(VampirismRegistries.Keys.SKILL_TREE, HolyVampirismMod.id("holy_hunter"));

        private Trees() {
        }
    }

    public static final class Nodes {
        public static final ResourceKey<ISkillNode> ROOT = node("root");
        public static final ResourceKey<ISkillNode> SILVER_EDGE = node("silver_edge");
        public static final ResourceKey<ISkillNode> HOLY_FERVOR = node("holy_fervor");
        public static final ResourceKey<ISkillNode> HOLY_SMITE = node("holy_smite");
        public static final ResourceKey<ISkillNode> HUNTERS_SURGE = node("hunters_surge");
        public static final ResourceKey<ISkillNode> IRON_WILL = node("iron_will");
        public static final ResourceKey<ISkillNode> VAMPIRIC_WARD = node("vampiric_ward");
        public static final ResourceKey<ISkillNode> SANCTUARY_AURA = node("sanctuary_aura");
        public static final ResourceKey<ISkillNode> DIVINE_AEGIS = node("divine_aegis");
        public static final ResourceKey<ISkillNode> UNDYING_RESOLVE = node("undying_resolve");
        public static final ResourceKey<ISkillNode> BLESSED_QUIVER = node("blessed_quiver");
        public static final ResourceKey<ISkillNode> SOLAR_INFUSION = node("solar_infusion");
        public static final ResourceKey<ISkillNode> SOLAR_FLARE = node("solar_flare");

        private static ResourceKey<ISkillNode> node(String path) {
            return ResourceKey.create(VampirismRegistries.Keys.SKILL_NODE, HolyVampirismMod.id("holy_hunter/" + path));
        }

        private Nodes() {
        }
    }
}
