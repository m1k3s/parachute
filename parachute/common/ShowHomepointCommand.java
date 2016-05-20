package com.parachute.common;

import com.parachute.client.HudGuiRenderer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
//import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ShowHomepointCommand extends CommandBase {
    private final List<String> aliases;

    public ShowHomepointCommand() {
        aliases = new ArrayList<>();
        aliases.add("showhomepoint");
    }

    @Override
    public String getCommandName() {
        return "showhomepoint";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.showhomepoint.usage";
    }

    @Override
    public List<String> getCommandAliases() {
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        World world = sender.getEntityWorld();
        if (!world.isRemote) { // server side
            notifyOperators(sender, this, "commands.showhomepoint.success", ConfigHandler.getHomepointString());
        }
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(@SuppressWarnings("NullableProblems") ICommand iCommand) {
        return 0;
    }
}
