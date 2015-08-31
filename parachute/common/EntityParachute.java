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
// Copyright 2011-2014 Michael Sheppard (crackedEgg)
//
package com.parachute.common;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
//import net.minecraftforge.common.util.ForgeDirection;

public class EntityParachute extends Entity {

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
	private boolean lavaThermals;
	private boolean ridingThermals;
	private double lavaDistance;
	private double maxThermalRise;
	private double curLavaDistance;
	private boolean weatherAffectsDrift;
	private boolean allowTurbulence;
	private boolean showContrails;

	final static double drift = 0.004;
	final static double ascend = drift * -10.0;

	final static int modeDrift = 0; // key up
	final static int modeAscend = 1; // key down

	final static double forwardSpeed = 0.75;

	private final double d2r = 0.0174532925199433; // degrees to radians
	private final double r2d = 57.2957795130823;   // radians to degrees

	private static boolean ascendMode;

	public EntityParachute(World world)
	{
		super(world);

		allowThermals = Parachute.instance.getAllowThermals();
		maxAltitude = Parachute.instance.getMaxAltitude();
		smallCanopy = Parachute.instance.isSmallCanopy();
		weatherAffectsDrift = Parachute.instance.getWeatherAffectsDrift();
		allowTurbulence = Parachute.instance.getAllowturbulence();
		showContrails = Parachute.instance.getShowContrails();
		lavaDistance = Parachute.instance.getMinLavaDistance();
		allowThermals = Parachute.instance.getAllowThermals();
		maxAltitude = Parachute.instance.getMaxAltitude();
		lavaThermals = Parachute.instance.getAllowLavaThermals();
		
		curLavaDistance = lavaDistance;

		preventEntitySpawning = true;
		setSize(2.0F, 1.0F);
		yOffset = height / 2F;
		motionFactor = 0.07D;
		ascendMode = false;
		ridingThermals = false;
		maxThermalRise = 48;
	}

	public EntityParachute(World world, double x, double y, double z)
	{
		this(world);

		setPosition(x, y + (double) yOffset, z);

		motionX = 0.0D;
		motionY = 0.0D;
		motionZ = 0.0D;

		prevPosX = x;
		prevPosY = y;
		prevPosZ = z;
	}

	static public void setAscendMode(boolean mode)
	{
		ascendMode = mode;
	}

	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}

	@Override
	protected void entityInit() {}
	
	@Override
	public boolean shouldDismountInWater(Entity rider)
	{
		return true;
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entity)
	{
		if (entity != riddenByEntity && entity.ridingEntity != this) {
			return entity.boundingBox;
		}
		return null;
	}

//	@Override
//	public boolean shouldRiderSit()
//	{
//		return isNearGround(posX, posX, posX, Math.abs(getMountedYOffset() + 1.0));
//	}

	@Override
	public AxisAlignedBB getBoundingBox()
	{
		return boundingBox;
	}

	@Override
	public boolean canBePushed()
	{
		return true;
	}

	@Override
	public double getMountedYOffset()
	{
		return smallCanopy ? -2.5 : -3.5;
	}

	public void destroyParachute()
	{
		this.setDead();
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return !isDead;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int inc)
	{
		double deltaX = x - posX;
		double deltaY = y - posY;
		double deltaZ = z - posZ;
		double magnitude = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;

		if (magnitude <= 1.0D) {
			return;
		}

		// forward/vertical motion
		motionX = velocityX;
		motionY = velocityY;
		motionZ = velocityZ;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void setVelocity(double x, double y, double z)
	{
		velocityX = motionX = x;
		velocityY = motionY = y;
		velocityZ = motionZ = z;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		// the player has probably been killed or pressed LSHIFT
		if (riddenByEntity == null) {
			if (!worldObj.isRemote) {
				destroyParachute();
			}
			return;
		}

		// forward velocity
		double velocity = Math.sqrt(motionX * motionX + motionZ * motionZ);
		
		if (showContrails) {
			showContrails(velocity);
		}
		
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		// drop the chute when close to ground
		if (Parachute.instance.isAutoDismount()) {
			double offset = Math.abs(getMountedYOffset());
			if (checkShouldDropChute(posX, posY, posZ, offset + 1.0)) {
				return;
			}
		}

		// forward velocity for 'W' keypress
		// moveForward happens when the 'W' key is pressed. Value is either 0.0 | ~0.98
		// when allowThermals is false forwardMovement is set to the constant 'forwardSpeed'
		// and appied to motionX and motionZ
		if (riddenByEntity != null && riddenByEntity instanceof EntityLivingBase) {
			EntityLivingBase rider = (EntityLivingBase) riddenByEntity;
			double forwardMovement = allowThermals ? (double) rider.moveForward : forwardSpeed;
			if (forwardMovement > 0.0) {
				double f = rider.rotationYaw + -rider.moveStrafing * 90.0;
				motionX += (-Math.sin((double) (f * d2r)) * motionFactor * 0.05) * forwardMovement;
				motionZ += (Math.cos((double) (f * d2r)) * motionFactor * 0.05) * forwardMovement;
			}
		}

		// forward velocity when drifting
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

		// apply drag
		motionX *= 0.99D;
		motionY *= 0.95D;
		motionZ *= 0.99D;

		rotationPitch = 0.0F;
		double yaw = rotationYaw;
		double delta_X = prevPosX - posX;
		double delta_Z = prevPosZ - posZ;

		if (delta_X * delta_X + delta_Z * delta_Z > 0.001D) {
			yaw = (float) ((Math.atan2(delta_Z, delta_X) * r2d));
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

		if ((isBadWeather() || allowTurbulence) && rand.nextBoolean() == true) {
			applyTurbulence(worldObj.isThundering());
		}
		
		// something bad happened, somehow the skydiver was killed.
		if (riddenByEntity != null && riddenByEntity.isDead) {
			riddenByEntity = null;
			if (!worldObj.isRemote) {
				destroyParachute();
			}
		}
	}
	
	public boolean isBadWeather()
	{
		return weatherAffectsDrift && (worldObj.isRaining() || worldObj.isThundering());
	}

	public double currentDescentRate()
	{
		double descentRate = drift; // defaults to drift

		if (weatherAffectsDrift) {
			if (worldObj.isRaining()) { // rain makes you fall faster
				descentRate += 0.002;
			}

			if (worldObj.isThundering()) { // more rain really makes you fall faster
				descentRate += 0.004;
			}
		}

		if (!allowThermals && !lavaThermals) {
			return descentRate;
		}

		if (lavaThermals) {
			descentRate = doLavaThermals();
			return descentRate;
		}

		if (ascendMode) {
			descentRate = ascend;
		}

		if (maxAltitude > 0.0D) { // altitude limiting
			if (posY >= maxAltitude) {
				descentRate = drift;
			}
		}

		return descentRate;
	}
	
	public boolean isLavaAt(double posx, double posy, double posz)
	{
		int x = MathHelper.floor_double(posx);
		int y = MathHelper.floor_double(posy);
		int z = MathHelper.floor_double(posz);
		Block block = worldObj.getBlock(x, y, z);
		
		return (block == Blocks.lava || block == Blocks.flowing_lava);
	}

	public boolean isLavaBelowInRange(double posx, double posy, double posz)
	{
		int x = MathHelper.floor_double(posX);
		int y = MathHelper.floor_double(posY);
		int z = MathHelper.floor_double(posZ);
		
		Vec3 v1 = Vec3.createVectorHelper(x, y, z);
		Vec3 v2 = Vec3.createVectorHelper(posx, posy, posz);
		MovingObjectPosition mop = worldObj.rayTraceBlocks(v1, v2, true);
		if (mop != null) {
			if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				if (isLavaAt(mop.blockX, mop.blockY, mop.blockZ)) {
					return true;
				}
			}
		}
		return false;
	}

	public double doLavaThermals()
	{
		double thermals = drift;
		double offset = Math.abs(getMountedYOffset());
		final double inc = 0.5;

		if (isLavaBelowInRange(posX, posY - offset - maxThermalRise, posZ)) {
			ridingThermals = true;
			curLavaDistance += inc;
			thermals = ascend;
			if (curLavaDistance >= maxThermalRise) {
				ridingThermals = false;
				curLavaDistance = lavaDistance;
				thermals = drift;
			}
		} else {
			ridingThermals = false;
			curLavaDistance = lavaDistance;
		}
		return thermals;
	}

	protected boolean checkShouldDropChute(double x, double y, double z, double distance)
	{
		boolean shouldDrop = false;

		if (isNearGround(x, y, z, distance)) {
			if (riddenByEntity != null) {
				dropParachute(this);
				if (!worldObj.isRemote) {
					destroyParachute();
				} else {
					riddenByEntity = null;
				}
			}
			shouldDrop = true;
		}
		return shouldDrop;
	}

	// Don't check for water - that is done by overriding shouldDismountInWater()
	// Don't check for lava - you don't want to land there anyway
	// Check for leaves and exclude - use LSHIFT, simulates getting caught in trees too!
	public boolean isNearGround(double posx, double posy, double posz, double distance)
	{
		boolean result = false;
		int x = MathHelper.floor_double(posx);
		int y = MathHelper.floor_double(posy - distance);
		int z = MathHelper.floor_double(posz);
		Block block = worldObj.getBlock(x, y, z);
		boolean isLeafBlock = (block == Blocks.leaves || block == Blocks.leaves2);
//		boolean isSolid = worldObj.isSideSolid(x, y, z, ForgeDirection.UP);
		
		if (!worldObj.isAirBlock(x, y, z) && !isLeafBlock) {
			return true;
		}
		return result;
	}

	public void dropParachute(Entity parachute)
	{
		if (parachute == null) {
			if (ridingEntity != null) {
				setLocationAndAngles(ridingEntity.posX, ridingEntity.boundingBox.minY + (double) ridingEntity.height, ridingEntity.posZ, rotationYaw, rotationPitch);
				ridingEntity.riddenByEntity = null;
			}
			ridingEntity = null;
		} else {
			if (ridingEntity != null) {
				ridingEntity.riddenByEntity = null;
			}
			ridingEntity = parachute;
			parachute.riddenByEntity = this;
		}
	}
	
	public void applyTurbulence(boolean roughWeather)
	{
		double rmin = 0.1;
		double deltaPos = rmin + 0.9 * rand.nextDouble();

		if (deltaPos >= 0.20) {
			double rmax = roughWeather ? 0.8 : 0.5;
			double deltaX = rmin + (rmax - rmin) * rand.nextDouble();
			double deltaY = rmin + 0.2 * rand.nextDouble();
			double deltaZ = rmin + (rmax - rmin) * rand.nextDouble();
			
			deltaPos = MathHelper.sqrt_double(deltaPos);
			double deltaInv = 1.0 / deltaPos;
			
			deltaX /= deltaPos;
			deltaY /= deltaPos;
			deltaZ /= deltaPos;

			if (deltaInv > 1.0) {
				deltaInv = 1.0;
			}

			deltaX *= deltaInv;
			deltaY *= deltaInv;
			deltaZ *= deltaInv;

			deltaX *= 0.05;
			deltaY *= 0.05;
			deltaZ *= 0.05;

			if (rand.nextBoolean()) {
				addVelocity(-deltaX, -deltaY, -deltaZ);
			} else {
				addVelocity(deltaX, deltaY, deltaZ);
			}
		}
	}

	public void showContrails(double velocity)
	{
		if (velocity >= 0.20) {
			double cosYaw = 2.0 * Math.cos((double) rotationYaw * d2r);
			double sinYaw = 2.0 * Math.sin((double) rotationYaw * d2r);

			for (int j = 0; (double) j < 1.0 + velocity * 15.0; ++j) {
				double s1 = (double) (rand.nextFloat() * 2.0 - 1.0) * 0.2;
				double s2 = (double) (rand.nextInt(2) * 2 - 1) * 0.7;
				double particleX = prevPosX - cosYaw * s1 * -0.1 + sinYaw * s2;
				double particleZ = prevPosZ - sinYaw * s1 * -0.1 - cosYaw * s2;
				
				worldObj.spawnParticle("smoke", particleX, posY - 0.25, particleZ, motionX, motionY, motionZ);
			}
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {}

}
