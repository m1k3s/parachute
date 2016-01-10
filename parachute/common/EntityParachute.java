package parachute.common;

import java.util.List;
import java.util.Random;
import java.lang.Thread;
import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.src.*;

//
//Copyright 2011 Michael Sheppard (crackedEgg)
//

public class EntityParachute extends Entity {
	private boolean isRotating;
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
	private double newMotionZ;
	private double motionFactor;
	private double maxAltitude;
	private boolean allowThermals;
	private static double descentRate;

	private final int hitTime = 10;
	private final int maxDamage = 40;

	final static double ascend = -0.020;
	final static double drift = 0.005;
	final static double descend = 0.030;

	public EntityParachute(World world) {
		super(world);

		preventEntitySpawning = true;
		setSize(1.0F, 0.5F);
		yOffset = height / 2F;
		descentRate = drift;
		motionFactor = 0.07D;
		isRotating = true;
		
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

	protected boolean canTriggerWalking() {
		return false;
	}

	protected void entityInit() {
		dataWatcher.addObject(17, new Integer(0)); // time since last hit
		dataWatcher.addObject(18, new Integer(1)); // forward direction
		dataWatcher.addObject(19, new Integer(0)); // damage taken | current damage
	}

	public AxisAlignedBB getCollisionBox(Entity entity) {
		return entity.boundingBox;
	}

	public AxisAlignedBB getBoundingBox() {
		return boundingBox;
	}

	public boolean canBePushed() {
		return false;
	}

	public double getMountedYOffset() {
		return -2.5D;
	}
	
	public void destroyParachute() {
		setDead();
		ItemParachute.deployed = false;
	}

	// parachute takes additional damage from being hit
	public boolean attackEntityFrom(DamageSource damagesource, int damage) {
		if (!worldObj.isRemote && !isDead) {
			setForwardDirection(-getForwardDirection());
			setTimeSinceHit(hitTime);
			setDamageTaken(getDamageTaken() + damage);
			setBeenAttacked();

			if (getDamageTaken() > maxDamage) {
				if (riddenByEntity != null) {
					riddenByEntity.mountEntity(this);
				}
				// drop a parachute item
				//dropItemWithOffset(Parachute.getItemID() + 256, 1, 0.0F);
				dropRemains();
				destroyParachute(); // aaaaiiiiieeeeee!!! ... thud!
			}
		}
		return true;
	}

	// use shears to cut the parachute coords...
	public boolean interact(EntityPlayer entityplayer) {
		ItemStack itemstack = entityplayer.inventory.getCurrentItem();
		if (itemstack != null && itemstack.itemID == Item.shears.shiftedIndex && riddenByEntity != null) {
			if (!worldObj.isRemote) {
				// instead of killing the parachute, remove riding entity
				// parachute death is handled in onUpdate()
				riddenByEntity = null; // ...rider plummets to death.
			}
			if (!entityplayer.capabilities.isCreativeMode) {
				itemstack.damageItem(2, entityplayer); // one use worth of damage
			}
			return true;
		}
		return true;
	}

	public boolean canBeCollidedWith() {
		return !isDead;
	}

	@SideOnly(Side.CLIENT)
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int inc) {
		if (isRotating) {
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

		// verticalMotion
		motionX = velocityX;
		motionY = velocityY;
		motionZ = newMotionZ;
	}
	
	@SideOnly(Side.CLIENT)
	public void setVelocity(double x, double y, double z) {
		velocityX = motionX = x;
		velocityY = motionY = y;
		newMotionZ = motionZ = z;
	}

	public void onUpdate() {
		super.onUpdate();

		// the player has probably been killed or mounted another entity ::snicker::
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
		if (getDamageTaken() > 0) {
			setDamageTaken(getDamageTaken() - 1);
		}

		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		// drop the chute when close to ground
		checkShouldDropChute(posX, posY, posZ, 3.0D);

		// forward velocity
		double velocity = Math.sqrt(motionX * motionX + motionZ * motionZ);

		if (worldObj.isRemote && isRotating) { // external (remote) server
			if (newRotationInc > 0) {
				double x = posX + (newPosX - posX) / (double) newRotationInc;
				double y = posY + (newPosY - posY) / (double) newRotationInc;
				double z = posZ + (newPosZ - posZ) / (double) newRotationInc;

				double adjYaw = MathHelper.wrapAngleTo180_double(newRotationYaw 	- (double) rotationYaw);

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
			if (riddenByEntity != null) {
				motionX += riddenByEntity.motionX * motionFactor;
				motionZ += riddenByEntity.motionZ * motionFactor;

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
			}

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

			double adjustedYaw = MathHelper.wrapAngleTo180_double(yaw
					- (double) rotationYaw);

			if (adjustedYaw > 45.0D) {
				adjustedYaw = 45.0D;
			}
			if (adjustedYaw < -45.0D) {
				adjustedYaw = -45.0D;
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
	}

	public double currentDescentRate() {
		EntityPlayer player = (EntityPlayer)riddenByEntity;
		if (player == null) {
			return descentRate;
		}
		PlayerInfo pInfo = PlayerManagerParachute.getInstance().getPlayerInfoFromPlayer(player);
		if (pInfo == null) {
			return descentRate;
		} else {
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
			
			default:
				descentRate = drift;
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
		dropItem(Item.silk.shiftedIndex, 1);
	}

	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
	}

	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
	}

	@SideOnly(Side.CLIENT)
	public float getShadowSize() {
		return 0.0F;
	}
	
	public void setDamageTaken(int damage) {
		dataWatcher.updateObject(19, Integer.valueOf(damage));
	}

	public int getDamageTaken() {
		return dataWatcher.getWatchableObjectInt(19);
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
	public void func_70270_d(boolean par1) {
		isRotating = par1;
	}
	
}
