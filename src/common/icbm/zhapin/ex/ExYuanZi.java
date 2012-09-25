package icbm.zhapin.ex;


import icbm.ICBM;
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
	public static final int NENG_LIANG = 200;
	public static final int CALL_COUNT = 80;
	
	public static final int RAY_CAST_INTERVAL = 2;
	
	public ExYuanZi(String name, int ID, int tier)
	{
		super(name, ID, tier);
		this.setYinXin(200);
	}
	
	@Override
	public void baoZhaQian(World worldObj, Vector3 position, Entity explosionSource)
	{
		EZhaPin source = (EZhaPin)explosionSource;
		
		int blockCount = 0;
		
		for(int x = -BAN_JING; x < BAN_JING; x++)
		{
			for(int y = -BAN_JING; y < BAN_JING; y++)
			{
				for(int z = -BAN_JING; z < BAN_JING; z++)
				{
					double distance = MathHelper.sqrt_double(x*x + y*y + z*z);
					
                    if(distance < BAN_JING && distance > BAN_JING - 1)
					{
                    	if(blockCount % RAY_CAST_INTERVAL == 0)
                    	{
                    		source.dataList.add(new Vector3(x, y, z));
                    	}
                    	
                    	blockCount ++;
					}
				}
			}
		}
		
		this.doDamageEntities(worldObj, position, BAN_JING, BAN_JING*100);
	}

	@Override
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int callCount)
	{		
		EZhaPin source = (EZhaPin)explosionSource;
	
		for(int i = callCount; i < callCount+CALL_COUNT; i ++)
		{
			if(i >= source.dataList.size())
			{
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
				
				return false;
			}
			
			Vector3 corner = (Vector3)source.dataList.get(i);
			
			float power = NENG_LIANG - (NENG_LIANG*worldObj.rand.nextFloat()/2);
			
			double xStep = (double)((float)corner.intX() / ((float)BAN_JING/2 - 1.0F) * 2.0F - 1.0F);
            double yStep = (double)((float)corner.intY() / ((float)BAN_JING/2 - 1.0F) * 2.0F - 1.0F);
            double zStep = (double)((float)corner.intZ() / ((float)BAN_JING/2 - 1.0F) * 2.0F - 1.0F);
            double diagonalDistance = Math.sqrt(xStep * xStep + yStep * yStep + zStep * zStep);
            xStep /= diagonalDistance;
            yStep /= diagonalDistance;
            zStep /= diagonalDistance;
            
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
						resistance = 4;
					}
					else if(blockID == Block.obsidian.blockID)
					{
						resistance = 30;
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
				
				targetPosition.x += xStep * (double)var21;
				targetPosition.y += yStep * (double)var21;
				targetPosition.z += zStep * (double)var21;
			}
		}

		int r = callCount/CALL_COUNT;
		
        if(worldObj.isRemote && r < BAN_JING*1.5)
        {        	
        	for(int x = -r; x < r; x++)
    		{
				for(int z = -r; z < r; z++)
				{
					double distance = MathHelper.sqrt_double(x*x + z*z);
					
                    if(distance < r && distance > r-1)
					{
						if(worldObj.rand.nextFloat() > 0.95)
						{
    						//worldObj.spawnParticle("largesmoke",  x + position.x, position.y, z + position.z, 0, 0, 0);
   							worldObj.spawnParticle("hugeexplosion", x + position.x, position.y, z + position.z, 0, 0, 0);
						}
					}
				}
    		}
        }
        		
		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.explosion", 7.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
		
		return true;
	}
	
	@Override
	public int countIncrement() { return CALL_COUNT;}
	
	/**
	 * The interval in ticks before the next procedural call of this explosive
	 * @param return - Return -1 if this explosive does not need proceudral calls
	 */
	@Override
	public int proceduralInterval(){ return 1; }

	@Override
	public void baoZhaHou(World worldObj, Vector3 position, Entity explosionSource)
	{
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
