package icbm.daodan;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Entity;

public class DFanDan extends DaoDan
{
	public static final int ABMRange = 30;
	
	protected DFanDan(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	@Override
	public void onTickFlight(EDaoDan missileObj)
	{
		if(missileObj.lockedTarget != null)
		{
			if(!missileObj.lockedTarget.isExploding)
			{
		        missileObj.motionX = (missileObj.lockedTarget.posX - missileObj.posX)*(0.3F);
		        missileObj.motionY = (missileObj.lockedTarget.posY - missileObj.posY)*(0.3F);
				missileObj.motionZ = (missileObj.lockedTarget.posZ - missileObj.posZ)*(0.3F);
			}
			else
			{
				missileObj.explode();
			}
			
			return;
		}
		
		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(missileObj.posX - ABMRange, missileObj.posY - ABMRange, missileObj.posZ - ABMRange, missileObj.posX + ABMRange, missileObj.posY + ABMRange, missileObj.posZ + ABMRange);
        Entity nearestEntity = missileObj.worldObj.findNearestEntityWithinAABB(EDaoDan.class, bounds, missileObj);

        if(nearestEntity instanceof EDaoDan)
        {
        	if(((EDaoDan)nearestEntity).ticksInAir >= 0)
        	{
	        	//Lock target onto missileObj missile
	        	missileObj.lockedTarget = (EDaoDan)nearestEntity;
	        	missileObj.didTargetLockBefore = true;
	    		missileObj.worldObj.playSoundAtEntity(missileObj, "icbm.targetlocked", 5F, 0.9F);
        	}
        }
        else
        {
        	missileObj.motionX = missileObj.xDifference/missileObj.flightTime;
			missileObj.motionZ = missileObj.zDifference/missileObj.flightTime;
			
			if(missileObj.didTargetLockBefore == true)
			{
				missileObj.explode();
			}
        }
	}
	
	@Override
	public void onExplode(EDaoDan missileObj)
	{
		missileObj.worldObj.createExplosion(missileObj, missileObj.posX, missileObj.posY, missileObj.posZ, 6F, true);
	}
	
	@Override
	public boolean isCruise() { return false; }
}
