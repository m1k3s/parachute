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


public class SetWaypointCommand extends CommandBase {
    private final List<String> aliases;

    public SetWaypointCommand() {
        aliases = new ArrayList<>();
        aliases.add("setwaypoint");
    }

    @Override
    public String getCommandName() {
        return "setwaypoint";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.setwaypoint.usage";
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
                HudGuiRenderer.setWaypoint(bp.getX(), bp.getZ());
                notifyOperators(sender, this, "commands.setwaypoint.current", bp.getX(), bp.getZ());
            } else if (isNumeric(args[0]) && isNumeric(args[1])) {
                HudGuiRenderer.setWaypoint(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
                notifyOperators(sender, this, "commands.setwaypoint.success", args[0], args[1]);
            } else {
                notifyOperators(sender, this, "commands.setwaypoint.failure");
            }
        }
    }

//    @Override
//    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
//        World world = sender.getEntityWorld();
//        if (!world.isRemote) { // server side
//            if (args.length == 0) { // set waypoint to current position
//                BlockPos bp = new BlockPos(sender.getPosition());
//                HudGuiRenderer.setWaypoint(bp.getX(), bp.getZ());
//                notifyOperators(sender, this, "commands.setwaypoint.current", bp.getX(), bp.getZ());
//            } else if (isNumeric(args[0]) && isNumeric(args[1])) {
//                HudGuiRenderer.setWaypoint(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
//                notifyOperators(sender, this, "commands.setwaypoint.success", args[0], args[1]);
//            } else {
//                notifyOperators(sender, this, "commands.setwaypoint.failure");
//            }
//        }
//    }

//    @Override
//    public boolean canCommandSenderUseCommand(ICommandSender sender) {
//        return true;
//    }
//
//    @Override
//    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
//        return null;
//    }

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
