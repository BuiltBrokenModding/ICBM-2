package icbm.explosion.zhapin.daodan;

import icbm.core.base.ModelICBM;
import icbm.explosion.explosive.explosion.BzYaSuo;
import icbm.explosion.model.missiles.MMFenZiDan;
import icbm.explosion.zhapin.daodan.EntityMissile.MissileType;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DFenZhiDan extends MissileTeBie
{
	public DFenZhiDan(String mingZi, int tier)
	{
		super(mingZi, tier);
		this.hasBlock = false;
	}

	public static final int MAX_CLUSTER = 12;

	@Override
	public void update(EntityMissile missileObj)
	{
		if (missileObj.motionY < -0.5)
		{
			if (missileObj.daoDanCount < MAX_CLUSTER)
			{
				if (!missileObj.worldObj.isRemote)
				{
					Vector3 position = new Vector3(missileObj);
					EntityMissile clusterMissile = new EntityMissile(missileObj.worldObj, position, new Vector3(missileObj), 0);
					clusterMissile.missileType = MissileType.CruiseMissile;
					clusterMissile.baoHuShiJian = 20;
					clusterMissile.launch(Vector3.add(missileObj.targetVector, new Vector3((missileObj.daoDanCount - MAX_CLUSTER / 2) * Math.random() * 6, (missileObj.daoDanCount - MAX_CLUSTER / 2) * Math.random() * 6, (missileObj.daoDanCount - MAX_CLUSTER / 2) * Math.random() * 6)));
					missileObj.worldObj.spawnEntityInWorld(clusterMissile);
				}
				missileObj.baoHuShiJian = 20;
				missileObj.daoDanCount++;
			}
			else
			{
				missileObj.setDead();
			}
		}
	}

	@Override
	public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
	{
		new BzYaSuo(world, entity, x, y, z, 6).setDestroyItems().explode();
	}

	@Override
	public boolean isCruise()
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ModelICBM getMissileModel()
	{
		return new MMFenZiDan();
	}
}
