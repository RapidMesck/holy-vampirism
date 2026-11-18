package com.rapidmesck.holyvampirism.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class SolarBurnData {

    public static final Codec<SolarBurnData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.optionalFieldOf("ticks", 0).forGetter(SolarBurnData::getRemainingTicks),
            Codec.FLOAT.optionalFieldOf("damage", 0.0F).forGetter(SolarBurnData::getDamagePerSecond)
    ).apply(inst, SolarBurnData::new));

    private int remainingTicks;
    private float damagePerSecond;

    public SolarBurnData() {
        this(0, 0.0F);
    }

    public SolarBurnData(int remainingTicks, float damagePerSecond) {
        this.remainingTicks = remainingTicks;
        this.damagePerSecond = damagePerSecond;
    }

    public int getRemainingTicks() {
        return remainingTicks;
    }

    public float getDamagePerSecond() {
        return damagePerSecond;
    }

    public void ignite(int ticks, float damagePerSecond) {
        this.remainingTicks = Math.max(this.remainingTicks, ticks);
        this.damagePerSecond = Math.max(this.damagePerSecond, damagePerSecond);
    }

    public boolean tick() {
        if (remainingTicks <= 0) {
            return false;
        }
        remainingTicks--;
        return true;
    }
}
