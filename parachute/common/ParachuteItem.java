/*
 * ParachuteItem.java
 *
 *  Copyright (c) 2019 Michael Sheppard
 *
 * =====GPL=============================================================
 * This program is free software: you can redistribute it and/or modify
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
 */
package com.parachute.common;

import com.parachute.client.ParachuteFlyingSound;
import com.parachute.client.ParachuteRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;

import javax.annotation.Nonnull;

public class ParachuteItem extends Item {

    private static final double OFFSET = 2.5;

    public ParachuteItem(Properties props) {
        super(props);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity entityplayer, @Nonnull Hand hand) {
        ItemStack itemstack = entityplayer.getHeldItem(hand);
        if (Parachute.isFalling(entityplayer) && entityplayer.getRidingEntity() == null) {
            boolean result = deployParachute(world, entityplayer);
            return new ActionResult(result ? ActionResultType.SUCCESS : ActionResultType.FAIL, itemstack); // unchecked
        } else { // toggle the AAD state
            toggleAAD(itemstack, world, entityplayer);
            return new ActionResult(ActionResultType.SUCCESS, itemstack); // unchecked
        }
    }

    public boolean deployParachute(World world, PlayerEntity entityplayer) {
        ParachuteEntity chute = new ParachuteEntity(world, entityplayer.posX, entityplayer.posY + OFFSET, entityplayer.posZ);
        chute.rotationYaw = entityplayer.rotationYaw; // set PARACHUTE facing player direction

        // check for block collisions
        if (world.checkBlockCollision(entityplayer.getBoundingBox().grow(-0.1D))) {
            return false;
        }

        float volume = 1.0F;
        chute.playSound(Parachute.RegistryEvents.OPENCHUTE, volume, pitch());

        if (world.isRemote) { // client side
            ParachuteRenderer.setParachuteColor(ConfigHandler.ClientConfig.getChuteColor());
            playFlyingSound(entityplayer);
        } else { // server side
            world.func_217376_c(chute);
        }
        entityplayer.startRiding(chute);
        entityplayer.addStat(Stats.ITEM_USED.get(this)); // update PARACHUTE deployed statistics

        ItemStack itemstack = null;
        Iterable<ItemStack> heldEquipment = entityplayer.getHeldEquipment();
        for (ItemStack itemStack : heldEquipment) {
            if (itemStack != null && itemStack.getItem() instanceof ParachuteItem) {
                itemstack = itemStack;
            }
        }
        if (itemstack != null) {
            boolean damageable = itemstack.isDamageable();
            if (!entityplayer.playerAbilities.isCreativeMode || damageable) {
                if (ConfigHandler.CommonConfig.getSingleUse()) {
                    itemstack.shrink(1);
                } else {
                    itemstack.setDamage(1);
                }
            }
        }
        return true;
    }

    // this function toggles the AAD state but does not update the saved config.
    // the player can still enable/disable the AAD in the config GUI.
    private void toggleAAD(ItemStack itemstack, World world, PlayerEntity entityplayer) {
        if (entityplayer != null) {
            boolean aadState = Parachute.getAADState();
            if (!world.isRemote) { // server side
                aadState = !aadState;
                Parachute.setAadState(aadState);
                itemstack.setDisplayName(new StringTextComponent(aadState ? "Parachute|AUTO" : "Parachute"));
                PacketHandler.HANDLER.sendTo(new ClientAADStateMessage(aadState), ((ServerPlayerEntity)entityplayer).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
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

    @OnlyIn(Dist.CLIENT)
    private void playFlyingSound(PlayerEntity entityplayer) {
        if (ConfigHandler.ClientConfig.getUseFlyingSound()) {
            Minecraft.getInstance().getSoundHandler().play(new ParachuteFlyingSound(entityplayer));
        }
    }
}
