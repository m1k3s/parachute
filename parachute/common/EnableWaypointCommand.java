package com.parachute.common;

import com.parachute.client.HudGuiRenderer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.LanguageRegistry;

import java.util.ArrayList;
import java.util.List;

public class EnableWaypointCommand extends CommandBase {
    private final List<String> aliases;
    private String enabled;
    private String disabled;

    public EnableWaypointCommand() {
        aliases = new ArrayList<>();
        aliases.add("enablewaypoint");
        enabled = LanguageRegistry.instance().getStringLocalization("commands.enablewaypoint.enabled");
        disabled = LanguageRegistry.instance().getStringLocalization("commands.enablewaypoint.disabled");
    }

    @Override
    public String getCommandName() {
        return "enablewaypoint";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.enablewaypoint.usage";
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
                notifyOperators(sender, this, "commands.enablewaypoint.success", (HudGuiRenderer.getEnableWaypoint() ? enabled : disabled));
                return;
            }
            // otherwise set the state
            HudGuiRenderer.enableWaypoint(Boolean.parseBoolean(args[0]));
            boolean isEnabled = args[0].equals("true") || args[0].equals("1");
            notifyOperators(sender, this, "commands.enablewaypoint.success", (isEnabled ? enabled : disabled));
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
