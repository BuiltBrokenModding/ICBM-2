package icbm.gangshao.turret.mount;

import icbm.api.explosion.IExplosive;
import icbm.api.sentry.AmmoPair;
import icbm.api.sentry.IAmmo;
import icbm.api.sentry.ProjectileTypes;
import icbm.core.ZhuYaoBase;
import icbm.gangshao.ZhuYaoGangShao;
import icbm.gangshao.actions.LookHelper;

import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.implement.IRedstoneReceptor;
import universalelectricity.prefab.multiblock.IMultiBlock;
import universalelectricity.prefab.multiblock.TileEntityMulti;
import universalelectricity.prefab.network.IPacketReceiver;

/** Railgun
 * 
 * @author Calclavia */
public class TileEntityRailTurret extends TileEntityMountableTurret implements IPacketReceiver, IRedstoneReceptor, IMultiBlock
{

	private int gunChargingTicks = 0;

	private boolean redstonePowerOn = false;
	/** Is current ammo antimatter */
	private boolean isAntimatter;

	private float explosionSize;

	private int explosionDepth;

	/** A counter used client side for the smoke and streaming effects of the Railgun after a shot. */
	private int endTicks = 0;

	public TileEntityRailTurret()
	{
		this.baseFiringDelay = 70;
		this.minFiringDelay = 30;
		this.maxHeat = 1000;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (this.getPlatform() != null)
		{
			if (this.redstonePowerOn && this.canActivateWeapon() && this.gunChargingTicks == 0)
			{
				this.onWeaponActivated();
			}

			if (this.gunChargingTicks > 0)
			{
				this.gunChargingTicks++;

				if (this.gunChargingTicks >= 30)
				{
					this.onFire();
					this.sendShotToClient(null);
					this.gunChargingTicks = 0;
				}
			}

			if (this.worldObj.isRemote && --this.endTicks > 0)
			{
				MovingObjectPosition objectMouseOver = this.rayTrace(2000);

				if (objectMouseOver != null)
				{
					this.drawParticleStreamTo(Vector3.add(new Vector3(objectMouseOver), 0.5));
				}
			}
		}
	}

	public void onFire()
	{
		while (this.explosionDepth > 0)
		{
			MovingObjectPosition objectMouseOver = this.rayTrace(2000);

			if (objectMouseOver != null)
			{
				if (!ZhuYaoGangShao.isProtected(this.worldObj, new Vector3(objectMouseOver), ZhuYaoGangShao.FLAG_RAILGUN) && objectMouseOver.typeOfHit == EnumMovingObjectType.TILE)
				{
					if (this.isAntimatter)
					{
						/** Remove Redmatter Explosions. */
						int radius = 50;
						AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(objectMouseOver.blockX - radius, objectMouseOver.blockY - radius, objectMouseOver.blockZ - radius, objectMouseOver.blockX + radius, objectMouseOver.blockY + radius, objectMouseOver.blockZ + radius);
						List<Entity> missilesNearby = worldObj.getEntitiesWithinAABB(Entity.class, bounds);

						for (Entity entity : missilesNearby)
						{
							if (entity instanceof IExplosive)
							{
								entity.setDead();
							}
						}
					}
					int blockID = this.worldObj.getBlockId(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
					Block block = Block.blocksList[blockID];
					/* Any hardness under zero is unbreakable  */
					if (block != null && block.getBlockHardness(this.worldObj, objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ) > 0)
					{
						this.worldObj.setBlock(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ, 0, 0, 2);
					}

					this.worldObj.newExplosion(this.mountedPlayer, objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ, explosionSize, true, true);
				}
			}

			this.explosionDepth--;
		}
	}

	public void drawParticleStreamTo(Vector3 endPosition)
	{
		if (this.worldObj.isRemote)
		{
			Vector3 startPosition = this.getMuzzle();
			Vector3 direction = LookHelper.getDeltaPositionFromRotation(this.currentRotationYaw - 15, this.currentRotationPitch);
			double xoffset = 1.3f;
			double yoffset = -.2;
			double zoffset = 0.3f;
			Vector3 horzdir = direction.normalize();
			horzdir.y = 0;
			horzdir = horzdir.normalize();
			double cx = startPosition.x + direction.x * xoffset - direction.y * horzdir.x * yoffset - horzdir.z * zoffset;
			double cy = startPosition.y + direction.y * xoffset + (1 - Math.abs(direction.y)) * yoffset;
			double cz = startPosition.z + direction.x * xoffset - direction.y * horzdir.x * yoffset + horzdir.x * zoffset;
			double dx = endPosition.x - cx;
			double dy = endPosition.y - cy;
			double dz = endPosition.z - cz;
			double ratio = Math.sqrt(dx * dx + dy * dy + dz * dz);

			while (Math.abs(cx - endPosition.x) > Math.abs(dx / ratio))
			{
				this.worldObj.spawnParticle("townaura", cx, cy, cz, 0.0D, 0.0D, 0.0D);
				cx += dx * 0.1 / ratio;
				cy += dy * 0.1 / ratio;
				cz += dz * 0.1 / ratio;
			}
		}
	}

	@Override
	public void renderShot(Vector3 target)
	{
		this.endTicks = 20;
	}

	@Override
	public void playFiringSound()
	{
		this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "icbm.railgun", 5F, 1F);
	}

	@Override
	public double getVoltage()
	{
		return 220;
	}

	@Override
	public void onDestroy(TileEntity callingBlock)
	{
		this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, 0);
		this.worldObj.setBlock(this.xCoord, this.yCoord + 1, this.zCoord, 0);
	}

	@Override
	public void onCreate(Vector3 position)
	{
		this.worldObj.setBlock(position.intX(), position.intY() + 1, position.intZ(), ZhuYaoBase.bJia.blockID, 0, 2);
		((TileEntityMulti) this.worldObj.getBlockTileEntity(position.intX(), position.intY() + 1, position.intZ())).setMainBlock(position);
	}

	@Override
	public Vector3 getMuzzle()
	{
		Vector3 position = new Vector3(this.xCoord + 0.5, this.yCoord + 1.5, this.zCoord + 0.5);
		float yaw = (float) Math.toDegrees(this.wantedRotationYaw);
		float pitch = (float) Math.toDegrees(this.wantedRotationPitch);
		//System.out.println("World:" + (this.worldObj.isRemote ? "Client" : "Server") + " Y:" + yaw + " P:" + pitch);
		return Vector3.add(position, Vector3.multiply(LookHelper.getDeltaPositionFromRotation(yaw - 10, pitch), 1.5));

	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);

		this.currentRotationPitch = this.wantedRotationPitch * 0.0175f;
		this.currentRotationYaw = this.wantedRotationYaw * 0.0175f;
	}

	@Override
	public void onPowerOn()
	{
		this.redstonePowerOn = true;
	}

	@Override
	public void onPowerOff()
	{
		this.redstonePowerOn = false;
	}

	@Override
	public double getFiringRequest()
	{
		return 300000;
	}

	@Override
	public void onWeaponActivated()
	{
		super.onWeaponActivated();
		this.gunChargingTicks = 1;
		this.redstonePowerOn = false;
		this.isAntimatter = false;
		AmmoPair<IAmmo, ItemStack> ammo = this.getPlatform().hasAmmunition(ProjectileTypes.RAILGUN);
		if (ammo != null)
		{
			if (ammo.getStack().equals(ZhuYaoGangShao.antimatterBullet) && this.getPlatform().useAmmunition(ammo))
			{
				this.isAntimatter = true;
			}
			else
			{
				this.getPlatform().useAmmunition(ammo);
			}
		}

		this.getPlatform().wattsReceived = 0;

		this.explosionSize = 5f;
		this.explosionDepth = 5;

		if (isAntimatter)
		{
			this.explosionSize = 8f;
			this.explosionDepth = 10;
		}
	}

	@Override
	public boolean canActivateWeapon()
	{
		if (this.getPlatform() != null)
		{
			if (this.getPlatform().hasAmmunition(ProjectileTypes.RAILGUN) != null)
			{
				if (this.getPlatform().wattsReceived >= this.getFiringRequest())
				{
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public float addInformation(HashMap<String, Integer> map, EntityPlayer player)
	{
		super.addInformation(map, player);
		return 2;
	}

	@Override
	public float getRotationSpeed()
	{
		return 1f;
	}

	@Override
	public double getRunningRequest()
	{
		return 10;
	}

	@Override
	public int getMaxHealth()
	{
		return 450;
	}

	@Override
	public double getHeatPerShot()
	{
		return 220;
	}
}
