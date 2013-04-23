package mffs.api.modules;

import mffs.api.security.IInterdictionMatrix;
import net.minecraft.entity.EntityLiving;

public interface IInterdictionMatrixModule extends IModule
{
	/**
	 * Called when the Interdiction Matrix attempts to defend a region.
	 * 
	 * @return True if to stop processing other modules in this list.
	 */
	public boolean onDefend(IInterdictionMatrix defenseStation, EntityLiving entityLiving);
}
