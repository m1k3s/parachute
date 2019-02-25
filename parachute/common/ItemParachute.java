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

//import com.parachute.client.ClientConfiguration;
import com.parachute.client.ParachuteFlyingSound;
import com.parachute.client.RenderParachute;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.item.Item;
import net.minecraftforge.fml.network.NetworkDirection;

import javax.annotation.Nonnull;

public class ItemParachute extends Item {

    private static final double OFFSET = 2.5;
    private static boolean aadState = true; //ConfigHandler.Common.getAADState();
    private static boolean singleUse = false; // ConfigHandler.Common.getSingleUse()

    public ItemParachute(Properties props) {
        super(props);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entityplayer, @Nonnull EnumHand hand) {
        ItemStack itemstack = entityplayer.getHeldItem(hand);
        if (Parachute.isFalling(entityplayer) && entityplayer.getRidingEntity() == null) {
            boolean result = deployParachute(world, entityplayer);
            return new ActionResult(result ? EnumActionResult.SUCCESS : EnumActionResult.FAIL, itemstack); // unchecked
        } else { // toggle the AAD state
            toggleAAD(itemstack, world, entityplayer);
            return new ActionResult(EnumActionResult.SUCCESS, itemstack); // unchecked
        }
    }

    public boolean deployParachute(World world, EntityPlayer entityplayer) {
        EntityParachute chute = new EntityParachute(world, entityplayer.posX, entityplayer.posY + OFFSET, entityplayer.posZ);
        chute.rotationYaw = entityplayer.rotationYawHead; // set parachute facing player direction

        // check for block collisions
        if (world.checkBlockCollision(entityplayer.getBoundingBox().grow(-0.1D))) {
            return false;
        }

        float volume = 1.0F;
        chute.playSound(Parachute.RegistryEvents.OPENCHUTE, volume, pitch());

        if (Parachute.isClientSide(world)) { // client side
            RenderParachute.setParachuteColor("random");//ConfigHandler.Client.getChuteColor());
            playFlyingSound(entityplayer);
        } else { // server side
            world.spawnEntity(chute);
        }
        entityplayer.startRiding(chute);
        entityplayer.addStat(StatList.ITEM_USED.get(this)); // update parachute deployed statistics

        ItemStack itemstack = null;
        Iterable<ItemStack> heldEquipment = entityplayer.getHeldEquipment();
        for (ItemStack itemStack : heldEquipment) {
            if (itemStack != null && itemStack.getItem() instanceof ItemParachute) {
                itemstack = itemStack;
            }
        }
        if (itemstack != null) {
//            boolean enchanted = EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByLocation("unbreaking"), itemstack) > 0;
            if (!entityplayer.abilities.isCreativeMode/* || !enchanted*/) {
                if (singleUse) {
                    itemstack.shrink(1);
                } else {
                    itemstack.damageItem(1, entityplayer);
                }
            }
        }
        return true;
    }

    // this function toggles the AAD state but does not update the saved config.
    // the player can still enable/disable the AAD in the config GUI.
    private void toggleAAD(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if (entityplayer != null) {
//            boolean aadState = ConfigHandler.Client.getAADState();
            if (Parachute.isServerSide(world)) { // server side
                aadState = !aadState;
                //ConfigHandler.setAADState(active);
                itemstack.setDisplayName(new TextComponentString(aadState ? "Parachute|AUTO" : "Parachute"));
                PacketHandler.HANDLER.sendTo(new ClientAADStateMessage(aadState), ((EntityPlayerMP)entityplayer).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
            } else { // client side
                world.playSound(entityplayer, new BlockPos(entityplayer.posX, entityplayer.posY, entityplayer.posZ), SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, 1.0f, 1.0f);
            }
        }
    }

    private float pitch() {
        return 1.0F / (random.nextFloat() * 0.4F + 0.8F);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return Items.STRING == repair.getItem();
    }

    private void playFlyingSound(EntityPlayer entityplayer) {
//        if (ClientConfiguration.getUseFlyingSoud()) {
            Minecraft.getInstance().getSoundHandler().play(new ParachuteFlyingSound(entityplayer));
//        }
    }

}
