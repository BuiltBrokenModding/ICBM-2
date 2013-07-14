package icbm.zhapin.baozha.bz;

import icbm.zhapin.baozha.BaoZha;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BzBianZhong extends BaoZha {
	public BzBianZhong(World world, Entity entity, double x, double y,
			double z, float size) {
		super(world, entity, x, y, z, size);
	}

	@Override
	public void doExplode() {

		if (!this.worldObj.isRemote) {
			AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x
					- this.getRadius(), position.y - this.getRadius(),
					position.z - this.getRadius(),
					position.x + this.getRadius(),
					position.y + this.getRadius(),
					position.z + this.getRadius());
			List<EntityLiving> entitiesNearby = worldObj.getEntitiesWithinAABB(
					EntityLiving.class, bounds);

			for (EntityLiving entity : entitiesNearby) {
				if (entity instanceof EntityPig) {
					EntityPigZombie newEntity = new EntityPigZombie(worldObj);
					newEntity.preventEntitySpawning = true;
					newEntity
							.setPosition(entity.posX, entity.posY, entity.posZ);
					entity.setDead();
				} else if (entity instanceof EntityVillager) {
					EntityZombie newEntity = new EntityZombie(worldObj);
					newEntity.preventEntitySpawning = true;
					newEntity
							.setPosition(entity.posX, entity.posY, entity.posZ);
					entity.setDead();
				}
			}
		}
	}

	@Override
	public float getRadius() {
		return 0;
	}

	@Override
	public float getEnergy() {
		return 50;
	}
}
