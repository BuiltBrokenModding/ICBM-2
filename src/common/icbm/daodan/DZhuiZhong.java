package icbm.daodan;

import icbm.dianqi.ItGenZongQi;
import icbm.jiqi.TXiaoFaSheQi;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.WorldServer;
import universalelectricity.prefab.Vector2;
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
		Entity trackingEntity =  worldServer.getEntityByID(missileObj.trackingEntity);
		
		if(trackingEntity != null)
		{
			if(trackingEntity == missileObj) missileObj.setExplode();
			
			missileObj.muBiao = Vector3.get(trackingEntity);
			
			missileObj.xDifference = missileObj.muBiao.x - missileObj.posX;
	        missileObj.yDifference = missileObj.muBiao.y - missileObj.posY;
	        missileObj.zDifference = missileObj.muBiao.z - missileObj.posZ;
	        
	        missileObj.flatDistance = Vector2.distance(missileObj.startingPosition.toVector2(),  missileObj.muBiao.toVector2());
	        missileObj.skyLimit = 150+(int)(missileObj.flatDistance*1.8);
	        missileObj.flightTime = (float)Math.max(100, 2.4*missileObj.flatDistance);
	        missileObj.acceleration = (float)missileObj.skyLimit*2/(missileObj.flightTime*missileObj.flightTime);
	        
	        missileObj.motionX = missileObj.xDifference/(missileObj.flightTime*0.6);
			missileObj.motionY = missileObj.yDifference/(missileObj.flightTime*0.6);
			missileObj.motionZ = missileObj.zDifference/(missileObj.flightTime*0.6);
		}
	}
	
	@Override
	public boolean onInteract(EDaoDan missileObj, EntityPlayer par1EntityPlayer)
	{
		if(par1EntityPlayer.getCurrentEquippedItem() != null)
    	{
    		if(par1EntityPlayer.getCurrentEquippedItem().getItem() instanceof ItGenZongQi)
    		{
    			if(ItGenZongQi.getTrackingEntity(par1EntityPlayer.getCurrentEquippedItem()) != null)
    			{
    				missileObj.trackingEntity = ItGenZongQi.getTrackingEntity(par1EntityPlayer.getCurrentEquippedItem()).entityId;
    				par1EntityPlayer.addChatMessage("Missile target locked to: "+ItGenZongQi.getTrackingEntity(par1EntityPlayer.getCurrentEquippedItem()).getEntityName());
    				return true;
    			}
    		}
    	}
		
		return false;
	}
	
	@Override
	public void onExplode(EDaoDan missileObj)
	{
		missileObj.worldObj.createExplosion(missileObj, missileObj.posX, missileObj.posY, missileObj.posZ, 6F);
	}
	
	@Override
	public boolean isCruise() { return false; }
}
