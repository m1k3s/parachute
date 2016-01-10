//  
//  =====GPL=============================================================
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; version 2 dated June, 1991.
// 
//  This program is distributed in the hope that it will be useful, 
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
// 
//  You should have received a copy of the GNU General Public License
//  along with this program;  if not, write to the Free Software
//  Foundation, Inc., 675 Mass Ave., Cambridge, MA 02139, USA.
//  =====================================================================
//


package parachute.common;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;

public class CommonProxyParachute { // implements IGuiHandler
	
	public void registerRenderer() {}
	
	public void registerKeyHandler() {}
	
	public void registerServerTickHandler() {
		TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
	}
	
	@Deprecated
	public void sendCustomPacket(Packet packet) {
		try {
			FMLClientHandler.instance().sendPacket(packet);
		} catch (NullPointerException e) {
			System.err.println("NPE in sendCustomPacket: " + e.toString());
			return;
		}
    }

//	@Override
//	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
//		// TODO add container code here
//		System.out.println("getServerGuiElement");
//		return null;
//	}
//
//	@Override
//	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
//		// TODO Auto-generated method stub
//		System.out.println("getClientGuiElement");
//		return null;
//	}
}
