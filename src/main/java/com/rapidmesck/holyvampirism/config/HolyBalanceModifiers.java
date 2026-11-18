package com.rapidmesck.holyvampirism.config;

import com.rapidmesck.holyvampirism.HolyVampirismMod;
import de.teamlapen.vampirism.config.BalanceBuilder;
import de.teamlapen.vampirism.config.BalanceConfig;
import de.teamlapen.vampirism.config.VampirismConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Applies Hunter-focused balance modifications to Vampirism's {@link BalanceConfig},
 * analogous to GodlyVampirism's modifications for Vampires.
 */
public final class HolyBalanceModifiers {

    private static final Set<Spec<?>> SPECS = new LinkedHashSet<>();
    private static List<Supplier<Optional<Spec<?>>>> pendingSuppliers = new ArrayList<>();
    private static boolean registered = false;

    private HolyBalanceModifiers() {
    }

    public static void registerDefaults() {
        if (registered) {
            return;
        }
        registered = true;

        // Crossbow, Stake, and Holy Water buffs
        addInteger("arrowVampireKillerMaxHealth", 100);
        addBoolean("allowInfiniteSpecialArrows", true);
        addDouble("crossbowDamageMult", 1.6);
        addInteger("holyWaterSplashDamage", 9);
        addDouble("holyWaterTierDamageInc", 3.0);
        addInteger("holyWaterNauseaDuration", 300);
        addInteger("holyWaterBlindnessDuration", 240);

        // Hunter player & skills
        addDouble("hpStrengthMaxMod", 0.75);
        addDouble("hsSmallAttackSpeedModifier", 0.35);
        addDouble("hsMajorAttackSpeedModifier", 0.75);
        addDouble("hsSmallAttackDamageModifier", 2.5);
        addDouble("hsInstantKill1MaxHealth", 0.45);
        addInteger("hsInstantKill2MaxHealth", 350);
        addInteger("hsGarlicDiffuserNormalDist", 1);
        addInteger("hsGarlicDiffuserEnhancedDist", 2);
        addInteger("hsGarlicDiffuserWeakDist", 3);

        // Existing Hunter actions
        addInteger("haDisguiseInvisibleSQ", 128);
        addDouble("haDisguiseVisibilityMod", 0.1);
        addInteger("haAwarenessRadius", 45);
        addInteger("haPotionResistanceDuration", 800);
        addInteger("haPotionResistanceCooldown", 600);

        // Minions
        addInteger("miMinionPerLordLevel", 2);
        addInteger("miEquipmentRepairAmount", 20);
    }

    public static void resolveSpecs() {
        if (pendingSuppliers == null) {
            return;
        }
        for (Supplier<Optional<Spec<?>>> supplier : pendingSuppliers) {
            supplier.get().ifPresent(SPECS::add);
        }
        pendingSuppliers = null;
    }

    public static int applyAll() {
        int count = 0;
        Spec<?> last = null;
        for (Spec<?> spec : SPECS) {
            spec.apply(false);
            last = spec;
            count++;
        }
        if (last != null) {
            last.apply(true);
        }
        return count;
    }

    private static <Q> void queueSpec(String fieldName, Q value) {
        pendingSuppliers.add(() -> {
            try {
                Field field = BalanceConfig.class.getDeclaredField(fieldName);
                @SuppressWarnings("unchecked")
                ModConfigSpec.ConfigValue<Q> configValue = (ModConfigSpec.ConfigValue<Q>) field.get(VampirismConfig.BALANCE);
                return Optional.of(new Spec<>(configValue, value));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                HolyVampirismMod.LOGGER.error("Failed to bind Vampirism BalanceConfig field '{}'", fieldName, e);
                return Optional.empty();
            }
        });
    }

    private static void addBoolean(String key, boolean def) {
        VampirismConfig.addBalanceModification(key, conf -> {
            try {
                ((BalanceBuilder.BoolConf) conf).setDefaultValue(def);
                conf.comment(conf.getComment() + " [Buffed by holy_vampirism]");
            } catch (Exception e) {
                HolyVampirismMod.LOGGER.error("Failed to modify boolean balance key '{}'", key, e);
            }
        });
        queueSpec(key, def);
    }

    private static void addDouble(String key, double def) {
        VampirismConfig.addBalanceModification(key, conf -> {
            try {
                ((BalanceBuilder.DoubleConf) conf).setDefaultValue(def);
                conf.comment(conf.getComment() + " [Buffed by holy_vampirism]");
            } catch (Exception e) {
                HolyVampirismMod.LOGGER.error("Failed to modify double balance key '{}'", key, e);
            }
        });
        queueSpec(key, def);
    }

    private static void addInteger(String key, int def) {
        VampirismConfig.addBalanceModification(key, conf -> {
            try {
                ((BalanceBuilder.IntConf) conf).setDefaultValue(def);
                conf.comment(conf.getComment() + " [Buffed by holy_vampirism]");
            } catch (Exception e) {
                HolyVampirismMod.LOGGER.error("Failed to modify int balance key '{}'", key, e);
            }
        });
        queueSpec(key, def);
    }

    private record Spec<T>(ModConfigSpec.ConfigValue<T> configValue, T targetValue) {
        void apply(boolean save) {
            configValue.set(targetValue);
            if (save) {
                configValue.save();
            }
        }
    }
}
