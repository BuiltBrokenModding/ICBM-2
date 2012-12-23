package icbm.common.zhapin.ex;

import icbm.common.zhapin.ZhaPin;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class ExBianZhong extends ZhaPin
{
	public ExBianZhong(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int radius, int callCount)
	{
		if (!worldObj.isRemote)
		{
			AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x - radius, position.y - radius, position.z - radius, position.x + radius, position.y + radius, position.z + radius);
			List<EntityLiving> entitiesNearby = worldObj.getEntitiesWithinAABB(EntityLiving.class, bounds);

			for (EntityLiving entity : entitiesNearby)
			{
				if (entity instanceof EntityPig)
				{
					EntityPigZombie newEntity = new EntityPigZombie(worldObj);
					newEntity.preventEntitySpawning = true;
					newEntity.setPosition(entity.posX, entity.posY, entity.posZ);
					entity.setDead();
				}
				else if (entity instanceof EntityVillager)
				{
					EntityZombie newEntity = new EntityZombie(worldObj);
					newEntity.preventEntitySpawning = true;
					newEntity.setPosition(entity.posX, entity.posY, entity.posZ);
					entity.setDead();
				}
			}
		}

		return false;
	}

	@Override
	public float getRadius()
	{
		return 0;
	}

	@Override
	public double getEnergy()
	{
		return 0;
	}
}
