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

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ServerTickHandler implements ITickHandler {
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if (type.equals(EnumSet.of(TickType.PLAYER))) {
			onPlayerTick((EntityPlayer)tickData[0]);
		}
	}
	
	// set a fallDistance greater than maxFallDistance to offset the unmount falling distance
	// and call ItemParachute.deployParachute if the player is falling
	private void onPlayerTick(EntityPlayer player) {
		World world = player.worldObj;
		boolean auto = (Parachute.instance.getAutoDeploy() && !player.capabilities.isCreativeMode);
		int maxFallDistance = Parachute.instance.getFallDistance();
		if (auto && player.fallDistance > maxFallDistance && !player.onGround && !player.isOnLadder()) {
			ItemStack itemstack = inventoryContainsParachute(player.inventory);
			if (itemstack != null) {
				((ItemParachute)itemstack.getItem()).deployParachute(itemstack, world, player);
			}
		} // else fall to death!
	}
	
	// search inventory for a parachute
	public ItemStack inventoryContainsParachute(InventoryPlayer inventory) {
		ItemStack itemstack = null;
		for (ItemStack s : inventory.mainInventory) {
			if (s != null && s.getItem() instanceof ItemParachute) {
				itemstack = s;
				break;
			}
		}
		return itemstack;
	}
	
	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {

	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.PLAYER, TickType.SERVER);
	}

	@Override
	public String getLabel() {
		return "Parachute Server";
	}

}
