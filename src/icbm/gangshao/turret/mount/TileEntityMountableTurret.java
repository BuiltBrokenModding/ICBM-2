package icbm.gangshao.turret.mount;

import icbm.gangshao.turret.TileEntityTurretBase;
import net.minecraft.potion.PotionEffect;

public abstract class TileEntityMountableTurret extends TileEntityTurretBase
{
	@Override
	public boolean canApplyPotion(PotionEffect par1PotionEffect)
	{
		return false;
	}

}
