package icbm.sentry.turret.sentries;

import icbm.core.ZhuYaoICBM;
import icbm.sentry.ICBMSentry;
import net.minecraft.util.AxisAlignedBB;
import universalelectricity.core.vector.Vector3;

/**
 * AA Turret, shoots down missiles and planes.
 * 
 * @author DarkGaurdsman
 */
public class TileEntityAAGun extends TileEntityAutoTurret
{
	public TileEntityAAGun()
	{
		this.targetAir = true;
		this.canTargetAir = true;

		this.baseTargetRange = 80;
		this.maxTargetRange = 120;

		this.rotationSpeed = 9;

		this.minFiringDelay = 8;
		this.baseFiringDelay = 15;

		this.minPitch = 40;
		this.maxPitch = 90;

		this.allowFreePitch = true;
	}

	@Override
	public AxisAlignedBB getTargetingBox()
	{
		return AxisAlignedBB.getBoundingBox(xCoord - this.getDetectRange(), yCoord - this.getDetectRange(), zCoord - this.getDetectRange(), xCoord + this.getDetectRange(), yCoord + this.getDetectRange(), zCoord + this.getDetectRange());
	}

	@Override
	public float getVoltage()
	{
		return 240;
	}

	@Override
	public float getFiringRequest()
	{
		return 50;
	}

	@Override
	public int getMaxHealth()
	{
		return 180;
	}

	@Override
	public void playFiringSound()
	{
		this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, ZhuYaoICBM.PREFIX + "aagun", 5F, 1F);
	}

	@Override
	public void renderShot(Vector3 target)
	{
		Vector3 center = this.getCenter();
		ICBMSentry.proxy.renderBeam(this.worldObj, Vector3.add(center, Vector3.getDeltaPositionFromRotation(this.currentRotationYaw - 25, this.currentRotationPitch * 1.4f).scale(1.15)), target, 1, 1, 1, 5);
		ICBMSentry.proxy.renderBeam(this.worldObj, Vector3.add(center, Vector3.getDeltaPositionFromRotation(this.currentRotationYaw + 25, this.currentRotationPitch * 1.4f).scale(1.15)), target, 1, 1, 1, 5);
	}

	@Override
	public Vector3 getCenter()
	{
		return new Vector3(this).add(new Vector3(0.5, 0.75, 0.5));
	}

	@Override
	public Vector3 getMuzzle()
	{
		return new Vector3(this).add(new Vector3(0.5, 0.75, 0.5)).add(Vector3.getDeltaPositionFromRotation(this.currentRotationYaw, this.currentRotationPitch).scale(1));
	}

}
