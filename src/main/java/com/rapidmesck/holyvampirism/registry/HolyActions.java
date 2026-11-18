package com.rapidmesck.holyvampirism.registry;

import com.rapidmesck.holyvampirism.HolyVampirismMod;
import com.rapidmesck.holyvampirism.action.DivineAegisAction;
import com.rapidmesck.holyvampirism.action.HolySmiteAction;
import com.rapidmesck.holyvampirism.action.HuntersSurgeAction;
import com.rapidmesck.holyvampirism.action.SanctuaryAuraAction;
import com.rapidmesck.holyvampirism.action.SolarFlareAction;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HolyActions {

    public static final DeferredRegister<IAction<?>> ACTIONS =
            DeferredRegister.create(VampirismRegistries.Keys.ACTION, HolyVampirismMod.MODID);

    public static final DeferredHolder<IAction<?>, HuntersSurgeAction> HUNTERS_SURGE =
            ACTIONS.register("hunters_surge", HuntersSurgeAction::new);

    public static final DeferredHolder<IAction<?>, HolySmiteAction> HOLY_SMITE =
            ACTIONS.register("holy_smite", HolySmiteAction::new);

    public static final DeferredHolder<IAction<?>, SanctuaryAuraAction> SANCTUARY_AURA =
            ACTIONS.register("sanctuary_aura", SanctuaryAuraAction::new);

    public static final DeferredHolder<IAction<?>, DivineAegisAction> DIVINE_AEGIS =
            ACTIONS.register("divine_aegis", DivineAegisAction::new);

    public static final DeferredHolder<IAction<?>, SolarFlareAction> SOLAR_FLARE =
            ACTIONS.register("solar_flare", SolarFlareAction::new);
}
