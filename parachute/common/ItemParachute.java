/*
 * ItemParachute.java
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

import javax.annotation.Nonnull;


public class ItemParachute extends Item {

    private static boolean active;

    public ItemParachute(String itemName) {
        super();
        setMaxDamage(ToolMaterial.IRON.getMaxUses());
        maxStackSize = 4;
        active = ConfigHandler.getIsAADActive();
        setCreativeTab(CreativeTabs.TRANSPORTATION); // place in the transportation tab in creative mode
        setItemName(this, itemName);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entityplayer, @Nonnull EnumHand hand) {
        ItemStack itemstack = entityplayer.getHeldItem(hand);
        if (ParachuteCommonProxy.isFalling(entityplayer) && entityplayer.getRidingEntity() == null) {
            deployParachute(world, entityplayer);
        } else { // toggle the AAD state
            toggleAAD(itemstack, world, entityplayer);
        }
        return new ActionResult(EnumActionResult.SUCCESS, itemstack); // unchecked
    }

    public void deployParachute(World world, EntityPlayer entityplayer) {
        double offset = ParachuteCommonProxy.getOffsetY();

        EntityParachute chute = new EntityParachute(world, entityplayer.posX, entityplayer.posY + offset, entityplayer.posZ);
        chute.rotationYaw = entityplayer.rotationYaw - 90.0f; // set parachute facing player direction
        float volume = 1.0F;
        chute.playSound(ParachuteCommonProxy.OPENCHUTE, volume, pitch());

        if (world.isRemote) { // client side
            RenderParachute.setParachuteColor(ConfigHandler.getChuteColor());
        } else { // server side
            world.spawnEntity(chute);
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
    }

    // this function toggles the AAD state but does not update the saved config.
    // the player can still enable/disable the AAD in the config GUI.
    private void toggleAAD(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if (!world.isRemote && entityplayer != null) { // server side
            active = !active;
            itemstack.setStackDisplayName(active ? "Parachute|AAD" : "Parachute");
            ConfigHandler.setAADState(active);
        } else if (world.isRemote && entityplayer != null) { // client side
            world.playSound(entityplayer, new BlockPos(entityplayer.posX, entityplayer.posY, entityplayer.posZ), SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, 1.0f, 1.0f);
        }
    }

    private float pitch() {
        return 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return Items.STRING == repair.getItem();
    }

    public void setItemName(final Item item, final String itemName) {
        item.setRegistryName(Parachute.MODID, itemName);
        item.setUnlocalizedName(item.getRegistryName().toString());
    }

}
