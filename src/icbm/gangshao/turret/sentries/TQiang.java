package icbm.gangshao.turret.sentries;

import icbm.gangshao.task.LookHelper;
import universalelectricity.core.vector.Vector3;

public class TQiang extends TPaoTaiZiDong
{
	public TQiang()
	{
		this.targetPlayers = true;
		this.targetHostile = true;

		this.baseTargetRange = 30;
		this.maxTargetRange = 100;

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
		return new Vector3(this).add(new Vector3(0.5, 0.65, 0.5)).add(Vector3.multiply(LookHelper.getDeltaPositionFromRotation(this.currentRotationYaw, this.currentRotationPitch), 1));
	}

}
