package icbm.zhapin.po;

import icbm.zhapin.ZhuYaoZhaPin;
import icbm.zhapin.zhapin.ZhaPin;
import icbm.zhapin.zhapin.ZhaPin.ZhaPinType;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.potion.CustomPotion;

public class PChuanRanDu extends CustomPotion
{
	public static final PChuanRanDu INSTANCE = new PChuanRanDu(22, false, 5149489, "Virus");

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
		if (ZhuYaoZhaPin.shiBaoHu(entityLiving.worldObj, new Vector3(entityLiving), ZhaPinType.QUAN_BU, ZhaPin.duQi))
		{
			int r = 13;
			AxisAlignedBB entitySurroundings = AxisAlignedBB.getBoundingBox(entityLiving.posX - r, entityLiving.posY - r, entityLiving.posZ - r, entityLiving.posX + r, entityLiving.posY + r, entityLiving.posZ + r);
			EntityLiving nearestEntity = (EntityLiving) entityLiving.worldObj.findNearestEntityWithinAABB(EntityLiving.class, entitySurroundings, entityLiving);

			if (nearestEntity != null)
			{
				if (nearestEntity instanceof EntityPig)
				{
					EntityPigZombie var2 = new EntityPigZombie(nearestEntity.worldObj);
					var2.setLocationAndAngles(nearestEntity.posX, nearestEntity.posY, nearestEntity.posZ, nearestEntity.rotationYaw, nearestEntity.rotationPitch);
					nearestEntity.worldObj.spawnEntityInWorld(var2);
					nearestEntity.setDead();
				}
				else if (nearestEntity instanceof EntityVillager)
				{
					EntityZombie var2 = new EntityZombie(nearestEntity.worldObj);
					var2.setLocationAndAngles(nearestEntity.posX, nearestEntity.posY, nearestEntity.posZ, nearestEntity.rotationYaw, nearestEntity.rotationPitch);
					nearestEntity.worldObj.spawnEntityInWorld(var2);
					nearestEntity.setDead();
				}

				ZhuYaoZhaPin.DU_CHUAN_RAN.poisonEntity(new Vector3(entityLiving), nearestEntity);
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
