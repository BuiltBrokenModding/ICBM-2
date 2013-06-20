package icbm.gangshao.turret.sentries;

import icbm.gangshao.actions.LookHelper;
import net.minecraft.util.AxisAlignedBB;
import universalelectricity.core.vector.Vector3;

/** AA Turret, shoots down missiles and planes.
 * 
 * @author DarkGaurdsman */
public class TileEntityAATurret extends TileEntityAutoTurret
{
	public TileEntityAATurret()
	{
		this.targetAir = true;

		this.baseTargetRange = 60;
		this.maxTargetRange = 200;

		this.idleRtSpeed = 4f;
		this.targetRtSpeed = 10f;

		this.maxHeat = 600;

		this.minFiringDelay = 1;
		this.baseFiringDelay = 3;
	}

	@Override
	public AxisAlignedBB getTargetingBox()
	{
		return AxisAlignedBB.getBoundingBox(xCoord - this.getDetectRange(), yCoord - this.getDetectRange(), zCoord - this.getDetectRange(), xCoord + this.getDetectRange(), yCoord + this.getDetectRange(), zCoord + this.getDetectRange());
	}

	@Override
	public double getVoltage()
	{
		return 240;
	}

	@Override
	public double getFiringRequest()
	{
		return 2;
	}

	@Override
	public Vector3 getMuzzle()
	{
		double x = 0.5;
		double y = 0.5;
		double z = 0.5;
		if (this.worldObj.isRemote)
		{
			if (this.gunBarrel == 1 || this.gunBarrel == 2)
			{
				y += 0.2;
			}
			if (this.gunBarrel == 1 || this.gunBarrel == 3)
			{
				z = -0.2;
			}
			else
			{
				z = 1.2;
			}
		}
		Vector3 position = new Vector3(this.xCoord + x, this.yCoord + y, this.zCoord + z);
		return Vector3.add(position, Vector3.multiply(LookHelper.getDeltaPositionFromRotation(this.currentRotationYaw, this.currentRotationPitch - 10), 2));
	}

	@Override
	public double getRunningRequest()
	{
		return 25;
	}

	@Override
	public int getMaxHealth()
	{
		return 150;
	}

	@Override
	public double getHeatPerShot()
	{
		return 15;
	}

	@Override
	public void playFiringSound()
	{
		this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "icbm.aagun", 5F, 1F);

	}

}
