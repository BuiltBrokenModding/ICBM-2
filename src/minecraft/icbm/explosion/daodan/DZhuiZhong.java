package icbm.explosion.daodan;

import icbm.explosion.daodan.EDaoDan.XingShi;
import icbm.explosion.dianqi.ItGenZongQi;
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
	public void onTickFlight(EDaoDan missileObj)
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

				if (missileObj.xiaoDanMotion.isEqual(new Vector3()) || missileObj.xiaoDanMotion == null)
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
