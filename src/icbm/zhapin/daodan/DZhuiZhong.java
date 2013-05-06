package icbm.zhapin.daodan;

import icbm.api.ITracker;
import icbm.zhapin.daodan.EDaoDan.XingShi;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import universalelectricity.core.vector.Vector2;
import universalelectricity.core.vector.Vector3;

public class DZhuiZhong extends DaoDan
{
	protected DZhuiZhong(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	@Override
	public void launch(EDaoDan missileObj)
	{
		if (!missileObj.worldObj.isRemote)
		{
			WorldServer worldServer = (WorldServer) missileObj.worldObj;
			Entity trackingEntity = worldServer.getEntityByID(missileObj.genZongE);

			if (trackingEntity != null)
			{
				if (trackingEntity == missileObj)
				{
					missileObj.setExplode();
				}

				missileObj.muBiao = new Vector3(trackingEntity);
			}
		}
	}

	@Override
	public void update(EDaoDan missileObj)
	{
		if (missileObj.feiXingTick > missileObj.feiXingShiJian / 2 && missileObj.xingShi == XingShi.DAO_DAN)
		{
			WorldServer worldServer = (WorldServer) missileObj.worldObj;
			Entity trackingEntity = worldServer.getEntityByID(missileObj.genZongE);

			if (trackingEntity != null)
			{
				if (trackingEntity == missileObj)
				{
					missileObj.setExplode();
				}

				missileObj.muBiao = new Vector3(trackingEntity);

				missileObj.xingShi = XingShi.XIAO_DAN;

				missileObj.xXiangCha = missileObj.muBiao.x - missileObj.posX;
				missileObj.yXiangCha = missileObj.muBiao.y - missileObj.posY;
				missileObj.zXiangCha = missileObj.muBiao.z - missileObj.posZ;

				missileObj.diShangJuLi = Vector2.distance(missileObj.kaiShi.toVector2(), missileObj.muBiao.toVector2());
				missileObj.tianGao = 150 + (int) (missileObj.diShangJuLi * 1.8);
				missileObj.feiXingShiJian = (float) Math.max(100, 2.4 * missileObj.diShangJuLi);
				missileObj.jiaSu = (float) missileObj.tianGao * 2 / (missileObj.feiXingShiJian * missileObj.feiXingShiJian);

				if (missileObj.xiaoDanMotion.equals(new Vector3()) || missileObj.xiaoDanMotion == null)
				{
					float suDu = 0.3f;
					missileObj.xiaoDanMotion = new Vector3();
					missileObj.xiaoDanMotion.x = missileObj.xXiangCha / (missileObj.feiXingShiJian * suDu);
					missileObj.xiaoDanMotion.y = missileObj.yXiangCha / (missileObj.feiXingShiJian * suDu);
					missileObj.xiaoDanMotion.z = missileObj.zXiangCha / (missileObj.feiXingShiJian * suDu);
				}
			}
		}
	}

	@Override
	public boolean onInteract(EDaoDan missileObj, EntityPlayer entityPlayer)
	{
		if (!missileObj.worldObj.isRemote && missileObj.feiXingTick <= 0)
		{
			if (entityPlayer.getCurrentEquippedItem() != null)
			{
				if (entityPlayer.getCurrentEquippedItem().getItem() instanceof ITracker)
				{
					Entity trackingEntity = ((ITracker) entityPlayer.getCurrentEquippedItem().getItem()).getTrackingEntity(missileObj.worldObj, entityPlayer.getCurrentEquippedItem());

					if (trackingEntity != null)
					{
						if (missileObj.genZongE != trackingEntity.entityId)
						{
							missileObj.genZongE = trackingEntity.entityId;
							entityPlayer.addChatMessage("Missile target locked to: " + trackingEntity.getEntityName());

							if (missileObj.getLauncher() != null && missileObj.getLauncher().getController() != null)
							{
								Vector3 newTarget = new Vector3(trackingEntity);
								newTarget.y = 0;
								missileObj.getLauncher().getController().setTarget(newTarget);
							}

							return true;
						}
					}
				}
			}
		}

		return false;
	}

	@Override
	public void onExplode(EDaoDan missileObj)
	{
		missileObj.worldObj.createExplosion(missileObj, missileObj.posX, missileObj.posY, missileObj.posZ, 6F, true);
	}

	@Override
	public boolean isCruise()
	{
		return false;
	}
}
