package icbm.gangshao.turret.sentries;

import icbm.gangshao.task.LookHelper;
import net.minecraft.util.AxisAlignedBB;
import universalelectricity.core.vector.Vector3;

/**
 * AA Turret, shoots down missiles and planes.
 * 
 * @author DarkGaurdsman
 */
public class TileEntityAATurret extends TPaoTaiZiDong
{
	public TileEntityAATurret()
	{
		this.targetAir = true;

		this.baseTargetRange = 60;
		this.maxTargetRange = 200;

		this.rotationSpeed = 4f;

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
	public int getMaxHealth()
	{
		return 150;
	}

	@Override
	public void playFiringSound()
	{
		this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "icbm.aagun", 5F, 1F);

	}

}
