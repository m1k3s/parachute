/*
 * ParachuteEntity.java
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

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

public class ParachuteEntity extends Entity {

    private double maxAltitude;
    private boolean allowThermals;
    private boolean lavaThermals;
    private double lavaDistance;
    private double maxLavaDistance;
    private double curLavaDistance;
    private boolean constantTurbulence;
    private boolean showContrails;
    private boolean rideInWater;
    private double forwardMomentum;
    private double backMomentum;
    private double rotationMomentum;
    private double slideMomentum;
    private double deltaRotation;

    @OnlyIn(Dist.CLIENT)
    private double velocityX;
    @OnlyIn(Dist.CLIENT)
    private double velocityY;
    @OnlyIn(Dist.CLIENT)
    private double velocityZ;


    private final static double DRIFT = 0.004; // value applied to motionY to descend or DRIFT downward
    private final static double ASCEND = DRIFT * -10.0; // -0.04 - value applied to motionY to ascend
    private final static double OFFSET = 2.5; // player Y offset from parachute
    private final static float HEAD_TURN_ANGLE = 120.0f;
    private final static double DECAY_MOMENTUM = 0.97;

    private static boolean ascendMode;

    public ParachuteEntity(EntityType<? extends ParachuteEntity> chute, World world) {
        super(chute, world);

        constantTurbulence = ConfigHandler.CommonConfig.getConstantTurbulence();
        showContrails = ConfigHandler.CommonConfig.getShowContrails();
        lavaDistance = ConfigHandler.CommonConfig.getMinLavaDistance();
        allowThermals = ConfigHandler.CommonConfig.getAllowThermals();
        maxAltitude = ConfigHandler.CommonConfig.getHeightLimit();
        lavaThermals = ConfigHandler.CommonConfig.getLavaThermals() && !(allowThermals && ConfigHandler.CommonConfig.getLavaDisablesThermals());
        rideInWater = ConfigHandler.CommonConfig.getRideInWater();
        maxLavaDistance = ConfigHandler.CommonConfig.getMaxLavaDistance();
        forwardMomentum = ConfigHandler.CommonConfig.getForwardMomentum();
        backMomentum = ConfigHandler.CommonConfig.getBackMomentum();
        rotationMomentum = ConfigHandler.CommonConfig.getRotationMomentum();
        slideMomentum = ConfigHandler.CommonConfig.getSlideMomentum();

        curLavaDistance = lavaDistance;
        this.world = world;
        preventEntitySpawning = true;
        ascendMode = false;
        setSilent(false);
    }

    public ParachuteEntity(World world, double x, double y, double z) {
        this(Parachute.RegistryEvents.PARACHUTE, world);
        setPosition(x, y, z);
        func_213317_d(Vec3d.ZERO);
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
    public AxisAlignedBB getCollisionBox(Entity entity) {
        if (entity != getControllingPassenger() && entity.getRidingEntity() != this) {
            return entity.getBoundingBox();
        }
        return null;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return getBoundingBox();
    }

    // skydiver should hang when on the parachute and then
    // pick up legs when landing.
    @Override
    public boolean shouldRiderSit() {
        Entity skyDiver = getControllingPassenger();
        boolean sitting = false;
        if (skyDiver != null) {
            BlockPos bp = new BlockPos(skyDiver.posX, skyDiver.getBoundingBox().minY - 3.0, skyDiver.posZ);
            sitting = !world.getBlockState(bp).isAir(world, bp);
        }
        return sitting;
    }

    @Nonnull
    @Override
    public Direction getAdjustedHorizontalFacing() {
        return getHorizontalFacing().rotateY();
    }

    @Override
    public boolean canPassengerSteer() {
        return true;
    }

    @Override
    @Nonnull
    public IPacket<?> func_213297_N() {
        return new SSpawnObjectPacket(getEntityId(), getUniqueID(), posX, posY, posZ, rotationPitch, rotationYaw, Parachute.RegistryEvents.PARACHUTE, 1, func_213322_ci());
//        return new SSpawnObjectPacket(this);
    }

    @Override
    public Entity getControllingPassenger() {
        List<Entity> list = getPassengers();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public boolean canBeRiddenInWater(Entity pilot) {
        return rideInWater;
    }

    @Override
    public double getMountedYOffset() {
        return -OFFSET;
    }

    @Override
    public boolean canBeCollidedWith() {
        return !removed;
    }

    @Override
    protected void readAdditional(@Nonnull CompoundNBT compound) {

    }

    @Override
    protected void func_213281_b(@Nonnull CompoundNBT p_213281_1_) {

    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        rotationYaw = passenger.rotationYaw;
        rotationPitch = passenger.rotationPitch;
    }

    @OnlyIn(Dist.CLIENT)
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
        func_213293_j(velocityX, velocityY, velocityZ);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void setVelocity(double x, double y, double z) {
        velocityX = x;
        velocityY = y;
        velocityZ = z;
        func_213293_j(velocityX, velocityY, velocityZ);
    }

    // updateInputs is called by ParachuteInputEvent class
    public void updateInputs(MovementInput input) {
        Parachute.getLogger().info("############### updateInputs() ################");
        if (isBeingRidden() && Parachute.isClientSide(world)) {
            double motionFactor = 0.0f;
            boolean WASDSteering = ConfigHandler.ClientConfig.getSteeringControl();

            if (input.forwardKeyDown) {
                motionFactor += forwardMomentum;
            }
            if (input.backKeyDown) {
                motionFactor -= backMomentum;
            }
            if (WASDSteering) {
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

            if (WASDSteering) {
                rotationYaw += deltaRotation;
            } else {
                Entity skyDiver = getControllingPassenger();
                if (skyDiver instanceof LivingEntity) {
                    LivingEntity pilot = (LivingEntity) skyDiver;
                    rotationYaw = (float) (pilot.rotationYaw + -pilot.moveStrafing * 90.0);
                }
            }

            double motionY = currentDescentRate() * (-1);
            double motionX = MathHelper.sin((float) Math.toRadians(-rotationYaw)) * motionFactor;
            double motionZ = MathHelper.cos((float) Math.toRadians(rotationYaw)) * motionFactor;
            func_213317_d(func_213322_ci().add(motionX, motionY, motionZ));

            if (((ConfigHandler.CommonConfig.getWeatherAffectsDrift() && isBadWeather()) || constantTurbulence) && rand.nextBoolean()) {
                applyTurbulence(world.isThundering());
            }
        }
    }

    @Override
    protected void registerData() {}

    @Override
    public void tick() {
        Entity skyDiver = getControllingPassenger();
        // the player has pressed LSHIFT or been killed,
        // may be necessary for LSHIFT to kill the parachute
        if (skyDiver == null && Parachute.isServerSide(world)) { // server side
            remove();
            return;
        }

        if (showContrails && skyDiver != null) {
            generateContrails(ascendMode);
        }

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        super.tick();

        if (allowThermals && ascendMode && skyDiver != null) { // play the lift sound. kinda like a hot air balloon's burners effect
            float burnVolume = (float) ConfigHandler.ClientConfig.getBurnVolume();
            skyDiver.playSound(Parachute.RegistryEvents.LIFTCHUTE, burnVolume, 1.0F / (rand.nextFloat() * 0.4F + 0.8F));
        }

        // apply momentum/decay
        Vec3d curMotion = func_213322_ci();
        double motionX = curMotion.x * DECAY_MOMENTUM;
        double motionY = curMotion.y * (curMotion.y < 0.0 ? 0.96 : 0.98);
        double motionZ = curMotion.z * DECAY_MOMENTUM;
        deltaRotation *= 0.9;
        func_213293_j(motionX, motionY, motionZ);

        // move the parachute with the motion equations applied
        func_213315_a(MoverType.SELF, func_213322_ci());

        // something bad happened, somehow the skydiver was killed.
        if (Parachute.isServerSide(world) && skyDiver != null && !skyDiver.isAlive()) { // server side
            skyDiver.stopRiding();
        }

        doBlockCollisions();
    }

    // check for bad weather, if the biome can rain or snow check to see
    // if it is raining (or snowing) or thundering.
    private boolean isBadWeather() {
        BlockPos bp = new BlockPos(posX, posY, posZ);
        Chunk chunk = world.getChunk(bp);
        boolean canSnow = chunk.getWorld().getBiome(bp).doesSnowGenerate(world, bp);
        boolean canRain = chunk.getWorld().getBiome(bp).getDownfall() > 0;
        return (canRain || canSnow) && (world.isRaining() || world.isThundering());
    }

    // determines the descent rate based on whether or not
    // the space bar has been pressed. weather and lava affect
    // the final result.
    private double currentDescentRate() {
        double descentRate = DRIFT; // defaults to DRIFT

        if (ConfigHandler.CommonConfig.getWeatherAffectsDrift()) {
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
        RayTraceResult mop = world.func_217299_a(new RayTraceContext(v1, v2, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, this));
        if (mop.func_216346_c() == RayTraceResult.Type.BLOCK) {
            BlockPos blockpos = new BlockPos(mop.func_216347_e().x, mop.func_216347_e().y, mop.func_216347_e().z);
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
    // aren't made of block with cubic cows either. If you
    // like, you can think of the trails as chemtrails.
    private void generateContrails(boolean ascending) {
        Vec3d motionVec = func_213322_ci();
        double velocity = Math.sqrt(motionVec.x * motionVec.x + motionVec.z * motionVec.z);
        double cosYaw = 2.25 * Math.cos(Math.toRadians(90.0 + rotationYaw));
        double sinYaw = 2.25 * Math.sin(Math.toRadians(90.0 + rotationYaw));

        for (int k = 0; (double) k < 1.0 + velocity; k++) {
            double sign = (rand.nextInt(2) * 2 - 1) * 0.7;
            double x = posX + (posX - prevPosX) + cosYaw * -0.45 + sinYaw * sign;
            double y = posY - 0.25;
            double z = posZ + (posZ - prevPosZ) + sinYaw * -0.45 - cosYaw * sign;

            if (ascending) {
                world.addParticle(ParticleTypes.LARGE_SMOKE, x, y, z, motionVec.x, motionVec.y, motionVec.z);
            }
            if (!ascending && velocity > 0.01) {
                world.addParticle(ParticleTypes.CLOUD, x, y, z, motionVec.x, motionVec.y, motionVec.z);
            }
        }
    }

    @Override
    public void updatePassenger(@Nonnull Entity passenger) {
        if (isPassenger(passenger)) {
            float offset = (float)(/*removed ? 0.01f : */getMountedYOffset() + passenger.getYOffset());
            Vec3d vec3d = (new Vec3d(0.0, 0.0, 0.0)).rotateYaw(-rotationYaw * ((float) Math.PI / 180F) - ((float) Math.PI / 2F));
            passenger.setPosition(posX + vec3d.x, posY + (double) offset, posZ + vec3d.z);
            passenger.rotationYaw += deltaRotation;
            passenger.setRotationYawHead(passenger.getRotationYawHead() + (float) deltaRotation);
            applyYawToEntity(passenger);

            // check for passenger collisions
            checkForPlayerCollisions(passenger);
        }
    }

    // check for player colliding with block. stop riding if block is not air, water,
    // grass/vines, or snow/snow layers
    private void checkForPlayerCollisions(Entity passenger) {
        AxisAlignedBB bb = passenger.getBoundingBox();
        if (Parachute.isServerSide(world) && world.checkBlockCollision(bb)) {
            // if in water check stop dismount-in-water flag, check for solid block below water
            if (world.isMaterialInBB(bb, Material.WATER)) {
                if (!rideInWater) {
                    passenger.stopRiding();
                } else {
                    BlockPos bp = new BlockPos(passenger.posX, passenger.posY, passenger.posZ);
                    bp.down(Math.round((float) bb.minY));
                    if (!world.getBlockState(bp).isSolid()) {
                        return;
                    }
                }
            } else if (world.isMaterialInBB(bb, Material.SNOW)) { // check for snow/snow layer, stop riding if solid block below
                BlockPos bp = new BlockPos(passenger.posX, passenger.posY, passenger.posZ);
                bp.down(Math.round((float) bb.minY));
                if (!world.getBlockState(bp).isSolid()) {
                    return;
                }
            } else if (world.isMaterialInBB(bb, Material.LEAVES)) { // pass through leaves
                return;
            } else if (world.isMaterialInBB(bb, Material.VINE)) { // handle special case tallgrass
                // check for tallgrass, only stop riding when reaching solid block below
                BlockPos bp = new BlockPos(passenger.posX, passenger.posY, passenger.posZ);
                bp.down(Math.round((float) bb.minY));
                if (!world.getBlockState(bp).isSolid()) {
                    return;
                }
            }
            passenger.stopRiding();
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

    @OnlyIn(Dist.CLIENT)
    @Override
    public void applyOrientationToEntity(Entity entityToUpdate) {
        applyYawToEntity(entityToUpdate);
    }

    @Override
    protected boolean canFitPassenger(Entity passenger) {
        return getPassengers().isEmpty();
    }

}