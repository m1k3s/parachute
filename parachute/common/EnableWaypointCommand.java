package com.parachute.common;

import com.parachute.client.HudGuiRenderer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class EnableWaypointCommand extends CommandBase {
    private final List<String> aliases;

    public EnableWaypointCommand() {
        aliases = new ArrayList<>();
        aliases.add("enablewaypoint");
    }

    @Override
    public String getCommandName() {
        return "enablewaypoint";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/setwaypoint <X coord> <Z coord>";
    }

    @Override
    public List<String> getCommandAliases() {
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        World world = sender.getEntityWorld();
        if (!world.isRemote) { // server side
            if (args.length == 0) { // display current setting
                notifyCommandListener(sender, this, "commands.enablewaypoint.success", (HudGuiRenderer.getEnableWaypoint() ? "enabled" : "disabled"));
                return;
            }
            // otherwise set the state
            HudGuiRenderer.enableWaypoint(Boolean.parseBoolean(args[0]));
            boolean isEnabled = args[0].equals("true") || args[0].equals("1");
            notifyCommandListener(sender, this, "commands.enablewaypoint.success", (isEnabled ? "enabled" : "disabled"));
        }
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }

}
