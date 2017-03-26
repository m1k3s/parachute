package com.parachute.common;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ShowHomepointCommand extends CommandBase {
    private final List<String> aliases;

    public ShowHomepointCommand() {
        aliases = new ArrayList<>();
        aliases.add("showhomepoint");
    }

    @Nonnull
    @Override
    public String getName() {
        return "showhomepoint";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender) {
        return "commands.showhomepoint.usage";
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
            notifyCommandListener(sender, this, "commands.showhomepoint.success", ConfigHandler.getHomepointString());
        }
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }

}
