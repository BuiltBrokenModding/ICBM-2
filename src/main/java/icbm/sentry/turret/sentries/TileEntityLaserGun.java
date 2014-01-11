package icbm.sentry.turret.sentries;

import icbm.Reference;
import icbm.sentry.ICBMSentry;
import icbm.sentry.ProjectileType;
import net.minecraft.util.MathHelper;
import universalelectricity.api.vector.Vector3;

public class TileEntityLaserGun extends TileAutoTurret
{
	/** Laser turret spins its barrels every shot. */
	public float barrelRotation;
	public float barrelRotationVelocity;

	public TileEntityLaserGun()
	{

		this.baseTargetRange = 20;
		this.maxTargetRange = 35;

		this.rotationSpeed = 3;

		this.baseFiringDelay = 13;
		this.minFiringDelay = 5;

		this.projectileType = ProjectileType.UNKNOWN;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (this.worldObj.isRemote)
		{
			this.barrelRotation = MathHelper.wrapAngleTo180_float(this.barrelRotation + this.barrelRotationVelocity);
			this.barrelRotationVelocity = Math.max(this.barrelRotationVelocity - 0.1f, 0);
		}
	}

	@Override
	public int getMaxHealth()
	{
		return 130;
	}

	public void playFiringSound()
	{
		this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, Reference.PREFIX + "lasershot", 5F, 1F - (this.worldObj.rand.nextFloat() * 0.2f));
	}

	public void renderShot(Vector3 target)
	{
		Vector3 center = this.pos();
		ICBMSentry.proxy.renderBeam(this.worldObj, Vector3.translate(center, Vector3.getDeltaPositionFromRotation(this.getYawServo().getRotation() - 6, this.getPitchServo().getRotation() * 1.4f).scale(1.2)), target, 1, 0.4f, 0.4f, 5);
		ICBMSentry.proxy.renderBeam(this.worldObj, Vector3.translate(center, Vector3.getDeltaPositionFromRotation(this.getYawServo().getRotation() + 6, this.getPitchServo().getRotation() * 1.4f).scale(1.2)), target, 1, 0.4f, 0.4f, 5);
		this.barrelRotationVelocity += 1;
	}
}
