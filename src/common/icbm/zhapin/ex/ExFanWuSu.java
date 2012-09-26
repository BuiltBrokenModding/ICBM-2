package icbm.zhapin.ex;


import icbm.ICBM;
import icbm.zhapin.EZhaPin;
import icbm.zhapin.ZhaPin;

import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.DamageSource;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import universalelectricity.UniversalElectricity;
import universalelectricity.prefab.Vector3;

public class ExFanWuSu extends ZhaPin
{	
	public static final int RADUIS = 60;
	public boolean destroyBedrock = false;
	
	public ExFanWuSu(String name, int ID, int tier)
	{
		super(name, ID, tier);
		this.setYinXin(300);
		this.destroyBedrock = UniversalElectricity.getConfigData(ICBM.CONFIGURATION, this.getMing()+"Destroy Bedrock", destroyBedrock);
	}
	
	/**
	 * Called before an explosion happens
	 */
	public void baoZhaQian(World worldObj, Vector3 position, Entity explosionSource)
	{
		explosionSource.posY += 5;
	}

	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity source, int callCount)
	{		
		Vector3 currentPos;
		int blockID;
		double dist;
				    		
		for(int x = -RADUIS; x < RADUIS; x++)
		{
			for(int z = -RADUIS; z < RADUIS; z++)
			{
				dist = MathHelper.sqrt_double((x*x + z*z));

				if(dist > RADUIS) continue;
				
				currentPos = new Vector3(position.x + x, position.y, position.z + z);
				blockID = worldObj.getBlockId(currentPos.intX(), currentPos.intY(), currentPos.intZ());		
				
				if(blockID > 0)
				{
					if(blockID == Block.bedrock.blockID && !destroyBedrock) continue;
					
					if(dist < RADUIS - 1 || worldObj.rand.nextInt(3) > 0)
					{
						worldObj.setBlock(currentPos.intX(), currentPos.intY(), currentPos.intZ(), 0);
					}
				}
			}
		}
    	
		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.explosion", 6.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
		
		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x - RADUIS, position.y - RADUIS, position.z - RADUIS, position.x + RADUIS, position.y + RADUIS, position.z + RADUIS);
        List<Entity> allEntities = worldObj.getEntitiesWithinAABB(Entity.class, bounds);
    	
        for(Entity entity : allEntities)
        {     
        	if(entity instanceof EZhaPin)
        	{
	        	if(entity != source) 
	        	{
	        		((EZhaPin)entity).endExplosion();
	        	}
        	}
        	else if(entity instanceof EntityLiving)
        	{
        		entity.attackEntityFrom(DamageSource.explosion, 99999999);
        	}
        }
		
		if(position.y <= 0)
		{
			return false;
		}
		
		source.posY = Math.round(position.y - 1);
		return true;
	}
	
	/**
	 * The interval in ticks before the next procedural call of this explosive
	 * @param return - Return -1 if this explosive does not need proceudral calls
	 */
	@Override
	public int proceduralInterval(){ return 4; }

	@Override
	public void baoZhaHou(World worldObj, Vector3 position, Entity explosionSource)
	{
		ZhaPin.DecayLand.doBaoZha(worldObj, position, null, 45, -1);
	}

	/**
	 * Called when the explosive is on fuse and going to explode.
	 * Called only when the explosive is in it's TNT form.
	 * @param fuseTicks - The amount of ticks this explosive is on fuse
	 */
	@Override
	public void onYinZha(World worldObj, Vector3 position, int fuseTicks)
	{
        super.onYinZha(worldObj, position, fuseTicks);
        
        if(fuseTicks % 25 == 0)
        {
    		worldObj.playSoundEffect((int)position.x, (int)position.y, (int)position.z, "icbm.alarm", 4F, 1F);
        }
	}

	@Override
	public void init()
	{
		
	}

}
