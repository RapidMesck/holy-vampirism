# Holy Vampirism

A standalone **Minecraft 1.21.1 (NeoForge)** addon for **Vampirism** that upgrades and expands the **Hunter** faction so it can match—and rival—**GodlyVampirism** Vampires.

## Features

### 1. Hunter Balance Overrides (Independent of GodlyVampirism)
Modifies Vampirism's default Hunter & anti-vampire balance settings at startup (also re-applicable via `/holy-vampirism apply`):
- **Crossbows & Stakes**: `crossbowDamageMult = 1.6`, `allowInfiniteSpecialArrows = true`, `arrowVampireKillerMaxHealth = 100`, `hsInstantKill1MaxHealth = 0.45`, `hsInstantKill2MaxHealth = 350`.
- **Holy Water**: `holyWaterSplashDamage = 9`, `holyWaterTierDamageInc = 3.0`, longer nausea (`300t`) & blindness (`240t`).
- **Hunter Combat**: `hpStrengthMaxMod = 0.75`, `hsSmallAttackSpeedModifier = 0.35`, `hsMajorAttackSpeedModifier = 0.75`, `hsSmallAttackDamageModifier = 2.5`.
- **Garlic & Hunter Actions**: expanded Garlic Diffuser chunk radii (`1 / 2 / 3`), improved Camouflage (`haDisguiseInvisibleSQ = 128`, `haDisguiseVisibilityMod = 0.1`), Awareness radius (`45`), Potion Resistance (`800t` duration / `600t` cooldown), and Minions (`2` per lord level, `20` repair).

### 2. Level-Scaled Hunter Attributes
Hunters now gain real passive scaling per level (configurable in `holy_vampirism-server.toml`):
- **Max Health**: up to `+20` (+10 hearts)
- **Natural Armor**: up to `+16`
- **Armor Toughness**: up to `+8`
- **Movement Speed**: up to `+35%`
- **Attack Speed**: up to `+25%`
- **Knockback Resistance**: up to `+30%`

### 3. New Skill Tree Tab: **Holy Hunter**
Automatically unlocked for all Hunters (with dedicated bonus Hunter skill points so Vampires are unaffected):

- **Divine Combat**
  - **Silver-Edged Strikes**: +25% damage to Vampires & Undead + brief Glowing & Weakness.
  - **Holy Fervor**: +15% Attack Speed, +15% Movement Speed, melee crits vs Vampires heal.
  - **Action — Holy Smite**: Radiant shockwave damaging Vampires/Undead, stripping Vampire stealth, and granting Strength II + Speed II.
  - **Action — Hunter's Surge**: High-speed forward dash with Speed III and temporary fall damage immunity.
- **Sacred Bastion**
  - **Iron Will**: +6 Max Health, +4 Armor, +30% Knockback Resistance, passive regen below 50% HP.
  - **Vampiric Ward**: -20% incoming damage from Vampires and immunity to Vampire Freeze.
  - **Action — Sanctuary Aura**: 15s aura healing allies/villagers/self while damaging & slowing nearby Vampires/Undead.
  - **Action — Divine Aegis**: 20s Absorption III + Resistance II + Fire Resistance + Holy thorns retaliation.
  - **Undying Resolve**: Cheat-death once every 120s, restoring 35% HP with protective buffs and an anti-vampire shockwave.
- **Arcane Arsenal**
  - **Blessed Quiver**: +25% crossbow bolt speed, +25% ranged damage vs Vampires/Undead, 35% ammo save chance, +15% Stake damage.
  - **Solar Infusion**: Attacks inflict Solar Burn (sun damage over time) and brief Garlic on Vampires.
  - **Action — Solar Flare**: Detonates artificial sunlight at your aim point, dealing heavy sun damage with Garlic, Blindness, and Slowness.

### 4. Commands
- `/holy-vampirism apply` — Applies Holy Vampirism's balance overrides to Vampirism's server config.
- `/holy-vampirism status` — Shows your current Hunter level, available skill points, and Undying Resolve cooldown.

## Building

```bash
./gradlew build
```
