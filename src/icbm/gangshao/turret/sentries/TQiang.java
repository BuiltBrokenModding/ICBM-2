package icbm.gangshao.turret.sentries;

import universalelectricity.core.vector.Vector3;
import calclavia.lib.CalculationHelper;

public class TQiang extends TPaoTaiZiDong
{
	public TQiang()
	{
		this.targetPlayers = true;
		this.targetHostile = true;

		this.baseTargetRange = 15;
		this.maxTargetRange = 30;

		this.rotationSpeed = 1.8f;

		this.baseFiringDelay = 20;
		this.minFiringDelay = 10;
	}

	@Override
	public double getVoltage()
	{
		return 240;
	}

	@Override
	public int getMaxHealth()
	{
		return 200;
	}

	@Override
	public double getFiringRequest()
	{
		return 1000;
	}

	@Override
	public void playFiringSound()
	{
		this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "icbm.machinegun", 5F, 1F);
	}

	@Override
	public Vector3 getMuzzle()
	{
		return this.getCenter().add(Vector3.multiply(CalculationHelper.getDeltaPositionFromRotation(this.currentRotationYaw, this.currentRotationPitch), 1));
	}

	public Vector3 getCenter()
	{
		return new Vector3(this).add(new Vector3(0.5, 0.65, 0.5));
	}
}
