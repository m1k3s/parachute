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
//
// Copyright 2011-2015 Michael Sheppard (crackedEgg)
//
package com.parachute.common;

import com.parachute.client.RenderParachute;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.item.Item;


public class ItemParachute extends Item {

    private static boolean active;

    public ItemParachute() {
        super();
        setMaxDamage(ToolMaterial.IRON.getMaxUses());
        maxStackSize = 4;
        active = ConfigHandler.getIsAADActive();
        setCreativeTab(CreativeTabs.TRANSPORTATION); // place in the transportation tab in creative mode
    }

    @SuppressWarnings("unchecked")
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entityplayer, EnumHand hand) {
        ItemStack itemstack = entityplayer.getHeldItem(hand);
        boolean result;
        if (entityplayer != null && ParachuteCommonProxy.isFalling(entityplayer) && entityplayer.getRidingEntity() == null) {
            result = deployParachute(world, entityplayer);
        } else { // toggle the AAD state
            result = toggleAAD(itemstack, world, entityplayer);
        }
        return new ActionResult(EnumActionResult.SUCCESS, itemstack); // unchecked
    }

    public boolean deployParachute(World world, EntityPlayer entityplayer) {
        double offset = ParachuteCommonProxy.getOffsetY();

        EntityParachute chute = new EntityParachute(world, entityplayer.posX, entityplayer.posY + offset, entityplayer.posZ);
        chute.rotationYaw = entityplayer.rotationYaw - 90.0f; // set parachute facing player direction
        float volume = 1.0F;
        chute.playSound(ParachuteCommonProxy.openChute, volume, pitch());

        if (world.isRemote) { // client side
            RenderParachute.setParachuteColor(ConfigHandler.getChuteColor());
        } else { // server side
            world.spawnEntityInWorld(chute);
        }
        entityplayer.startRiding(chute);
        ParachuteCommonProxy.setDeployed(true);
        entityplayer.addStat(Parachute.parachuteDeployed, 1); // update parachute deployed statistics

        ItemStack itemstack = null;
        Iterable<ItemStack> heldEquipment = entityplayer.getHeldEquipment();
        for (ItemStack itemStack : heldEquipment) {
            if (itemStack != null && itemStack.getItem() instanceof ItemParachute) {
                itemstack = itemStack;
            }
        }
        if (itemstack != null) {
            boolean enchanted = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByLocation("unbreaking"), itemstack) > 0;
            if (!entityplayer.capabilities.isCreativeMode || !enchanted) {
                itemstack.damageItem(ConfigHandler.getParachuteDamageAmount(itemstack), entityplayer);
            }
        }
        return true;
    }

    // this function toggles the AAD state but does not update the saved config.
    // the player can still enable/disable the AAD in the config GUI.
    public boolean toggleAAD(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if (!world.isRemote && entityplayer != null) { // server side
            active = !active;
            itemstack.setStackDisplayName(active ? "Parachute|AAD" : "Parachute");
            ConfigHandler.setAADState(active);
        } else if (world.isRemote && entityplayer != null) { // client side
            world.playSound(entityplayer, new BlockPos(entityplayer.posX, entityplayer.posY, entityplayer.posZ), SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, 1.0f, 1.0f);
        } else {
            return false;
        }
        return true;
    }

    private float pitch() {
        return 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F);
    }

    @Override
    public boolean getIsRepairable(ItemStack itemstack1, ItemStack itemstack2) {
        return Items.STRING == itemstack2.getItem() || super.getIsRepairable(itemstack1, itemstack2);
    }

}
