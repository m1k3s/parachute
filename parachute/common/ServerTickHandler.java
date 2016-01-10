//
// This work is licensed under the Creative Commons
// Attribution-ShareAlike 3.0 Unported License. To view a copy of this
// license, visit http://creativecommons.org/licenses/by-sa/3.0/
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
