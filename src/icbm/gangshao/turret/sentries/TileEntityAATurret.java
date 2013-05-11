package icbm.gangshao.turret.sentries;

import icbm.gangshao.actions.LookHelper;
import net.minecraft.util.AxisAlignedBB;
import universalelectricity.core.vector.Vector3;

/**
 * AA Turret, shoots down missiles and planes.
 * 
 * @author DarkGaurdsman
 * 
 */
public class TileEntityAATurret extends TileEntityAutoTurret
{

	public TileEntityAATurret()
	{
		this.targetMissiles = true;
		this.targetCrafts = true;
		// TODO re-add ground target firing at a reduced efficiency
		this.targetPlayers = false;
		this.targetLiving = false;
	}

	@Override
	public boolean isRunning()
	{
		return super.isRunning() && this.getPlatform().wattsReceived > 0 && !this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public boolean canActivateWeapon()
	{
		return super.canActivateWeapon() && !this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public double getDetectRange()
	{
		int baseRange = 70;

		if (this.getPlatform() != null)
		{
			return baseRange + 10 * this.getPlatform().getUpgrades("Capacity");
		}

		return baseRange;
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
	public int getCooldown()
	{
		return 2;
	}

	@Override
	public double getRequest()
	{
		return 30;
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
	public void onWeaponActivated()
	{
		if (this.onFire())
		{
			this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "icbm.aagun", 5F, 1F);
			this.sendShotToClient(new Vector3(this.target).add(new Vector3(0, this.target.getEyeHeight(), 0)));
		}

	}

	@Override
	public float getRotationSpeed()
	{
		if (this.speedUpRotation)
		{
			return 10f;
		}
		return 4f;
	}

}
