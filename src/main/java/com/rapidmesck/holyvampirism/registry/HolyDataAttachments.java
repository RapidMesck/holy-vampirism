package com.rapidmesck.holyvampirism.registry;

import com.rapidmesck.holyvampirism.HolyVampirismMod;
import com.rapidmesck.holyvampirism.attachment.HolyHunterData;
import com.rapidmesck.holyvampirism.attachment.SolarBurnData;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class HolyDataAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, HolyVampirismMod.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<HolyHunterData>> HUNTER_DATA =
            ATTACHMENTS.register("hunter_data", () -> AttachmentType.builder(HolyHunterData::new)
                    .serialize(HolyHunterData.CODEC)
                    .build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<SolarBurnData>> SOLAR_BURN =
            ATTACHMENTS.register("solar_burn", () -> AttachmentType.builder(SolarBurnData::new)
                    .serialize(SolarBurnData.CODEC)
                    .build());
}
