package com.parachute.common;

import com.parachute.client.HudGuiRenderer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;


public class SetHomePointCommand extends CommandBase {
    private final List<String> aliases;

    public SetHomePointCommand() {
        aliases = new ArrayList<>();
        aliases.add("sethomepoint");
    }

    @Override
    public String getCommandName() {
        return "sethomepoint";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.sethomepoint.usage";
    }

    @Override
    public List<String> getCommandAliases() {
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        World world = sender.getEntityWorld();
        if (!world.isRemote) { // server side
            if (args.length == 0) { // set waypoint to current position
                BlockPos bp = new BlockPos(sender.getPosition());
                HudGuiRenderer.setHomepoint(new int[] {bp.getX(), bp.getZ()});
                ConfigHandler.setHomepoint(bp.getX(), bp.getZ());
                notifyOperators(sender, this, "commands.sethomepoint.current", bp.getX(), bp.getZ());
            } else if (isNumeric(args[0]) && isNumeric(args[1])) {
                HudGuiRenderer.setWaypoint(new int[] {Integer.parseInt(args[0]), Integer.parseInt(args[1])});
                notifyOperators(sender, this, "commands.sethomepoint.success", args[0], args[1]);
            } else {
                notifyOperators(sender, this, "commands.sethomepoint.failure");
            }
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

    public boolean isNumeric(String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }
}
