package icbm.gangshao.turret.sentries;

import icbm.gangshao.actions.LookHelper;
import universalelectricity.core.vector.Vector3;
import net.minecraft.util.AxisAlignedBB;

/**
 * AA Turret, shoots down missiles and planes.
 * 
 * @author DarkGaurdsman
 * 
 */
public class TileEntityAATurret extends TileEntityAutoTurret
{
	int gunBarrel = 0;
	@Override
	public void initiate()
	{
		super.initiate();
		this.targetMissiles = true;
		this.targetCrafts = true;
		// TODO re-add ground target firing at a reduced efficiency
		// this.targetPlayers = false;
		// this.targetLiving = false;
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
		return new Vector3(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5);
	}

	@Override
	public void onWeaponActivated()
	{
		if (this.onFire())
		{
			this.gunBarrel++;
			this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "icbm.aagun", 5F, 1F);
			//this.sendShotToClient(new Vector3(this.target));
			
			if(this.gunBarrel>= 3)
			{
				this.gunBarrel = 0;
			}
		}

	}

	@Override
	public float getRotationSpeed()
	{
		return 8f;
	}

}
