/*
 * ParachuteFlyingSound.java
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

package com.parachute.client;

import com.parachute.common.EntityParachute;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParachuteFlyingSound extends MovingSound {
    private final EntityPlayer player;
    private int time;

    public ParachuteFlyingSound(EntityPlayer player) {
        super(SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.PLAYERS);
        this.player = player;
        volume = 0.1f;
        repeat = true;
        repeatDelay = 0;
        attenuationType = AttenuationType.NONE;
    }

    @Override
    public void tick() {
        ++time;
        
        if (player.isAlive() && (time <= 20 || (player.isPassenger() && player.getRidingEntity() instanceof EntityParachute))) {
            double dx = player.posX - player.prevPosX;
            double dz = player.posZ - player.prevPosZ;
            double dy = player.posY - player.prevPosY;
            float velocity = MathHelper.sqrt(dx * dx + dz * dz + dy * dy);
            if ((double) velocity >= 0.01) {
                volume = MathHelper.clamp(velocity, 0.0f, 1.0f);
            } else {
                volume = 0.0f;
            }
            if (time < 20) {
                volume = 0.0f;
            } else if (time < 40) {
                volume = (float) ((double) volume * ((double) (time - 20) / 20.0));
            }
            if (volume > 0.8f) {
                pitch = 1.0f + (volume - 0.8f);
            } else {
                pitch = 1.0f;
            }
        } else {
            donePlaying = true;
        }
    }
}
