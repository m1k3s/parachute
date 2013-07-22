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

import java.util.List;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

//
//Copyright 2011 Michael Sheppard (crackedEgg)
//

public class EntityParachute extends Entity {
	private boolean isTurning;
	private int newRotationInc;
	private double newPosX;
	private double newPosY;
	private double newPosZ;
	private double newRotationYaw;
	private double newRotationPitch;
	@SideOnly(Side.CLIENT)
	private double velocityX;
	@SideOnly(Side.CLIENT)
	private double velocityY;
	@SideOnly(Side.CLIENT)
	private double velocityZ;
	private double motionFactor;
	private double maxAltitude;
	private boolean allowThermals;
	private boolean smallCanopy;

	private final int hitTime = 10;
	private final float maxDamage = 40.0F;

	final static double ascend = -0.040;
	final static double drift = 0.004;
	final static double descend = 0.040;
	
	private static double descentRate = drift;

	public EntityParachute(World world) {
		super(world);
		
		smallCanopy = Parachute.instance.getCanopyType();

		preventEntitySpawning = true;
		setSize(2.0F, 1.0F);
		yOffset = height / 2F;
		descentRate = drift;
		motionFactor = 0.07D;
		isTurning = true;
		
		allowThermals = Parachute.instance.getAllowThermals();
		maxAltitude = Parachute.instance.getMaxAltitude();
	}

	public EntityParachute(World world, double x, double y, double z) {
		this(world);

		setPosition(x, y + (double) yOffset, z);

		motionX = 0.0D;
		motionY = 0.0D;
		motionZ = 0.0D;

		prevPosX = x;
		prevPosY = y;
		prevPosZ = z;
	}
	
	// FIXME: This fucks up movement in 1.6.2, movement packets are not sent to server if
	// shouldRiderSit returns false. 
	// need for shouldRiderSit to return true in order to receive packets. need for it to return
	// false for player to not be in the sitting position on the parachute.
	// skydiver should 'hang' when on the parachute and then
	// 'pick up legs' when landing
//	public boolean shouldRiderSit() {
//		if (isNearGround(posX, posY, posZ, smallCanopy ? 3.0 : 4.0)) {
//			return true;
//		}
//		return false;
//	}

    @Override
	protected boolean canTriggerWalking() {
		return false;
	}
	
    @Override
	protected void entityInit() {
		dataWatcher.addObject(17, new Integer(0)); // time since last hit
		dataWatcher.addObject(18, new Integer(1)); // forward direction
		dataWatcher.addObject(19, new Float(0.0F)); // damage taken | current damage
	}

    @Override
	public AxisAlignedBB getCollisionBox(Entity entity) {
		if (entity != riddenByEntity && entity.ridingEntity != this) {
			return entity.boundingBox;
		}
		return null;
	}

    @Override
	public AxisAlignedBB getBoundingBox() {
		return boundingBox;
	}

    @Override
	public boolean canBePushed() {
		return false;
	}

    @Override
	public double getMountedYOffset() {
		return smallCanopy ? -2.5 : -3.5;
	}
	
	public void destroyParachute() {
		this.setDead();
	}
	
	// parachute takes additional damage from being hit
    @Override
	public boolean attackEntityFrom(DamageSource damagesource, float damage) {
		if (!worldObj.isRemote && !isDead) {
			setForwardDirection(-getForwardDirection());
			setTimeSinceHit(hitTime);
			setDamageTaken(getDamageTaken() + damage * 10.0F);
			setBeenAttacked();

			if (getDamageTaken() > maxDamage) {
				if (riddenByEntity != null) {
					riddenByEntity.mountEntity(this);
				}
				// drop a parachute item
				//dropItemWithOffset(Parachute.getItemID(), 1, 0.0F);
				dropRemains();
				destroyParachute(); // aaaaiiiiieeeeee!!! ... thud!
			}
		}
		return true;
	}

	// use shears to cut the parachute coords...
    @Override
	public boolean func_130002_c(EntityPlayer entityplayer) { // old interact(EntityPlayer entityplayer)
		ItemStack itemstack = entityplayer.inventory.getCurrentItem();
		if (itemstack != null && itemstack.itemID == Item.shears.itemID && riddenByEntity != null) {
			if (!worldObj.isRemote) {
				// instead of killing the parachute, remove riding entity
				// parachute death is handled in onUpdate()
				riddenByEntity = null; // ...rider plummets to death.
			}
			if (!entityplayer.capabilities.isCreativeMode) {
				itemstack.damageItem(1, entityplayer); // one use worth of damage
			}
			return true;
		}
		return false;
	}

    @Override
	public boolean canBeCollidedWith() {
		return !isDead;
	}
	
//	@SideOnly(Side.CLIENT)
    @Override
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int inc) {
		if (isTurning) {
			newRotationInc = inc + 5;
		} else {
			double newX = x - posX;
			double newY = y - posY;
			double newZ = z - posZ;
			double magnitude = newX * newX + newY * newY + newZ * newZ;

			if (magnitude <= 1.0D) {
				return;
			}

			newRotationInc = 3;
		}

		// position
		newPosX = x;
		newPosY = y;
		newPosZ = z;

		// rotation
		newRotationYaw = yaw;
		newRotationPitch = pitch;

		// forward/vertical motion
		motionX = velocityX;
		motionY = velocityY;
		motionZ = velocityZ;
	}
	
//	@SideOnly(Side.CLIENT)
    @Override
	public void setVelocity(double x, double y, double z) {
		velocityX = motionX = x;
		velocityY = motionY = y;
		velocityZ = motionZ = z;
	}

    @Override
	public void onUpdate() {
		super.onUpdate();

		// the player has probably been killed or mounted another entity,
		// perhaps a boat, minecart or pig? This also happens when the player has
		// cut away the chute with shears.
		if (riddenByEntity == null) {
			if (!worldObj.isRemote) {
				dropRemains();
				destroyParachute();
			}
			return;
		}

		if (getTimeSinceHit() > 0) {
			setTimeSinceHit(getTimeSinceHit() - 1);
		}
		if (getDamageTaken() > 0.0F) {
			setDamageTaken(getDamageTaken() - 1.0F);
		}

		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		
		// drop the chute when close to ground
		checkShouldDropChute(posX, posY, posZ, smallCanopy ? 3.0 : 4.0); // 3.0D original 

		// forward velocity
		double velocity = Math.sqrt(motionX * motionX + motionZ * motionZ);

		if (worldObj.isRemote && isTurning) { // external (remote) server
			if (newRotationInc > 0) {
				double x = posX + (newPosX - posX) / (double) newRotationInc;
				double y = posY + (newPosY - posY) / (double) newRotationInc;
				double z = posZ + (newPosZ - posZ) / (double) newRotationInc;

				double adjYaw = MathHelper.wrapAngleTo180_double(newRotationYaw - (double) rotationYaw);

				rotationYaw += adjYaw / (double) newRotationInc;
				rotationPitch += (newRotationPitch - (double) rotationPitch) / (double) newRotationInc;
				--newRotationInc;

				setPosition(x, y, z);
				setRotation(rotationYaw, rotationPitch);
			} else {
				motionY -= currentDescentRate();

				double x = posX + motionX;
				double y = posY + motionY;
				double z = posZ + motionZ;
				setPosition(x, y, z);

				motionX *= 0.99D;
				motionY *= 0.95D;
				motionZ *= 0.99D;
			}
		} else { // single player world - integrated server
			double forwardMovement = (double)((EntityLivingBase)riddenByEntity).moveForward * (smallCanopy ? 1.0 : 0.85);
			if (riddenByEntity != null && riddenByEntity instanceof EntityLivingBase) {
                if (forwardMovement > 0.0) {
                    double x = -Math.sin((double)(riddenByEntity.rotationYaw * 0.0174532925199433));
                    double z = Math.cos((double)(riddenByEntity.rotationYaw * 0.0174532925199433));
                    motionX += x * motionFactor * 0.05;
                    motionZ += z * motionFactor * 0.05;
                }
            }

			// forward velocity
			double localvelocity = Math.sqrt(motionX * motionX + motionZ * motionZ);

			if (localvelocity > 0.35D) {
				double motionAdj = 0.35D / localvelocity;
				motionX *= motionAdj;
				motionZ *= motionAdj;
				localvelocity = 0.35D;
			}

			if (localvelocity > velocity && motionFactor < 0.35D) {
				motionFactor += (0.35D - motionFactor) / 35.0D;
				if (motionFactor > 0.35D) {
					motionFactor = 0.35D;
				}
			} else {
				motionFactor -= (motionFactor - 0.07D) / 35.0D;
				if (motionFactor < 0.07D) {
					motionFactor = 0.07D;
				}
			}

			motionY -= currentDescentRate();

			moveEntity(motionX, motionY, motionZ);
			
			motionX *= 0.99D;
			motionY *= 0.95D;
			motionZ *= 0.99D;

			rotationPitch = 0.0F;
			double yaw = rotationYaw;
			double delta_X = prevPosX - posX;
			double delta_Z = prevPosZ - posZ;

			if (delta_X * delta_X + delta_Z * delta_Z > 0.001D) {
				yaw = (float) ((Math.atan2(delta_Z, delta_X) * 57.2957795F));
			}

			double adjustedYaw = MathHelper.wrapAngleTo180_double(yaw - (double) rotationYaw);

			if (adjustedYaw > 20.0D) {
				adjustedYaw = 20.0D;
			}
			if (adjustedYaw < -20.0D) {
				adjustedYaw = -20.0D;
			}

			rotationYaw += adjustedYaw;
			setRotation(rotationYaw, rotationPitch);

			if (!worldObj.isRemote) {
				List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(0.2D, 0.0D, 0.2D));
				if (list != null && list.size() > 0) {
					for (int k = 0; k < list.size(); k++) {
						Entity entity = (Entity) list.get(k);
						if (entity != riddenByEntity && entity.canBePushed() && (entity instanceof EntityParachute)) {
							entity.applyEntityCollision(this);
						}
					}
				}

				// something bad happened, somehow the skydiver was killed.
				if (riddenByEntity != null && riddenByEntity.isDead) {
					riddenByEntity = null;
					if (!worldObj.isRemote) {
						dropRemains();
						destroyParachute();
					}
				}
			}
		}
		descentRate = drift;
	}

	public double currentDescentRate() {
		descentRate = drift;
		EntityPlayer player = (EntityPlayer)riddenByEntity;
		if (player == null) {
			return descentRate;
		}
		if (!allowThermals) {
			return descentRate;
		}
		
		PlayerInfo pInfo = PlayerManagerParachute.getInstance().getPlayerInfoFromPlayer(player);
		if (pInfo == null) {
			return descentRate;
		} else {
//			GameSettings gs = FMLClientHandler.instance().getClient().gameSettings;
//			if (gs.isKeyDown(gs.keyBindJump)) {
//				descentRate = ascend;
//			} 
//			if (gs.isKeyDown(gs.keyBindSneak)) {
//				descentRate = descend;
//			} 
//			if (!gs.isKeyDown(gs.keyBindJump) && !gs.isKeyDown(gs.keyBindSneak)) {
//				descentRate = drift;
//			}
//		}
			switch(pInfo.mode) {
                case 0:
                    descentRate = drift;
                    break;
                case 1:
                    descentRate = ascend;
                    break;

                case 2:
                    descentRate = descend;
                    break;
			}
		}
		
		if (maxAltitude > 0.0D) { // altitude limiting
			if (posY >= maxAltitude) {
				descentRate = drift;
			}
		}
		
		return descentRate;
	}
	
	protected boolean checkShouldDropChute(double x, double y, double z, double distance) {
		boolean shouldDrop = false;

		if (isNearGround(x, y, z, distance)) {
			if (riddenByEntity != null) {
				riddenByEntity.fallDistance = 0.0F;
				riddenByEntity.mountEntity(this);
				if (!worldObj.isRemote) {
//					dropItemWithOffset(Parachute.getItemID(), 1, 0.0F);
					destroyParachute();
				} else {
					riddenByEntity = null;
				}
			}
			shouldDrop = true;
		}
		return shouldDrop;
	}

	public boolean isNearGround(double posx, double posy, double posz, double distance) {
		boolean nearGround = false;

		int x = MathHelper.floor_double(posx);
		int y = MathHelper.floor_double(posy - distance);
		int z = MathHelper.floor_double(posz);

		if (worldObj.getBlockId(x, y, z) > 0) {
			nearGround = true;
		}
		return nearGround;
	}

	@Override
	public void updateRiderPosition() {
		if (riddenByEntity != null) {
			double cosYaw = Math.cos((double) rotationYaw * 0.01745329252D) * 0.4D;
			double sinYaw = Math.sin((double) rotationYaw * 0.01745329252D) * 0.4D;
			riddenByEntity.setPosition(posX + cosYaw, posY + getMountedYOffset() + riddenByEntity.getYOffset(), posZ + sinYaw);
		}
	}

	// when parachute is destroyed drop the 'remains'
	protected void dropRemains() {
		dropItem(Block.cloth.blockID, 2);
		dropItem(Item.silk.itemID, 1);
	}

    @Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {}

    @Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {}
	
	@SideOnly(Side.CLIENT)
    @Override
	public float getShadowSize() {
		return 0.0F;
	}
	
	public void setDamageTaken(float f) {
		dataWatcher.updateObject(19, Float.valueOf(f));
	}

	public float getDamageTaken() {
		return dataWatcher.func_111145_d(19);
	}

	public void setTimeSinceHit(int time) {
		dataWatcher.updateObject(17, Integer.valueOf(time));
	}

	public int getTimeSinceHit() {
		return dataWatcher.getWatchableObjectInt(17);
	}

	public void setForwardDirection(int forward) {
		dataWatcher.updateObject(18, Integer.valueOf(forward));
	}

	public int getForwardDirection() {
		return dataWatcher.getWatchableObjectInt(18);
	}

	@SideOnly(Side.CLIENT)
	public void func_70270_d(boolean turning) {
		isTurning = turning;
	}
	
	public boolean isOnLadder() {
        return false;
    }
	
}
