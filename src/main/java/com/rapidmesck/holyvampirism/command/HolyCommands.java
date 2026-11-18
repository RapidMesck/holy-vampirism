package com.rapidmesck.holyvampirism.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.rapidmesck.holyvampirism.attachment.HolyHunterData;
import com.rapidmesck.holyvampirism.config.HolyBalanceModifiers;
import com.rapidmesck.holyvampirism.registry.HolyDataAttachments;
import com.rapidmesck.holyvampirism.registry.HolySkills;
import com.rapidmesck.holyvampirism.util.HolyHelper;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class HolyCommands {

    private HolyCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("holy-vampirism")
                        .then(Commands.literal("apply")
                                .requires(src -> !(src.getServer() instanceof DedicatedServer) || src.hasPermission(3))
                                .executes(ctx -> applyConfig(ctx.getSource())))
                        .then(Commands.literal("status")
                                .executes(ctx -> showStatus(ctx.getSource())))
        );
    }

    private static int applyConfig(CommandSourceStack source) {
        int count = HolyBalanceModifiers.applyAll();
        source.sendSuccess(() -> Component.translatable("command.holy_vampirism.apply.success", count).withStyle(ChatFormatting.GOLD), true);
        return count;
    }

    private static int showStatus(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.translatable("command.holy_vampirism.status.player_only"));
            return 0;
        }
        if (!Helper.isHunter(player)) {
            source.sendSuccess(() -> Component.translatable("command.holy_vampirism.status.not_hunter").withStyle(ChatFormatting.RED), false);
            return 0;
        }

        HunterPlayer hunter = HunterPlayer.get(player);
        int level = HolyHelper.getHunterLevel(player);
        int skillPointsLeft = hunter.getSkillHandler().getLeftSkillPoints();
        HolyHunterData data = player.getData(HolyDataAttachments.HUNTER_DATA);
        long ticksLeft = Math.max(0L, data.getUndyingResolveReadyTick() - player.level().getGameTime());

        source.sendSuccess(() -> Component.translatable("command.holy_vampirism.status.header", level, hunter.getMaxLevel(), skillPointsLeft)
                .withStyle(ChatFormatting.GOLD), false);

        if (HolyHelper.hasSkill(player, HolySkills.UNDYING_RESOLVE.get())) {
            String cdText = ticksLeft == 0 ? "READY" : (ticksLeft / 20) + "s";
            source.sendSuccess(() -> Component.translatable("command.holy_vampirism.status.undying_resolve", cdText)
                    .withStyle(ticksLeft == 0 ? ChatFormatting.GREEN : ChatFormatting.YELLOW), false);
        }
        return 1;
    }
}
