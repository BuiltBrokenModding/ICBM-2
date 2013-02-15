package icbm.sentry.turret;

import icbm.sentry.ICBMSentry;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.DamageSource;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.TranslationHelper;

public class TileEntityGunTurret extends TileEntityAutoTurret
{
	@Override
	public void onWeaponActivated()
	{
		if (this.getPlatform() != null)
		{
			if (this.target instanceof EntityLiving && this.getPlatform().useAmmunition(ICBMSentry.conventionalBullet))
			{
				((EntityLiving) this.target).attackEntityFrom(DamageSource.generic, 2);
				Vector3 look = lookHelper.getDeltaPositionFromRotation(this.rotationYaw, this.rotationPitch);
				look.multiply(-3);
				((EntityLiving) this.target).knockBack(null, 0, look.intX(), look.intZ());
				this.getPlatform().wattsReceived -= this.getRequest();

				if (!this.worldObj.isRemote && this.worldObj.rand.nextFloat() > 0.1)
				{
					Vector3 spawnPos = this.getMuzzle();
					EntityItem entityShell = new EntityItem(this.worldObj, spawnPos.x, spawnPos.y, spawnPos.z, ICBMSentry.bulletShell.copy());
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
		return super.isRunning() && !this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public boolean canActivateWeapon()
	{
		return super.canActivateWeapon() && !this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public double getDetectRange()
	{
		return 20;
	}

	@Override
	public String getName()
	{
		return TranslationHelper.getLocal("tile.turret.0.name");
	}

	@Override
	public double getVoltage(Object... data)
	{
		return 120;
	}

	@Override
	public int getCooldown()
	{
		return 5;
	}

	@Override
	public double getRequest()
	{
		return 10;
	}

}
