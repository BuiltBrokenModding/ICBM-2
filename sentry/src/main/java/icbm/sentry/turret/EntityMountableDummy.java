package icbm.sentry.turret;

import icbm.sentry.turret.block.TileTurret;
import icbm.sentry.turret.mounted.TurretMounted;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import universalelectricity.api.vector.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Entity that fakes being the sentry gun so that the player can ride the
 * sentry. This entity also handles most damage interaction with other entities.
 * 
 * @Author DarkGuardsman
 */
public class EntityMountableDummy extends EntityLiving
{
	private TileTurret turretProvider;

	public EntityMountableDummy(World world)
	{
		super(world);
	}

	public EntityMountableDummy(TileTurret controller)
	{
		this(controller.worldObj);
		this.isImmuneToFire = true;
		this.setPosition(controller.xCoord + 0.5, controller.yCoord + 0.5, controller.zCoord + 0.5);
		this.turretProvider = controller;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		return true;
	}

	@Override
	public boolean isPotionApplicable(PotionEffect par1PotionEffect)
	{
		return false;
	}

	@Override
	public String getEntityName()
	{
		return "Seat";
	}

	/** Called to update the entity's position/logic. */
	@Override
	public void onUpdate()
	{
		if (this.ridingEntity != null && this.ridingEntity.isDead)
		{
			this.ridingEntity = null;
		}
		if (this.turretProvider == null || this.turretProvider.isInvalid())
		{
			this.setDead();
			return;
		}

		if (this.turretProvider instanceof TileTurret)
		{
			if (((TileTurret) this.turretProvider).getTurret() instanceof TurretMounted)
				((TileTurret) this.turretProvider).setFakeEntity(this);
		}

		if (this.worldObj.isRemote && this.riddenByEntity != null)
		{
			this.riddenByEntity.updateRiderPosition();
		}

		// TODO adjust for center of sentry
		this.setPosition(this.turretProvider.xCoord + 0.5, this.turretProvider.yCoord, this.turretProvider.zCoord + 0.5);
	}

	@Override
	public void updateRidden()
	{
		super.updateRidden();
	}

	@Override
	public void updateRiderPosition()
	{
		super.updateRiderPosition();/*
									 * // TODO cache this value and only update it when the yaw ||
									 * pitch changes
									 * if (this.riddenByEntity != null)
									 * {System.out.println("TEST");
									 * Vector3 vec = new Vector3(this.turretProvider);
									 * Vector3 offset = new Vector3(0, 0, 1);
									 * vec.translate(this.turretProvider.getTurret().getCenterOffset(
									 * ));
									 * if (this.turretProvider.getTurret() instanceof TurretMounted)
									 * {
									 * offset = ((TurretMounted)
									 * this.turretProvider.getTurret()).getRiderOffset();
									 * }
									 * offset.rotate(turretProvider.getTurret().getServo().yaw,
									 * turretProvider.getTurret().getServo().pitch);
									 * vec.add(offset);
									 * this.riddenByEntity.setPosition(vec.x, vec.y, vec.z);
									 * this.riddenByEntity.rotationYaw = (float)
									 * turretProvider.getTurret().getServo().yaw;
									 * this.riddenByEntity.rotationPitch = (float)
									 * turretProvider.getTurret().getServo().pitch;
									 * }
									 */
	}

	@Override
	public double getMountedYOffset()
	{
		if (this.turretProvider.getTurret() instanceof TurretMounted)
			return ((TurretMounted) this.turretProvider.getTurret()).getRiderOffset().y;
		else
			return -0.5;
	}

	@Override
	public boolean shouldRiderSit()
	{
		return true;
	}

	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity par1Entity)
	{
		return AxisAlignedBB.getBoundingBox(this.posX - .6, this.posY - .6, this.posZ - .6, this.posX + .6, this.posY + .6, this.posZ + .6);
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInRangeToRenderVec3D(Vec3 par1Vec3)
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double par1)
	{
		return false;
	}

	@Override
	public void setVelocity(double par1, double par3, double par5)
	{

	}

	@Override
	public boolean isInsideOfMaterial(Material par1Material)
	{
		return false;
	}

	@Override
	public boolean interact(EntityPlayer player)
	{
		if (this.turretProvider != null && player != null)
		{
			Block block = Block.blocksList[this.worldObj.getBlockId(this.turretProvider.xCoord, this.turretProvider.yCoord, this.turretProvider.zCoord)];

			if (block != null)
			{
				return block.onBlockActivated(this.turretProvider.worldObj, this.turretProvider.xCoord, this.turretProvider.yCoord, this.turretProvider.zCoord, player, 0, 0, 0, 0);
			}
		}
		return false;
	}

}