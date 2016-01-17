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


public class SetWaypointCommand implements ICommand {
    private final List<String> aliases;

    public SetWaypointCommand() {
        aliases = new ArrayList<String>();
        aliases.add("setwaypoint");
        aliases.add("setway");
    }

    @Override
    public String getCommandName() {
        return "setwaypoint";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "setwaypoint <X coord> <Z coord>";
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
                sender.addChatMessage(new ChatComponentText("Usage: setwaypoint <waypoint X coord> <waypoint Z coord>"));
                return;
            }
            HudGuiRenderer.setWaypoints(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
            sender.addChatMessage(new ChatComponentText("setwaypoint to " + args[0] + " " + args[1]));
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
