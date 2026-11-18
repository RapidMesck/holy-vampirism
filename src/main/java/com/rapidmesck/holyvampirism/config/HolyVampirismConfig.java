package com.rapidmesck.holyvampirism.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class HolyVampirismConfig {

    public static final Server SERVER;
    public static final ModConfigSpec SERVER_SPEC;

    static {
        Pair<Server, ModConfigSpec> serverPair = new ModConfigSpec.Builder().configure(Server::new);
        SERVER = serverPair.getLeft();
        SERVER_SPEC = serverPair.getRight();
    }

    public static class Server {
        public final ModConfigSpec.DoubleValue extraHunterSkillPointsPerLevel;

        public final ModConfigSpec.DoubleValue hunterMaxHealthBonus;
        public final ModConfigSpec.DoubleValue hunterArmorBonus;
        public final ModConfigSpec.DoubleValue hunterArmorToughnessBonus;
        public final ModConfigSpec.DoubleValue hunterSpeedBonus;
        public final ModConfigSpec.DoubleValue hunterAttackSpeedBonus;
        public final ModConfigSpec.DoubleValue hunterKnockbackResistanceBonus;
        public final ModConfigSpec.DoubleValue hunterAttributeScalingExponent;

        public final ModConfigSpec.DoubleValue silverEdgeBonusDamageMultiplier;
        public final ModConfigSpec.DoubleValue holyFervorAttackSpeedBonus;
        public final ModConfigSpec.DoubleValue holyFervorMoveSpeedBonus;
        public final ModConfigSpec.DoubleValue holyFervorCritHeal;
        public final ModConfigSpec.DoubleValue ironWillHealthBonus;
        public final ModConfigSpec.DoubleValue ironWillArmorBonus;
        public final ModConfigSpec.DoubleValue ironWillKnockbackBonus;
        public final ModConfigSpec.DoubleValue vampiricWardDamageReduction;
        public final ModConfigSpec.DoubleValue blessedQuiverDamageBonus;
        public final ModConfigSpec.DoubleValue blessedQuiverStakeDamageBonus;
        public final ModConfigSpec.DoubleValue blessedQuiverVelocityMultiplier;
        public final ModConfigSpec.DoubleValue blessedQuiverAmmoSaveChance;
        public final ModConfigSpec.DoubleValue solarInfusionTickDamage;
        public final ModConfigSpec.IntValue solarInfusionDurationTicks;
        public final ModConfigSpec.IntValue undyingResolveCooldownSeconds;
        public final ModConfigSpec.DoubleValue undyingResolveRestoreFraction;

        public final ModConfigSpec.IntValue huntersSurgeCooldownSeconds;
        public final ModConfigSpec.DoubleValue huntersSurgeVelocity;
        public final ModConfigSpec.IntValue holySmiteCooldownSeconds;
        public final ModConfigSpec.DoubleValue holySmiteBaseDamage;
        public final ModConfigSpec.DoubleValue holySmiteRadius;
        public final ModConfigSpec.IntValue sanctuaryAuraDurationSeconds;
        public final ModConfigSpec.IntValue sanctuaryAuraCooldownSeconds;
        public final ModConfigSpec.DoubleValue sanctuaryAuraRadius;
        public final ModConfigSpec.DoubleValue sanctuaryAuraVampireDamage;
        public final ModConfigSpec.IntValue divineAegisDurationSeconds;
        public final ModConfigSpec.IntValue divineAegisCooldownSeconds;
        public final ModConfigSpec.DoubleValue divineAegisThornsDamage;
        public final ModConfigSpec.IntValue solarFlareCooldownSeconds;
        public final ModConfigSpec.DoubleValue solarFlareBaseDamage;
        public final ModConfigSpec.DoubleValue solarFlareRadius;

        public Server(ModConfigSpec.Builder builder) {
            builder.comment("Hunter Skill Point Progression").push("progression");
            extraHunterSkillPointsPerLevel = builder
                    .comment("Extra skill points granted per Hunter level (on top of Vampirism's skillPointsPerLevel) so Hunters can unlock the Holy Hunter tree.")
                    .defineInRange("extraHunterSkillPointsPerLevel", 2.5, 0.0, 10.0);
            builder.pop();

            builder.comment("Level-scaled passive attribute bonuses for Hunters (designed to match Vampire passive scaling)").push("levelAttributes");
            hunterMaxHealthBonus = builder
                    .comment("Bonus Max Health added to Hunters at max level (20.0 = +10 hearts, matching GodlyVampirism Vampire health).")
                    .defineInRange("hunterMaxHealthBonus", 20.0, 0.0, 100.0);
            hunterArmorBonus = builder
                    .comment("Bonus Natural Armor added to Hunters at max level.")
                    .defineInRange("hunterArmorBonus", 16.0, 0.0, 60.0);
            hunterArmorToughnessBonus = builder
                    .comment("Bonus Armor Toughness added to Hunters at max level.")
                    .defineInRange("hunterArmorToughnessBonus", 8.0, 0.0, 40.0);
            hunterSpeedBonus = builder
                    .comment("Multiplicative Movement Speed bonus at max Hunter level (0.35 = +35%).")
                    .defineInRange("hunterSpeedBonus", 0.35, 0.0, 3.0);
            hunterAttackSpeedBonus = builder
                    .comment("Multiplicative Attack Speed bonus at max Hunter level (0.25 = +25%).")
                    .defineInRange("hunterAttackSpeedBonus", 0.25, 0.0, 3.0);
            hunterKnockbackResistanceBonus = builder
                    .comment("Knockback Resistance added to Hunters at max level (0.30 = 30%).")
                    .defineInRange("hunterKnockbackResistanceBonus", 0.30, 0.0, 1.0);
            hunterAttributeScalingExponent = builder
                    .comment("Scaling curve exponent with Hunter level (0.5 = square root like Vampires, 1.0 = linear).")
                    .defineInRange("hunterAttributeScalingExponent", 0.5, 0.25, 2.0);
            builder.pop();

            builder.comment("Holy Hunter Skill Tuning").push("skills");
            silverEdgeBonusDamageMultiplier = builder
                    .comment("Bonus damage multiplier from Silver Edge against Vampires and Undead (0.25 = +25%).")
                    .defineInRange("silverEdgeBonusDamageMultiplier", 0.25, 0.0, 3.0);
            holyFervorAttackSpeedBonus = builder
                    .comment("Multiplicative attack speed bonus from Holy Fervor.")
                    .defineInRange("holyFervorAttackSpeedBonus", 0.15, 0.0, 2.0);
            holyFervorMoveSpeedBonus = builder
                    .comment("Multiplicative movement speed bonus from Holy Fervor.")
                    .defineInRange("holyFervorMoveSpeedBonus", 0.15, 0.0, 2.0);
            holyFervorCritHeal = builder
                    .comment("Health restored when landing a melee critical hit against a Vampire with Holy Fervor.")
                    .defineInRange("holyFervorCritHeal", 2.0, 0.0, 20.0);
            ironWillHealthBonus = builder
                    .comment("Flat max health bonus granted by Iron Will.")
                    .defineInRange("ironWillHealthBonus", 6.0, 0.0, 40.0);
            ironWillArmorBonus = builder
                    .comment("Flat armor bonus granted by Iron Will.")
                    .defineInRange("ironWillArmorBonus", 4.0, 0.0, 30.0);
            ironWillKnockbackBonus = builder
                    .comment("Knockback resistance bonus granted by Iron Will.")
                    .defineInRange("ironWillKnockbackBonus", 0.3, 0.0, 1.0);
            vampiricWardDamageReduction = builder
                    .comment("Incoming damage reduction from Vampires with Vampiric Ward (0.20 = 20% reduced damage).")
                    .defineInRange("vampiricWardDamageReduction", 0.20, 0.0, 0.85);
            blessedQuiverDamageBonus = builder
                    .comment("Crossbow/arrow bonus damage multiplier vs Vampires and Undead with Blessed Quiver (0.25 = +25%).")
                    .defineInRange("blessedQuiverDamageBonus", 0.25, 0.0, 3.0);
            blessedQuiverStakeDamageBonus = builder
                    .comment("Bonus melee damage multiplier with Stakes & Hunter weapons against Vampires with Blessed Quiver.")
                    .defineInRange("blessedQuiverStakeDamageBonus", 0.15, 0.0, 3.0);
            blessedQuiverVelocityMultiplier = builder
                    .comment("Multiplier applied to crossbow bolt velocity with Blessed Quiver.")
                    .defineInRange("blessedQuiverVelocityMultiplier", 1.25, 1.0, 2.5);
            blessedQuiverAmmoSaveChance = builder
                    .comment("Chance to refund an arrow/quarrel when firing with Blessed Quiver (0.35 = 35%).")
                    .defineInRange("blessedQuiverAmmoSaveChance", 0.35, 0.0, 1.0);
            solarInfusionTickDamage = builder
                    .comment("Sun damage dealt per second by Solar Infusion's Solar Burn on Vampires.")
                    .defineInRange("solarInfusionTickDamage", 3.0, 0.5, 30.0);
            solarInfusionDurationTicks = builder
                    .comment("Duration of Solar Burn in ticks (80 = 4 seconds).")
                    .defineInRange("solarInfusionDurationTicks", 80, 20, 600);
            undyingResolveCooldownSeconds = builder
                    .comment("Cooldown of Undying Resolve cheat-death in seconds.")
                    .defineInRange("undyingResolveCooldownSeconds", 120, 10, 1800);
            undyingResolveRestoreFraction = builder
                    .comment("Fraction of max health restored when Undying Resolve triggers.")
                    .defineInRange("undyingResolveRestoreFraction", 0.35, 0.05, 1.0);
            builder.pop();

            builder.comment("Holy Hunter Action Tuning").push("actions");
            huntersSurgeCooldownSeconds = builder
                    .defineInRange("huntersSurgeCooldownSeconds", 5, 1, 120);
            huntersSurgeVelocity = builder
                    .defineInRange("huntersSurgeVelocity", 1.45, 0.4, 4.0);
            holySmiteCooldownSeconds = builder
                    .defineInRange("holySmiteCooldownSeconds", 25, 1, 300);
            holySmiteBaseDamage = builder
                    .defineInRange("holySmiteBaseDamage", 10.0, 1.0, 100.0);
            holySmiteRadius = builder
                    .defineInRange("holySmiteRadius", 6.5, 2.0, 20.0);
            sanctuaryAuraDurationSeconds = builder
                    .defineInRange("sanctuaryAuraDurationSeconds", 15, 1, 120);
            sanctuaryAuraCooldownSeconds = builder
                    .defineInRange("sanctuaryAuraCooldownSeconds", 45, 1, 300);
            sanctuaryAuraRadius = builder
                    .defineInRange("sanctuaryAuraRadius", 7.0, 2.0, 20.0);
            sanctuaryAuraVampireDamage = builder
                    .defineInRange("sanctuaryAuraVampireDamage", 3.5, 0.5, 40.0);
            divineAegisDurationSeconds = builder
                    .defineInRange("divineAegisDurationSeconds", 20, 1, 120);
            divineAegisCooldownSeconds = builder
                    .defineInRange("divineAegisCooldownSeconds", 50, 1, 300);
            divineAegisThornsDamage = builder
                    .defineInRange("divineAegisThornsDamage", 4.0, 0.0, 40.0);
            solarFlareCooldownSeconds = builder
                    .defineInRange("solarFlareCooldownSeconds", 30, 1, 300);
            solarFlareBaseDamage = builder
                    .defineInRange("solarFlareBaseDamage", 14.0, 1.0, 120.0);
            solarFlareRadius = builder
                    .defineInRange("solarFlareRadius", 9.0, 2.0, 24.0);
            builder.pop();
        }
    }
}
