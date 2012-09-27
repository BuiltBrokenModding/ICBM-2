package icbm.zhapin;

import icbm.ICBM;
import icbm.daodan.DaoDan;
import icbm.daodan.EDaoDan;
import icbm.zhapin.ex.ExBianZhong;
import icbm.zhapin.ex.ExBingDan;
import icbm.zhapin.ex.ExBingDan2;
import icbm.zhapin.ex.ExDecayLand;
import icbm.zhapin.ex.ExDianCi;
import icbm.zhapin.ex.ExDianCiSignal;
import icbm.zhapin.ex.ExDianCiWave;
import icbm.zhapin.ex.ExFanWuSu;
import icbm.zhapin.ex.ExHongSu;
import icbm.zhapin.ex.ExHuanYuan;
import icbm.zhapin.ex.ExHuo;
import icbm.zhapin.ex.ExPiaoFu;
import icbm.zhapin.ex.ExQunDan;
import icbm.zhapin.ex.ExShengBuo;
import icbm.zhapin.ex.ExTaiYang;
import icbm.zhapin.ex.ExTaiYang2;
import icbm.zhapin.ex.ExTuPuo;
import icbm.zhapin.ex.ExYaSuo;
import icbm.zhapin.ex.ExYuanZi;

import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.DamageSource;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.Vec3;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.UniversalElectricity;
import universalelectricity.implement.ITier;
import universalelectricity.prefab.Vector3;

public abstract class ZhaPin implements ITier
{
	public static final ZhaPin Condensed = new ExYaSuo("Condensed", 0, 1);
	public static final ZhaPin Shrapnel = new ExQunDan("Shrapnel", 1, 1);
	public static final ZhaPin Incendiary = new ExHuo("Incendiary", 2, 1);
	public static final ZhaPin Chemical = new EDu("Chemical", 3, 1);
	
	public static final int MAX_TIER_ONE = 4;
	
	public static final ZhaPin Fragmentation = new ExQunDan("Fragmentation", 4, 2);
	public static final ZhaPin Contagious = new EDu("Contagious", 5, 2);
	public static final ZhaPin Sonic = new ExShengBuo("Sonic", 6, 2);
	public static final ZhaPin Breaching = new ExTuPuo("Breaching", 7, 2);
	public static final ZhaPin Rejuvenation = new ExHuanYuan("Rejuvenation", 8, 2);
	
	public static final int MAX_TIER_TWO = 9;
	
	public static final ZhaPin Nuclear = new ExYuanZi("Nuclear", 9, 3);
	public static final ZhaPin EMP = new ExDianCi("EMP", 10, 3);
	public static final ZhaPin Conflagration = new ExTaiYang("Conflagration", 11, 3);
	public static final ZhaPin Endothermic = new ExBingDan("Endothermic", 12, 3);
	public static final ZhaPin AntiGravity = new ExPiaoFu("Anti-Gravitational", 13, 3);
	
	public static final ZhaPin Antimatter = new ExFanWuSu("Antimatter", 14, 4);
	public static final ZhaPin Redmatter = new ExHongSu("Red Matter", 15, 4);

	public static final int MAX_EXPLOSIVE_ID = 16;
	
	//Hidden Explosives
	public static final ZhaPin EMPWave = new ExDianCiWave("EMP", 20, 3);
	public static final ZhaPin EMPSignal = new ExDianCiSignal("EMP", 21, 3);
	public static final ZhaPin ConflagrationFire = new ExTaiYang2("Conflagration", 22, 3);
	public static final ZhaPin DecayLand = new ExDecayLand("Decay Land", 23, 3);
	public static final ZhaPin Mutation = new ExBianZhong("Mutation Living", 24, 3);
	public static final ZhaPin EndothermicIce = new ExBingDan2("Endothermic", 25, 3);
	
	public static ZhaPin[] list;
	
	private String mingZi;
	private int ID;
	private int tier;
	private int yinXin;
	private DaoDan daoDan;
	protected boolean isDisabled;
	protected boolean isMobile = false;

	protected ZhaPin(String name, int ID, int tier)
	{
    	if(list == null)
    	{
    		list = new ZhaPin[32];
    	}
    	
    	if(list[ID] != null)
        {
            throw new IllegalArgumentException("Explosive " + ID + " is already occupied by "+list[ID].getClass().getSimpleName()+"!");
        }
    	
    	list[ID] = this;
    	this.mingZi = name;
        this.tier = tier;
        this.yinXin = 100;
        this.ID = ID;
        this.daoDan = new DaoDan(name, ID, tier);
        
        this.isDisabled = UniversalElectricity.getConfigData(ICBM.CONFIGURATION, "Disable "+this.mingZi, false);
    }
	
	public int getID() { return this.ID; }
	
	public String getMing() { return this.mingZi; }
	
	public String getZhaPinMing() { return this.mingZi+" Explosive"; }

	public String getGrenadeName() { return this.mingZi+" Grenade"; }
	
	public String getDaoDanMing() { return this.mingZi+" Missile"; }

	@Override
	public int getTier() { return this.tier; }

	@Override
	public void setTier(int tier) { this.tier = tier; }
	
	public void setYinXin(int fuse) { this.yinXin = fuse; }
	
	/**
	 * The fuse of the explosion
	 * @return The Fuse
	 */
	public int yinXin() { return yinXin; }
	
	/**
	 * Called at the start of a detontation
	 * @param worldObj
	 * @param entity
	 */
	public void yinZhaQian(World worldObj, Entity entity)
	{
		worldObj.playSoundAtEntity(entity, "random.fuse", 1.0F, 1.0F);
	}
	
	/**
	 * Called when the explosive is on fuse and going to explode.
	 * Called only when the explosive is in it's TNT form.
	 * @param fuseTicks - The amount of ticks this explosive is on fuse
	 */
	public void onYinZha(World worldObj, Vector3 position, int fuseTicks)
	{
        worldObj.spawnParticle("smoke", position.x, position.y + 0.5D, position.z, 0.0D, 0.0D, 0.0D);
	}
	
	/**
	 * Called when the TNT for of this explosive is destroy by an explosion
	 * @return - Fuse left
	 */
	public int onBeiZha()
    {
        return (int)(this.yinXin / 2 + Math.random() *this.yinXin / 4);
    }
	
	/**
	 * The interval in ticks before the next procedural call of this explosive
	 * @param return - Return -1 if this explosive does not need proceudral calls
	 */
	protected int proceduralInterval() { return -1; }
	
	public int proceduralInterval(World worldObj, int callCounts) { return this.proceduralInterval(); }
	
	/**
	 * Called before an explosion happens
	 */
	public void baoZhaQian(World worldObj, Vector3 position, Entity explosionSource) {}
	
	/**
	 * Called to do an explosion
	 * @param explosionSource - The entity that did the explosion
	 * @param explosiveID - The metadata of the explosive
	 * @param callCount - The amount of calls done for calling this explosion. Use only by procedural explosions
	 * @return - True if this explosive needs to continue to procedurally explode. False if otherwise
	 */
	public void doBaoZha(World worldObj, Vector3 position, Entity explosionSource) {}

	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int callCount)
	{
		doBaoZha(worldObj, position, explosionSource);
		return false;
	}
	
	public boolean doBaoZha(World worldObj, Vector3 position, Entity explosionSource, int metadata, int callCount)
	{
		return doBaoZha(worldObj, position, explosionSource, callCount);
	}
	
	/**
	 * Called every tick when this explosive is doing it's procedural explosion
	 * @param ticksExisted - The ticks in which this explosive existed
	 */
	public void gengXin(World worldObj, Vector3 position, int ticksExisted) {}
	
	/**
	 * Called after the explosion is completed
	 */
	public void baoZhaHou(World worldObj, Vector3 position, Entity explosionSource){};
	
	public int countIncrement() { return 1;}
	
	/**
	 * Spawns an explosive (TNT form) in the world
	 * @param worldObj
	 * @param position
	 * @param cause - 0: N/A, 1: Destruction, 2: Fire
	 */
	public void spawnZhaDan(World worldObj, Vector3 position, ForgeDirection orientation, byte cause)
	{
		if(!this.isDisabled)
		{
			position.add(0.5D);
			EZhaDan eZhaDan = new EZhaDan(worldObj, position, (byte) orientation.ordinal(), this.getID());
			
			switch(cause)
			{
				case 1: eZhaDan.destroyedByExplosion(); break;
				case 2: eZhaDan.setFire(10); break;
			}
	
			worldObj.spawnEntityInWorld(eZhaDan);
		}
	}
	
	public void spawnZhaDan(World worldObj, Vector3 position, byte orientation)
	{
		this.spawnZhaDan(worldObj, position, ForgeDirection.getOrientation(orientation), (byte)0);
	}
	
	/**
	 * Called to add the recipe for this explosive
	 */
	public void init() {};
	
	public ItemStack getItemStack() { return new ItemStack(ICBM.blockZha4Dan4, 1, this.getID()); }
	
	public ItemStack getItemStack(int amount) { return new ItemStack(ICBM.blockZha4Dan4, amount, this.getID()); }
	
	public static void createBaoZha(World worldObj, Vector3 position, Entity entity, int explosiveID)
	{
		if(!list[explosiveID].isDisabled)
		{
			if(list[explosiveID].proceduralInterval(worldObj, -1) > 0)
	        {
				if(!worldObj.isRemote)
				{
					worldObj.spawnEntityInWorld(new EZhaPin(worldObj, new Vector3(position.x, position.y, position.z), explosiveID, list[explosiveID].isMobile));
				}
	        }
	        else
	        {
	        	list[explosiveID].doBaoZha(worldObj, new Vector3(position.x, position.y, position.z), entity, explosiveID , -1);
	        }
		}
	}
	
	public static void doDamageEntities(World worldObj, Vector3 position, float radius, float power)
	{
		doDamageEntities(worldObj, position, radius, power, true);
	}
	
	public static void doDamageEntities(World worldObj, Vector3 position, float radius, float power, boolean destroyItem)
    {
    	//Step 2: Damage all entities
    	int var3;
        int var4;
        int var5;
        double var15;
        double var17;
        double var19;
        
        radius *= 2.0F;
        var3 = MathHelper.floor_double(position.x - radius - 1.0D);
        var4 = MathHelper.floor_double(position.x + radius + 1.0D);
        var5 = MathHelper.floor_double(position.y - radius - 1.0D);
        int var29 = MathHelper.floor_double(position.y + radius + 1.0D);
        int var7 = MathHelper.floor_double(position.z - radius - 1.0D);
        int var30 = MathHelper.floor_double(position.z + radius + 1.0D);
        List allEntities = worldObj.getEntitiesWithinAABBExcludingEntity(null, AxisAlignedBB.getBoundingBox(var3, var5, var7, var4, var29, var30));
        Vec3 var31 = Vec3.createVectorHelper(position.x, position.y, position.z);

        for (int var11 = 0; var11 < allEntities.size(); ++var11)
        {
            Entity entity = (Entity)allEntities.get(var11);
            
            if(entity instanceof EDaoDan)
            {
            	((EDaoDan)entity).explode();
            	break;
            }
            
            if(entity instanceof EntityItem && !destroyItem) continue;
            
            double var13 = entity.getDistance(position.x, position.y, position.z) / radius;

            if (var13 <= 1.0D)
            {
                var15 = entity.posX - position.x;
                var17 = entity.posY - position.y;
                var19 = entity.posZ - position.z;
                double var35 = MathHelper.sqrt_double(var15 * var15 + var17 * var17 + var19 * var19);
                var15 /= var35;
                var17 /= var35;
                var19 /= var35;
                double var34 = worldObj.getBlockDensity(var31, entity.boundingBox);
                double var36 = (1.0D - var13) * var34;
                int damage = 0;
                
                damage = (int)((var36 * var36 + var36) / 2.0D * 8.0D * power + 1.0D);
                
                entity.attackEntityFrom(DamageSource.explosion, damage);
                                
                entity.motionX += var15 * var36;
                entity.motionY += var17 * var36;
                entity.motionZ += var19 * var36;
            }
        }
	}
}
