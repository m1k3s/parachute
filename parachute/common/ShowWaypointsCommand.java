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

public class ShowWaypointsCommand implements ICommand {
    private final List<String> aliases;

    public ShowWaypointsCommand() {
        aliases = new ArrayList<String>();
        aliases.add("showwaypoints");
    }

    @Override
    public String getCommandName() {
        return "showwaypoints";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "showwaypoints";
    }

    @Override
    public List<String> getCommandAliases() {
        return aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        World world = sender.getEntityWorld();
        if (!world.isRemote) {
            sender.addChatMessage(new ChatComponentText("Current waypoints: " + HudGuiRenderer.getWaypoints()));
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
