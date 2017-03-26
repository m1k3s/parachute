package com.parachute.common;

import com.parachute.client.HudGuiRenderer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;


public class SetHomePointCommand extends CommandBase {
    private final List<String> aliases;

    public SetHomePointCommand() {
        aliases = new ArrayList<>();
        aliases.add("sethomepoint");
    }

    @Nonnull
    @Override
    public String getName() {
        return "sethomepoint";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender) {
        return "commands.sethomepoint.usage";
    }

    @Nonnull
    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        World world = sender.getEntityWorld();
        if (!world.isRemote) { // server side
            if (args.length == 0) { // set waypoint to current position
                BlockPos bp = new BlockPos(sender.getPosition());
                HudGuiRenderer.setHomepoint(new int[] {bp.getX(), bp.getZ()});
                ConfigHandler.setHomepoint(bp.getX(), bp.getZ());
                notifyCommandListener(sender, this, "commands.sethomepoint.current", bp.getX(), bp.getZ());
            } else if (isNumeric(args[0]) && isNumeric(args[1])) {
                HudGuiRenderer.setWaypoint(new int[] {Integer.parseInt(args[0]), Integer.parseInt(args[1])});
                notifyCommandListener(sender, this, "commands.sethomepoint.success", args[0], args[1]);
            } else {
                notifyCommandListener(sender, this, "commands.sethomepoint.failure");
            }
        }
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }

    public boolean isNumeric(String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }
}
