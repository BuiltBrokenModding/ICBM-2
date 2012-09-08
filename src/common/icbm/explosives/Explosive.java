package icbm.explosives;

import icbm.EntityExplosive;
import icbm.EntityMissile;
import icbm.EntityProceduralExplosion;
import icbm.ICBM;
import icbm.missiles.Missile;

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
import universalelectricity.Vector3;
import universalelectricity.extend.ITier;

public abstract class Explosive implements ITier
{
	public static final Explosive Condensed = new ExplosiveCondensed("Condensed", 0, 1);
	public static final Explosive Shrapnel = new ExplosiveFragmentation("Shrapnel", 1, 1);
	public static final Explosive Incendiary = new ExplosiveIncendiary("Incendiary", 2, 1);
	public static final Explosive Chemical = new ExplosiveChemical("Chemical", 3, 1);
	
	public static final int MAX_TIER_ONE = 4;
	
	public static final Explosive Fragmentation = new ExplosiveFragmentation("Fragmentation", 4, 2);
	public static final Explosive Contagious = new ExplosiveChemical("Contagious", 5, 2);
	public static final Explosive Sonic = new ExplosiveSonic("Sonic", 6, 2);
	public static final Explosive Breaching = new ExplosiveBreaching("Breaching", 7, 2);
	public static final Explosive Rejuvenation = new ExplosiveRejuvenation("Rejuvenation", 8, 2);
	
	public static final int MAX_TIER_TWO = 9;
	
	public static final Explosive Nuclear = new ExplosiveNuclear("Nuclear", 9, 3);
	public static final Explosive EMP = new ExplosiveEMP("EMP", 10, 3);
	public static final Explosive Conflagration = new ExplosiveConflagration("Conflagration", 11, 3);
	public static final Explosive Endothermic = new ExplosiveEndothermic("Endothermic", 12, 3);
	public static final Explosive AntiGravity = new ExplosiveAntiGravity("Anti-Gravitational", 13, 3);
	
	public static final Explosive Antimatter = new ExplosiveAntimatter("Antimatter", 14, 4);
	public static final Explosive Redmatter = new ExplosiveRedmatter("Red Matter", 15, 4);

	public static final int MAX_EXPLOSIVE_ID = 16;
	
	//Hidden Explosives
	public static final Explosive EMPWave = new ExplosiveEMPWave("EMP", 20, 3);
	public static final Explosive EMPSignal = new ExplosiveEMPSignal("EMP", 21, 3);
	public static final Explosive ConflagrationFire = new ExplosiveConflagrationFire("Conflagration", 22, 3);
	public static final Explosive DecayLand = new ExplosiveDecayLand("Decay Land", 23, 3);
	public static final Explosive Mutation = new ExplosiveMutation("Mutation Living", 24, 3);
	public static final Explosive EndothermicIce = new ExplosiveEndothermicIce("Endothermic", 25, 3);
	
	public static Explosive[] list;
	
	private String name;
	private int ID;
	private int tier;
	private int fuse;
	private Missile missile;
	public boolean isDisabled;

	protected Explosive(String name, int ID, int tier)
	{
    	if(list == null)
    	{
    		list = new Explosive[32];
    	}
    	
    	if(list[ID] != null)
        {
            throw new IllegalArgumentException("Explosive " + ID + " is already occupied by "+list[ID].getClass().getSimpleName()+"!");
        }
    	
    	list[ID] = this;
    	this.name = name;
        this.tier = tier;
        this.fuse = 100;
        this.ID = ID;
        this.missile = new Missile(name, ID, tier);
        
        this.isDisabled = getExplosiveConfig("Disabled", false);
    }
	
	public int getID() { return this.ID; }
	
	public String getName() { return this.name; }
	
	public String getExplosiveName() { return this.name+" Explosive"; }

	public String getGrenadeName() { return this.name+" Grenade"; }
	
	public String getMissileName() { return this.name+" Missile"; }

	@Override
	public int getTier() { return this.tier; }

	@Override
	public void setTier(int tier) { this.tier = tier; }
	
	public void setFuse(int fuse) { this.fuse = fuse; }
	
	/**
	 * The fuse of the explosion
	 * @return The Fuse
	 */
	public int getFuse() { return fuse; }
	
	/**
	 * Called at the start of a detontation
	 * @param worldObj
	 * @param entity
	 */
	public void preDetontation(World worldObj, Entity entity)
	{
		worldObj.playSoundAtEntity(entity, "random.fuse", 1.0F, 1.0F);
	}
	
	/**
	 * Called when the explosive is on fuse and going to explode.
	 * Called only when the explosive is in it's TNT form.
	 * @param fuseTicks - The amount of ticks this explosive is on fuse
	 */
	public void onDetonating(World worldObj, Vector3 position, int fuseTicks)
	{
        worldObj.spawnParticle("smoke", position.x, position.y + 0.5D, position.z, 0.0D, 0.0D, 0.0D);
	}
	
	/**
	 * Called when the TNT for of this explosive is destroy by an explosion
	 * @return - Fuse left
	 */
	public int onDestroyedByExplosion()
    {
        return (int)(this.fuse / 2 + Math.random() *this.fuse / 4);
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
	public void preExplosion(World worldObj, Vector3 position, Entity explosionSource) {}
	
	/**
	 * Called to do an explosion
	 * @param explosionSource - The entity that did the explosion
	 * @param explosiveID - The metadata of the explosive
	 * @param callCount - The amount of calls done for calling this explosion. Use only by procedural explosions
	 * @return - True if this explosive needs to continue to procedurally explode. False if otherwise
	 */
	public void doExplosion(World worldObj, Vector3 position, Entity explosionSource) {}

	public boolean doExplosion(World worldObj, Vector3 position, Entity explosionSource, int callCount)
	{
		doExplosion(worldObj, position, explosionSource);
		return false;
	}
	
	public boolean doExplosion(World worldObj, Vector3 position, Entity explosionSource, int metadata, int callCount)
	{
		return doExplosion(worldObj, position, explosionSource, callCount);
	}
	
	/**
	 * Called every tick when this explosive is doing it's procedural explosion
	 * @param ticksExisted - The ticks in which this explosive existed
	 */
	public void onUpdate(World worldObj, Vector3 position, int ticksExisted) {}
	
	/**
	 * Called after the explosion is completed
	 */
	public void postExplosion(World worldObj, Vector3 position, Entity explosionSource){};
	
	public int countIncrement() { return 1;}
	
	/**
	 * Spawns an explosive (TNT form) in the world
	 * @param worldObj
	 * @param position
	 * @param cause - 0: N/A, 1: Destruction, 2: Fire
	 */
	public void spawnExplosive(World worldObj, Vector3 position, ForgeDirection orientation, byte cause)
	{
		position.add(0.5D);
		EntityExplosive entityExplosive = new EntityExplosive(worldObj, position, (byte) orientation.ordinal(), this.getID());
		
		switch(cause)
		{
			case 1: entityExplosive.destroyedByExplosion(); break;
			case 2: entityExplosive.setFire(10); break;
		}

		worldObj.spawnEntityInWorld(entityExplosive);
	}
	
	public void spawnExplosive(World worldObj, Vector3 position, byte orientation)
	{
		this.spawnExplosive(worldObj, position, ForgeDirection.getOrientation(orientation), (byte)0);
	}
	
	public boolean getExplosiveConfig(String comment, boolean defaultValue)
	{
		if(comment != null && comment != "")
		{
			comment = "_"+comment;
		}
		
		return ICBM.getBooleanConfig(this.name+comment, defaultValue);
	}
	
	/**
	 * Called to add the recipe for this explosive
	 */
	public void addCraftingRecipe() {};
	
	public ItemStack getItemStack() { return new ItemStack(ICBM.blockExplosive, 1, this.getID()); }
	
	public ItemStack getItemStack(int amount) { return new ItemStack(ICBM.blockExplosive, amount, this.getID()); }
	
	public static void createExplosion(World worldObj, Vector3 position, Entity entity, int explosiveID)
	{
		if(list[explosiveID].proceduralInterval(worldObj, -1) > 0)
        {
			if(!worldObj.isRemote)
			{
				worldObj.spawnEntityInWorld(new EntityProceduralExplosion(worldObj, new Vector3(position.x, position.y, position.z), explosiveID));
			}
        }
        else
        {
        	list[explosiveID].doExplosion(worldObj, new Vector3(position.x, position.y, position.z), entity, explosiveID , -1);
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
            
            if(entity instanceof EntityMissile)
            {
            	((EntityMissile)entity).explode();
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
