package com.rapidmesck.holyvampirism.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class HolyHunterData {

    public static final Codec<HolyHunterData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.LONG.optionalFieldOf("undying_resolve_ready_tick", 0L).forGetter(HolyHunterData::getUndyingResolveReadyTick),
            Codec.LONG.optionalFieldOf("fall_immunity_until_tick", 0L).forGetter(HolyHunterData::getFallImmunityUntilTick)
    ).apply(inst, HolyHunterData::new));

    private long undyingResolveReadyTick;
    private long fallImmunityUntilTick;

    public HolyHunterData() {
        this(0L, 0L);
    }

    public HolyHunterData(long undyingResolveReadyTick, long fallImmunityUntilTick) {
        this.undyingResolveReadyTick = undyingResolveReadyTick;
        this.fallImmunityUntilTick = fallImmunityUntilTick;
    }

    public long getUndyingResolveReadyTick() {
        return undyingResolveReadyTick;
    }

    public void setUndyingResolveReadyTick(long tick) {
        this.undyingResolveReadyTick = tick;
    }

    public long getFallImmunityUntilTick() {
        return fallImmunityUntilTick;
    }

    public void setFallImmunityUntilTick(long tick) {
        this.fallImmunityUntilTick = tick;
    }

    public HolyHunterData copy() {
        return new HolyHunterData(this.undyingResolveReadyTick, this.fallImmunityUntilTick);
    }
}
