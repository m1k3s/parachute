/*
 * EnableWaypointCommand.java
 *
 * Copyright (c) 2017 Michael Sheppard
 *
 *  =====GPL=============================================================
 * $program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 * =====================================================================
 *
 */

package com.parachute.common;

import com.parachute.client.HudGuiRenderer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class EnableWaypointCommand extends CommandBase {
    private final List<String> aliases;

    public EnableWaypointCommand() {
        aliases = new ArrayList<>();
        aliases.add("enablewaypoint");
    }

    @Nonnull
    @Override
    public String getName() {
        return "enablewaypoint";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/setwaypoint <X coord> <Z coord>";
    }

    @Nonnull
    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) {
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
