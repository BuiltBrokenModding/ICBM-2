package mffs.api.modules;

import mffs.api.IDefenseStation;
import net.minecraft.entity.EntityLiving;

public interface IDefenseStationModule extends IModule
{
	/**
	 * Called when the defense station attempts to defend a region.
	 * 
	 * @param defenseStation
	 * @param entityLiving
	 * @return True if to stop processing other modules in this list.
	 */
	public boolean onDefend(IDefenseStation defenseStation, EntityLiving entityLiving);
}
