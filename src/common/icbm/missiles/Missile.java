package icbm.missiles;

import icbm.EntityMissile;
import icbm.ICBM;
import icbm.explosives.Explosive;
import net.minecraft.src.ItemStack;
import universalelectricity.Vector3;
import universalelectricity.extend.ITier;

public class Missile implements ITier
{
	public static final Missile AntiBallistic = new MissileAntiBallistic("Anti-Ballistic", 101, 2);
	public static final Missile Cluster = new MissileCluster("Cluster", 102, 2);
	public static final Missile NuclearCluster = new MissileNuclearCluster("Nuclear Cluster", 103, 2);

	public static Missile[] list;

	private String name;
	private int ID;
	private int tier;

	public Missile(String name, int ID, int tier)
	{
    	if(list == null)
    	{
    		list = new Missile[256];
    	}
    	
    	if(list[ID] != null)
        {
            throw new IllegalArgumentException("Missile " + ID + " is already occupied when adding "+this+"!");
        }
    	
    	list[ID] = this;
    	this.name = name;
        this.tier = tier;
        this.ID = ID;
    }
	
	public int getID() { return this.ID; }
	
	public String getName() { return this.name; }

	@Override
	public int getTier() { return this.tier; }

	@Override
	public void setTier(int tier) { }
	
	/**
	 * Called every tick while flying
	 */
	public void onTickFlight(EntityMissile missileObj)
	{
		
	}
	
	/**
	 * Called when the missile blows up
	 * @param missileObj
	 */
	public void onExplode(EntityMissile missileObj)
	{
		Explosive.createExplosion(missileObj.worldObj, Vector3.get(missileObj), missileObj, missileObj.missileID);
	}
	
	public ItemStack getItemStack()
	{
		return new ItemStack(ICBM.itemMissile, 1, this.getID());
	}
	
	public boolean isCruise() { return true; }
}
