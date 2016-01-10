//
// This work is licensed under the Creative Commons
// Attribution-ShareAlike 3.0 Unported License. To view a copy of this
// license, visit http://creativecommons.org/licenses/by-sa/3.0/
//

package parachute.common;

import parachute.client.RenderParachute;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemParachute extends Item {
//	static public boolean deployed = false;
	
	public ItemParachute(int i, EnumToolMaterial enumtoolmaterial) {
		super(i);
		maxStackSize = 16;
		setMaxDamage(enumtoolmaterial.getMaxUses()); // this damage is for number of uses only
		setCreativeTab(CreativeTabs.tabTransport); // place in the transportation tab in creative mode
	}

	public ItemParachute(int i) {
		super(i);
		maxStackSize = 16;
		setMaxDamage(EnumToolMaterial.WOOD.getMaxUses()); // this damage is for number of uses only
		setCreativeTab(CreativeTabs.tabTransport); // place in the transportation tab in creative mode
	}

	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		return deployParachute(itemstack, world, entityplayer);
	}

	public ItemStack deployParachute(ItemStack itemstack, World world,	EntityPlayer entityplayer) {
		// don't deploy if entityplayer is null or if player is not falling or if already on a parachute.
		if (entityplayer == null || !isFalling(entityplayer) || entityplayer.ridingEntity != null)
			return itemstack;

		world.playSoundAtEntity(entityplayer, "step.cloth", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F));

		double x = entityplayer.prevPosX + (entityplayer.posX - entityplayer.prevPosX);
		double y = (entityplayer.prevPosY + (entityplayer.posY - entityplayer.prevPosY) + 1.62D) - (double) entityplayer.yOffset;
		double z = entityplayer.prevPosZ + (entityplayer.posZ - entityplayer.prevPosZ);

		if (!world.isRemote) {
			EntityParachute chute = new EntityParachute(world, (float) x, (float) y - 2.5F, (float) z);
			world.spawnEntityInWorld(chute);
			entityplayer.mountEntity(chute);
			
			// change the color if random
			if (FMLCommonHandler.instance().getSide().isClient()) {
				if (RenderParachute.isColorRandom()) {
					RenderParachute.randomParachuteColor();
				} else {
					RenderParachute.setParachuteColor(Parachute.instance.getChuteColor());
				}
			}
		}

		if (!entityplayer.capabilities.isCreativeMode) {
			itemstack.damageItem(2, entityplayer);
		}
		
//		deployed = true;

		return itemstack;
	}
	
	@SideOnly(Side.CLIENT)
	public String getTextureFile() {
		return "/textures/parachuteItem.png";
	}

	public boolean isFalling(EntityPlayer entity) {
		return (entity.fallDistance > 0 && !entity.onGround && !entity.isOnLadder());
	}

}
