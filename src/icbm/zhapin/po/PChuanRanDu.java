package icbm.zhapin.po;

import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.zhapin.ZhaPin;
import icbm.zhapin.zhapin.ZhaPinType;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import universalelectricity.core.vector.Vector3;

public class PChuanRanDu extends PICBM
{
	public static PChuanRanDu INSTANCE;

	public PChuanRanDu(int id, boolean isBadEffect, int color, String name)
	{
		super(id, isBadEffect, color, name);
		this.setIconIndex(6, 0);
	}

	@Override
	public void performEffect(EntityLiving entityLiving, int amplifier)
	{
		if (!(entityLiving instanceof EntityZombie) && !(entityLiving instanceof EntityPigZombie))
		{
			entityLiving.attackEntityFrom(DamageSource.magic, 1);
		}

		// Poison things around it
		if (!ZhuYaoZhaPin.shiBaoHu(entityLiving.worldObj, new Vector3(entityLiving), ZhaPinType.QUAN_BU, ZhaPin.duQi))
		{
			int r = 13;
			AxisAlignedBB entitySurroundings = AxisAlignedBB.getBoundingBox(entityLiving.posX - r, entityLiving.posY - r, entityLiving.posZ - r, entityLiving.posX + r, entityLiving.posY + r, entityLiving.posZ + r);
			List<EntityLiving> entities = entityLiving.worldObj.getEntitiesWithinAABB(EntityLiving.class, entitySurroundings);

			for (EntityLiving entity : entities)
			{
				if (entity != null)
				{
					if (entity instanceof EntityPig)
					{
						EntityPigZombie var2 = new EntityPigZombie(entity.worldObj);
						var2.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
						entity.worldObj.spawnEntityInWorld(var2);
						entity.setDead();
					}
					else if (entity instanceof EntityVillager)
					{
						EntityZombie var2 = new EntityZombie(entity.worldObj);
						var2.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
						entity.worldObj.spawnEntityInWorld(var2);
						entity.setDead();
					}

					ZhuYaoZhaPin.DU_CHUAN_RAN.poisonEntity(new Vector3(entityLiving), entity);
				}
			}
		}
	}

	@Override
	public boolean isReady(int duration, int amplifier)
	{
		if (duration % (20 * 2) == 0)
		{
			return true;
		}

		return false;
	}
}
