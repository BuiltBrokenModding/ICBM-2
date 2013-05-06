package icbm.gangshao.turret.sentries;

import net.minecraft.util.AxisAlignedBB;

/**
 * AA Turret, shoots down missiles and planes.
 * 
 * @author DarkGaurdsman
 * 
 */
public class TileEntityAATurret extends TileEntityAutoTurret
{
	@Override
	public void initiate()
	{
		super.initiate();
		this.targetMissiles = true;
		this.targetCrafts = true;
		//TODO re-add ground target firing at a reduced efficiency 
		//this.targetPlayers = false;
		//this.targetLiving = false;
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
	public void onWeaponActivated()
	{
		if (this.onFire())
		{
			this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "icbm.aagun", 5F, 1F);
		}

	}

	@Override
	public float getRotationSpeed()
	{
		return 8f;
	}

}
