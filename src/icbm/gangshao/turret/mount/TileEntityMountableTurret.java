package icbm.gangshao.turret.mount;

import net.minecraft.potion.PotionEffect;
import icbm.gangshao.turret.TileEntityTurretBase;

public abstract class TileEntityMountableTurret extends TileEntityTurretBase
{
	@Override
	public boolean canApplyPotion(PotionEffect par1PotionEffect)
	{
		return false;
	}

}
