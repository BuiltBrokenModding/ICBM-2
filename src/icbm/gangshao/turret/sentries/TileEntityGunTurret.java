package icbm.gangshao.turret.sentries;

public class TileEntityGunTurret extends TileEntityAutoTurret
{

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
		int baseRange = 15;

		if (this.getPlatform() != null)
		{
			return baseRange + 10 * this.getPlatform().getUpgrades("Capacity");
		}

		return baseRange;
	}

	@Override
	public double getVoltage()
	{
		return 120;
	}

	@Override
	public int getCooldown()
	{
		return 10;
	}

	@Override
	public double getRequest()
	{
		return 10;
	}

	@Override
	public void onWeaponActivated()
	{
		if (this.onFire())
		{
			this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "icbm.machinegun", 5F, 1F);
		}
	}

}
