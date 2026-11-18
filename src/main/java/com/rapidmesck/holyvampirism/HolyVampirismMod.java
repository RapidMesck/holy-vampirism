package com.rapidmesck.holyvampirism;

import com.mojang.logging.LogUtils;
import com.rapidmesck.holyvampirism.command.HolyCommands;
import com.rapidmesck.holyvampirism.config.HolyBalanceModifiers;
import com.rapidmesck.holyvampirism.config.HolyVampirismConfig;
import com.rapidmesck.holyvampirism.event.HolyHunterEvents;
import com.rapidmesck.holyvampirism.registry.HolyActions;
import com.rapidmesck.holyvampirism.registry.HolyDataAttachments;
import com.rapidmesck.holyvampirism.registry.HolySkills;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.SkillPointProviders;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(HolyVampirismMod.MODID)
public class HolyVampirismMod {

    public static final String MODID = "holy_vampirism";
    public static final Logger LOGGER = LogUtils.getLogger();

    public HolyVampirismMod(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, HolyVampirismConfig.SERVER_SPEC);

        HolyBalanceModifiers.registerDefaults();

        HolyActions.ACTIONS.register(modEventBus);
        HolySkills.SKILLS.register(modEventBus);
        HolyDataAttachments.ATTACHMENTS.register(modEventBus);

        SkillPointProviders.register(
                id("hunter_bonus"),
                factionPlayer -> factionPlayer instanceof IHunterPlayer && HolyVampirismConfig.SERVER_SPEC.isLoaded()
                        ? (int) Math.floor(Math.max(0, factionPlayer.getLevel() - 1) * HolyVampirismConfig.SERVER.extraHunterSkillPointsPerLevel.get())
                        : 0
        );

        modEventBus.addListener(this::onCommonSetup);

        NeoForge.EVENT_BUS.register(HolyHunterEvents.class);
        NeoForge.EVENT_BUS.register(HolyCommands.class);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(HolyBalanceModifiers::resolveSpecs);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
