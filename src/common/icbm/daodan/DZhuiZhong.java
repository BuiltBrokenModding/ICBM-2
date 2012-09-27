package icbm.daodan;

import net.minecraft.src.Entity;
import net.minecraft.src.WorldServer;
import universalelectricity.prefab.Vector3;

public class DZhuiZhong extends DaoDan
{	
	protected DZhuiZhong(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	@Override
	public void onTickFlight(EDaoDan missileObj)
	{
		WorldServer worldServer = (WorldServer)missileObj.worldObj;
		Entity trackingEntity =  worldServer.getEntityByID(missileObj.tracking);
		
		if(trackingEntity != null)
		{
			missileObj.targetPosition = Vector3.get(trackingEntity);
			
			missileObj.xDifference = missileObj.targetPosition.x - missileObj.posX;
	        missileObj.yDifference = missileObj.targetPosition.y - missileObj.posY;
	        missileObj.zDifference = missileObj.targetPosition.z - missileObj.posZ;
	        		        /*
	        missileObj.flatDistance = Vector2.distance(missileObjstartingPosition.toVector2(),  ((TXiaoFaSheQi)tileEntity).getTarget().toVector2());
	        missileObj.skyLimit = 150+(int)(missileObjflatDistance*1.8);
	        missileObj.flightTime = (float)Math.max(100, 2.4*flatDistance);
	        missileObj.acceleration = (float)skyLimit*2/(flightTime*flightTime);*/
	        
	        missileObj.motionX = missileObj.xDifference/(missileObj.flightTime*0.6);
			missileObj.motionY = missileObj.yDifference/(missileObj.flightTime*0.6);
			missileObj.motionZ = missileObj.zDifference/(missileObj.flightTime*0.6);
		}
	}
	
	@Override
	public void onExplode(EDaoDan missileObj)
	{
		missileObj.worldObj.createExplosion(missileObj, missileObj.posX, missileObj.posY, missileObj.posZ, 6F);
	}
	
	@Override
	public boolean isCruise() { return false; }
}
