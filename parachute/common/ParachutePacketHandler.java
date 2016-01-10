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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.java.games.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ParachutePacketHandler implements IPacketHandler, IConnectionHandler {
	
	public static final byte KeyPress = 0;
	private static final int KEY_DESCEND = 45; // Keyboard.KEY_X = 45
	private static final int KEY_ASCEND = 46; // Keyboard.KEY_C = 46
	
	@Override
	// server handles key press custom packets from the player
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player p) {
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet.data));
		byte keyCode = 0;
		boolean pressed;
		byte type;
		
		try {
			type = dis.readByte();
			EntityPlayer player = (EntityPlayer)p;
			if (player == null) {
				return;
			}
			
			if (type == KeyPress) {
				PlayerInfo pi = PlayerManagerParachute.getInstance().getPlayerInfoFromPlayer(player);
				if (pi == null) {
					return;
				}
					
				keyCode = dis.readByte();
				pressed = dis.readBoolean();
				if (keyCode == KEY_ASCEND) {
					if (pressed) {
						pi.setLiftMode(1); // ascend
					} else {
						pi.setLiftMode(0); // drift
					}
				}
				
				if (keyCode == KEY_DESCEND) {
					if (pressed) {
						pi.setLiftMode(2); // descend
					} else {
						pi.setLiftMode(0); // drift
					}
				}
			}
		} catch (IOException e) {
			return;
		}
	}
	
	@SideOnly(Side.CLIENT)
	// send key press events in a custom packet to the server
	public static void sendKeyPress(int keyCode, boolean pressed) {
		Minecraft client = FMLClientHandler.instance().getClient();
		WorldClient world = client.theWorld;
		if (world == null || !world.isRemote) {
			return;
		} else {
			try	{
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(bos);
				Packet250CustomPayload packet = new Packet250CustomPayload();

				dos.write(KeyPress);        // key press type packet
				dos.writeByte(keyCode);		// the keycode
				dos.writeBoolean(pressed);  // true if key is pressed 
				dos.close();

				packet.channel = Parachute.CHANNEL;
				packet.data = bos.toByteArray();
				packet.length = bos.size();
				packet.isChunkDataPacket = false;

//				Parachute.proxy.sendCustomPacket(pkt);
				PacketDispatcher.sendPacketToServer(packet);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void playerLoggedIn(Player p, NetHandler netHandler, INetworkManager manager) {
		PlayerManagerParachute.getInstance().Players.add(new PlayerInfo(((EntityPlayer)p).username, manager));
	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) {
		return null;
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) {
		
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {
		
	}

	@Override
	public void connectionClosed(INetworkManager manager) {
		PlayerInfo PI = new PlayerInfo("", manager);
		for(int i = 0; i < PlayerManagerParachute.getInstance().Players.size() && PI != null; i++) {
			if(PlayerManagerParachute.getInstance().Players.get(i).networkManager == manager) {
				PlayerManagerParachute.getInstance().Players.remove(i);
			}  
		}
	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {
		PlayerManagerParachute.getInstance().Players.add(new PlayerInfo(clientHandler.getPlayer().username, manager));
	}

}
