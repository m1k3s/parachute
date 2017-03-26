package com.parachute.common;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ShowWaypointCommand extends CommandBase {
    private final List<String> aliases;

    public ShowWaypointCommand() {
        aliases = new ArrayList<>();
        aliases.add("showwaypoint");
    }

    @Nonnull
    @Override
    public String getName() {
        return "showwaypoint";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender) {
        return "commands.showwaypoint.usage";
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
            notifyCommandListener(sender, this, "commands.showwaypoint.success", ConfigHandler.getWaypointString());
        }
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }

}
