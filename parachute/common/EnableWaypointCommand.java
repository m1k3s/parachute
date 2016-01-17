package com.parachute.common;

import com.parachute.client.HudGuiRenderer;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class EnableWaypointCommand implements ICommand {
    private final List<String> aliases;

    public EnableWaypointCommand() {
        aliases = new ArrayList<String>();
        aliases.add("enablewaypoints");
        aliases.add("enableway");
    }

    @Override
    public String getCommandName() {
        return "enablewaypoints";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "enablewaypoints <true|false>";
    }

    @Override
    public List<String> getCommandAliases() {
        return aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        World world = sender.getEntityWorld();
        if (!world.isRemote) {
            if (args.length == 0) {
                sender.addChatMessage(new ChatComponentText("enablewaypoints is true"));
                return;
            }
            HudGuiRenderer.enableWaypoints(Boolean.parseBoolean(args[0]));
            sender.addChatMessage(new ChatComponentText("enablewaypoints set to " + args[0]));
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return null;
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
