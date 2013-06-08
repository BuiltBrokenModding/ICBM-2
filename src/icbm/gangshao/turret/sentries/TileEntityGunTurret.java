package icbm.gangshao.turret.sentries;

import universalelectricity.core.vector.Vector3;

public class TileEntityGunTurret extends TileEntityAutoTurret
{
	public TileEntityGunTurret()
	{
		this.targetPlayers = true;
		this.targetHostile = true;
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
		int baseRange = 20;

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
	public int getFiringDelay()
	{
		return 10;
	}

	@Override
	public double getFiringRequest()
	{
		return 10;
	}

	@Override
	public void onWeaponActivated()
	{
		if (this.onFire())
		{
			this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "icbm.machinegun", 5F, 1F);
			this.sendShotToClient(new Vector3(this.target).add(new Vector3(0, this.target.getEyeHeight(), 0)));
		}
	}

	@Override
	public float getRotationSpeed()
	{
		if (this.target != null || this.worldObj.isRemote)
		{
			return 4f;
		}
		return 2f;
	}

	@Override
	public int getMaxHealth()
	{
		return 120;
	}

	@Override
	public double getSafeHeatLvL()
	{		
		return 500;
	}

	@Override
	public double getRunningRequest()
	{		
		return 5;
	}

}
