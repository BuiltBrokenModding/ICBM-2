package icbm.gangshao.turret;

import icbm.api.IMissile;
import icbm.api.sentry.IAATarget;
import icbm.gangshao.ZhuYaoGangShao;
import icbm.gangshao.actions.LookHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import universalelectricity.core.vector.Vector3;

/**
 * AA Turret, shoots down missiles and planes.
 * 
 * @author DarkGaurdsman
 * 
 */
public class TileEntityAATurret extends TileEntityAutoTurret
{
	@Override
	public void initiate()
	{
		super.initiate();
		this.targetMissiles = true;
		this.targetCrafts = true;
	}

	@Override
	public void onWeaponActivated()
	{
		if (this.getPlatform() != null && this.getPlatform().useAmmunition(ZhuYaoGangShao.conventionalBullet))
		{
			boolean fired = false;
			if (this.target instanceof EntityLiving)
			{
				if (this.worldObj.rand.nextFloat() > 0.1)
				{
					((EntityLiving) this.target).attackEntityFrom(DamageSource.setExplosionSource(null), 10);
				}

				Vector3 look = LookHelper.getDeltaPositionFromRotation(this.targetRotationYaw, this.targetRotationPitch);
				look.multiply(-1);
				((EntityLiving) this.target).knockBack(null, 0, look.intX(), look.intZ());
				this.getPlatform().wattsReceived -= this.getRequest();
				fired = true;

			}
			else if (this.target instanceof IMissile)
			{
				if (this.worldObj.rand.nextFloat() > 0.6)
				{
					((IMissile) this.target).normalExplode();
					System.out.println("Missile Killed");
				}
				fired = true;
			}
			else if (this.target instanceof IAATarget)
			{
				if (this.worldObj.rand.nextFloat() > 0.3)
				{
					int damage = ((IAATarget) this.target).doDamage(10);
					if (damage == -1 && this.worldObj.rand.nextFloat() > 0.7)
					{
						((IAATarget) this.target).explode();
					}
					else if (damage < 0)
					{
						((IAATarget) this.target).explode();
					}
				}
				fired = true;
			}
			if (fired)
			{
				if (!this.worldObj.isRemote && this.worldObj.rand.nextFloat() > 0.8)
				{
					Vector3 spawnPos = this.getMuzzle();
					EntityItem entityShell = new EntityItem(this.worldObj, spawnPos.x, spawnPos.y, spawnPos.z, ZhuYaoGangShao.bulletShell.copy());
					entityShell.delayBeforeCanPickup = 20;
					this.worldObj.spawnEntityInWorld(entityShell);
				}

				this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "icbm.machinegun", 5F, 1F);
			}
		}
	}

	@Override
	public boolean isRunning()
	{
		return super.isRunning() && this.getPlatform().wattsReceived > 0 && !this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public boolean canActivateWeapon()
	{
		return super.canActivateWeapon() && !this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public double getDetectRange()
	{
		int baseRange = 50;

		if (this.getPlatform() != null)
		{
			return baseRange + 10 * this.getPlatform().getUpgrades("Capacity");
		}

		return baseRange;
	}
	@Override
	public AxisAlignedBB getTargetingBox()
	{
		return AxisAlignedBB.getBoundingBox(xCoord - this.getDetectRange(), yCoord - this.getDetectRange(), zCoord - this.getDetectRange(), xCoord + this.getDetectRange(), yCoord + this.getDetectRange(), zCoord + this.getDetectRange());
	}
	@Override
	public double getVoltage()
	{
		return 120;
	}

	@Override
	public int getCooldown()
	{
		return 1;
	}

	@Override
	public double getRequest()
	{
		return 27;
	}

}
