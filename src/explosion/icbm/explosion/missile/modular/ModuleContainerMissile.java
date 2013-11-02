package icbm.explosion.missile.modular;

import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class ModuleContainerMissile
{
	/** Missile warhead */
	protected ModuleMissileWarhead warhead;
	/** Missile casing */
	protected ModuleMissileCasing casing;
	/** Missile engine */
	protected ModuleMissileEngine engine;

	public ModuleContainerMissile(ModuleMissileWarhead warhead, ModuleMissileCasing casing, ModuleMissileEngine engine)
	{
		this.warhead = warhead;
		this.casing = casing;
		this.engine = engine;
	}

	/** Checks if the current modular setup is valid */
	public boolean isValidMissile()
	{
		if (warhead != null && casing != null && engine != null)
		{
			// Casing is the base setup of the missile so all parts must be same or lower tier than
			// it
			if (casing.getTier() >= warhead.getTier() && casing.getTier() >= engine.getTier())
			{
				return true;
			}
		}
		return false;
	}

	public static void createMissile(World world, ModuleContainerMissile design, Vector3 spawnLocation, float yaw, float pitch)
	{
		if (design.isValidMissile())
		{
			EntityModularMissile missile = new EntityModularMissile(world, spawnLocation, pitch, pitch);
		}
	}
}
