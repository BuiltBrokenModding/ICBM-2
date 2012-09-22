package icbm.zhapin;

import icbm.daodan.EDaoDan;

import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Entity;
import net.minecraft.src.World;
import universalelectricity.prefab.Vector3;

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
	public boolean doExplosion(World worldObj, Vector3 position, Entity explosionSource, int radius, int callCount)
	{
		//Drop all missiles
        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x - radius, 0, position.z - radius, position.x + radius, worldObj.getHeight(), position.z + radius);
        List<EDaoDan> entitiesNearby = worldObj.getEntitiesWithinAABB(EDaoDan.class, bounds);

        for (EDaoDan missile : entitiesNearby)
        {
        	if(missile.ticksInAir > -1)
        	{
        		missile.dropMissileAsItem();
        	}
        }
    	
		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.emp", 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
		return false;
	}

}
