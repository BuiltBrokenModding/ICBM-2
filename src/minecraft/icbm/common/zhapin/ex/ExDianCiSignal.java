package icbm.common.zhapin.ex;

import icbm.api.IMissile;
import icbm.api.RadarRegistry;
import icbm.common.zhapin.ZhaPin;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class ExDianCiSignal extends ZhaPin
{

	public ExDianCiSignal(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	/**
	 * World worldObj, Vector3 position, int amount, boolean isExplosive
	 */
	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int radius, int callCount)
	{
		// Drop all missiles
		List<Entity> entitiesNearby = RadarRegistry.getEntitiesWithinRadius(position.toVector2(), radius);

		for (Entity entity : entitiesNearby)
		{
			if (entity instanceof IMissile)
			{
				if (((IMissile) entity).getTicksInAir() > -1)
				{
					((IMissile) entity).dropMissileAsItem();
				}
			}
		}

		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.emp", 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
		return false;
	}

	@Override
	public float getRadius()
	{
		return 50;
	}

	@Override
	public double getEnergy()
	{
		return 0;
	}
}
