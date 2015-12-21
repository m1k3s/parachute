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
// Copyright © 2011-2015 Michael Sheppard (crackedEgg)
//
package com.parachute.common;

import com.parachute.client.AltitudeDisplay;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockLeaves;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityParachute extends Entity {

	private long tickCount;
	private double velocityX;
	private double velocityY;
	private double velocityZ;
	private double motionFactor;
	private double maxAltitude;
	private boolean allowThermals;
	private boolean lavaThermals;
	private double lavaDistance;
	private double maxThermalRise;
	private double curLavaDistance;
	private boolean weatherAffectsDrift;
	private boolean allowTurbulence;
	private boolean showContrails;
	private boolean altitudeMSL;
	private boolean autoDismount;
	private boolean fixedGlideRate;
	private boolean dismountInWater;

//	final static int Damping = 5; // value of 10 allows the altitude display to update about every half second
	final static double MSL = 63.0; // sea level - Mean Sea Level
	final static double drift = 0.004; // value applied to motionY to descend or drift downward
	final static double ascend = drift * -10.0; // -0.04 - value applied to motionY to ascend

//	final static int modeDrift = 0;  // key up
//	final static int modeAscend = 1; // key down

	private final double d2r = 0.0174532925199433; // degrees to radians
//	private final double r2d = 57.2957795130823;

	private static boolean ascendMode;

	public EntityParachute(World world)
	{
		super(world);
		weatherAffectsDrift = ConfigHandler.getWeatherAffectsDrift();
		allowTurbulence = ConfigHandler.getAllowturbulence();
		showContrails = ConfigHandler.getShowContrails();
		lavaDistance = ConfigHandler.getMinLavaDistance();
		allowThermals = ConfigHandler.getAllowThermals();
		maxAltitude = ConfigHandler.getMaxAltitude();
		lavaThermals = ConfigHandler.getAllowLavaThermals();
		altitudeMSL = ConfigHandler.getAltitudeMSL();
		autoDismount = ConfigHandler.isAutoDismount();
		fixedGlideRate = ConfigHandler.getFixedGlideRate();
		dismountInWater = ConfigHandler.getDismountInWater();

		curLavaDistance = lavaDistance;
		worldObj = world;
		preventEntitySpawning = true;
		setSize(1.5f, 0.0625f);
		motionFactor = 0.07;
		ascendMode = false;
		maxThermalRise = 48;
		tickCount = 0;
	}

	public EntityParachute(World world, double x, double y, double z)
	{
		this(world);
		setPosition(x, y, z);

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
	protected void entityInit()
	{
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entity)
	{
		if (entity != riddenByEntity && entity.ridingEntity != this) {
			return entity.getEntityBoundingBox();
		}
		return null;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox()
	{
		return getEntityBoundingBox();
	}
	
	//
	// FIXME: Unfortunately this stopped working around 1.6.x
	// movement packets are not sent to the server if the 'shouldRiderSit' method
	// returns false. We need for the 'shouldRiderSit' method to return true in
	// order to send packets, we need for it to return false for player to not
	// be in the sitting position on the parachute.
	//
	// skydiver should 'hang' when on the parachute and then
	// 'pick up legs' when landing.
//	@Override
//	public boolean shouldRiderSit()
//	{
//		boolean sitting = false;
//		if (riddenByEntity != null) {
//			BlockPos bp = new BlockPos(riddenByEntity.posX, riddenByEntity.posY - 1.0, riddenByEntity.posZ);
//			sitting = checkForGroundProximity(bp);
//		}
//		return sitting;
//	}
	
	@Override
	public boolean shouldDismountInWater(Entity pilot)
	{
		return dismountInWater;
	}

	@Override
	public double getMountedYOffset()
	{
		return -(ParachuteCommonProxy.getOffsetY());
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return !isDead;
	}

	@Override
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int inc, boolean unused)
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

		// the player has pressed LSHIFT or been killed,
		// this is necessary for LSHIFT to kill the parachute
		if (riddenByEntity == null && !worldObj.isRemote) {
			killParachute();
			return;
		}

		// initial forward velocity for this update
		double initialVelocity = Math.sqrt(motionX * motionX + motionZ * motionZ);

		if (showContrails && initialVelocity > 0.2) {
			generateContrails(initialVelocity);
		}

		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		// Altimeter, the altitude display
		if (riddenByEntity != null && worldObj.isRemote /*&& (tickCount % Damping == 0)*/) { // execute only on the client
			// use the pilot's position for the altitude reference
			BlockPos entityPos = new BlockPos(riddenByEntity.posX, riddenByEntity.posY, riddenByEntity.posZ);
			AltitudeDisplay.setAltitudeDouble(getCurrentAltitude(entityPos, altitudeMSL));
		}

		// drop the chute when close to ground
		if (autoDismount && riddenByEntity != null) {
			double pilotFeetPos = riddenByEntity.getEntityBoundingBox().minY;
			BlockPos bp = new BlockPos(riddenByEntity.posX, pilotFeetPos - 1.0, riddenByEntity.posZ);
			if (checkForGroundProximity(bp)) {
				riddenByEntity.mountEntity(this);
				killParachute();
				return;
			}
		}

		// update forward velocity for 'W' key press
		// moveForward is > 0.0 when the 'W' key is pressed. Value is either 0.0 | ~0.98
		if (riddenByEntity != null && riddenByEntity instanceof EntityLivingBase) {
			EntityLivingBase pilot = (EntityLivingBase) riddenByEntity;
			double yaw = pilot.rotationYaw + -pilot.moveStrafing * 90.0;
			if (fixedGlideRate) { // forward motion constant, governed by glide rate
				motionX += -Math.sin(yaw * d2r) * motionFactor * 0.049;
				motionZ += Math.cos(yaw * d2r) * motionFactor * 0.049;
			} else { // forward speed determined by 'W' keypress
				motionX += -Math.sin(yaw * d2r) * motionFactor * 0.05 * (pilot.moveForward * 1.05);
				motionZ += Math.cos(yaw * d2r) * motionFactor * 0.05 * (pilot.moveForward * 1.05);
			}
		}

		// forward velocity after forward movement is applied
		double adjustedVelocity = Math.sqrt(motionX * motionX + motionZ * motionZ);
		// clamp the adjustedVelocity and modify motionX/Z
		if (adjustedVelocity > 0.35D) {
			double motionAdj = 0.35D / adjustedVelocity;
			motionX *= motionAdj;
			motionZ *= motionAdj;
			adjustedVelocity = 0.35D;
		}
		// clamp the motionFactor between 0.07 and 0.35
		if (adjustedVelocity > initialVelocity && motionFactor < 0.35D) {
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

		// calculate the descent rate
		motionY -= currentDescentRate();

		// move the parachute with the motion equations applied
		moveEntity(motionX, motionY, motionZ);

		// apply drag
		motionX *= 0.99D;
		motionY *= 0.95D;
		motionZ *= 0.99D;

		// update pitch and yaw. Pitch is always 0.0
		rotationPitch = 0.0f;
		double yaw = rotationYaw;
		double delta_X = prevPosX - posX;
		double delta_Z = prevPosZ - posZ;

		// update direction (yaw)
		if (delta_X * delta_X + delta_Z * delta_Z > 0.001D) {
			yaw = Math.atan2(delta_Z, delta_X) * 57.2957795130823;
		}

		// update and clamp yaw between -180 and 180
		double adjustedYaw = MathHelper.wrapAngleTo180_double(yaw - rotationYaw);
		// further clamp yaw between -20 and 20 per update, slower turn radius
		if (adjustedYaw > 20.0D) {
			adjustedYaw = 20.0D;
		}
		if (adjustedYaw < -20.0D) {
			adjustedYaw = -20.0D;
		}
		// update final yaw and apply to parachute
		rotationYaw += adjustedYaw;
		setRotation(rotationYaw, rotationPitch);
		// finally apply turbulence if flags allow
		if (((weatherAffectsDrift && isBadWeather()) || allowTurbulence) && rand.nextBoolean()) {
			applyTurbulence(worldObj.isThundering());
		}

		// something bad happened, somehow the skydiver was killed.
		if (!worldObj.isRemote && riddenByEntity != null && riddenByEntity.isDead) {
			killParachute();
		}
		// increment tick count for altitude display damping
		tickCount++;

        // update distance by parachute statistics
        if (riddenByEntity != null) {
            double dX = posX - prevPosX;
            double dZ = posZ - prevPosZ;
            int distance = Math.round(MathHelper.sqrt_double(dX * dX + dZ * dZ) * 100.0F);
            ((EntityPlayer) riddenByEntity).addStat(Parachute.parachuteDistance, distance);
        }
	}
	
	public void killParachute()
	{
		riddenByEntity = null;
		ParachuteCommonProxy.setDeployed(false);
		setDead();
	}

	public boolean isBadWeather()
	{
		return (worldObj.isRaining() || worldObj.isThundering());
	}

	// determines the descent rate based on whether or not
	// the space bar has been pressed. several factors affect
	// the final result.
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
			if (!allowThermals) {
    			return descentRate;
    		}
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

	// the following three methods detect lava below the player
	// at upto 'maxThermalRise' distance.
	public boolean isLavaAt(BlockPos bp)
	{
		Block block = worldObj.getBlockState(bp).getBlock();
		return (block == Blocks.lava || block == Blocks.flowing_lava);
	}

	public boolean isLavaBelowInRange(BlockPos bp)
	{
		Vec3 v1 = new Vec3(posX, posY, posZ);
		Vec3 v2 = new Vec3(bp.getX(), bp.getY(), bp.getZ());
		MovingObjectPosition mop = worldObj.rayTraceBlocks(v1, v2, true);
		if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			BlockPos blockpos = mop.getBlockPos();
			if (isLavaAt(blockpos)) {
				return true;
			}
		}
		return false;
	}

	public double doLavaThermals()
	{
		double thermals = drift;
		final double inc = 0.5;

		BlockPos blockPos = new BlockPos(posX, posY - ParachuteCommonProxy.getOffsetY() - maxThermalRise, posZ);

		if (isLavaBelowInRange(blockPos)) {
			curLavaDistance += inc;
			thermals = ascend;
			if (curLavaDistance >= maxThermalRise) {
				curLavaDistance = lavaDistance;
				thermals = drift;
			}
		} else {
			curLavaDistance = lavaDistance;
		}
		return thermals;
	}

	// BlockPos bp is the pilot's position. The pilot's feet posY - 1.0
	// to be exact. We check for air blocks, flowers, leaves, and grass at
	// that position Y. The check for leaves means the parachute can get 
	// hung up in the trees. Also means that the pilot must manually
	// dismount to land on trees. Dismounting over water is handled by the
	// shouldDismountInWater method.
	public boolean checkForGroundProximity(BlockPos bp)
	{
		boolean result = false;

		if (!worldObj.isRemote && !isDead) {
			Block block = worldObj.getBlockState(bp).getBlock();
			boolean isAir = (block == Blocks.air);
			boolean isVegetation = (block instanceof BlockFlower) || (block instanceof BlockGrass) || (block instanceof BlockLeaves);
			result = (!isAir && !isVegetation);
		}
		return result;
	}

	// apply 'turbulence' in the form of a collision force
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
	
	// generate condensation trails at the trailing edge
	// of the parachute. Yes I know that most parachutes
	// aren't fast or high enough to generate contrails,
	// but most worlds aren't made of blocks with cubic
	// cows either. If you like, you can think of the
	// trails as chemtrails.
	public void generateContrails(double velocity)
	{
		double cosYaw = 2.0 * Math.cos(rotationYaw * d2r);
		double sinYaw = 2.0 * Math.sin(rotationYaw * d2r);

		for (int k = 0; (double) k < 1.0 + velocity; k++) {
			double sign = (double) (rand.nextInt(2) * 2 - 1) * 0.7;
			double x = prevPosX - cosYaw * -0.35 + sinYaw * sign;
			double y = posY - 0.25;
			double z = prevPosZ - sinYaw * -0.35 - cosYaw * sign;

			worldObj.spawnParticle(EnumParticleTypes.CLOUD, x, y, z, motionX, motionY, motionZ);
		}
	}

	@Override
	public void updateRiderPosition()
	{
		if (riddenByEntity != null) {
			double x = posX + (Math.cos(rotationYaw * d2r) * 0.04);
			double y = posY + getMountedYOffset() + riddenByEntity.getYOffset();
			double z = posZ + (Math.sin(rotationYaw * d2r) * 0.04);
			riddenByEntity.setPosition(x, y, z);
		}
	}

	// only allow altitude calculations in the surface world
	// return a weirdly random number if in nether or end.
	public double getCurrentAltitude(BlockPos entityPos, boolean referenceMSL)
	{
		if (worldObj.provider.isSurfaceWorld()) {
			if (referenceMSL) {
				return getAltitudeMSL(entityPos); // altitude MSL
			} else {
				return getAltitudeAboveGround(entityPos); // altitude above ground
			}
		}
		return 1000.0 * rand.nextGaussian();
	}
	
	// calculate altitude in meters above ground. starting at the entity
	// count down until a non-air block is encountered.
	public double getAltitudeAboveGround(BlockPos entityPos)
	{
		BlockPos blockPos = new BlockPos(entityPos.getX(), entityPos.getY(), entityPos.getZ());
		while (worldObj.isAirBlock(blockPos.down())) {
			blockPos = blockPos.down();
		}
		// calculate the entity's current altitude above the ground
		return entityPos.getY() - blockPos.getY();
	}

	// calculate altitude in meters above the ground. starting at block
	// level 64 count up until an air block is encountered.
	// this method produces negative numbers below the sea level (64)
	// Also it won't get overhangs, the counting will stop at the first
	// airblock encountered while counting up.
	public double getAltitudeMSL(BlockPos entityPos)
	{
		BlockPos bp1 = new BlockPos(entityPos.getX(), MSL, entityPos.getZ());
		while (!worldObj.isAirBlock(bp1.up())) {
			bp1 = bp1.up();
		}
		// calculate the entity's current altitude above the ground
		return entityPos.getY() - bp1.getY();
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt)
	{
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt)
	{
	}

	@Override
	public String toString()
	{
		return String.format("%s: {x=%.1f, y=%.1f, z=%.1f}, {yaw=%.1f, pitch=%.1f}", getClass().getSimpleName(), posX, posY, posZ, rotationYaw, rotationPitch);
	}

}
