package icbm.gangshao.turret.sentries;

public class TileEntityGunTurret extends TPaoTaiZiDong
{
	public TileEntityGunTurret()
	{
		this.targetPlayers = true;
		this.targetHostile = true;

		this.baseTargetRange = 20;
		this.maxTargetRange = 90;

		this.rotationSpeed = 2f;

		this.baseFiringDelay = 10;
		this.minFiringDelay = 5;
	}

	@Override
	public double getVoltage()
	{
		return 240;
	}

	@Override
	public int getMaxHealth()
	{
		return 120;
	}

	@Override
	public double getFiringRequest()
	{
		return 25;
	}

	@Override
	public void playFiringSound()
	{
		this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "icbm.machinegun", 5F, 1F);
	}

}
