package icbm.gangshao.turret.sentries;

import icbm.core.ZhuYaoICBM;
import universalelectricity.core.vector.Vector3;
import calclavia.lib.CalculationHelper;

public class TQiang extends TPaoTaiZiDong
{
	public TQiang()
	{
		this.targetPlayers = true;
		this.targetHostile = true;

		this.baseTargetRange = 13;
		this.maxTargetRange = 25;

		this.rotationSpeed = 2;

		this.baseFiringDelay = 18;
		this.minFiringDelay = 10;
	}

	@Override
	public float getVoltage()
	{
		return 240;
	}

	@Override
	public int getMaxHealth()
	{
		return 200;
	}

	@Override
	public float getFiringRequest()
	{
		return 1000;
	}

	@Override
	public void playFiringSound()
	{
		this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, ZhuYaoICBM.PREFIX + "machinegun", 5F, 1F);
	}

	@Override
	public Vector3 getMuzzle()
	{
		return this.getCenter().add(Vector3.multiply(CalculationHelper.getDeltaPositionFromRotation(this.currentRotationYaw, this.currentRotationPitch), 1));
	}

	@Override
	public Vector3 getCenter()
	{
		return new Vector3(this).add(new Vector3(0.5, 0.65, 0.5));
	}
}
