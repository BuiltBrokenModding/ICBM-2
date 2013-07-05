package icbm.gangshao.turret.sentries;

import icbm.api.sentry.IAATarget;
import icbm.gangshao.ProjectileType;
import icbm.gangshao.ZhuYaoGangShao;
import icbm.gangshao.task.LookHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.DamageSource;
import universalelectricity.core.vector.Vector3;

public class TLeiShe extends TPaoTaiZiDong
{
	public TLeiShe()
	{
		this.targetPlayers = true;
		this.targetHostile = true;

		this.baseTargetRange = 20;
		this.maxTargetRange = 80;

		this.rotationSpeed = 3f;

		this.baseFiringDelay = 10;
		this.minFiringDelay = 5;

		this.baseAmmoType = ProjectileType.UNKNOWN;
	}

	@Override
	public double getFiringRequest()
	{
		return 20000;
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

	@Override
	public void renderShot(Vector3 target)
	{
		Vector3 center = new Vector3(this).add(new Vector3(0.5, 0.45, 0.5));
		ZhuYaoGangShao.proxy.renderBeam(this.worldObj, Vector3.add(center, LookHelper.getDeltaPositionFromRotation(this.currentRotationYaw - 6, this.currentRotationPitch * 1.4f).multiply(1.2)), target, 1, 0.4f, 0.4f, 5);
		ZhuYaoGangShao.proxy.renderBeam(this.worldObj, Vector3.add(center, LookHelper.getDeltaPositionFromRotation(this.currentRotationYaw + 6, this.currentRotationPitch * 1.4f).multiply(1.2)), target, 1, 0.4f, 0.4f, 5);
	}

	@Override
	protected boolean onFire()
	{
		if (!this.worldObj.isRemote)
		{
			if (this.getPlatform() != null)
			{
				if (this.target instanceof EntityLiving)
				{
					this.getPlatform().wattsReceived -= this.getFiringRequest();
					this.target.attackEntityFrom(DamageSource.inFire, 1);
					this.target.setFire(80);
					return true;
				}
				else if (this.target instanceof IAATarget)
				{
					if (this.worldObj.rand.nextFloat() > 0.2)
					{
						int damage = ((IAATarget) this.target).doDamage(10);

						if (damage == -1 && this.worldObj.rand.nextFloat() > 0.7)
						{
							((IAATarget) this.target).destroyCraft();
						}
						else if (damage <= 0)
						{
							((IAATarget) this.target).destroyCraft();
						}
					}

					return true;
				}
			}
		}

		return false;
	}
}
