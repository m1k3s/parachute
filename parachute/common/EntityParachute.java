/*
 * EntityParachute.java
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

import com.parachute.client.ClientConfiguration;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.*;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class EntityParachute extends Entity {

    private double maxAltitude;
    private boolean allowThermals;
    private boolean lavaThermals;
    private double lavaDistance;
    private double maxLavaDistance;
    private double curLavaDistance;
    private boolean allowTurbulence;
    private boolean showContrails;
    private boolean dismountInWater;

    private double deltaRotation;
    private double forwardMomentum;
    private double backMomentum;
    private double rotationMomentum;
    private double slideMomentum;

    @SideOnly(Side.CLIENT)
    private double velocityX;
    @SideOnly(Side.CLIENT)
    private double velocityY;
    @SideOnly(Side.CLIENT)
    private double velocityZ;

    private final static double DRIFT = 0.004; // value applied to motionY to descend or DRIFT downward
    private final static double ASCEND = DRIFT * -10.0; // -0.04 - value applied to motionY to ascend
    private final static double OFFSET = 2.5; // player Y offset from parachute
    private final static float HEAD_TURN_ANGLE = 120.0f;
    private final static double DECAY_MOMENTUM = 0.97;

    private static boolean ascendMode;

    public EntityParachute(World world) {
        super(world);
        allowTurbulence = ConfigHandler.getAllowturbulence();
        showContrails = ConfigHandler.getShowContrails();
        lavaDistance = ConfigHandler.getMinLavaDistance();
        allowThermals = ConfigHandler.getAllowThermals();
        maxAltitude = ConfigHandler.getMaxAltitude();
        lavaThermals = ConfigHandler.getAllowLavaThermals();
        dismountInWater = ConfigHandler.getDismountInWater();
        maxLavaDistance = ConfigHandler.getMaxLavaDistance();

        forwardMomentum = ConfigHandler.getForwardMomentum();
        backMomentum = ConfigHandler.getBackMomentum();
        rotationMomentum = ConfigHandler.getRotationMomentum();
        slideMomentum = ConfigHandler.getSlideMomentum();

        curLavaDistance = lavaDistance;
        this.world = world;
        preventEntitySpawning = true;
        float SCALE = 1.0f / 16.0f;
        setSize(3.25f, SCALE);
        ascendMode = false;
        updateBlocked = false;
        setSilent(false);
    }

    public EntityParachute(World world, double x, double y, double z) {
        this(world);
        setPosition(x, y, z);

        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;

        prevPosX = x;
        prevPosY = y;
        prevPosZ = z;
    }

    @Override
    public boolean canRiderInteract() {
        return true;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected void entityInit() {
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity) {
        if (entity != getControllingPassenger() && entity.getRidingEntity() != this) {
            return entity.getEntityBoundingBox();
        }
        return null;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return getEntityBoundingBox();
    }

    // skydiver should hang when on the parachute and then
    // pick up legs when landing.
    @Override
    public boolean shouldRiderSit() {
        Entity skyDiver = getControllingPassenger();
        boolean sitting = false;
        if (skyDiver != null) {
            BlockPos bp = new BlockPos(skyDiver.posX, skyDiver.getEntityBoundingBox().minY - 3.0, skyDiver.posZ);
            sitting = (world.getBlockState(bp).getBlock() != Blocks.AIR);
        }
        return sitting;
    }

    @Nonnull
    @Override
    public EnumFacing getAdjustedHorizontalFacing() {
        return getHorizontalFacing().rotateY();
    }

    @Override
    public boolean canPassengerSteer() {
        return true;
    }

    @Override
    public Entity getControllingPassenger() {
        List<Entity> list = getPassengers();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public boolean shouldDismountInWater(Entity pilot) {
        return dismountInWater;
    }

    @Override
    public double getMountedYOffset() {
        return -OFFSET;
    }

    @Override
    public boolean canBeCollidedWith() {
        return !isDead;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int inc, boolean teleport) {
        double deltaX = x - posX;
        double deltaY = y - posY;
        double deltaZ = z - posZ;
        double magnitude = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;

        if (magnitude <= 1.0D) {
            return;
        }

        // forward & vertical motion
        motionX = velocityX;
        motionY = velocityY;
        motionZ = velocityZ;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void setVelocity(double x, double y, double z) {
        motionX = x;
        motionY = y;
        motionZ = z;

        velocityX = motionX;
        velocityY = motionY;
        velocityZ = motionZ;
    }

    // updateInputs is called by ParachuteInputEvent class
    public void updateInputs(MovementInput input) {
        if (isBeingRidden() && world.isRemote) {
            double motionFactor = 0.0f;
            String steeringControl = ConfigHandler.getSteeringControl();

            if (input.forwardKeyDown) {
                motionFactor += forwardMomentum;
            }
            if (input.backKeyDown) {
                motionFactor -= backMomentum;
            }
            if (steeringControl.equals("WASD")) {
                if (input.leftKeyDown) {
                    deltaRotation += -(rotationMomentum);
                }
                if (input.rightKeyDown) {
                    deltaRotation += rotationMomentum;
                }

                // slight forward momentum while turning
                if (input.rightKeyDown != input.leftKeyDown && !input.forwardKeyDown && !input.backKeyDown) {
                    motionFactor += slideMomentum;
                }
            }

            ascendMode = input.jump;

            motionY -= currentDescentRate();
            if (steeringControl.equals("WASD")) {
                rotationYaw += deltaRotation;
            } else {
                Entity skyDiver = getControllingPassenger();
                if (skyDiver instanceof EntityLivingBase) {
                    EntityLivingBase pilot = (EntityLivingBase) skyDiver;
                    rotationYaw = (float) (pilot.rotationYaw + -pilot.moveStrafing * 90.0);
                }
            }

            motionX += MathHelper.sin((float) Math.toRadians(-rotationYaw)) * motionFactor;
            motionZ += MathHelper.cos((float) Math.toRadians(rotationYaw)) * motionFactor;

            if (((ConfigHandler.getWeatherAffectsDrift() && isBadWeather()) || allowTurbulence) && rand.nextBoolean()) {
                applyTurbulence(world.isThundering());
            }
        }
    }

    @Override
    public void onUpdate() {
        Entity skyDiver = getControllingPassenger();
        // the player has pressed LSHIFT or been killed,
        // this is necessary for LSHIFT to kill the parachute
        if (skyDiver == null && !world.isRemote) { // server side
            setDead();
            return;
        }

        if (showContrails && skyDiver != null) {
            generateContrails(ascendMode);
        }

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        super.onUpdate();

        if (allowThermals && ascendMode && skyDiver != null) { // play the lift sound. kinda like a hot air balloon's burners effect
            skyDiver.playSound(Parachute.LIFTCHUTE, ClientConfiguration.getBurnVolume(), 1.0F / (rand.nextFloat() * 0.4F + 0.8F));
        }

        // apply momentum/decay
        motionX *= DECAY_MOMENTUM;
        motionY *= (motionY < 0.0 ? 0.96 : 0.98); // rises faster than falls
        motionZ *= DECAY_MOMENTUM;
        deltaRotation *= 0.9;
        // move the parachute with the motion equations applied
        move(MoverType.SELF, motionX, motionY, motionZ);

        // something bad happened, somehow the skydiver was killed.
        if (!world.isRemote && skyDiver != null && skyDiver.isDead) { // server side
            skyDiver.dismountRidingEntity();
        }

        // update distance for parachute statistics
        if (skyDiver != null) {
            double dX = posX - prevPosX;
            double dZ = posZ - prevPosZ;
            int distance = Math.round(MathHelper.sqrt(dX * dX + dZ * dZ) * 100.0F);
            if (skyDiver instanceof EntityPlayer) {
                ((EntityPlayer) skyDiver).addStat(Parachute.parachuteDistance, distance);
            }
        }
        doBlockCollisions();
    }

    // check for bad weather, if the biome can rain or snow check to see
    // if it is raining (or snowing) or thundering.
    private boolean isBadWeather() {
        BlockPos bp = new BlockPos(posX, posY, posZ);
        Chunk chunk = world.getChunkFromBlockCoords(bp);
        boolean canSnow = chunk.getBiome(bp, world.provider.getBiomeProvider()).getEnableSnow();
        boolean canRain = chunk.getBiome(bp, world.provider.getBiomeProvider()).getRainfall() > 0;
        return (canRain || canSnow) && (world.isRaining() || world.isThundering());
    }

    // determines the descent rate based on whether or not
    // the space bar has been pressed. weather and lava affect
    // the final result.
    private double currentDescentRate() {
        double descentRate = DRIFT; // defaults to DRIFT

        if (ConfigHandler.getWeatherAffectsDrift()) {
            if (world.isRaining()) {  // rain makes you fall faster
                descentRate += 0.002;
            }

            if (world.isThundering()) {  // more rain really makes you fall faster
                descentRate += 0.004;
            }
        }

        if (lavaThermals) {
            descentRate = doHeatSourceThermals();
            if (!allowThermals) {
                return descentRate;
            }
        }

        if (allowThermals && ascendMode) {
            descentRate = ASCEND;
        }

        if (maxAltitude > 0.0D) { // altitude limiting
            if (posY >= maxAltitude) {
                descentRate = DRIFT;
            }
        }

        return descentRate;
    }

    // the following three methods detect lava|fire below the player
    // at up to 'maxLavaDistance' blocks.
    private boolean isHeatSource(BlockPos bp) {
        return world.isFlammableWithin(new AxisAlignedBB(bp).expand(0, 1, 0));
    }

    private boolean isHeatSourceInRange(BlockPos bp) {
        Vec3d v1 = new Vec3d(posX, posY, posZ);
        Vec3d v2 = new Vec3d(bp.getX(), bp.getY(), bp.getZ());
        RayTraceResult mop = world.rayTraceBlocks(v1, v2, true);
        if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos blockpos = mop.getBlockPos();
            return isHeatSource(blockpos);
        }
        return false;
    }

    private double doHeatSourceThermals() {
        double thermals = DRIFT;
        final double inc = 0.5;

        BlockPos blockPos = new BlockPos(posX, posY - OFFSET - maxLavaDistance, posZ);

        if (isHeatSourceInRange(blockPos)) {
            curLavaDistance += inc;
            thermals = ASCEND;
            if (curLavaDistance >= maxLavaDistance) {
                curLavaDistance = lavaDistance;
                thermals = DRIFT;
            }
        } else {
            curLavaDistance = lavaDistance;
        }
        return thermals;
    }

    // apply 'turbulence' in the form of a collision force
    private void applyTurbulence(boolean roughWeather) {
        double rmin = 0.1;
        double deltaPos = rmin + 0.9 * rand.nextDouble();

        if (deltaPos >= 0.20) {
            double rmax = roughWeather ? 0.8 : 0.5;
            rmax = (rand.nextInt(5) == 0) ? 1.0 : rmax;  // gusting
            double deltaX = rmin + (rmax - rmin) * rand.nextDouble();
            double deltaY = rmin + 0.2 * rand.nextDouble();
            double deltaZ = rmin + (rmax - rmin) * rand.nextDouble();

            deltaPos = MathHelper.sqrt(deltaPos);
            double deltaInv = 1.0 / deltaPos;

            deltaX /= deltaPos;
            deltaY /= deltaPos;
            deltaZ /= deltaPos;

            deltaInv = deltaInv > 1.0 ? 1.0 : deltaInv;

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
    // of the parachute. Yes, I know that most parachutes
    // don't generate contrails (no engines), but most worlds
    // aren't made of blocks with cubic cows either. If you
    // like, you can think of the trails as chemtrails.
    private void generateContrails(boolean ascending) {
        double velocity = Math.sqrt(motionX * motionX + motionZ * motionZ);
        double cosYaw = 2.25 * Math.cos(Math.toRadians(90.0 + rotationYaw));
        double sinYaw = 2.25 * Math.sin(Math.toRadians(90.0 + rotationYaw));

        for (int k = 0; (double) k < 1.0 + velocity; k++) {
            double sign = (rand.nextInt(2) * 2 - 1) * 0.7;
            double x = posX + (posX - prevPosX) + cosYaw * -0.45 + sinYaw * sign;
            double y = posY - 0.25;
            double z = posZ + (posZ - prevPosZ) + sinYaw * -0.45 - cosYaw * sign;

            if (ascending) {
                world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x, y, z, motionX, motionY, motionZ);
            }
            if (!ascending && velocity > 0.01) {
                world.spawnParticle(EnumParticleTypes.CLOUD, x, y, z, motionX, motionY, motionZ);
            }
        }
    }

    @Override
    public void updatePassenger(@Nonnull Entity passenger) {
        if (isPassenger(passenger)) {
            float offset = (float) ((isDead ? 0.01 : getMountedYOffset()) + passenger.getYOffset());
            Vec3d vec3d = (new Vec3d(0.0, 0.0, 0.0)).rotateYaw(-rotationYaw * 0.017453292F - ((float) Math.PI / 2F));
            passenger.setPosition(posX + vec3d.x, posY + (double) offset, posZ + vec3d.z);
            passenger.rotationYaw += deltaRotation;
            passenger.setRotationYawHead(passenger.getRotationYawHead() + (float) deltaRotation);
            applyYawToEntity(passenger);

            // check for player colliding with blocks. dismounting if the blocks are not air, water, or grass/vines
            AxisAlignedBB bb = passenger.getEntityBoundingBox();
            if (!world.isRemote && world.checkBlockCollision(bb)) {
                if (world.isMaterialInBB(bb, Material.WATER)) {
                    if (ConfigHandler.getDismountInWater()) {
                        passenger.dismountRidingEntity();
                    } else {
                        BlockPos bp = new BlockPos(passenger.posX, passenger.posY, passenger.posZ);
                        bp.down(Math.round((float) bb.minY));
                        if (!world.getBlockState(bp).isSideSolid(world, bp, EnumFacing.UP)) {
                            return;
                        }
                    }
                } else if (world.isMaterialInBB(bb, Material.LEAVES)) {
                    return;
                } else if (world.isMaterialInBB(bb, Material.VINE)) { // handle special case tallgrass
                    BlockPos bp = new BlockPos(passenger.posX, passenger.posY, passenger.posZ);
                    bp.down(Math.round((float) bb.minY));
                    if (!world.getBlockState(bp).isSideSolid(world, bp, EnumFacing.UP)) {
                        return;
                    }
                }
                passenger.dismountRidingEntity();
            }
        }
    }

    protected void applyYawToEntity(Entity entityToUpdate) {
        entityToUpdate.setRenderYawOffset(rotationYaw);
        float yaw = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - rotationYaw);
        float yawClamp = MathHelper.clamp(yaw, -HEAD_TURN_ANGLE, HEAD_TURN_ANGLE);
        entityToUpdate.prevRotationYaw += yawClamp - yaw;
        entityToUpdate.rotationYaw += yawClamp - yaw;
        entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void applyOrientationToEntity(Entity entityToUpdate) {
        applyYawToEntity(entityToUpdate);
    }

    @Override
    public void writeEntityToNBT(@Nonnull NBTTagCompound nbt) {
    }

    @Override
    public void readEntityFromNBT(@Nonnull NBTTagCompound nbt) {
    }

}
