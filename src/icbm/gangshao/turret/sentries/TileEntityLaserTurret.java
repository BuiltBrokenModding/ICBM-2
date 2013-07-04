package icbm.gangshao.turret.sentries;

public class TileEntityLaserTurret extends TPaoTaiZiDong
{
	public TileEntityLaserTurret()
	{
		this.targetPlayers = true;
		this.targetHostile = true;

		this.baseTargetRange = 30;
		this.maxTargetRange = 90;

		this.rotationSpeed = 3f;

		this.baseFiringDelay = 20;
		this.minFiringDelay = 10;
	}

	@Override
	public double getFiringRequest()
	{
		return 1000;
	}

	@Override
	public double getVoltage()
	{
		return 480;
	}

	@Override
	public int getMaxHealth()
	{
		return 120;
	}

	@Override
	public void playFiringSound()
	{
		this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "icbm.lasershot", 5F, 1F - (this.worldObj.rand.nextFloat() * 0.2f));
	}

}
