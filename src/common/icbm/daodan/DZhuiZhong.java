package icbm.daodan;

import icbm.dianqi.ItGenZongQi;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.WorldServer;
import universalelectricity.core.vector.Vector2;
import universalelectricity.core.vector.Vector3;

public class DZhuiZhong extends DaoDan
{
	protected DZhuiZhong(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	@Override
	public void onTickFlight(EDaoDan missileObj)
	{
		if (missileObj.feiXingTick > missileObj.feiXingShiJian / 2 && !missileObj.isCruise)
		{
			WorldServer worldServer = (WorldServer) missileObj.worldObj;
			Entity trackingEntity = worldServer.getEntityByID(missileObj.genZongE);

			if (trackingEntity != null)
			{
				if (trackingEntity == missileObj)
					missileObj.setExplode();

				missileObj.muBiao = Vector3.get(trackingEntity);

				missileObj.isCruise = true;

				missileObj.xXiangCha = missileObj.muBiao.x - missileObj.posX;
				missileObj.yXiangCha = missileObj.muBiao.y - missileObj.posY;
				missileObj.zXiangCha = missileObj.muBiao.z - missileObj.posZ;

				missileObj.diShangJuLi = Vector2.distance(missileObj.kaoShi.toVector2(), missileObj.muBiao.toVector2());
				missileObj.tianGao = 150 + (int) (missileObj.diShangJuLi * 1.8);
				missileObj.feiXingShiJian = (float) Math.max(100, 2.4 * missileObj.diShangJuLi);
				missileObj.jiaSu = (float) missileObj.tianGao * 2 / (missileObj.feiXingShiJian * missileObj.feiXingShiJian);

				missileObj.motionX = missileObj.xXiangCha / (missileObj.feiXingShiJian * 0.4);
				missileObj.motionY = missileObj.yXiangCha / (missileObj.feiXingShiJian * 0.4);
				missileObj.motionZ = missileObj.zXiangCha / (missileObj.feiXingShiJian * 0.4);
			}
		}
	}

	@Override
	public boolean onInteract(EDaoDan missileObj, EntityPlayer par1EntityPlayer)
	{
		if (missileObj.feiXingTick <= 0 && !missileObj.worldObj.isRemote)
		{
			if (par1EntityPlayer.getCurrentEquippedItem() != null)
			{
				if (par1EntityPlayer.getCurrentEquippedItem().getItem() instanceof ItGenZongQi)
				{
					Entity trackingEntity = ItGenZongQi.getTrackingEntity(missileObj.worldObj, par1EntityPlayer.getCurrentEquippedItem());

					if (trackingEntity != null)
					{
						if (missileObj.genZongE != trackingEntity.entityId)
						{
							missileObj.genZongE = trackingEntity.entityId;
							par1EntityPlayer.addChatMessage("Missile target locked to: " + trackingEntity.getEntityName());
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
