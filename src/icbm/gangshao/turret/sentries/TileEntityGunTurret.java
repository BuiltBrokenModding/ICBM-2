package icbm.gangshao.turret.sentries;

import icbm.api.sentry.AmmoPair;
import icbm.api.sentry.IAmmo;
import icbm.api.sentry.ProjectileTypes;
import icbm.gangshao.ZhuYaoGangShao;
import icbm.gangshao.actions.LookHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import universalelectricity.core.vector.Vector3;

public class TileEntityGunTurret extends TileEntityAutoTurret
{

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
		int baseRange = 15;

		if (this.getPlatform() != null)
		{
			return baseRange + 10 * this.getPlatform().getUpgrades("Capacity");
		}

		return baseRange;
	}

	@Override
	public double getVoltage()
	{
		return 120;
	}

	@Override
	public int getCooldown()
	{
		return 5;
	}

	@Override
	public double getRequest()
	{
		return 10;
	}

}
