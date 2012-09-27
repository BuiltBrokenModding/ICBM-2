package icbm.zhapin.ex;


import icbm.ICBM;
import icbm.ParticleSpawner;
import icbm.zhapin.EZhaPin;
import icbm.zhapin.ZhaPin;
import net.minecraft.src.Block;
import net.minecraft.src.BlockFluid;
import net.minecraft.src.Entity;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import universalelectricity.prefab.Vector3;
import universalelectricity.recipe.RecipeManager;

public class ExYuanZi extends ZhaPin
{	
	public static final int BAN_JING = 35;
	public static final int NENG_LIANG = 30;
	public static final int CALC_SPEED = 500;
	
	public ExYuanZi(String name, int ID, int tier)
	{
		super(name, ID, tier);
		this.setYinXin(200);
	}
	
	@Override
	public void baoZhaQian(World worldObj, Vector3 position, Entity explosionSource) 
	{
		EZhaPin source = (EZhaPin)explosionSource;
		
		int steps = (int)Math.ceil(3.141592653589793D / Math.atan(1.0D / BAN_JING));
		
		for (int phi_n = 0; phi_n < 2 * steps; phi_n++)
		{
			for (int theta_n = 0; theta_n < steps; theta_n++)
			{			
				double phi = 6.283185307179586D / steps * phi_n;
				double theta = 3.141592653589793D / steps * theta_n;

				source.dataList.add(new Vector3(Math.sin(theta) * Math.cos(phi), Math.cos(theta), Math.sin(theta) * Math.sin(phi)));
			}
		}
		
        doDamageEntities(worldObj, position, BAN_JING, NENG_LIANG*1000);
		
		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.explosion", 7.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
	}

	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int callCount)
	{
		EZhaPin source = (EZhaPin)explosionSource;
		
		int i = (callCount+1)*CALC_SPEED - CALC_SPEED;
		
		for(; i < source.dataList.size(); i ++)
		{			
			if(i > (callCount+1)*CALC_SPEED) break;
			
			Vector3 delta = (Vector3)source.dataList.get(i);
		    			
			float power = NENG_LIANG - (NENG_LIANG*worldObj.rand.nextFloat()/2);
            
            Vector3 targetPosition = position.clone();
			
			for(float var21 = 0.3F; power > 0f; power -= var21 * 0.75F)
			{
				int blockID = worldObj.getBlockId(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());
				
				if(blockID > 0)
				{
					float resistance = 0;
					
					if(blockID == Block.bedrock.blockID)
					{
						break;
					}
					else if(Block.blocksList[blockID] instanceof BlockFluid)
					{
						resistance = 2;
					}
					else if(blockID == Block.obsidian.blockID)
					{
						resistance = 8;
					}
					else
					{
						resistance = Block.blocksList[blockID].getExplosionResistance(explosionSource, worldObj, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), position.intX(), position.intY(), position.intZ());
					}
					
					power -= resistance;	
					
					if(power > 0f)
					{						
						if(!source.dataList2.contains(targetPosition))
						{
							source.dataList2.add(targetPosition.clone());
						}
					}
				}

				if(targetPosition.distanceTo(position) > BAN_JING+10) break;
				
				targetPosition.x += delta.x;
				targetPosition.y += delta.y;
				targetPosition.z += delta.z;
			}
		}
		
		int r = callCount;

    	boolean reverse = false;
    	
    	if(r > BAN_JING)
		{
    		r = BAN_JING - (r - BAN_JING);
    		reverse = true;
		}
    	    	        	
    	if(r > 0)
    	{
    		if(worldObj.isRemote)
            {
	        	for(int x = -r; x < r; x++)
	    		{
					for(int z = -r; z < r; z++)
					{
						double distance = MathHelper.sqrt_double(x*x + z*z);
						
	                    if(distance < r && distance > r-1)
						{
							if(worldObj.rand.nextFloat() < Math.max(0.0006*r, 0.005) || (ICBM.ADVANCED_VISUALS && worldObj.rand.nextFloat() < Math.max(0.0008*r, 0.008)))
							{
								Vector3 targetPosition = Vector3.add(position, new Vector3(x, 0, z));
								
								if(!reverse)
								{
									worldObj.spawnParticle("hugeexplosion", targetPosition.x, targetPosition.y, targetPosition.z, 0, 0, 0);
								}
								else if(ICBM.ADVANCED_VISUALS)
								{
									ParticleSpawner.spawnParticle("smoke", worldObj, targetPosition, 0F, 0F, 0F, 5F, 1F);
								}
							}
						}
					}
	    		}
            }
    	}
    	
    	if(r <= 0 && i > source.dataList.size())
    	{
    		return false;
        }
    	        		
		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.redmatter", 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
		
		return true;
	}
	
	/**
	 * The interval in ticks before the next procedural call of this explosive
	 * @param return - Return -1 if this explosive does not need proceudral calls
	 */
	@Override
	public int proceduralInterval(){ return 1; }

	@Override
	public void baoZhaHou(World worldObj, Vector3 position, Entity explosionSource)
	{
		EZhaPin source = (EZhaPin)explosionSource;

		for(Object obj : source.dataList2)
		{
			Vector3 targetPosition = (Vector3)obj;
			int blockID = worldObj.getBlockId(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());
			
			if(blockID > 0)
			{
				worldObj.setBlockWithNotify(targetPosition.intX(), targetPosition.intY(), targetPosition.intZ(), 0);
	            Block.blocksList[blockID].onBlockDestroyedByExplosion(worldObj, targetPosition.intX(), targetPosition.intY(), targetPosition.intZ());
			}
		}
		
        doDamageEntities(worldObj, position, BAN_JING, NENG_LIANG*1000);
		
		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.explosion", 10.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
		
		ZhaPin.DecayLand.doBaoZha(worldObj, position, null, BAN_JING+5, -1);
		ZhaPin.Mutation.doBaoZha(worldObj, position, null, BAN_JING+5, -1);
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
        RecipeManager.addRecipe(this.getItemStack(), new Object [] {"?@?", "@!@", "?@?", '!', Condensed.getItemStack(), '@', Block.tnt, '?', "ingotUranium"}, this.getMing(), ICBM.CONFIGURATION, true);
	}
}
