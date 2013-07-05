package icbm.gangshao.turret.sentries;

import icbm.gangshao.ZhuYaoGangShao;
import icbm.gangshao.task.LookHelper;
import net.minecraft.util.AxisAlignedBB;
import universalelectricity.core.vector.Vector3;

/**
 * AA Turret, shoots down missiles and planes.
 * 
 * @author DarkGaurdsman
 */
public class TFanKong extends TPaoTaiZiDong
{
	public TFanKong()
	{
		this.targetAir = true;
		this.canTargetAir = true;

		this.baseTargetRange = 70;
		this.maxTargetRange = 200;

		this.rotationSpeed = 5;

		this.minFiringDelay = 2;
		this.baseFiringDelay = 7;
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
		return 8000;
	}

	@Override
	public int getMaxHealth()
	{
		return 180;
	}

	@Override
	public void playFiringSound()
	{
		this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "icbm.aagun", 5F, 1F);
	}

	@Override
	public void renderShot(Vector3 target)
	{
		Vector3 center = new Vector3(this).add(new Vector3(0.5, 0.75, 0.5));
		ZhuYaoGangShao.proxy.renderBeam(this.worldObj, Vector3.add(center, LookHelper.getDeltaPositionFromRotation(this.currentRotationYaw - 25, this.currentRotationPitch * 1.4f).multiply(1.15)), target, 1, 1, 1, 5);
		ZhuYaoGangShao.proxy.renderBeam(this.worldObj, Vector3.add(center, LookHelper.getDeltaPositionFromRotation(this.currentRotationYaw + 25, this.currentRotationPitch * 1.4f).multiply(1.15)), target, 1, 1, 1, 5);
	}

	@Override
	public Vector3 getMuzzle()
	{
		return new Vector3(this).add(new Vector3(0.5, 0.75, 0.5)).add(LookHelper.getDeltaPositionFromRotation(this.currentRotationYaw, this.currentRotationPitch).multiply(1));
	}

}
